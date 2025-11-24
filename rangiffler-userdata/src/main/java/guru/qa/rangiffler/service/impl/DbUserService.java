package guru.qa.rangiffler.service.impl;

import guru.qa.rangiffler.data.FriendshipEntity;
import guru.qa.rangiffler.data.FriendshipStatus;
import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.data.projection.UserWithStatus;
import guru.qa.rangiffler.data.repository.FriendshipRepository;
import guru.qa.rangiffler.data.repository.UserRepository;
import guru.qa.rangiffler.ex.FriendshipNotFoundException;
import guru.qa.rangiffler.ex.InvalidFriendshipOperationException;
import guru.qa.rangiffler.ex.SameUsernameException;
import guru.qa.rangiffler.ex.UserNotFoundException;
import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.service.GrpcUserService;
import guru.qa.rangiffler.service.UserService;
import guru.qa.rangiffler.service.api.GrpcCountriesClient;
import guru.qa.rangiffler.service.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@ParametersAreNonnullByDefault
public class DbUserService implements UserService {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcUserService.class);

  private final UserRepository userRepository;
  private final FriendshipRepository friendshipRepository;
  private final UserMapper userMapper;

  @Autowired
  public DbUserService(UserRepository userRepository,
                       FriendshipRepository friendshipRepository,
                       GrpcCountriesClient grpcCountriesClient,
                       UserMapper userMapper) {
    this.userRepository = userRepository;
    this.friendshipRepository = friendshipRepository;
    this.userMapper = userMapper;
  }

  @Override
  @Transactional(readOnly = true)
  public @Nonnull UserResponse getCurrentUser(String username) {
    return userRepository.findByUsername(username)
      .map(userMapper::toProto)
      .orElseThrow(() -> new UserNotFoundException("Can't find user with username " + username));
  }

  @Override
  @Transactional(readOnly = true)
  public @Nonnull UsersPaginatedResponse getAllUsers(Pageable pageable, String username) {
    Page<UserEntity> users = userRepository.findByUsernameNot(pageable, username);
    return userMapper.toProtoList(users);
  }

  @Override
  @Transactional
  public @Nonnull UserUpdateResponse updateUser(UserUpdateRequest user) {
    UserEntity ue = getRequiredUser(user.getUsername());
    if (!Objects.equals(ue.getUsername(), user.getUsername())) {
      throw new SecurityException("User can only update their own profile");
    }
    ue.setFirstname(user.hasFirstname() ? user.getFirstname() : "");
    ue.setSurname(user.hasSurname() ? user.getSurname() : "");
    ue.setCountryId(UUID.fromString(user.getCountryId()));
    ue.setPhoto(user.hasPhoto() ? user.getPhoto().toByteArray() : new byte[0]);

    return userMapper.toUpdateProto(userRepository.save(ue));
  }

  @Override
  @Transactional(readOnly = true)
  public @Nonnull AllFriendsPaginatedResponse allFriends(Pageable pageable,
                                                         String username,
                                                         @Nullable String searchQuery) {
    if (username.isEmpty()) {
      throw new IllegalArgumentException("User can't be empty.");
    }
    UserEntity ue = getRequiredUser(username);
    Page<UserWithStatus> users = searchQuery == null ?
      friendshipRepository.findFriends(ue, pageable) :
      friendshipRepository.findFriends(ue, searchQuery, pageable);
    return userMapper.toProtoFriendsList(users);
  }

  @Override
  @Transactional(readOnly = true)
  public @Nonnull InvitationsPaginatedResponse incomeInvitations(Pageable pageable,
                                                                 String username,
                                                                 @Nullable String searchQuery) {
    if (username.isEmpty()) {
      throw new IllegalArgumentException("User can't be empty.");
    }
    UserEntity ue = getRequiredUser(username);
    Page<UserWithStatus> users = searchQuery == null ?
      friendshipRepository.findIncomeInvitations(ue, pageable) :
      friendshipRepository.findIncomeInvitations(ue, searchQuery, pageable);
    return userMapper.toProtoInvitationsList(users);
  }

  @Override
  @Transactional(readOnly = true)
  public @Nonnull InvitationsPaginatedResponse outcomeInvitations(Pageable pageable,
                                                                  String username,
                                                                  @Nullable String searchQuery) {
    if (username.isEmpty()) {
      throw new IllegalArgumentException("User can't be empty.");
    }
    UserEntity ue = getRequiredUser(username);
    Page<UserWithStatus> users = searchQuery == null ?
      friendshipRepository.findOutcomeInvitations(ue, pageable) :
      friendshipRepository.findOutcomeInvitations(ue, searchQuery, pageable);
    return userMapper.toProtoInvitationsList(users);
  }

  @Override
  @Transactional
  public @Nonnull FriendshipResponse sendFriendshipRequest(String username, String targetUsername) {
    if (Objects.equals(username, targetUsername)) {
      throw new SameUsernameException("Can't create friendship request for self user");
    }
    UserEntity user = getRequiredUser(username);
    UserEntity target = getRequiredUser(targetUsername);
    Optional<FriendshipEntity> existingFriendship = findAnyFriendshipBetween(user, target);

    final FriendshipStatus friendshipStatusToSet;

    if (existingFriendship.isPresent()) {
      FriendshipEntity fe = existingFriendship.get();
      FriendshipStatus status = fe.getStatus();

      if (status == FriendshipStatus.PENDING) {
        if (fe.getAddressee().equals(user)) {
          fe.setStatus(FriendshipStatus.ACCEPTED);
          friendshipStatusToSet = FriendshipStatus.ACCEPTED;
          friendshipRepository.save(fe);
        } else {
          friendshipStatusToSet = FriendshipStatus.PENDING;
        }
      } else {
        friendshipStatusToSet = status;
      }
    } else {
      user.addFriends(FriendshipStatus.PENDING, target);
      friendshipStatusToSet = FriendshipStatus.PENDING;
    }

    userRepository.save(user);
    userRepository.save(target);

    UserWithStatus invited = UserWithStatus.fromEntity(target, friendshipStatusToSet);

    return userMapper.toProtoFriendship(invited);
  }

  @Override
  @Transactional
  public @Nonnull FriendshipResponse acceptFriendship(String username, String targetUsername) {
    if (Objects.equals(username, targetUsername)) {
      throw new SameUsernameException("Can't accept friendship request from self user");
    }

    UserEntity user = getRequiredUser(username);
    UserEntity target = getRequiredUser(targetUsername);
    Optional<FriendshipEntity> existingFriendship = findAnyFriendshipBetween(user, target);

    final FriendshipStatus friendshipStatusToSet;

    if (existingFriendship.isPresent()) {
      FriendshipEntity fe = existingFriendship.get();
      FriendshipStatus status = fe.getStatus();

      if (status == FriendshipStatus.PENDING) {
        if (fe.getAddressee().equals(user)) {
          fe.setStatus(FriendshipStatus.ACCEPTED);
          friendshipStatusToSet = FriendshipStatus.ACCEPTED;
          friendshipRepository.save(fe);
        } else {
          throw new InvalidFriendshipOperationException("Cannot accept your own friendship request");
        }
      } else if (status == FriendshipStatus.ACCEPTED) {
        friendshipStatusToSet = FriendshipStatus.ACCEPTED;
      } else {
        throw new InvalidFriendshipOperationException("Cannot accept friendship with status: " + status);
      }
    } else {
      throw new FriendshipNotFoundException("Friendship request not found between users: " + username + " and " + targetUsername);
    }

    userRepository.save(user);
    userRepository.save(target);

    UserWithStatus accepted = UserWithStatus.fromEntity(target, friendshipStatusToSet);

    return userMapper.toProtoFriendship(accepted);
  }

  @Override
  @Transactional
  public @Nonnull FriendshipResponse declineFriendship(String username, String targetUsername) {
    if (Objects.equals(username, targetUsername)) {
      throw new SameUsernameException("Can't decline friendship request for self user");
    }

    UserEntity user = getRequiredUser(username);
    UserEntity target = getRequiredUser(targetUsername);

    FriendshipEntity friendship = findAnyFriendshipBetween(user, target)
      .orElseThrow(() -> new FriendshipNotFoundException(
        "Friendship request not found between users: " + username + " and " + targetUsername));

    if (friendship.getStatus() != FriendshipStatus.PENDING) {
      throw new InvalidFriendshipOperationException("Can only decline pending friendship requests");
    }
    if (!friendship.getAddressee().equals(user) && !friendship.getRequester().equals(user)) {
      throw new InvalidFriendshipOperationException("User is not part of this friendship request");
    }

    boolean isRequester = friendship.getRequester().equals(user);

    LOG.info("### Attempting to delete friendship: requester={}, addressee={}, status={} ###",
      friendship.getRequester(), friendship.getAddressee(), friendship.getStatus()
    );

    user.getFriendshipRequests().remove(friendship);
    user.getFriendshipAddressees().remove(friendship);
    target.getFriendshipRequests().remove(friendship);
    target.getFriendshipAddressees().remove(friendship);

    userRepository.save(user);
    userRepository.save(target);

    friendshipRepository.delete(friendship);
    friendshipRepository.flush();

    UserWithStatus declined = UserWithStatus.fromEntityWithRole(target, FriendshipStatus.DECLINED, isRequester);
    return userMapper.toProtoFriendship(declined);
  }

  @Override
  @Transactional
  public @Nonnull FriendshipResponse deleteFriend(String username, String targetUsername) {
    if (Objects.equals(username, targetUsername)) {
      throw new SameUsernameException("Can't decline friendship request for self user");
    }

    UserEntity user = getRequiredUser(username);
    UserEntity target = getRequiredUser(targetUsername);

    FriendshipEntity friendship = findAnyFriendshipBetween(user, target)
      .orElseThrow(() -> new FriendshipNotFoundException(
        "Friendship not found between users: " + username + " and " + targetUsername));

    if (friendship.getStatus() != FriendshipStatus.ACCEPTED) {
      throw new InvalidFriendshipOperationException("Can only delete accepted friendship");
    }
    if (!friendship.getAddressee().equals(user) && !friendship.getRequester().equals(user)) {
      throw new InvalidFriendshipOperationException("User is not part of this friendship");
    }

    boolean isRequester = friendship.getRequester().equals(user);

    LOG.info("### Attempting to delete friendship: requester={}, addressee={}, status={} ###",
      friendship.getRequester(), friendship.getAddressee(), friendship.getStatus()
    );

    user.getFriendshipRequests().remove(friendship);
    user.getFriendshipAddressees().remove(friendship);
    target.getFriendshipRequests().remove(friendship);
    target.getFriendshipAddressees().remove(friendship);

    userRepository.save(user);
    userRepository.save(target);

    friendshipRepository.delete(friendship);

    friendshipRepository.flush();

    UserWithStatus declined = UserWithStatus.fromEntityWithRole(target, FriendshipStatus.DECLINED, isRequester);
    return userMapper.toProtoFriendship(declined);
  }

  //TODO validate photo
  public static boolean isPhotoString(String photo) {
    return photo != null && photo.startsWith("data:image");
  }

  private @Nonnull String cleanString(String input) {
    if (input == null) {
      return "";
    }
    return input.replaceAll("\\x00", "")
      .replaceAll("[\\x00-\\x1F\\x7F]", "")
      .trim();
  }

  @Nonnull
  UserEntity getRequiredUser(String username) {
    return userRepository.findByUsername(username).orElseThrow(
      () -> new UserNotFoundException("Can`t find user by username: '" + username + "'")
    );
  }

  private @Nonnull Optional<FriendshipEntity> findAnyFriendshipBetween(UserEntity currentUser, UserEntity targetUser) {
    Optional<FriendshipEntity> requester = currentUser.getFriendshipRequests()
      .stream()
      .filter(fe -> fe.getAddressee() != null && fe.getAddressee().equals(targetUser))
      .findFirst();

    if (requester.isPresent()) {
      return requester;
    }

    Optional<FriendshipEntity> addressee = currentUser.getFriendshipAddressees()
      .stream()
      .filter(fe -> fe.getRequester() != null && fe.getRequester().equals(targetUser))
      .findFirst();

    return addressee;
  }
}