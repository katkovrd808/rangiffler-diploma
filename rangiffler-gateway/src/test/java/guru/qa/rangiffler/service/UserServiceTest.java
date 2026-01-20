package guru.qa.rangiffler.service;

import guru.qa.rangiffler.api.GrpcCountriesClient;
import guru.qa.rangiffler.api.GrpcUserdataClient;
import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.model.graphql.countries.CountryInputGql;
import guru.qa.rangiffler.model.graphql.userdata.UserGql;
import guru.qa.rangiffler.model.graphql.userdata.UserInputGql;
import guru.qa.rangiffler.model.graphql.userdata.UsersSliceGql;
import guru.qa.rangiffler.service.impl.UserServiceImpl;
import guru.qa.rangiffler.service.mapper.FriendshipMapper;
import guru.qa.rangiffler.service.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {
  @Mock
  private GrpcUserdataClient grpcUserdataClient;

  @Mock
  private GrpcCountriesClient grpcCountriesClient;

  @Mock
  private UserMapper userMapper;

  @Mock
  private FriendshipMapper friendshipMapper;

  @Mock
  private Pageable pageable;

  private UserServiceImpl userServiceImpl;

  @BeforeEach
  void setUp() {
    userServiceImpl = new UserServiceImpl(
      grpcUserdataClient, grpcCountriesClient, userMapper, friendshipMapper
    );
  }

  @Test
  void findUser_ByUsername_ShouldReturnUser() {
    final String username = "testuser";
    final String userId = UUID.randomUUID().toString();
    final String countryId = UUID.randomUUID().toString();

    final UserResponse userResponse = UserResponse.newBuilder()
      .setId(userId)
      .setUsername(username)
      .setCountryId(countryId)
      .build();

    final CountryResponse countryResponse = CountryResponse.newBuilder()
      .setId(countryId)
      .setName("Test Country")
      .setCode("TC")
      .build();

    final UserGql expectedUser = UserGql.userWithId(userId);

    when(grpcUserdataClient.findUser(username, null)).thenReturn(userResponse);
    when(grpcCountriesClient.getCountry(null, countryId)).thenReturn(countryResponse);
    when(userMapper.toUserGql(userResponse, countryResponse)).thenReturn(expectedUser);

    final UserGql actualUser = userServiceImpl.findUser(username, null);

    verify(grpcUserdataClient).findUser(username, null);
    verify(grpcCountriesClient).getCountry(null, countryId);
    verify(userMapper).toUserGql(userResponse, countryResponse);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  void findUser_ByUserId_ShouldReturnUser() {
    final UUID userId = UUID.randomUUID();
    final String countryId = UUID.randomUUID().toString();

    final UserResponse userResponse = UserResponse.newBuilder()
      .setId(userId.toString())
      .setUsername("testuser")
      .setCountryId(countryId)
      .build();

    final CountryResponse countryResponse = CountryResponse.newBuilder()
      .setId(countryId)
      .setName("Test Country")
      .setCode("TC")
      .build();

    final UserGql expectedUser = UserGql.userWithId(userId.toString());

    when(grpcUserdataClient.findUser(null, userId)).thenReturn(userResponse);
    when(grpcCountriesClient.getCountry(null, countryId)).thenReturn(countryResponse);
    when(userMapper.toUserGql(userResponse, countryResponse)).thenReturn(expectedUser);

    final UserGql actualUser = userServiceImpl.findUser(null, userId);

    verify(grpcUserdataClient).findUser(null, userId);
    verify(grpcCountriesClient).getCountry(null, countryId);
    verify(userMapper).toUserGql(userResponse, countryResponse);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  void findUserWithFriendStatus_ShouldReturnUserWithStatus() {
    final String targetUserId = UUID.randomUUID().toString();
    final String currentUserId = UUID.randomUUID().toString();
    final String countryId = UUID.randomUUID().toString();

    final UserResponse userResponse = UserResponse.newBuilder()
      .setId(targetUserId)
      .setUsername("targetuser")
      .setCountryId(countryId)
      .build();

    final CountryResponse countryResponse = CountryResponse.newBuilder()
      .setId(countryId)
      .setName("Test Country")
      .setCode("TC")
      .build();

    final UserGql expectedUser = UserGql.userWithId(targetUserId);

    when(grpcUserdataClient.findUserWithFriendStatus(targetUserId, currentUserId)).thenReturn(userResponse);
    when(grpcCountriesClient.getCountry(null, countryId)).thenReturn(countryResponse);
    when(userMapper.toUserGql(userResponse, countryResponse)).thenReturn(expectedUser);

    final UserGql actualUser = userServiceImpl.findUserWithFriendStatus(targetUserId, currentUserId);

    verify(grpcUserdataClient).findUserWithFriendStatus(targetUserId, currentUserId);
    verify(grpcCountriesClient).getCountry(null, countryId);
    verify(userMapper).toUserGql(userResponse, countryResponse);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  void allUsers_ShouldReturnUsersSlice() {
    final String username = "currentuser";
    final UsersPaginatedResponse usersResponse = UsersPaginatedResponse.newBuilder()
      .addUsers(UserResponse.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setUsername("user1")
        .build())
      .addUsers(UserResponse.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setUsername("user2")
        .build())
      .build();

    final CountriesResponse countriesResponse = CountriesResponse.newBuilder()
      .addCountries(CountryResponse.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setName("Country1")
        .build())
      .addCountries(CountryResponse.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setName("Country2")
        .build())
      .build();

    final UsersSliceGql expectedSlice = new UsersSliceGql(List.of(), null);

    when(grpcUserdataClient.findAllExceptCurrent(pageable, username)).thenReturn(usersResponse);
    when(grpcCountriesClient.allCountries()).thenReturn(countriesResponse);
    when(userMapper.toUsersListGql(usersResponse, countriesResponse)).thenReturn(expectedSlice);

    final UsersSliceGql actualSlice = userServiceImpl.allUsers(pageable, username);

    verify(grpcUserdataClient).findAllExceptCurrent(pageable, username);
    verify(grpcCountriesClient).allCountries();
    verify(userMapper).toUsersListGql(usersResponse, countriesResponse);
    assertEquals(expectedSlice, actualSlice);
  }

  @Test
  void updateUser_ShouldUpdateAndReturnUser() {
    final String username = "testuser";
    final String countryId = UUID.randomUUID().toString();
    final String countryCode = "US";

    final UserInputGql userInput = new UserInputGql(
      "John",
      "Doe",
      new byte[] {},
      new CountryInputGql(countryCode)
    );

    final CountryResponse countryResponse = CountryResponse.newBuilder()
      .setId(countryId)
      .setName("United States")
      .setCode(countryCode)
      .build();

    final UserUpdateResponse updateResponse = UserUpdateResponse.newBuilder()
      .setId(UUID.randomUUID().toString())
      .setUsername(username)
      .setFirstname("John")
      .setSurname("Doe")
      .build();

    final UserGql expectedUser = UserGql.userWithId(updateResponse.getId());

    when(grpcCountriesClient.getCountry(countryCode, null)).thenReturn(countryResponse);
    when(grpcUserdataClient.updateUser(username, userInput, countryId)).thenReturn(updateResponse);
    when(userMapper.toUserGql(updateResponse, countryResponse)).thenReturn(expectedUser);

    final UserGql actualUser = userServiceImpl.updateUser(username, userInput);

    verify(grpcCountriesClient).getCountry(countryCode, null);
    verify(grpcUserdataClient).updateUser(username, userInput, countryId);
    verify(userMapper).toUserGql(updateResponse, countryResponse);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  void friends_ShouldReturnFriendsList() {
    final String username = "testuser";
    final String searchQuery = "john";

    final AllFriendsPaginatedResponse friendsResponse = AllFriendsPaginatedResponse.newBuilder()
      .addFriends(Friend.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setUsername("john")
        .build())
      .build();

    final CountriesResponse countriesResponse = CountriesResponse.newBuilder()
      .addCountries(CountryResponse.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setName("Country")
        .build())
      .build();

    final UsersSliceGql expectedSlice = new UsersSliceGql(List.of(), null);

    when(grpcUserdataClient.findFriends(pageable, username, searchQuery)).thenReturn(friendsResponse);
    when(grpcCountriesClient.allCountries()).thenReturn(countriesResponse);
    when(friendshipMapper.toFriendsListGql(friendsResponse, countriesResponse)).thenReturn(expectedSlice);

    final UsersSliceGql actualSlice = userServiceImpl.friends(pageable, username, searchQuery);

    verify(grpcUserdataClient).findFriends(pageable, username, searchQuery);
    verify(grpcCountriesClient).allCountries();
    verify(friendshipMapper).toFriendsListGql(friendsResponse, countriesResponse);
    assertEquals(expectedSlice, actualSlice);
  }

  @Test
  void friends_WithoutSearchQuery_ShouldReturnAllFriends() {
    final String username = "testuser";

    final AllFriendsPaginatedResponse friendsResponse = AllFriendsPaginatedResponse.newBuilder()
      .addFriends(Friend.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setUsername("friend1")
        .build())
      .addFriends(Friend.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setUsername("friend2")
        .build())
      .build();

    final CountriesResponse countriesResponse = CountriesResponse.newBuilder()
      .addCountries(CountryResponse.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setName("Country")
        .build())
      .build();

    final UsersSliceGql expectedSlice = new UsersSliceGql(List.of(), null);

    when(grpcUserdataClient.findFriends(pageable, username, null)).thenReturn(friendsResponse);
    when(grpcCountriesClient.allCountries()).thenReturn(countriesResponse);
    when(friendshipMapper.toFriendsListGql(friendsResponse, countriesResponse)).thenReturn(expectedSlice);

    final UsersSliceGql actualSlice = userServiceImpl.friends(pageable, username, null);

    verify(grpcUserdataClient).findFriends(pageable, username, null);
    verify(grpcCountriesClient).allCountries();
    verify(friendshipMapper).toFriendsListGql(friendsResponse, countriesResponse);
    assertEquals(expectedSlice, actualSlice);
  }

  @Test
  void incomeInvitations_ShouldReturnIncomeInvitations() {
    final String username = "testuser";
    final String searchQuery = "inviter";

    final InvitationsPaginatedResponse invitationsResponse = InvitationsPaginatedResponse.newBuilder()
      .addInvitations(Friend.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setUsername("inviter")
        .build())
      .build();

    final CountriesResponse countriesResponse = CountriesResponse.newBuilder()
      .addCountries(CountryResponse.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setName("Country")
        .build())
      .build();

    final UsersSliceGql expectedSlice = new UsersSliceGql(List.of(), null);

    when(grpcUserdataClient.getIncomeInvitations(pageable, username, searchQuery)).thenReturn(invitationsResponse);
    when(grpcCountriesClient.allCountries()).thenReturn(countriesResponse);
    when(friendshipMapper.toInvitationsListGql(invitationsResponse, countriesResponse)).thenReturn(expectedSlice);

    final UsersSliceGql actualSlice = userServiceImpl.incomeInvitations(pageable, username, searchQuery);

    verify(grpcUserdataClient).getIncomeInvitations(pageable, username, searchQuery);
    verify(grpcCountriesClient).allCountries();
    verify(friendshipMapper).toInvitationsListGql(invitationsResponse, countriesResponse);
    assertEquals(expectedSlice, actualSlice);
  }

  @Test
  void outcomeInvitations_ShouldReturnOutcomeInvitations() {
    final String username = "testuser";
    final String searchQuery = "invitee";

    final InvitationsPaginatedResponse invitationsResponse = InvitationsPaginatedResponse.newBuilder()
      .addInvitations(Friend.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setUsername("invitee")
        .build())
      .build();

    final CountriesResponse countriesResponse = CountriesResponse.newBuilder()
      .addCountries(CountryResponse.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setName("Country")
        .build())
      .build();

    final UsersSliceGql expectedSlice = new UsersSliceGql(List.of(), null);

    when(grpcUserdataClient.getOutcomeInvitations(pageable, username, searchQuery)).thenReturn(invitationsResponse);
    when(grpcCountriesClient.allCountries()).thenReturn(countriesResponse);
    when(friendshipMapper.toInvitationsListGql(invitationsResponse, countriesResponse)).thenReturn(expectedSlice);

    final UsersSliceGql actualSlice = userServiceImpl.outcomeInvitations(pageable, username, searchQuery);

    verify(grpcUserdataClient).getOutcomeInvitations(pageable, username, searchQuery);
    verify(grpcCountriesClient).allCountries();
    verify(friendshipMapper).toInvitationsListGql(invitationsResponse, countriesResponse);
    assertEquals(expectedSlice, actualSlice);
  }

  @Test
  void findUser_WithBothUsernameAndUserId_ShouldPreferUsername() {
    final String username = "testuser";
    final UUID userId = UUID.randomUUID();
    final String countryId = UUID.randomUUID().toString();

    final UserResponse userResponse = UserResponse.newBuilder()
      .setId(userId.toString())
      .setUsername(username)
      .setCountryId(countryId)
      .build();

    final CountryResponse countryResponse = CountryResponse.newBuilder()
      .setId(countryId)
      .setName("Test Country")
      .setCode("TC")
      .build();

    final UserGql expectedUser = UserGql.userWithId(userId.toString());

    when(grpcUserdataClient.findUser(username, userId)).thenReturn(userResponse);
    when(grpcCountriesClient.getCountry(null, countryId)).thenReturn(countryResponse);
    when(userMapper.toUserGql(userResponse, countryResponse)).thenReturn(expectedUser);

    final UserGql actualUser = userServiceImpl.findUser(username, userId);

    verify(grpcUserdataClient).findUser(username, userId);
    verify(grpcCountriesClient).getCountry(null, countryId);
    verify(userMapper).toUserGql(userResponse, countryResponse);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  void findUser_WithNullParameters_ShouldCallClientWithNulls() {
    final String userId = UUID.randomUUID().toString();
    final String countryId = UUID.randomUUID().toString();

    final UserResponse userResponse = UserResponse.newBuilder()
      .setId(userId)
      .setUsername("default")
      .setCountryId(countryId)
      .build();

    final CountryResponse countryResponse = CountryResponse.newBuilder()
      .setId(countryId)
      .setName("Test Country")
      .setCode("TC")
      .build();

    final UserGql expectedUser = UserGql.userWithId(userId);

    when(grpcUserdataClient.findUser(null, null)).thenReturn(userResponse);
    when(grpcCountriesClient.getCountry(null, countryId)).thenReturn(countryResponse);
    when(userMapper.toUserGql(userResponse, countryResponse)).thenReturn(expectedUser);

    final UserGql actualUser = userServiceImpl.findUser(null, null);

    verify(grpcUserdataClient).findUser(null, null);
    verify(grpcCountriesClient).getCountry(null, countryId);
    verify(userMapper).toUserGql(userResponse, countryResponse);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  void allUsers_ShouldHandleEmptyResponse() {
    final String username = "currentuser";
    final UsersPaginatedResponse emptyResponse = UsersPaginatedResponse.newBuilder().build();
    final CountriesResponse countriesResponse = CountriesResponse.newBuilder().build();
    final UsersSliceGql expectedSlice = new UsersSliceGql(List.of(), null);

    when(grpcUserdataClient.findAllExceptCurrent(pageable, username)).thenReturn(emptyResponse);
    when(grpcCountriesClient.allCountries()).thenReturn(countriesResponse);
    when(userMapper.toUsersListGql(emptyResponse, countriesResponse)).thenReturn(expectedSlice);

    final UsersSliceGql actualSlice = userServiceImpl.allUsers(pageable, username);

    verify(grpcUserdataClient).findAllExceptCurrent(pageable, username);
    verify(grpcCountriesClient).allCountries();
    verify(userMapper).toUsersListGql(emptyResponse, countriesResponse);
    assertEquals(expectedSlice, actualSlice);
  }

  @Test
  void updateUser_ShouldHandleEmptyAvatar() {
    final String username = "testuser";
    final String countryId = UUID.randomUUID().toString();
    final String countryCode = "US";

    final UserInputGql userInput = new UserInputGql(
      "John",
      "Doe",
      null,
      new CountryInputGql(countryCode)
    );

    final CountryResponse countryResponse = CountryResponse.newBuilder()
      .setId(countryId)
      .setName("United States")
      .setCode(countryCode)
      .build();

    final UserUpdateResponse updateResponse = UserUpdateResponse.newBuilder()
      .setId(UUID.randomUUID().toString())
      .setUsername(username)
      .setFirstname("John")
      .setSurname("Doe")
      .build();

    final UserGql expectedUser = UserGql.userWithId(updateResponse.getId());

    when(grpcCountriesClient.getCountry(countryCode, null)).thenReturn(countryResponse);
    when(grpcUserdataClient.updateUser(username, userInput, countryId)).thenReturn(updateResponse);
    when(userMapper.toUserGql(updateResponse, countryResponse)).thenReturn(expectedUser);

    final UserGql actualUser = userServiceImpl.updateUser(username, userInput);

    verify(grpcCountriesClient).getCountry(countryCode, null);
    verify(grpcUserdataClient).updateUser(username, userInput, countryId);
    verify(userMapper).toUserGql(updateResponse, countryResponse);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  void friends_ShouldHandleEmptyFriendsList() {
    final String username = "testuser";
    final AllFriendsPaginatedResponse emptyResponse = AllFriendsPaginatedResponse.newBuilder().build();
    final CountriesResponse countriesResponse = CountriesResponse.newBuilder().build();
    final UsersSliceGql expectedSlice = new UsersSliceGql(List.of(), null);

    when(grpcUserdataClient.findFriends(pageable, username, null)).thenReturn(emptyResponse);
    when(grpcCountriesClient.allCountries()).thenReturn(countriesResponse);
    when(friendshipMapper.toFriendsListGql(emptyResponse, countriesResponse)).thenReturn(expectedSlice);

    final UsersSliceGql actualSlice = userServiceImpl.friends(pageable, username, null);

    verify(grpcUserdataClient).findFriends(pageable, username, null);
    verify(grpcCountriesClient).allCountries();
    verify(friendshipMapper).toFriendsListGql(emptyResponse, countriesResponse);
    assertEquals(expectedSlice, actualSlice);
  }

  @Test
  void incomeInvitations_ShouldHandleEmptyInvitations() {
    final String username = "testuser";
    final InvitationsPaginatedResponse emptyResponse = InvitationsPaginatedResponse.newBuilder().build();
    final CountriesResponse countriesResponse = CountriesResponse.newBuilder().build();
    final UsersSliceGql expectedSlice = new UsersSliceGql(List.of(), null);

    when(grpcUserdataClient.getIncomeInvitations(pageable, username, null)).thenReturn(emptyResponse);
    when(grpcCountriesClient.allCountries()).thenReturn(countriesResponse);
    when(friendshipMapper.toInvitationsListGql(emptyResponse, countriesResponse)).thenReturn(expectedSlice);

    final UsersSliceGql actualSlice = userServiceImpl.incomeInvitations(pageable, username, null);

    verify(grpcUserdataClient).getIncomeInvitations(pageable, username, null);
    verify(grpcCountriesClient).allCountries();
    verify(friendshipMapper).toInvitationsListGql(emptyResponse, countriesResponse);
    assertEquals(expectedSlice, actualSlice);
  }

  @Test
  void outcomeInvitations_ShouldHandleEmptyInvitations() {
    final String username = "testuser";
    final InvitationsPaginatedResponse emptyResponse = InvitationsPaginatedResponse.newBuilder().build();
    final CountriesResponse countriesResponse = CountriesResponse.newBuilder().build();
    final UsersSliceGql expectedSlice = new UsersSliceGql(List.of(), null);

    when(grpcUserdataClient.getOutcomeInvitations(pageable, username, null)).thenReturn(emptyResponse);
    when(grpcCountriesClient.allCountries()).thenReturn(countriesResponse);
    when(friendshipMapper.toInvitationsListGql(emptyResponse, countriesResponse)).thenReturn(expectedSlice);

    final UsersSliceGql actualSlice = userServiceImpl.outcomeInvitations(pageable, username, null);

    verify(grpcUserdataClient).getOutcomeInvitations(pageable, username, null);
    verify(grpcCountriesClient).allCountries();
    verify(friendshipMapper).toInvitationsListGql(emptyResponse, countriesResponse);
    assertEquals(expectedSlice, actualSlice);
  }

  @Test
  void friendship_ShouldHandleDifferentFriendshipActions() {
    final String currentUser = "user1";
    final String targetUser = "user2";

    for (FriendshipAction action : FriendshipAction.values()) {
      final FriendshipResponse friendshipResponse = FriendshipResponse.newBuilder()
        .setUsername(targetUser)
        .build();

      final UserResponse targetUserResponse = UserResponse.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setUsername(targetUser)
        .build();

      final UserResponse currentUserResponse = UserResponse.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setUsername(currentUser)
        .build();

      final UserResponse userWithStatusResponse = UserResponse.newBuilder()
        .setId(targetUserResponse.getId())
        .setUsername(targetUser)
        .build();

      final CountryResponse countryResponse = CountryResponse.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setName("Country")
        .build();

      final UserGql expectedUser = UserGql.userWithId(targetUserResponse.getId());

      when(grpcUserdataClient.friendship(currentUser, targetUser, action)).thenReturn(friendshipResponse);
      when(grpcUserdataClient.findUser(targetUser, null)).thenReturn(targetUserResponse);
      when(grpcUserdataClient.findUser(currentUser, null)).thenReturn(currentUserResponse);
      when(grpcUserdataClient.findUserWithFriendStatus(anyString(), anyString())).thenReturn(userWithStatusResponse);
      when(grpcCountriesClient.getCountry(any(), anyString())).thenReturn(countryResponse);
      when(userMapper.toUserGql((UserResponse) any(), any())).thenReturn(expectedUser);

      final UserGql actualUser = userServiceImpl.friendship(currentUser, targetUser, action);

      verify(grpcUserdataClient).friendship(currentUser, targetUser, action);

      reset(grpcUserdataClient, grpcCountriesClient, userMapper);
    }
  }
}
