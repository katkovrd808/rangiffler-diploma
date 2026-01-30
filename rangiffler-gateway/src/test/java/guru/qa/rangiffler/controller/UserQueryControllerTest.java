package guru.qa.rangiffler.controller;

import guru.qa.rangiffler.controller.userdata.UserQueryController;
import guru.qa.rangiffler.model.graphql.userdata.UserGql;
import guru.qa.rangiffler.model.graphql.userdata.UsersSliceGql;
import guru.qa.rangiffler.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserQueryControllerTest {
  @Mock
  private UserService userService;
  @Mock
  private Jwt jwt;

  private UserQueryController userQueryController;

  @BeforeEach
  void setUp() {
    userQueryController = new UserQueryController(userService);
  }

  @Test
  void user_ShouldReturnCurrentUser() {
    final String username = "testuser";
    final UserGql expectedUser = UserGql.userWithId(UUID.randomUUID().toString());

    when(jwt.getClaim("sub")).thenReturn(username);
    when(userService.findUser(username, null)).thenReturn(expectedUser);

    final UserGql actualUser = userQueryController.user(jwt);

    verify(userService).findUser(username, null);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  void users_ShouldReturnUsersSlice() {
    final String username = "testuser";
    final int page = 0;
    final int size = 10;
    final UsersSliceGql expectedUsersSlice = new UsersSliceGql(null, null);

    when(jwt.getClaim("sub")).thenReturn(username);
    when(userService.allUsers(PageRequest.of(page, size), username, null)).thenReturn(expectedUsersSlice);

    final UsersSliceGql actualUsersSlice = userQueryController.users(jwt, size, page, null);

    verify(userService).allUsers(PageRequest.of(page, size), username, null);
    assertEquals(expectedUsersSlice, actualUsersSlice);
  }

  @Test
  void friends_ShouldReturnFriendsSlice() {
    final UserGql user = UserGql.userWithId(UUID.randomUUID().toString());
    final int page = 1;
    final int size = 20;
    final String searchQuery = "john";
    final UsersSliceGql expectedFriendsSlice = new UsersSliceGql(null, null);

    when(userService.friends(PageRequest.of(page, size), user.username(), searchQuery))
      .thenReturn(expectedFriendsSlice);

    final UsersSliceGql actualFriendsSlice = userQueryController.friends(user, page, size, searchQuery);

    verify(userService).friends(PageRequest.of(page, size), user.username(), searchQuery);
    assertEquals(expectedFriendsSlice, actualFriendsSlice);
  }

  @Test
  void friends_WithNullSearchQuery_ShouldReturnAllFriends() {
    final UserGql user = UserGql.userWithId(UUID.randomUUID().toString());
    final int page = 0;
    final int size = 10;
    final UsersSliceGql expectedFriendsSlice = new UsersSliceGql(null, null);

    when(userService.friends(PageRequest.of(page, size), user.username(), null))
      .thenReturn(expectedFriendsSlice);

    final UsersSliceGql actualFriendsSlice = userQueryController.friends(user, page, size, null);

    verify(userService).friends(PageRequest.of(page, size), user.username(), null);
    assertEquals(expectedFriendsSlice, actualFriendsSlice);
  }

  @Test
  void incomeInvitations_ShouldReturnIncomeInvitationsSlice() {
    final UserGql user = UserGql.userWithId(UUID.randomUUID().toString());
    final int page = 2;
    final int size = 15;
    final String searchQuery = "inviter";
    final UsersSliceGql expectedInvitationsSlice = new UsersSliceGql(null, null);

    when(userService.incomeInvitations(PageRequest.of(page, size), user.username(), searchQuery))
      .thenReturn(expectedInvitationsSlice);

    final UsersSliceGql actualInvitationsSlice = userQueryController.incomeInvitations(user, page, size, searchQuery);

    verify(userService).incomeInvitations(PageRequest.of(page, size), user.username(), searchQuery);
    assertEquals(expectedInvitationsSlice, actualInvitationsSlice);
  }

  @Test
  void outcomeInvitations_ShouldReturnOutcomeInvitationsSlice() {
    final UserGql user = UserGql.userWithId(UUID.randomUUID().toString());
    final int page = 0;
    final int size = 5;
    final String searchQuery = "invitee";
    final UsersSliceGql expectedInvitationsSlice = new UsersSliceGql(null, null);

    when(userService.outcomeInvitations(PageRequest.of(page, size), user.username(), searchQuery))
      .thenReturn(expectedInvitationsSlice);

    final UsersSliceGql actualInvitationsSlice = userQueryController.outcomeInvitations(user, page, size, searchQuery);

    verify(userService).outcomeInvitations(PageRequest.of(page, size), user.username(), searchQuery);
    assertEquals(expectedInvitationsSlice, actualInvitationsSlice);
  }

  @Test
  void incomeInvitations_WithNullSearchQuery_ShouldReturnAllInvitations() {
    final UserGql user = UserGql.userWithId(UUID.randomUUID().toString());
    final int page = 0;
    final int size = 10;
    final UsersSliceGql expectedInvitationsSlice = new UsersSliceGql(null, null);

    when(userService.incomeInvitations(PageRequest.of(page, size), user.username(), null))
      .thenReturn(expectedInvitationsSlice);

    final UsersSliceGql actualInvitationsSlice = userQueryController.incomeInvitations(user, page, size, null);

    verify(userService).incomeInvitations(PageRequest.of(page, size), user.username(), null);
    assertEquals(expectedInvitationsSlice, actualInvitationsSlice);
  }

  @Test
  void outcomeInvitations_WithNullSearchQuery_ShouldReturnAllInvitations() {
    final UserGql user = UserGql.userWithId(UUID.randomUUID().toString());
    final int page = 0;
    final int size = 10;
    final UsersSliceGql expectedInvitationsSlice = new UsersSliceGql(null, null);

    when(userService.outcomeInvitations(PageRequest.of(page, size), user.username(), null))
      .thenReturn(expectedInvitationsSlice);

    final UsersSliceGql actualInvitationsSlice = userQueryController.outcomeInvitations(user, page, size, null);

    verify(userService).outcomeInvitations(PageRequest.of(page, size), user.username(), null);
    assertEquals(expectedInvitationsSlice, actualInvitationsSlice);
  }

  @Test
  void user_WithNullJwt_ShouldThrowException() {
    assertThrows(NullPointerException.class, () ->
      userQueryController.user(null)
    );
  }

  @Test
  void user_WithNullUsernameInJwt_ShouldReturnUserFromService() {
    final UserGql expectedUser = UserGql.userWithId(UUID.randomUUID().toString());

    when(jwt.getClaim("sub")).thenReturn(null);
    when(userService.findUser(null, null)).thenReturn(expectedUser);

    final UserGql actualUser = userQueryController.user(jwt);

    verify(userService).findUser(null, null);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  void users_WithNullJwt_ShouldThrowException() {
    final int page = 0;
    final int size = 10;

    assertThrows(NullPointerException.class, () ->
      userQueryController.users(null, size, page, null)
    );
  }

  @Test
  void users_WithNullUsernameInJwt_ShouldReturnUsersSliceFromService() {
    final int page = 0;
    final int size = 10;
    final UsersSliceGql expectedUsersSlice = new UsersSliceGql(null, null);

    when(jwt.getClaim("sub")).thenReturn(null);
    when(userService.allUsers(PageRequest.of(page, size), null, null)).thenReturn(expectedUsersSlice);

    final UsersSliceGql actualUsersSlice = userQueryController.users(jwt, size, page, null);

    verify(userService).allUsers(PageRequest.of(page, size), null, null);
    assertEquals(expectedUsersSlice, actualUsersSlice);
  }

  @Test
  void users_WithNegativePage_ShouldThrowException() {
    final String username = "testuser";
    final int page = -1;
    final int size = 10;

    when(jwt.getClaim("sub")).thenReturn(username);

    assertThrows(IllegalArgumentException.class, () ->
      userQueryController.users(jwt, size, page, null)
    );
  }

  @Test
  void users_WithZeroSize_ShouldThrowException() {
    final String username = "testuser";
    final int page = 0;
    final int size = 0;

    when(jwt.getClaim("sub")).thenReturn(username);

    assertThrows(IllegalArgumentException.class, () ->
      userQueryController.users(jwt, size, page, null)
    );
  }

  @Test
  void friends_WithNegativePage_ShouldThrowException() {
    final UserGql user = UserGql.userWithId(UUID.randomUUID().toString());
    final int page = -2;
    final int size = 10;
    final String searchQuery = "test";

    assertThrows(IllegalArgumentException.class, () ->
      userQueryController.friends(user, page, size, searchQuery)
    );
  }

  @Test
  void incomeInvitations_WithZeroSize_ShouldThrowException() {
    final UserGql user = UserGql.userWithId(UUID.randomUUID().toString());
    final int page = 0;
    final int size = 0;
    final String searchQuery = "test";

    assertThrows(IllegalArgumentException.class, () ->
      userQueryController.incomeInvitations(user, page, size, searchQuery)
    );
  }

  @Test
  void outcomeInvitations_WithLargePageNumber_ShouldHandleGracefully() {
    final UserGql user = UserGql.userWithId(UUID.randomUUID().toString());
    final int page = 100;
    final int size = 10;
    final String searchQuery = "test";
    final UsersSliceGql expectedInvitationsSlice = new UsersSliceGql(null, null);

    when(userService.outcomeInvitations(PageRequest.of(page, size), user.username(), searchQuery))
      .thenReturn(expectedInvitationsSlice);

    final UsersSliceGql actualInvitationsSlice = userQueryController.outcomeInvitations(user, page, size, searchQuery);

    verify(userService).outcomeInvitations(PageRequest.of(page, size), user.username(), searchQuery);
    assertEquals(expectedInvitationsSlice, actualInvitationsSlice);
  }

  @Test
  void friends_WithEmptySearchQuery_ShouldReturnAllFriends() {
    final UserGql user = UserGql.userWithId(UUID.randomUUID().toString());
    final int page = 0;
    final int size = 10;
    final String emptySearchQuery = "";
    final UsersSliceGql expectedFriendsSlice = new UsersSliceGql(null, null);

    when(userService.friends(PageRequest.of(page, size), user.username(), emptySearchQuery))
      .thenReturn(expectedFriendsSlice);

    final UsersSliceGql actualFriendsSlice = userQueryController.friends(user, page, size, emptySearchQuery);

    verify(userService).friends(PageRequest.of(page, size), user.username(), emptySearchQuery);
    assertEquals(expectedFriendsSlice, actualFriendsSlice);
  }

  @Test
  void user_ShouldReturnSameInstanceFromService() {
    final String username = "testuser";
    final UserGql expectedUser = UserGql.userWithId(UUID.randomUUID().toString());

    when(jwt.getClaim("sub")).thenReturn(username);
    when(userService.findUser(username, null)).thenReturn(expectedUser);

    final UserGql actualUser = userQueryController.user(jwt);

    verify(userService).findUser(username, null);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  void users_ShouldReturnSameInstanceFromService() {
    final String username = "testuser";
    final int page = 2;
    final int size = 15;
    final UsersSliceGql expectedUsersSlice = new UsersSliceGql(null, null);

    when(jwt.getClaim("sub")).thenReturn(username);
    when(userService.allUsers(PageRequest.of(page, size), username, null)).thenReturn(expectedUsersSlice);

    final UsersSliceGql actualUsersSlice = userQueryController.users(jwt, size, page, null);

    verify(userService).allUsers(PageRequest.of(page, size), username, null);
    assertEquals(expectedUsersSlice, actualUsersSlice);
  }

  @Test
  void friends_ShouldReturnSameInstanceFromService() {
    final UserGql user = UserGql.userWithId(UUID.randomUUID().toString());
    final int page = 0;
    final int size = 10;
    final String searchQuery = "friend";
    final UsersSliceGql expectedFriendsSlice = new UsersSliceGql(null, null);

    when(userService.friends(PageRequest.of(page, size), user.username(), searchQuery))
      .thenReturn(expectedFriendsSlice);

    final UsersSliceGql actualFriendsSlice = userQueryController.friends(user, page, size, searchQuery);

    verify(userService).friends(PageRequest.of(page, size), user.username(), searchQuery);
    assertEquals(expectedFriendsSlice, actualFriendsSlice);
  }

  @Test
  void friends_WithNullUser_ShouldThrowException() {
    final int page = 0;
    final int size = 10;
    final String searchQuery = "test";

    assertThrows(NullPointerException.class, () ->
      userQueryController.friends(null, page, size, searchQuery)
    );
  }
}