package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.FriendshipEntity;
import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.data.projection.UserWithStatus;
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
    value = """
        SELECT DISTINCT NEW guru.qa.rangiffler.data.projection.UserWithStatus(
                                        u.id, u.username, u.firstname, u.surname, u.photo, u.countryId, f.status)
        FROM UserEntity u
        JOIN FriendshipEntity f
        ON u = f.requester
        WHERE (f.status IS NOT NULL)
        AND f.addressee = :addressee
        ORDER BY f.status DESC
      """,
    countQuery = """
        SELECT COUNT(DISTINCT u)
        FROM UserEntity u
        JOIN FriendshipEntity f ON u = f.requester
        WHERE (f.status IS NOT NULL)
        AND f.addressee = :addressee
      """
  )
  @Nonnull
  Page<UserWithStatus> findFriends(@Param("addressee") UserEntity addressee,
                                   Pageable pageable);

  @Query(
    value = """
        SELECT DISTINCT NEW guru.qa.rangiffler.data.projection.UserWithStatus(
                                        u.id, u.username, u.firstname, u.surname, u.photo, u.countryId, f.status)
        FROM UserEntity u
        JOIN FriendshipEntity f
        ON u = f.requester
        WHERE (f.status IS NOT NULL)
        AND f.addressee = :addressee
        AND (lower(u.username) like lower(concat('%', :searchQuery, '%'))
              or lower(u.firstname) like lower(concat('%', :searchQuery, '%'))
              or lower(u.surname) like lower(concat('%', :searchQuery, '%')))
        ORDER BY f.status DESC
      """,
    countQuery = """
        SELECT COUNT(DISTINCT u)
        FROM UserEntity u
        JOIN FriendshipEntity f ON u = f.requester
        WHERE (f.status IS NOT NULL)
        AND f.addressee = :addressee
        AND (lower(u.username) like lower(concat('%', :searchQuery, '%'))
              or lower(u.firstname) like lower(concat('%', :searchQuery, '%'))
              or lower(u.surname) like lower(concat('%', :searchQuery, '%')))
      """
  )
  @Nonnull
  Page<UserWithStatus> findFriends(@Param("addressee") UserEntity addressee,
                                   @Param("searchQuery") String searchQuery,
                                   Pageable pageable);

  @Query(
    value = """
        SELECT DISTINCT NEW guru.qa.rangiffler.data.projection.UserWithStatus(
            u.id, u.username, u.firstname, u.surname, u.photo, u.countryId, f.status)
        FROM UserEntity u
        JOIN FriendshipEntity f ON u = f.requester
        WHERE f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
            AND f.addressee = :currentUser
        ORDER BY u.username
    """,
    countQuery = """
        SELECT COUNT(DISTINCT u)
        FROM UserEntity u
        JOIN FriendshipEntity f ON u = f.requester
        WHERE f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
            AND f.addressee = :currentUser
    """
  )
  @Nonnull
  Page<UserWithStatus> findIncomeInvitations(@Param("addressee") UserEntity addressee,
                                             Pageable pageable);

  @Query(
    value = """
        SELECT DISTINCT NEW guru.qa.rangiffler.data.projection.UserWithStatus(
            u.id, u.username, u.firstname, u.surname, u.photo, u.countryId, f.status)
        FROM UserEntity u
        JOIN FriendshipEntity f ON u = f.requester
        WHERE f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
            AND f.addressee = :currentUser
            AND (lower(u.username) LIKE lower(concat('%', :searchQuery, '%'))
                OR lower(u.firstname) LIKE lower(concat('%', :searchQuery, '%'))
                OR lower(u.surname) LIKE lower(concat('%', :searchQuery, '%')))
        ORDER BY u.username
    """,
    countQuery = """
        SELECT COUNT(DISTINCT u)
        FROM UserEntity u
        JOIN FriendshipEntity f ON u = f.requester
        WHERE f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
            AND f.addressee = :currentUser
            AND (lower(u.username) LIKE lower(concat('%', :searchQuery, '%'))
                OR lower(u.firstname) LIKE lower(concat('%', :searchQuery, '%'))
                OR lower(u.surname) LIKE lower(concat('%', :searchQuery, '%')))
    """
  )
  @Nonnull
  Page<UserWithStatus> findIncomeInvitations(@Param("currentUser") UserEntity currentUser,
                                             @Param("searchQuery") String searchQuery,
                                             @Nonnull Pageable pageable);

  @Query(
    value = """
        SELECT DISTINCT NEW guru.qa.rangiffler.data.projection.UserWithStatus(
            u.id, u.username, u.firstname, u.surname, u.photo, u.countryId, f.status)
        FROM UserEntity u
        JOIN FriendshipEntity f ON u = f.addressee
        WHERE f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
            AND f.requester = :currentUser
        ORDER BY u.username
    """,
    countQuery = """
        SELECT COUNT(DISTINCT u)
        FROM UserEntity u
        JOIN FriendshipEntity f ON u = f.addressee
        WHERE f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
            AND f.requester = :currentUser
    """
  )
  @Nonnull
  Page<UserWithStatus> findOutcomeInvitations(@Param("currentUser") UserEntity currentUser,
                                              @Nonnull Pageable pageable);

  @Query(
    value = """
        SELECT DISTINCT NEW guru.qa.rangiffler.data.projection.UserWithStatus(
            u.id, u.username, u.firstname, u.surname, u.photo, u.countryId, f.status)
        FROM UserEntity u
        JOIN FriendshipEntity f ON u = f.addressee
        WHERE f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
            AND f.requester = :currentUser
            AND (lower(u.username) LIKE lower(concat('%', :searchQuery, '%'))
                OR lower(u.firstname) LIKE lower(concat('%', :searchQuery, '%'))
                OR lower(u.surname) LIKE lower(concat('%', :searchQuery, '%')))
        ORDER BY u.username
    """,
    countQuery = """
        SELECT COUNT(DISTINCT u)
        FROM UserEntity u
        JOIN FriendshipEntity f ON u = f.addressee
        WHERE f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
            AND f.requester = :currentUser
            AND (lower(u.username) LIKE lower(concat('%', :searchQuery, '%'))
                OR lower(u.firstname) LIKE lower(concat('%', :searchQuery, '%'))
                OR lower(u.surname) LIKE lower(concat('%', :searchQuery, '%')))
    """
  )
  @Nonnull
  Page<UserWithStatus> findOutcomeInvitations(@Param("currentUser") UserEntity currentUser,
                                              @Param("searchQuery") String searchQuery,
                                              @Nonnull Pageable pageable);
}
