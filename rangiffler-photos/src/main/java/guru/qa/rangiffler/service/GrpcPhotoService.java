package guru.qa.rangiffler.service;

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

  @Autowired
  public GrpcPhotoService(PhotoService photoService) {
    this.photoService = photoService;
  }

  @Override
  public void addPhoto(PhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
    PhotoResponse response = photoService.createPhoto(request);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getFeed(FeedRequest request, StreamObserver<FeedResponse> responseObserver) {
    super.getFeed(request, responseObserver);
  }
}
