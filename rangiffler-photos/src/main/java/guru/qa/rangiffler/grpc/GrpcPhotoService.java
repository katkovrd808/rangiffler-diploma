package guru.qa.rangiffler.grpc;

import com.google.protobuf.Empty;
import guru.qa.rangiffler.service.PhotoLikeService;
import guru.qa.rangiffler.service.PhotoService;
import guru.qa.rangiffler.service.impl.FeedServiceImpl;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GrpcPhotoService extends RangifflerPhotosServiceGrpc.RangifflerPhotosServiceImplBase {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcPhotoService.class);

  private final PhotoService photoService;
  private final PhotoLikeService photoLikeService;
  private final FeedServiceImpl feedService;

  @Autowired
  public GrpcPhotoService(PhotoService photoService,
                          PhotoLikeService photoLikeService,
                          FeedServiceImpl feedService) {
    this.photoService = photoService;
    this.photoLikeService = photoLikeService;
    this.feedService = feedService;
  }

  @Override
  public void getPhotoWithLikes(PhotoWithLikesRequest request, StreamObserver<PhotoResponse> responseObserver) {
    PhotoResponse response = photoService.getPhotoWithLikes(request);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void addPhoto(PhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
    PhotoResponse response = photoService.createPhoto(request);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void updatePhoto(PhotoUpdateRequest request, StreamObserver<PhotoResponse> responseObserver) {
    PhotoResponse response = photoService.updatePhoto(request);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void deletePhoto(PhotoDeleteRequest request, StreamObserver<PhotoDeleteResponse> responseObserver) {
    PhotoDeleteResponse response = photoService.deletePhoto(request);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void addPhotoLike(PhotoLikeRequest request, StreamObserver<PhotoResponse> responseObserver) {
    PhotoResponse response = photoLikeService.likePhoto(request);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void deletePhotoLike(PhotoLikeRequest request, StreamObserver<PhotoResponse> responseObserver) {
    PhotoResponse response = photoLikeService.deleteLike(request);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getUserFeed(FeedRequest request, StreamObserver<FeedResponse> responseObserver) {
    FeedResponse response = feedService.getFeed(request, createPageable(request.getPaginationRequest()));
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Nonnull
  private Pageable createPageable(PaginationRequest paginationRequest) {
    final int DEFAULT_PAGE = 0;
    final int DEFAULT_SIZE = 10;
    final int MAX_PAGE_SIZE = 100;

    if (paginationRequest == null) {
      LOG.info("### Using default pagination: page = {}, size = {}", DEFAULT_PAGE, DEFAULT_SIZE);
      return PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE);
    }

    int originalPage = paginationRequest.getPage();
    int originalSize = paginationRequest.getSize();

    int page = Math.max(DEFAULT_PAGE, originalPage);
    int size = Math.min(MAX_PAGE_SIZE, Math.max(1, originalSize));

    if (originalPage != page || originalSize != size) {
      LOG.debug("### Pagination parameters adjusted from page={}, size={} to page={}, size={} ###",
        originalPage, originalSize, page, size);
    }

    return PageRequest.of(page, size);
  }
}
