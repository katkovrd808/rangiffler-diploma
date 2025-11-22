package guru.qa.rangiffler.service;

import guru.qa.rangiffler.domain.UserGql;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface UserService {
  @Nonnull
  UserGql findByUsername(String username);

  @Nonnull
  Slice<UserGql> allUsers(Pageable pageable);

  @Nonnull
  UserGql updateUser(UserGql user);

  @Nonnull
  Slice<UserGql> friends(String username, Pageable pageable);

  @Nonnull
  Slice<UserGql> incomeInvitations(String username, Pageable pageable);

  @Nonnull
  Slice<UserGql> outcomeInvitations(String username, Pageable pageable);
}
