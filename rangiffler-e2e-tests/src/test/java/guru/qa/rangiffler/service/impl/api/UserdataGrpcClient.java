package guru.qa.rangiffler.service.impl.api;

import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.model.UdUserJson;
import guru.qa.rangiffler.service.UserdataClient;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.qameta.allure.grpc.AllureGrpc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class UserdataGrpcClient implements UserdataClient {
  private static final Config CFG = Config.getInstance();

  private static final Channel channel = ManagedChannelBuilder
    .forAddress(CFG.userdataGrpcAddress(), CFG.userdataGrpcPort())
    .usePlaintext()
    .maxInboundMessageSize(1024 * 1024)
    .intercept(new AllureGrpc())
    .build();

  private static final RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub blockingStub
    = RangifflerUserdataServiceGrpc.newBlockingStub(channel);

  @NotNull
  @Override
  public UdUserJson currentUser(String username) {
    final UserRequest request = UserRequest.newBuilder()
      .setUsername(username)
      .build();

    return UdUserJson.fromProto(blockingStub.getUser(request));
  }

  @NotNull
  @Override
  public List<UdUserJson> findAllFriends(String username) {
    final AllFriendsRequest request = AllFriendsRequest.newBuilder()
      .setTargetUsername(username)
      .build();

    return blockingStub.allFriends(request).getFriendsList().stream()
      .map(UdUserJson::fromProto)
      .toList();
  }

  @NotNull
  @Override
  public List<UdUserJson> findIncomeInvitations(String username, @Nullable String searchQuery) {
    final InvitationsPaginatedRequest request = InvitationsPaginatedRequest.newBuilder()
      .setTargetUsername(username)
      .setSearchQuery(searchQuery != null ? searchQuery : "")
      .setPaginationRequest(PaginationRequest.newBuilder()
        .setPage(0)
        .setSize(50)
        .build()
      )
      .build();

    return blockingStub.getIncomeInvitations(request).getInvitationsList().stream()
      .map(UdUserJson::fromProto)
      .toList();
  }

  @NotNull
  @Override
  public List<UdUserJson> findOutcomeInvitations(String username, @Nullable String searchQuery) {
    final InvitationsPaginatedRequest request = InvitationsPaginatedRequest.newBuilder()
      .setTargetUsername(username)
      .setPaginationRequest(PaginationRequest.newBuilder()
        .setPage(0)
        .setSize(50)
        .build()
      )
      .build();

    return blockingStub.getOutcomeInvitations(request).getInvitationsList().stream()
      .map(UdUserJson::fromProto)
      .toList();
  }
}
