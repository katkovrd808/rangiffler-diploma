package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.FriendStatus;
import guru.qa.rangiffler.data.FriendshipEntity;
import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.data.projection.FriendWithStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface FriendshipRepository extends JpaRepository<FriendshipEntity, UUID> {
  @Query(
    """
        SELECT
              u.id as id,
              u.username as username,
              u.firstname as firstname,
              u.surname as surname,
              u.photo as photo,
              u.countryId as countryId,
              f.status as status
          FROM FriendshipEntity f
          JOIN UserEntity u ON (
              (f.requester = :user AND u = f.addressee) OR
              (f.addressee = :user AND u = f.requester)
          )
          WHERE f.status = :status
      """
  )
  @Nonnull
  Page<FriendWithStatus> findFriendsWithStatus(Pageable pageable,
                                               @Param("user") UserEntity user,
                                               @Param("status") FriendStatus status);
}
