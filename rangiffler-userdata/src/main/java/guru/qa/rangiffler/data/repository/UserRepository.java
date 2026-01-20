package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.data.projection.UserWithStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  @Nonnull
  @Query("SELECT u FROM UserEntity u WHERE u.username = :username")
  Optional<UserEntity> findByUsername(@Param("username") String username);

  @Nonnull
  @Query(value = """
        SELECT DISTINCT NEW guru.qa.rangiffler.data.projection.UserWithStatus(
            u.id, u.username, u.firstname, u.surname, u.photo, u.countryId,
            CASE
                WHEN u.id = :currentUserId THEN guru.qa.rangiffler.data.FriendshipStatus.ACCEPTED
                WHEN f IS NULL THEN guru.qa.rangiffler.data.FriendshipStatus.DELETED
                ELSE f.status
            END,
            CASE
                WHEN u.id = :currentUserId THEN false
                WHEN f.requester.id = :currentUserId THEN true
                ELSE false
            END)
        FROM UserEntity u
        LEFT JOIN FriendshipEntity f ON (
            (f.requester.id = :currentUserId AND f.addressee.id = u.id) OR
            (f.addressee.id = :currentUserId AND f.requester.id = u.id)
        )
        WHERE u.id = :targetUserId
    """)
  Optional<UserWithStatus> findByIdWithFriendStatus(@Param("targetUserId") UUID targetUserId,
                                                    @Param("currentUserId") UUID currentUserId);

  @Nonnull
  @Query(value = """
        SELECT DISTINCT NEW guru.qa.rangiffler.data.projection.UserWithStatus(
            u.id, u.username, u.firstname, u.surname, u.photo, u.countryId,
            CASE 
                WHEN f IS NULL THEN guru.qa.rangiffler.data.FriendshipStatus.DELETED
                ELSE f.status 
            END,
            CASE 
                WHEN f.requester.id = :currentUserId THEN true 
                ELSE false 
            END)
        FROM UserEntity u
        LEFT JOIN FriendshipEntity f ON (
            (f.requester.id = :currentUserId AND f.addressee.id = u.id) OR
            (f.addressee.id = :currentUserId AND f.requester.id = u.id)
        )
        WHERE u.id != :currentUserId
        ORDER BY u.username
    """, countQuery = """
        SELECT COUNT(DISTINCT u)
        FROM UserEntity u
        WHERE u.id != :currentUserId
    """)
  Page<UserWithStatus> findByIdNot(@Param("currentUserId") UUID currentUserId, Pageable pageable);

  @Nonnull
  @Query(value = """
        SELECT DISTINCT NEW guru.qa.rangiffler.data.projection.UserWithStatus(
            u.id, u.username, u.firstname, u.surname, u.photo, u.countryId,
            CASE 
                WHEN f IS NULL THEN guru.qa.rangiffler.data.FriendshipStatus.DELETED
                ELSE f.status 
            END,
            CASE 
                WHEN f.requester.id = :currentUserId THEN true 
                ELSE false 
            END)
        FROM UserEntity u
        LEFT JOIN FriendshipEntity f ON (
            (f.requester.id = :currentUserId AND f.addressee.id = u.id) OR
            (f.addressee.id = :currentUserId AND f.requester.id = u.id)
        )
        WHERE u.id != :currentUserId
        AND (
            :searchQuery IS NULL 
            OR :searchQuery = ''
            OR LOWER(u.username) LIKE LOWER(CONCAT('%', :searchQuery, '%'))
            OR LOWER(u.firstname) LIKE LOWER(CONCAT('%', :searchQuery, '%'))
            OR LOWER(u.surname) LIKE LOWER(CONCAT('%', :searchQuery, '%'))
        )
        ORDER BY u.username
    """, countQuery = """
        SELECT COUNT(DISTINCT u)
        FROM UserEntity u
        LEFT JOIN FriendshipEntity f ON (
            (f.requester.id = :currentUserId AND f.addressee.id = u.id) OR
            (f.addressee.id = :currentUserId AND f.requester.id = u.id)
        )
        WHERE u.id != :currentUserId
        AND (
            :searchQuery IS NULL 
            OR :searchQuery = ''
            OR LOWER(u.username) LIKE LOWER(CONCAT('%', :searchQuery, '%'))
            OR LOWER(u.firstname) LIKE LOWER(CONCAT('%', :searchQuery, '%'))
            OR LOWER(u.surname) LIKE LOWER(CONCAT('%', :searchQuery, '%'))
        )
    """)
  Page<UserWithStatus> findByIdNot(@Param("currentUserId") UUID currentUserId,
                                   @Param("searchQuery") @Nullable String searchQuery,
                                   Pageable pageable);
}
