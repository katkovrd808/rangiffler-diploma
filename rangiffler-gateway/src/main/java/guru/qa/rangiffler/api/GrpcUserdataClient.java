package guru.qa.rangiffler.api;

import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.model.graphql.userdata.UserInputGql;
import guru.qa.rangiffler.service.FriendshipAction;
import guru.qa.rangiffler.service.mapper.FriendshipMapper;
import guru.qa.rangiffler.service.mapper.UserMapper;
import guru.qa.rangiffler.service.utils.GrpcExceptionHandler;
import io.grpc.StatusRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@Component
@ParametersAreNonnullByDefault
public class GrpcUserdataClient {
  private final RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub rangifflerUserdataServiceBlockingStub;
  private final UserMapper userMapper;
  private final FriendshipMapper friendshipMapper;
  private final GrpcExceptionHandler grpcExceptionHandler;

  @Autowired
  public GrpcUserdataClient(RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub rangifflerUserdataServiceBlockingStub,
                            UserMapper userMapper,
                            FriendshipMapper friendshipMapper) {
    this.rangifflerUserdataServiceBlockingStub = rangifflerUserdataServiceBlockingStub;
    this.userMapper = userMapper;
    this.friendshipMapper = friendshipMapper;
    this.grpcExceptionHandler = new GrpcExceptionHandler(
      GrpcUserdataClient.class,
      "gRPC Userdata service"
    );
  }

  @Nonnull
  public UserResponse findUser(@Nullable String username, @Nullable UUID userId) {
    try {
      UserRequest request = userMapper.toProtoRequest(username, userId);
      return rangifflerUserdataServiceBlockingStub.getUser(request);
    } catch (StatusRuntimeException e) {
      throw grpcExceptionHandler.handleGraphQLError(e);
    }
  }

  @Nonnull
  public UsersPaginatedResponse findAllExceptCurrent(Pageable pageable, String username) {
    try {
      UsersPaginatedRequest request = userMapper.toProtoUsersRequest(pageable, username);
      return rangifflerUserdataServiceBlockingStub.allUsers(request);
    } catch (StatusRuntimeException e) {
      throw grpcExceptionHandler.handleGraphQLError(e);
    }
  }

  @Nonnull
  public UserResponse findUserWithFriendStatus(String targetUserId, String currentUserId) {
    try {
      UserWithStatusRequest request = userMapper.toProtoRequest(targetUserId, currentUserId);
      return rangifflerUserdataServiceBlockingStub.getUserWithFriendStatus(request);
    } catch (StatusRuntimeException e) {
      throw grpcExceptionHandler.handleGraphQLError(e);
    }
  }

  @Nonnull
  public UserUpdateResponse updateUser(String username, UserInputGql user, String countryId) {
    try {
      UserUpdateRequest request = userMapper.toProtoUpdateUserRequest(username, user, countryId);
      return rangifflerUserdataServiceBlockingStub.updateUser(request);
    } catch (StatusRuntimeException e) {
      throw grpcExceptionHandler.handleGraphQLError(e);
    }
  }

  @Nonnull
  public AllFriendsPaginatedResponse findFriends(Pageable pageable, String username, @Nullable String searchQuery) {
    try {
      AllFriendsPaginatedRequest request = friendshipMapper.toProtoFriendsRequest(pageable, username, searchQuery);
      return rangifflerUserdataServiceBlockingStub.allFriendsPaginated(request);
    } catch (StatusRuntimeException e) {
      throw grpcExceptionHandler.handleGraphQLError(e);
    }
  }

  @Nonnull
  public InvitationsPaginatedResponse getIncomeInvitations(Pageable pageable, String username, @Nullable String searchQuery) {
    try {
      InvitationsPaginatedRequest request = friendshipMapper.toProtoInvitationsRequest(pageable, username, searchQuery);
      return rangifflerUserdataServiceBlockingStub.getIncomeInvitations(request);
    } catch (StatusRuntimeException e) {
      throw grpcExceptionHandler.handleGraphQLError(e);
    }
  }

  @Nonnull
  public InvitationsPaginatedResponse getOutcomeInvitations(Pageable pageable, String username, @Nullable String searchQuery) {
    try {
      InvitationsPaginatedRequest request = friendshipMapper.toProtoInvitationsRequest(pageable, username, searchQuery);
      return rangifflerUserdataServiceBlockingStub.getOutcomeInvitations(request);
    } catch (StatusRuntimeException e) {
      throw grpcExceptionHandler.handleGraphQLError(e);
    }
  }

  @Nonnull
  public FriendshipResponse friendship(String username, String targetUsername, FriendshipAction friendshipAction) {
    try {
      FriendshipRequest request = friendshipMapper.toFriendshipRequest(username, targetUsername);
      return switch (friendshipAction) {
        case ADD -> rangifflerUserdataServiceBlockingStub.createFriendshipRequest(request);
        case ACCEPT -> rangifflerUserdataServiceBlockingStub.acceptFriendshipRequest(request);
        case REJECT -> rangifflerUserdataServiceBlockingStub.declineFriendshipRequest(request);
        case DELETE -> rangifflerUserdataServiceBlockingStub.deleteFriend(request);
      };
    } catch (StatusRuntimeException e) {
      throw grpcExceptionHandler.handleGraphQLError(e);
    }
  }
}
