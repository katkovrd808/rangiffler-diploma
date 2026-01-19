package guru.qa.rangiffler.service;

import guru.qa.rangiffler.data.FriendshipEntity;
import guru.qa.rangiffler.data.FriendshipStatus;
import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.data.projection.UserWithStatus;
import guru.qa.rangiffler.data.repository.FriendshipRepository;
import guru.qa.rangiffler.data.repository.UserRepository;
import guru.qa.rangiffler.ex.InvalidFriendshipOperationException;
import guru.qa.rangiffler.ex.SameUsernameException;
import guru.qa.rangiffler.ex.UserNotFoundException;
import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.service.impl.DbUserService;
import guru.qa.rangiffler.service.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private FriendshipRepository friendshipRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private Pageable pageable;

  private DbUserService dbUserService;

  @BeforeEach
  void setUp() {
    dbUserService = new DbUserService(userRepository, friendshipRepository, userMapper);
  }

  @Test
  void findByUsername_WithValidUsername_ShouldReturnUser() {
    final String username = "testuser";
    final UserEntity userEntity = new UserEntity();
    final UserResponse expectedResponse = UserResponse.newBuilder()
      .setId(UUID.randomUUID().toString())
      .setUsername(username)
      .build();

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
    when(userMapper.toProto(userEntity)).thenReturn(expectedResponse);

    final UserResponse actualResponse = dbUserService.findByUsername(username);

    verify(userRepository).findByUsername(username);
    verify(userMapper).toProto(userEntity);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void findByUsername_WhenUserNotFound_ShouldThrowException() {
    final String username = "nonexistent";
    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    final UserNotFoundException exception = assertThrows(
      UserNotFoundException.class,
      () -> dbUserService.findByUsername(username)
    );

    assertEquals("Can't find user with username: " + username, exception.getMessage());
    verify(userRepository).findByUsername(username);
    verify(userMapper, never()).toProto((UserEntity) any());
  }

  @Test
  void findById_WithValidId_ShouldReturnUser() {
    final UUID userId = UUID.randomUUID();
    final String userIdString = userId.toString();
    final UserEntity userEntity = new UserEntity();
    final UserResponse expectedResponse = UserResponse.newBuilder()
      .setId(userIdString)
      .setUsername("testuser")
      .build();

    when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
    when(userMapper.toProto(userEntity)).thenReturn(expectedResponse);

    final UserResponse actualResponse = dbUserService.findById(userIdString);

    verify(userRepository).findById(userId);
    verify(userMapper).toProto(userEntity);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void findById_WhenUserNotFound_ShouldThrowException() {
    final UUID userId = UUID.randomUUID();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    final UserNotFoundException exception = assertThrows(
      UserNotFoundException.class,
      () -> dbUserService.findById(userId.toString())
    );

    assertEquals("Can't find user with id: " + userId, exception.getMessage());
    verify(userRepository).findById(userId);
    verify(userMapper, never()).toProto((UserEntity) any());
  }

  @Test
  void findById_WithInvalidUUID_ShouldThrowException() {
    final String invalidUuid = "not-a-uuid";

    assertThrows(
      IllegalArgumentException.class,
      () -> dbUserService.findById(invalidUuid)
    );

    verify(userRepository, never()).findById(any());
    verify(userMapper, never()).toProto((UserEntity) any());
  }

  @Test
  void findUserWithFriendStatus_ShouldReturnUserWithStatus() {
    final UUID targetUserId = UUID.randomUUID();
    final UUID currentUserId = UUID.randomUUID();
    final String targetUserIdString = targetUserId.toString();
    final String currentUserIdString = currentUserId.toString();

    final UserWithStatus userWithStatus = new UserWithStatus(
      targetUserId, "targetuser", "John", "Doe",
      new byte[] {}, UUID.randomUUID(), FriendshipStatus.ACCEPTED, false
    );
    final UserResponse expectedResponse = UserResponse.newBuilder()
      .setId(targetUserIdString)
      .setUsername("targetuser")
      .build();

    when(userRepository.findByIdWithFriendStatus(targetUserId, currentUserId))
      .thenReturn(Optional.of(userWithStatus));
    when(userMapper.toProto(userWithStatus)).thenReturn(expectedResponse);

    final UserResponse actualResponse = dbUserService.findUserWithFriendStatus(
      targetUserIdString, currentUserIdString);

    verify(userRepository).findByIdWithFriendStatus(targetUserId, currentUserId);
    verify(userMapper).toProto(userWithStatus);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void findUserWithFriendStatus_WhenUserNotFound_ShouldThrowException() {
    final UUID targetUserId = UUID.randomUUID();
    final UUID currentUserId = UUID.randomUUID();

    when(userRepository.findByIdWithFriendStatus(targetUserId, currentUserId))
      .thenReturn(Optional.empty());

    final UserNotFoundException exception = assertThrows(
      UserNotFoundException.class,
      () -> dbUserService.findUserWithFriendStatus(
        targetUserId.toString(), currentUserId.toString())
    );

    assertEquals("Can't find user with ID: " + targetUserId, exception.getMessage());
  }

  @Test
  void getAllUsers_ShouldReturnPaginatedUsers() {
    final String username = "currentuser";
    final UUID currentUserId = UUID.randomUUID();

    final UserEntity currentUserEntity = new UserEntity();
    currentUserEntity.setId(currentUserId);
    currentUserEntity.setUsername(username);

    final UserResponse currentUserResponse = UserResponse.newBuilder()
      .setId(currentUserId.toString())
      .setUsername(username)
      .build();

    final List<UserWithStatus> users = List.of(
      new UserWithStatus(UUID.randomUUID(), "user1", null, null, null, UUID.randomUUID(), FriendshipStatus.DELETED, false),
      new UserWithStatus(UUID.randomUUID(), "user2", null, null, null, UUID.randomUUID(), FriendshipStatus.DELETED, false)
    );
    final Page<UserWithStatus> userPage = new PageImpl<>(users);

    final UsersPaginatedResponse expectedResponse = UsersPaginatedResponse.newBuilder()
      .addUsers(UserResponse.newBuilder().setId(UUID.randomUUID().toString()))
      .addUsers(UserResponse.newBuilder().setId(UUID.randomUUID().toString()))
      .build();

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(currentUserEntity));
    when(userMapper.toProto(currentUserEntity)).thenReturn(currentUserResponse);
    when(userRepository.findByIdNot(currentUserId, pageable)).thenReturn(userPage);
    when(userMapper.toProtoList(userPage)).thenReturn(expectedResponse);

    final UsersPaginatedResponse actualResponse = dbUserService.getAllUsers(pageable, username);

    verify(userRepository).findByUsername(username);
    verify(userRepository).findByIdNot(currentUserId, pageable);
    verify(userMapper).toProtoList(userPage);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void updateUser_WithValidData_ShouldUpdateUser() {
    final String username = "testuser";
    final String countryId = UUID.randomUUID().toString();
    final UserUpdateRequest request = UserUpdateRequest.newBuilder()
      .setUsername(username)
      .setFirstname("John")
      .setSurname("Doe")
      .setCountryId(countryId)
      .build();

    final UserEntity userEntity = new UserEntity();
    userEntity.setUsername(username);

    final UserUpdateResponse expectedResponse = UserUpdateResponse.newBuilder()
      .setUsername(username)
      .setFirstname("John")
      .setSurname("Doe")
      .build();

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
    when(userRepository.save(userEntity)).thenReturn(userEntity);
    when(userMapper.toUpdateProto(userEntity)).thenReturn(expectedResponse);

    final UserUpdateResponse actualResponse = dbUserService.updateUser(request);

    verify(userRepository).findByUsername(username);
    verify(userRepository).save(userEntity);
    verify(userMapper).toUpdateProto(userEntity);

    assertEquals("John", userEntity.getFirstname());
    assertEquals("Doe", userEntity.getSurname());
    assertEquals(UUID.fromString(countryId), userEntity.getCountryId());
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void updateUser_WithDifferentUsername_ShouldThrowException() {
    final String username = "testuser";
    final String differentUsername = "otheruser";
    final UserUpdateRequest request = UserUpdateRequest.newBuilder()
      .setUsername(differentUsername)
      .build();

    final UserEntity userEntity = new UserEntity();
    userEntity.setUsername(username);

    when(userRepository.findByUsername(differentUsername)).thenReturn(Optional.of(userEntity));

    final SecurityException exception = assertThrows(
      SecurityException.class,
      () -> dbUserService.updateUser(request)
    );

    assertEquals("User can only update their own profile.", exception.getMessage());
    verify(userRepository).findByUsername(differentUsername);
    verify(userRepository, never()).save(any());
    verify(userMapper, never()).toUpdateProto(any());
  }

  @Test
  void updateUser_WithEmptyFields_ShouldSetEmptyStrings() {
    final String username = "testuser";
    final String countryId = UUID.randomUUID().toString();
    final UserUpdateRequest request = UserUpdateRequest.newBuilder()
      .setUsername(username)
      .setFirstname("")
      .setSurname("")
      .setCountryId(countryId)
      .build();

    final UserEntity userEntity = new UserEntity();
    userEntity.setUsername(username);
    userEntity.setFirstname("Old");
    userEntity.setSurname("Name");

    final UserUpdateResponse expectedResponse = UserUpdateResponse.newBuilder()
      .setUsername(username)
      .build();

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
    when(userRepository.save(userEntity)).thenReturn(userEntity);
    when(userMapper.toUpdateProto(userEntity)).thenReturn(expectedResponse);

    dbUserService.updateUser(request);

    assertEquals("", userEntity.getFirstname());
    assertEquals("", userEntity.getSurname());
    assertEquals(UUID.fromString(countryId), userEntity.getCountryId());
  }

  @Test
  void allFriendsPaginated_WithoutSearchQuery_ShouldReturnFriends() {
    final String username = "testuser";
    final UserEntity userEntity = new UserEntity();
    userEntity.setUsername(username);

    final List<UserWithStatus> friends = List.of(
      new UserWithStatus(UUID.randomUUID(), "friend1", null, null, null, UUID.randomUUID(), FriendshipStatus.ACCEPTED, false),
      new UserWithStatus(UUID.randomUUID(), "friend2", null, null, null, UUID.randomUUID(), FriendshipStatus.ACCEPTED, false)
    );
    final Page<UserWithStatus> friendPage = new PageImpl<>(friends);

    final AllFriendsPaginatedResponse expectedResponse = AllFriendsPaginatedResponse.newBuilder()
      .addFriends(Friend.newBuilder().setId(UUID.randomUUID().toString()))
      .addFriends(Friend.newBuilder().setId(UUID.randomUUID().toString()))
      .build();

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
    when(friendshipRepository.findFriends(userEntity, pageable)).thenReturn(friendPage);
    when(userMapper.toProtoFriendsListResponse(friendPage)).thenReturn(expectedResponse);

    final AllFriendsPaginatedResponse actualResponse = dbUserService.allFriends(pageable, username, null);

    verify(userRepository).findByUsername(username);
    verify(friendshipRepository).findFriends(userEntity, pageable);
    verify(userMapper).toProtoFriendsListResponse(friendPage);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void allFriendsPaginated_WithSearchQuery_ShouldReturnFilteredFriends() {
    final String username = "testuser";
    final String searchQuery = "john";
    final UserEntity userEntity = new UserEntity();
    userEntity.setUsername(username);

    final List<UserWithStatus> friends = List.of(
      new UserWithStatus(UUID.randomUUID(), "john", null, null, null, UUID.randomUUID(), FriendshipStatus.ACCEPTED, false)
    );
    final Page<UserWithStatus> friendPage = new PageImpl<>(friends);

    final AllFriendsPaginatedResponse expectedResponse = AllFriendsPaginatedResponse.newBuilder()
      .addFriends(Friend.newBuilder().setId(UUID.randomUUID().toString()))
      .build();

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
    when(friendshipRepository.findFriends(userEntity, searchQuery, pageable)).thenReturn(friendPage);
    when(userMapper.toProtoFriendsListResponse(friendPage)).thenReturn(expectedResponse);

    final AllFriendsPaginatedResponse actualResponse = dbUserService.allFriends(pageable, username, searchQuery);

    verify(userRepository).findByUsername(username);
    verify(friendshipRepository).findFriends(userEntity, searchQuery, pageable);
    verify(userMapper).toProtoFriendsListResponse(friendPage);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void allFriendsPaginated_WithEmptyUsername_ShouldThrowException() {
    final IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> dbUserService.allFriends(pageable, "", null)
    );

    assertEquals("User can't be empty.", exception.getMessage());
    verify(userRepository, never()).findByUsername(any());
    verify(friendshipRepository, never()).findFriends(any(), any());
  }

  @Test
  void allFriendsPaginated_WithNullUsername_ShouldThrowNullPointerException() {
    assertThrows(
      NullPointerException.class,
      () -> dbUserService.allFriends(pageable, null, null)
    );

    verify(userRepository, never()).findByUsername(any());
    verify(friendshipRepository, never()).findFriends(any(), any());
  }

  @Test
  void sendFriendshipRequest_ToDifferentUser_ShouldCreateRequest() {
    final String username = "user1";
    final String targetUsername = "user2";

    final UserEntity user = new UserEntity();
    user.setUsername(username);
    user.setId(UUID.randomUUID());

    final UserEntity target = new UserEntity();
    target.setUsername(targetUsername);
    target.setId(UUID.randomUUID());

    final UserWithStatus invitedUser = UserWithStatus.fromEntity(target, FriendshipStatus.PENDING, true);
    final FriendshipResponse expectedResponse = FriendshipResponse.newBuilder()
      .setUsername(targetUsername)
      .setStatus(FriendStatus.INVITATION_SENT)
      .build();

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(userRepository.findByUsername(targetUsername)).thenReturn(Optional.of(target));
    when(userRepository.save(user)).thenReturn(user);
    when(userRepository.save(target)).thenReturn(target);
    when(userMapper.toProtoFriendshipResponse(invitedUser)).thenReturn(expectedResponse);

    final FriendshipResponse actualResponse = dbUserService.sendFriendshipRequest(username, targetUsername);

    verify(userRepository, times(2)).save(any());
    verify(userMapper).toProtoFriendshipResponse(any());
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void sendFriendshipRequest_ToSelf_ShouldThrowException() {
    final String username = "user1";

    final SameUsernameException exception = assertThrows(
      SameUsernameException.class,
      () -> dbUserService.sendFriendshipRequest(username, username)
    );

    assertEquals("Can't create friendship request for self user", exception.getMessage());
    verify(userRepository, never()).findByUsername(any());
    verify(userRepository, never()).save(any());
  }

  @Test
  void sendFriendshipRequest_WhenRequestAlreadyExists_ShouldThrowException() {
    final String username = "user1";
    final String targetUsername = "user2";

    final UserEntity user = new UserEntity();
    user.setUsername(username);
    user.setId(UUID.randomUUID());

    final UserEntity target = new UserEntity();
    target.setUsername(targetUsername);
    target.setId(UUID.randomUUID());

    final FriendshipEntity existingFriendship = new FriendshipEntity();
    existingFriendship.setStatus(FriendshipStatus.PENDING);
    existingFriendship.setRequester(user);
    existingFriendship.setAddressee(target);
    user.getFriendshipRequests().add(existingFriendship);

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(userRepository.findByUsername(targetUsername)).thenReturn(Optional.of(target));

    final InvalidFriendshipOperationException exception = assertThrows(
      InvalidFriendshipOperationException.class,
      () -> dbUserService.sendFriendshipRequest(username, targetUsername)
    );

    assertTrue(exception.getMessage().contains("Friendship request to user"));
    verify(userRepository, never()).save(any());
  }

  @Test
  void acceptFriendship_ShouldAcceptPendingRequest() {
    final String username = "user1";
    final String targetUsername = "user2";

    final UserEntity user = new UserEntity();
    user.setUsername(username);
    user.setId(UUID.randomUUID());

    final UserEntity target = new UserEntity();
    target.setUsername(targetUsername);
    target.setId(UUID.randomUUID());

    final FriendshipEntity pendingFriendship = new FriendshipEntity();
    pendingFriendship.setStatus(FriendshipStatus.PENDING);
    pendingFriendship.setRequester(target);
    pendingFriendship.setAddressee(user);
    user.getFriendshipAddressees().add(pendingFriendship);
    target.getFriendshipRequests().add(pendingFriendship);

    final UserWithStatus acceptedUser = UserWithStatus.fromEntity(target, FriendshipStatus.ACCEPTED, false);
    final FriendshipResponse expectedResponse = FriendshipResponse.newBuilder()
      .setUsername(targetUsername)
      .setStatus(FriendStatus.FRIEND)
      .build();

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(userRepository.findByUsername(targetUsername)).thenReturn(Optional.of(target));
    when(userRepository.save(user)).thenReturn(user);
    when(userRepository.save(target)).thenReturn(target);
    when(userMapper.toProtoFriendshipResponse(acceptedUser)).thenReturn(expectedResponse);

    final FriendshipResponse actualResponse = dbUserService.acceptFriendship(username, targetUsername);

    assertEquals(FriendshipStatus.ACCEPTED, pendingFriendship.getStatus());
    verify(userRepository, times(2)).save(any());
    verify(userMapper).toProtoFriendshipResponse(any());
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void acceptFriendship_WhenAcceptingOwnRequest_ShouldThrowException() {
    final String username = "user1";
    final String targetUsername = "user2";

    final UserEntity user = new UserEntity();
    user.setUsername(username);
    user.setId(UUID.randomUUID());

    final UserEntity target = new UserEntity();
    target.setUsername(targetUsername);
    target.setId(UUID.randomUUID());

    final FriendshipEntity pendingFriendship = new FriendshipEntity();
    pendingFriendship.setStatus(FriendshipStatus.PENDING);
    pendingFriendship.setRequester(user);
    pendingFriendship.setAddressee(target);
    user.getFriendshipRequests().add(pendingFriendship);
    target.getFriendshipAddressees().add(pendingFriendship);

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(userRepository.findByUsername(targetUsername)).thenReturn(Optional.of(target));

    final InvalidFriendshipOperationException exception = assertThrows(
      InvalidFriendshipOperationException.class,
      () -> dbUserService.acceptFriendship(username, targetUsername)
    );

    assertEquals("Can't accept your own friendship request", exception.getMessage());
    verify(userRepository, never()).save(any());
  }

  @Test
  void declineFriendship_ShouldDeclinePendingRequest() {
    final String username = "user1";
    final String targetUsername = "user2";

    final UserEntity user = new UserEntity();
    user.setUsername(username);
    user.setId(UUID.randomUUID());

    final UserEntity target = new UserEntity();
    target.setUsername(targetUsername);
    target.setId(UUID.randomUUID());

    final FriendshipEntity pendingFriendship = new FriendshipEntity();
    pendingFriendship.setStatus(FriendshipStatus.PENDING);
    pendingFriendship.setRequester(target);
    pendingFriendship.setAddressee(user);
    user.getFriendshipAddressees().add(pendingFriendship);
    target.getFriendshipRequests().add(pendingFriendship);

    final UserWithStatus declinedUser = UserWithStatus.fromEntity(target, FriendshipStatus.DECLINED, false);
    final FriendshipResponse expectedResponse = FriendshipResponse.newBuilder()
      .setUsername(targetUsername)
      .setStatus(FriendStatus.NOT_FRIEND)
      .build();

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(userRepository.findByUsername(targetUsername)).thenReturn(Optional.of(target));
    when(userRepository.save(user)).thenReturn(user);
    when(userRepository.save(target)).thenReturn(target);
    when(userMapper.toProtoFriendshipResponse(declinedUser)).thenReturn(expectedResponse);

    final FriendshipResponse actualResponse = dbUserService.declineFriendship(username, targetUsername);

    verify(friendshipRepository).delete(pendingFriendship);
    verify(userMapper).toProtoFriendshipResponse(any());
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void deleteFriend_ShouldDeleteAcceptedFriendship() {
    final String username = "user1";
    final String targetUsername = "user2";

    final UserEntity user = new UserEntity();
    user.setUsername(username);
    user.setId(UUID.randomUUID());

    final UserEntity target = new UserEntity();
    target.setUsername(targetUsername);
    target.setId(UUID.randomUUID());

    final FriendshipEntity acceptedFriendship = new FriendshipEntity();
    acceptedFriendship.setStatus(FriendshipStatus.ACCEPTED);
    acceptedFriendship.setRequester(user);
    acceptedFriendship.setAddressee(target);
    user.getFriendshipRequests().add(acceptedFriendship);
    target.getFriendshipAddressees().add(acceptedFriendship);

    final FriendshipResponse expectedResponse = FriendshipResponse.newBuilder()
      .setUsername(targetUsername)
      .setStatus(FriendStatus.NOT_FRIEND)
      .build();

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(userRepository.findByUsername(targetUsername)).thenReturn(Optional.of(target));
    when(userRepository.save(user)).thenReturn(user);
    when(userRepository.save(target)).thenReturn(target);
    when(userMapper.toProtoFriendshipResponse(any(UserWithStatus.class))).thenReturn(expectedResponse);

    final FriendshipResponse actualResponse = dbUserService.deleteFriend(username, targetUsername);

    verify(friendshipRepository).delete(acceptedFriendship);
    verify(userMapper).toProtoFriendshipResponse(any(UserWithStatus.class));
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void allFriends_ShouldReturnAllFriends() {
    final String username = "testuser";
    final UserEntity userEntity = new UserEntity();
    userEntity.setUsername(username);

    final List<UserWithStatus> friends = List.of(
      new UserWithStatus(UUID.randomUUID(), "friend1", null, null, null, UUID.randomUUID(), FriendshipStatus.ACCEPTED, false),
      new UserWithStatus(UUID.randomUUID(), "friend2", null, null, null, UUID.randomUUID(), FriendshipStatus.ACCEPTED, false)
    );

    final AllFriendsResponse expectedResponse = AllFriendsResponse.newBuilder()
      .addFriends(Friend.newBuilder().setId(UUID.randomUUID().toString()))
      .addFriends(Friend.newBuilder().setId(UUID.randomUUID().toString()))
      .build();

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
    when(friendshipRepository.findFriends(userEntity)).thenReturn(friends);
    when(userMapper.toProtoFriendsListResponse(friends)).thenReturn(expectedResponse);

    final AllFriendsResponse actualResponse = dbUserService.allFriends(username);

    verify(friendshipRepository).findFriends(userEntity);
    verify(userMapper).toProtoFriendsListResponse(friends);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void incomeInvitations_WithoutSearchQuery_ShouldReturnInvitations() {
    final String username = "testuser";
    final UserEntity userEntity = new UserEntity();
    userEntity.setUsername(username);

    final List<UserWithStatus> invitations = List.of(
      new UserWithStatus(UUID.randomUUID(), "inviter", null, null, null, UUID.randomUUID(), FriendshipStatus.PENDING, false)
    );
    final Page<UserWithStatus> invitationPage = new PageImpl<>(invitations);

    final InvitationsPaginatedResponse expectedResponse = InvitationsPaginatedResponse.newBuilder()
      .addInvitations(Friend.newBuilder().setId(UUID.randomUUID().toString()))
      .build();

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
    when(friendshipRepository.findIncomeInvitations(userEntity, pageable)).thenReturn(invitationPage);
    when(userMapper.toProtoInvitationsList(invitationPage)).thenReturn(expectedResponse);

    final InvitationsPaginatedResponse actualResponse = dbUserService.incomeInvitations(pageable, username, null);

    verify(friendshipRepository).findIncomeInvitations(userEntity, pageable);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void outcomeInvitations_WithSearchQuery_ShouldReturnFilteredInvitations() {
    final String username = "testuser";
    final String searchQuery = "friend";
    final UserEntity userEntity = new UserEntity();
    userEntity.setUsername(username);

    final List<UserWithStatus> invitations = List.of(
      new UserWithStatus(UUID.randomUUID(), "friend", null, null, null, UUID.randomUUID(), FriendshipStatus.PENDING, true)
    );
    final Page<UserWithStatus> invitationPage = new PageImpl<>(invitations);

    final InvitationsPaginatedResponse expectedResponse = InvitationsPaginatedResponse.newBuilder()
      .addInvitations(Friend.newBuilder().setId(UUID.randomUUID().toString()))
      .build();

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
    when(friendshipRepository.findOutcomeInvitations(userEntity, searchQuery, pageable)).thenReturn(invitationPage);
    when(userMapper.toProtoInvitationsList(invitationPage)).thenReturn(expectedResponse);

    final InvitationsPaginatedResponse actualResponse = dbUserService.outcomeInvitations(pageable, username, searchQuery);

    verify(friendshipRepository).findOutcomeInvitations(userEntity, searchQuery, pageable);
    assertEquals(expectedResponse, actualResponse);
  }
}