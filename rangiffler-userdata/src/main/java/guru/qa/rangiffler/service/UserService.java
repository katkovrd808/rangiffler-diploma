package guru.qa.rangiffler.service;

import guru.qa.rangiffler.grpc.*;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface UserService {
  @Nonnull UserResponse getCurrentUser(String username);

  @Nonnull UserResponse findById(String id);

  @Nonnull UsersPaginatedResponse getAllUsers(Pageable pageable, String username);

  @Nonnull UserUpdateResponse updateUser(UserUpdateRequest user);

  @Nonnull AllFriendsPaginatedResponse allFriends(Pageable pageable, String username, @Nullable String searchQuery);

  @Nonnull InvitationsPaginatedResponse incomeInvitations(Pageable pageable, String username, @Nullable String searchQuery);

  @Nonnull InvitationsPaginatedResponse outcomeInvitations(Pageable pageable, String username, @Nullable String searchQuery);

  @Nonnull FriendshipResponse sendFriendshipRequest(String username, String targetUsername);

  @Nonnull FriendshipResponse acceptFriendship(String username, String targetUsername);

  @Nonnull FriendshipResponse declineFriendship(String username, String targetUsername);

  @Nonnull FriendshipResponse deleteFriend(String username, String targetUsername);
}
