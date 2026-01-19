package guru.qa.rangiffler.api;

import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.model.graphql.photos.PhotoInputGql;
import guru.qa.rangiffler.service.mapper.FeedMapper;
import guru.qa.rangiffler.service.mapper.PhotoMapper;
import guru.qa.rangiffler.service.utils.GrpcExceptionHandler;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@Component
@ParametersAreNonnullByDefault
public class GrpcPhotosClient {
  private static final Logger LOG = LoggerFactory.getLogger(GrpcPhotosClient.class);

  private final RangifflerPhotosServiceGrpc.RangifflerPhotosServiceBlockingStub rangifflerPhotosServiceBlockingStub;
  private final GrpcExceptionHandler grpcExceptionHandler;
  private final PhotoMapper photoMapper;
  private final FeedMapper feedMapper;

  @Autowired
  public GrpcPhotosClient(RangifflerPhotosServiceGrpc.RangifflerPhotosServiceBlockingStub rangifflerPhotosServiceBlockingStub, PhotoMapper photoMapper,
                          FeedMapper feedMapper) {
    this.rangifflerPhotosServiceBlockingStub = rangifflerPhotosServiceBlockingStub;
    this.grpcExceptionHandler = new GrpcExceptionHandler(
      GrpcPhotosClient.class,
      "gRPC Photos service"
    );
    this.photoMapper = photoMapper;
    this.feedMapper = feedMapper;
  }

  @Nonnull
  public PhotoResponse addPhoto(String userId, PhotoInputGql photo) {
    try {
      PhotoRequest request = photoMapper.toProtoPhotoSaveRequest(userId, photo);
      PhotoResponse response = rangifflerPhotosServiceBlockingStub.addPhoto(request);
      LOG.info("### Photo with ID: {} was saved successfully ###", response.getId());
      return response;
    } catch (StatusRuntimeException e) {
      throw grpcExceptionHandler.handleGraphQLError(e);
    }
  }

  @Nonnull
  public PhotoResponse updatePhoto(String userId, PhotoInputGql photo) {
    try {
      PhotoUpdateRequest request = photoMapper.toProtoPhotoUpdateRequest(userId, photo);
      PhotoResponse response = rangifflerPhotosServiceBlockingStub.updatePhoto(request);
      LOG.info("### Photo with ID: {} was updated successfully ###", response.getId());
      return response;
    } catch (StatusRuntimeException e) {
      throw grpcExceptionHandler.handleGraphQLError(e);
    }
  }

  @Nonnull
  public String deletePhoto(String userId, String photoId) {
    try {
      PhotoDeleteRequest request = photoMapper.toProtoPhotoDeleteRequest(userId, photoId);
      return rangifflerPhotosServiceBlockingStub.deletePhoto(request).getId();
    } catch (StatusRuntimeException e) {
      throw grpcExceptionHandler.handleGraphQLError(e);
    }
  }

  @Nonnull
  public PhotoResponse likePhoto(String userId, PhotoInputGql photo) {
    PhotoLikeRequest request = photoMapper.toProtoPhotoLikeRequest(userId, photo);
    return rangifflerPhotosServiceBlockingStub.addPhotoLike(request);
  }

  @Nonnull
  public FeedResponse getFeed(Pageable pageable, String userId, boolean withFriends) {
    try {
      FeedRequest request = feedMapper.toProtoFeedRequest(pageable, userId, withFriends);
      return rangifflerPhotosServiceBlockingStub.getUserFeed(request);
    } catch (StatusRuntimeException e) {
      throw grpcExceptionHandler.handleGraphQLError(e);
    }
  }
}
