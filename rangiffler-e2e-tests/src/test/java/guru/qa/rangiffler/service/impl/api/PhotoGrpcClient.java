package guru.qa.rangiffler.service.impl.api;

import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.service.PhotoClient;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.qameta.allure.Step;
import io.qameta.allure.grpc.AllureGrpc;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PhotoGrpcClient implements PhotoClient {
  private static final Config CFG = Config.getInstance();

  private static final Channel channel = ManagedChannelBuilder
    .forAddress(CFG.photosGrpcAddress(), CFG.photosGrpcPort())
    .usePlaintext()
    .maxInboundMessageSize(1024 * 1024)
    .intercept(new AllureGrpc())
    .build();

  private static final RangifflerPhotosServiceGrpc.RangifflerPhotosServiceBlockingStub blockingStub
    = RangifflerPhotosServiceGrpc.newBlockingStub(channel);

  @Override
  @Nonnull
  @Step("Creating user photo")
  public PhotoResponse create(PhotoRequest request) {
    return blockingStub.addPhoto(request);
  }

  @Override
  @Nonnull
  @Step("Updating user photo")
  public PhotoResponse update(PhotoUpdateRequest request) {
    return blockingStub.updatePhoto(request);
  }

  @Override
  @Nonnull
  @Step("Deleting user photo")
  public PhotoDeleteResponse delete(PhotoDeleteRequest request) {
    return blockingStub.deletePhoto(request);
  }

  @NotNull
  @Override
  public FeedResponse feed(FeedRequest request) {
    return blockingStub.getUserFeed(request);
  }
}
