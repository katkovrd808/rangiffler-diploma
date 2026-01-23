package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.entity.auth.AuthUserEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface AuthUserRepository {

  @Nonnull
  AuthUserEntity create(AuthUserEntity user);

  @Nonnull
  Optional<AuthUserEntity> findById(UUID id);

  @Nonnull
  Optional<AuthUserEntity> findByUsername(String username);

  @Nonnull
  List<AuthUserEntity> findAll();

  void delete(AuthUserEntity user);
}
