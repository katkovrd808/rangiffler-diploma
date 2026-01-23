package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.entity.userdata.UdUserEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UserdataUserRepository {
  @Nonnull
  UdUserEntity create(UdUserEntity user);

  @Nonnull
  Optional<UdUserEntity> findById(UUID id);

  @Nonnull
  List<UdUserEntity> findAll();

  @Nonnull
  Optional<UdUserEntity> findByUsername(String username);

  @Nonnull
  UdUserEntity update(UdUserEntity user);

  @Nonnull
  List<UdUserEntity> findFriends(String username, @Nullable String searchQuery);

  @Nonnull
  List<UdUserEntity> findIncomeInvitations(String username);

  @Nonnull
  List<UdUserEntity> findOutcomeInvitations(String username);

  void sendInvitation(UdUserEntity requester, UdUserEntity addressee);

  void addFriend(UdUserEntity requester, UdUserEntity addressee);

  void delete(UdUserEntity user);
}
