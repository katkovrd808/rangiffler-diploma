package guru.qa.rangiffler.service.impl;

import guru.qa.rangiffler.api.GrpcPhotosClient;
import guru.qa.rangiffler.api.GrpcUserdataClient;
import guru.qa.rangiffler.grpc.FeedResponse;
import guru.qa.rangiffler.grpc.PhotoResponse;
import guru.qa.rangiffler.model.graphql.photos.FeedGql;
import guru.qa.rangiffler.model.graphql.photos.PhotoGql;
import guru.qa.rangiffler.model.graphql.photos.PhotoInputGql;
import guru.qa.rangiffler.service.PhotoService;
import guru.qa.rangiffler.service.mapper.FeedMapper;
import guru.qa.rangiffler.service.mapper.PhotoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@Component
@ParametersAreNonnullByDefault
public class PhotoServiceImpl implements PhotoService {
  private static final Logger LOG = LoggerFactory.getLogger(PhotoServiceImpl.class);

  private final GrpcPhotosClient grpcPhotosClient;
  private final GrpcUserdataClient grpcUserdataClient;
  private final PhotoMapper photoMapper;
  private final FeedMapper feedMapper;

  @Autowired
  public PhotoServiceImpl(GrpcPhotosClient grpcPhotosClient, GrpcUserdataClient grpcUserdataClient, PhotoMapper photoMapper, FeedMapper feedMapper) {
    this.grpcPhotosClient = grpcPhotosClient;
    this.grpcUserdataClient = grpcUserdataClient;
    this.photoMapper = photoMapper;
    this.feedMapper = feedMapper;
  }

  @Override
  public PhotoGql save(String username, PhotoInputGql photo) {
    final String userId = findUserIdByUsername(username);
    final PhotoResponse response = grpcPhotosClient.addPhoto(userId, photo);
    return photoMapper.toPhotoGql(response, username);
  }

  @Override
  public PhotoGql update(String username, PhotoInputGql photo) {
    final String userId = findUserIdByUsername(username);
    final PhotoResponse response = grpcPhotosClient.updatePhoto(userId, photo);
    return photoMapper.toPhotoGql(response, username);
  }

  @Override
  public PhotoGql updateWithLike(String username, PhotoInputGql photo) {
    final String userId = findUserIdByUsername(username);
    final PhotoResponse response = grpcPhotosClient.likePhoto(userId, photo);
    return photoMapper.toPhotoGql(response, username);
  }

  @Override
  public String delete(String username, String photoId) {
    final String userId = findUserIdByUsername(username);
    return grpcPhotosClient.deletePhoto(userId, photoId);
  }

  @Override
  public FeedGql feed(Pageable pageable, String username, boolean withFriends) {
    final String userId = findUserIdByUsername(username);
    FeedResponse response = grpcPhotosClient.getFeed(pageable, userId, withFriends);
    return feedMapper.toFeedGql(response, username, withFriends);
  }

  @Nonnull
  private String findUserIdByUsername(String username) {
    if (username == null) {
      throw new IllegalArgumentException("Username can't be null value.");
    }
    final String userId = grpcUserdataClient.findUser(username, null).getId();
    LOG.info("### User with ID: {} was found in Userdata service ###", userId);
    return userId;
  }
}
