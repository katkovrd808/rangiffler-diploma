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
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface FriendshipRepository extends JpaRepository<FriendshipEntity, UUID> {
  @Query(
    value = """
        SELECT DISTINCT NEW guru.qa.rangiffler.data.projection.UserWithStatus(
            u.id, u.username, u.firstname, u.surname, u.photo, u.countryId, f.status,
                  CASE WHEN f.requester = :user THEN true ELSE false END)
        FROM UserEntity u
        JOIN FriendshipEntity f
        ON (u = f.requester AND f.addressee = :user)
           OR (u = f.addressee AND f.requester = :user)
        WHERE f.status = guru.qa.rangiffler.data.FriendshipStatus.ACCEPTED
        ORDER BY u.username ASC
      """
  )
  @Nonnull
  List<UserWithStatus> findFriends(@Param("user") UserEntity user);

  @Query(
    value = """
        SELECT DISTINCT NEW guru.qa.rangiffler.data.projection.UserWithStatus(
            u.id, u.username, u.firstname, u.surname, u.photo, u.countryId, f.status,
                  CASE WHEN f.requester = :user THEN true ELSE false END)
        FROM UserEntity u
        JOIN FriendshipEntity f
        ON (u = f.requester AND f.addressee = :user)
           OR (u = f.addressee AND f.requester = :user)
        WHERE f.status = guru.qa.rangiffler.data.FriendshipStatus.ACCEPTED
        ORDER BY u.username ASC
      """,
    countQuery = """
        SELECT COUNT(DISTINCT u)
        FROM UserEntity u
        JOIN FriendshipEntity f
        ON (u = f.requester AND f.addressee = :user)
           OR (u = f.addressee AND f.requester = :user)
        WHERE f.status = guru.qa.rangiffler.data.FriendshipStatus.ACCEPTED
      """
  )
  @Nonnull
  Page<UserWithStatus> findFriends(@Param("user") UserEntity user,
                                   Pageable pageable);

  @Query(
    value = """
        SELECT DISTINCT NEW guru.qa.rangiffler.data.projection.UserWithStatus(
            u.id, u.username, u.firstname, u.surname, u.photo, u.countryId, f.status,
                  CASE WHEN f.requester = :user THEN true ELSE false END)
        FROM UserEntity u
        JOIN FriendshipEntity f
        ON (u = f.requester AND f.addressee = :user)
           OR (u = f.addressee AND f.requester = :user)
        WHERE f.status = guru.qa.rangiffler.data.FriendshipStatus.ACCEPTED
        AND (lower(u.username) LIKE lower(concat('%', :searchQuery, '%'))
              OR lower(u.firstname) LIKE lower(concat('%', :searchQuery, '%'))
              OR lower(u.surname) LIKE lower(concat('%', :searchQuery, '%')))
        ORDER BY u.username ASC
      """,
    countQuery = """
        SELECT COUNT(DISTINCT u)
        FROM UserEntity u
        JOIN FriendshipEntity f
        ON (u = f.requester AND f.addressee = :user)
           OR (u = f.addressee AND f.requester = :user)
        WHERE f.status = guru.qa.rangiffler.data.FriendshipStatus.ACCEPTED
        AND (lower(u.username) LIKE lower(concat('%', :searchQuery, '%'))
              OR lower(u.firstname) LIKE lower(concat('%', :searchQuery, '%'))
              OR lower(u.surname) LIKE lower(concat('%', :searchQuery, '%')))
      """
  )
  @Nonnull
  Page<UserWithStatus> findFriends(@Param("user") UserEntity user,
                                   @Param("searchQuery") String searchQuery,
                                   Pageable pageable);

  @Query(
    value = """
          SELECT DISTINCT NEW guru.qa.rangiffler.data.projection.UserWithStatus(
              u.id, u.username, u.firstname, u.surname, u.photo, u.countryId, f.status,
                  CASE WHEN f.requester = :user THEN true ELSE false END)
          FROM UserEntity u
          JOIN FriendshipEntity f ON u = f.requester
          WHERE f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
              AND f.addressee = :user
          ORDER BY u.username
      """,
    countQuery = """
          SELECT COUNT(DISTINCT u)
          FROM UserEntity u
          JOIN FriendshipEntity f ON u = f.requester
          WHERE f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
              AND f.addressee = :user
      """
  )
  @Nonnull
  Page<UserWithStatus> findIncomeInvitations(@Param("user") UserEntity user,
                                             Pageable pageable);

  @Query(
    value = """
          SELECT DISTINCT NEW guru.qa.rangiffler.data.projection.UserWithStatus(
              u.id, u.username, u.firstname, u.surname, u.photo, u.countryId, f.status,
                  CASE WHEN f.requester = :user THEN true ELSE false END)
          FROM UserEntity u
          JOIN FriendshipEntity f ON u = f.requester
          WHERE f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
              AND f.addressee = :user
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
              AND f.addressee = :user
              AND (lower(u.username) LIKE lower(concat('%', :searchQuery, '%'))
                  OR lower(u.firstname) LIKE lower(concat('%', :searchQuery, '%'))
                  OR lower(u.surname) LIKE lower(concat('%', :searchQuery, '%')))
      """
  )
  @Nonnull
  Page<UserWithStatus> findIncomeInvitations(@Param("user") UserEntity user,
                                             @Param("searchQuery") String searchQuery,
                                             @Nonnull Pageable pageable);

  @Query(
    value = """
          SELECT DISTINCT NEW guru.qa.rangiffler.data.projection.UserWithStatus(
              u.id, u.username, u.firstname, u.surname, u.photo, u.countryId, f.status,
                  CASE WHEN f.requester = :user THEN true ELSE false END)
          FROM UserEntity u
          JOIN FriendshipEntity f ON u = f.addressee
          WHERE f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
              AND f.requester = :user
          ORDER BY u.username
      """,
    countQuery = """
          SELECT COUNT(DISTINCT u)
          FROM UserEntity u
          JOIN FriendshipEntity f ON u = f.addressee
          WHERE f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
              AND f.requester = :user
      """
  )
  @Nonnull
  Page<UserWithStatus> findOutcomeInvitations(@Param("user") UserEntity user,
                                              @Nonnull Pageable pageable);

  @Query(
    value = """
          SELECT DISTINCT NEW guru.qa.rangiffler.data.projection.UserWithStatus(
              u.id, u.username, u.firstname, u.surname, u.photo, u.countryId, f.status,
                  CASE WHEN f.requester = :user THEN true ELSE false END)
          FROM UserEntity u
          JOIN FriendshipEntity f ON u = f.addressee
          WHERE f.status = guru.qa.rangiffler.data.FriendshipStatus.PENDING
              AND f.requester = :user
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
              AND f.requester = :user
              AND (lower(u.username) LIKE lower(concat('%', :searchQuery, '%'))
                  OR lower(u.firstname) LIKE lower(concat('%', :searchQuery, '%'))
                  OR lower(u.surname) LIKE lower(concat('%', :searchQuery, '%')))
      """
  )
  @Nonnull
  Page<UserWithStatus> findOutcomeInvitations(@Param("user") UserEntity user,
                                              @Param("searchQuery") String searchQuery,
                                              @Nonnull Pageable pageable);
}
