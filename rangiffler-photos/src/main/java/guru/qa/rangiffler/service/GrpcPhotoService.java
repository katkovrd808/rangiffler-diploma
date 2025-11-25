package guru.qa.rangiffler.service;

import com.google.protobuf.Empty;
import guru.qa.rangiffler.grpc.*;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrpcPhotoService extends RangifflerPhotosServiceGrpc.RangifflerPhotosServiceImplBase {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcPhotoService.class);

  private final PhotoService photoService;
  private final PhotoLikeService photoLikeService;

  @Autowired
  public GrpcPhotoService(PhotoService photoService, PhotoLikeService photoLikeService) {
    this.photoService = photoService;
    this.photoLikeService = photoLikeService;
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
  public void deletePhoto(PhotoDeleteRequest request, StreamObserver<Empty> responseObserver) {
    Empty response = photoService.deletePhoto(request);
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
  public void deletePhotoLike(PhotoLikeRequest request, StreamObserver<Empty> responseObserver) {
    Empty response = photoLikeService.deleteLike(request);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
