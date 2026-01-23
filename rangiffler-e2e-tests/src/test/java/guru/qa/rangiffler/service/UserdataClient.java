package guru.qa.rangiffler.service;

import guru.qa.rangiffler.model.UdUserJson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface UserdataClient {

  @Nonnull
  UdUserJson currentUser(String username);

  @Nonnull
  List<UdUserJson> allUsersExceptCurrent(String username, @Nullable String searchQuery);

  @Nonnull
  UdUserJson sendInvitation(String username, String targetUsername);

  @Nonnull
  UdUserJson acceptInvitation(String username, String targetUsername);

  @Nonnull
  List<UdUserJson> findIncomeInvitations(String username);

  @Nonnull
  List<UdUserJson> findOutcomeInvitations(String username);

  @Nonnull
  List<UdUserJson> findAllFriends(String username, @Nullable String searchQuery);
}
