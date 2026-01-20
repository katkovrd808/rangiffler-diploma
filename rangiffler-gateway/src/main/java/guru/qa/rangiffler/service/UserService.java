package guru.qa.rangiffler.service;

import guru.qa.rangiffler.model.graphql.userdata.UserGql;
import guru.qa.rangiffler.model.graphql.userdata.UserInputGql;
import guru.qa.rangiffler.model.graphql.userdata.UsersSliceGql;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UserService {
  @Nonnull
  UserGql findUser(@Nullable String username, @Nullable UUID userId);

  @Nonnull
  UsersSliceGql allUsers(Pageable pageable, String username);

  @Nonnull
  UserGql updateUser(String username, UserInputGql user);

  @Nonnull
  UsersSliceGql friends(Pageable pageable, String username, @Nullable String searchQuery);

  @Nonnull
  UsersSliceGql incomeInvitations(Pageable pageable, String username, @Nullable String searchQuery);

  @Nonnull
  UsersSliceGql outcomeInvitations(Pageable pageable, String username, @Nullable String searchQuery);

  @Nonnull
  UserGql friendship(String currentUser, String targetUser, FriendshipAction friendshipAction);
}
