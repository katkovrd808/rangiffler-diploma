package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  @Nonnull
  @Query("SELECT u FROM UserEntity u WHERE u.username = :username")
  Optional<UserEntity> findByUsername(@Param("username") String username);

  @Nonnull
  @Query("SELECT u FROM UserEntity u WHERE u.username <> :username")
  List<UserEntity> findByUsernameNot(@Param("username") String username);
}
