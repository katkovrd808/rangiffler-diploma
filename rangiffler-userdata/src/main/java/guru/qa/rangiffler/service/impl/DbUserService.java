package guru.qa.rangiffler.service.impl;

import guru.qa.rangiffler.data.FriendStatus;
import guru.qa.rangiffler.data.FriendshipEntity;
import guru.qa.rangiffler.data.FriendshipStatus;
import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.data.projection.FriendWithStatus;
import guru.qa.rangiffler.data.repository.FriendshipRepository;
import guru.qa.rangiffler.data.repository.UserRepository;
import guru.qa.rangiffler.ex.NotFoundException;
import guru.qa.rangiffler.ex.SameUsernameException;
import guru.qa.rangiffler.ex.UserNotFoundException;
import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.service.UserService;
import guru.qa.rangiffler.service.api.GrpcCountriesClient;
import guru.qa.rangiffler.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@ParametersAreNonnullByDefault
public class DbUserService implements UserService {

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

  //TODO FIX
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
  public @Nonnull AllFriendsPaginatedResponse allFriends(Pageable pageable, String username) {
    UserEntity ue = getRequiredUser(username);
    return userMapper.toProtoFriendsList(
      friendshipRepository.findFriendsWithStatus(pageable, ue, FriendStatus.FRIEND)
    );
  }

  @Override
  @Transactional(readOnly = true)
  public @Nonnull InvitationsPaginatedResponse incomeInvitations(Pageable pageable, String username) {
    UserEntity ue = getRequiredUser(username);
    return userMapper.toProtoInvitationsList(
      friendshipRepository.findFriendsWithStatus(pageable, ue, FriendStatus.INVITATION_RECEIVED)
    );
  }

  @Override
  @Transactional(readOnly = true)
  public @Nonnull InvitationsPaginatedResponse outcomeInvitations(Pageable pageable, String username) {
    UserEntity ue = getRequiredUser(username);
    return userMapper.toProtoInvitationsList(
      friendshipRepository.findFriendsWithStatus(pageable, ue, FriendStatus.INVITATION_SENT)
    );
  }

  @Override
  @Transactional
  public @Nonnull FriendshipResponse sendFriendshipRequest(String username, String targetUsername) {
    if (Objects.equals(username, targetUsername)) {
      throw new SameUsernameException("Can`t create friendship request for self user");
    }
    UserEntity user = getRequiredUser(username);
    UserEntity target = getRequiredUser(targetUsername);
    final guru.qa.rangiffler.data.FriendStatus returnedStates;
    Optional<FriendshipEntity> mayBeInvite = getFriendshipRequest(user, target);
    if (mayBeInvite.isPresent()) {
      mayBeInvite.get().setStatus(FriendshipStatus.ACCEPTED);
      user.addFriends(FriendshipStatus.ACCEPTED, target);
      returnedStates = FriendStatus.FRIEND;
    } else {
      user.addFriends(FriendshipStatus.PENDING, target);
      returnedStates = FriendStatus.INVITATION_SENT;
    }
    userRepository.save(user);
    userRepository.save(target);
    return userMapper.toProtoFriendship(FriendWithStatus.fromEntity(target, returnedStates));
  }

  @Override
  @Transactional
  public @Nonnull FriendshipResponse acceptFriendship(String username, String targetUsername) {
    if (Objects.equals(username, targetUsername)) {
      throw new SameUsernameException("Can`t accept friendship request for self user");
    }
    UserEntity user = getRequiredUser(username);
    UserEntity target = getRequiredUser(targetUsername);

    FriendshipEntity invite = getFriendshipRequest(user, target)
      .orElseThrow(() -> new NotFoundException("Can`t find invitation from username: '" + targetUsername + "'"));

    invite.setStatus(FriendshipStatus.ACCEPTED);
    user.addFriends(FriendshipStatus.ACCEPTED, target);
    userRepository.save(user);
    return userMapper.toProtoFriendship(FriendWithStatus.fromEntity(target, FriendStatus.FRIEND));
  }

  @Override
  @Transactional
  public @Nonnull FriendshipResponse declineFriendship(String username, String targetUsername) {
    if (Objects.equals(username, targetUsername)) {
      throw new SameUsernameException("Can`t decline friendship request for self user");
    }
    UserEntity user = getRequiredUser(username);
    UserEntity target = getRequiredUser(targetUsername);

    user.removeInvites(target);
    target.removeFriends(user);

    userRepository.save(user);
    userRepository.save(target);
    return userMapper.toProtoFriendship(FriendWithStatus.fromEntity(target, FriendStatus.NOT_FRIEND));
  }

  @Override
  @Transactional
  public @Nonnull FriendshipResponse deleteFriend(String username, String targetUsername) {
    if (Objects.equals(username, targetUsername)) {
      throw new SameUsernameException("Can`t remove friendship relation for self user");
    }
    UserEntity user = getRequiredUser(username);
    UserEntity target = getRequiredUser(targetUsername);

    user.removeFriends(target);
    user.removeInvites(target);
    target.removeFriends(user);
    target.removeInvites(user);

    userRepository.save(user);
    userRepository.save(target);
    return userMapper.toProtoFriendship(FriendWithStatus.fromEntity(target, FriendStatus.NOT_FRIEND));
  }

  @Nonnull UserEntity getRequiredUser(String username) {
    return userRepository.findByUsername(username).orElseThrow(
      () -> new UserNotFoundException("Can`t find user by username: '" + username + "'")
    );
  }

  private @Nonnull Optional<FriendshipEntity> getFriendshipRequest(UserEntity currentUser, UserEntity targetUser) {
    return currentUser.getFriendshipAddressees()
      .stream()
      .filter(fe -> fe.getRequester() != null && fe.getRequester().equals(targetUser))
      .findFirst();
  }
}