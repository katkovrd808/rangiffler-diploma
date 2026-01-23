package guru.qa.rangiffler.service.impl.api;

import guru.qa.rangiffler.model.UdUserJson;
import guru.qa.rangiffler.service.UserdataClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class UserdataApiClient implements UserdataClient {
  @NotNull
  @Override
  public UdUserJson currentUser(String username) {
    return null;
  }

  @NotNull
  @Override
  public List<UdUserJson> allUsersExceptCurrent(String username, @Nullable String searchQuery) {
    return List.of();
  }

  @NotNull
  @Override
  public UdUserJson sendInvitation(String username, String targetUsername) {
    return null;
  }

  @NotNull
  @Override
  public UdUserJson acceptInvitation(String username, String targetUsername) {
    return null;
  }

  @NotNull
  @Override
  public List<UdUserJson> findIncomeInvitations(String username) {
    return List.of();
  }

  @NotNull
  @Override
  public List<UdUserJson> findOutcomeInvitations(String username) {
    return List.of();
  }

  @NotNull
  @Override
  public List<UdUserJson> findAllFriends(String username, @Nullable String searchQuery) {
    return List.of();
  }
}
