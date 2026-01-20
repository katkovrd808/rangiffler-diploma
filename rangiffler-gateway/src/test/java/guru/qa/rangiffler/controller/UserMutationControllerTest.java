package guru.qa.rangiffler.controller;

import guru.qa.rangiffler.controller.userdata.UserMutationController;
import guru.qa.rangiffler.model.graphql.userdata.FriendshipInputGql;
import guru.qa.rangiffler.model.graphql.userdata.UserGql;
import guru.qa.rangiffler.model.graphql.userdata.UserInputGql;
import guru.qa.rangiffler.service.FriendshipAction;
import guru.qa.rangiffler.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserMutationControllerTest {
  @Mock
  private UserService userService;
  @Mock
  private Jwt jwt;

  private UserMutationController userMutationController;

  @BeforeEach
  void setUp() {
    userMutationController = new UserMutationController(userService);
  }

  @Test
  void user_ShouldUpdateUserAndReturnUserGql() {
    final String username = "testuser";
    final UserInputGql userInput = new UserInputGql(
      "John",
      "Doe",
      new byte[]{},
      null
    );
    final UserGql expectedUser = UserGql.userWithId(UUID.randomUUID().toString());

    when(jwt.getClaim("sub")).thenReturn(username);
    when(userService.updateUser(username, userInput)).thenReturn(expectedUser);

    final UserGql actualUser = userMutationController.user(jwt, userInput);

    verify(userService).updateUser(username, userInput);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  void friendship_ShouldProcessFriendshipAndReturnUserGql() {
    final String principalUsername = "user1";
    final UUID targetUserId = UUID.randomUUID();
    final String targetUsername = "user2";
    final FriendshipInputGql friendshipInput = new FriendshipInputGql(
      targetUserId,
      FriendshipAction.ADD
    );
    final UserGql targetUser = new UserGql(
      targetUserId,
      targetUsername,
      "John",
      "Doe",
      null,
      null,
      null
    );
    final UserGql expectedUser = new UserGql(
      UUID.randomUUID(),
      "user1",
      "First",
      "Last",
      null,
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(principalUsername);
    when(userService.findUser(null, targetUserId)).thenReturn(targetUser);
    when(userService.friendship(principalUsername, targetUsername, FriendshipAction.ADD))
      .thenReturn(expectedUser);

    final UserGql actualUser = userMutationController.friendship(jwt, friendshipInput);

    verify(userService).findUser(null, targetUserId);
    verify(userService).friendship(principalUsername, targetUsername, FriendshipAction.ADD);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  void friendship_WithDeleteAction_ShouldProcessFriendshipDelete() {
    final String principalUsername = "user1";
    final UUID targetUserId = UUID.randomUUID();
    final String targetUsername = "user2";
    final FriendshipInputGql friendshipInput = new FriendshipInputGql(
      targetUserId,
      FriendshipAction.DELETE
    );
    final UserGql targetUser = new UserGql(
      targetUserId,
      targetUsername,
      "John",
      "Doe",
      null,
      null,
      null
    );
    final UserGql expectedUser = new UserGql(
      UUID.randomUUID(),
      "user1",
      "First",
      "Last",
      null,
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(principalUsername);
    when(userService.findUser(null, targetUserId)).thenReturn(targetUser);
    when(userService.friendship(principalUsername, targetUsername, FriendshipAction.DELETE))
      .thenReturn(expectedUser);

    final UserGql actualUser = userMutationController.friendship(jwt, friendshipInput);

    verify(userService).findUser(null, targetUserId);
    verify(userService).friendship(principalUsername, targetUsername, FriendshipAction.DELETE);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  void user_WithEmptyUserInput_ShouldUpdateUser() {
    final String username = "testuser";
    final UserInputGql emptyUserInput = new UserInputGql(
      null,
      null,
      null,
      null
    );
    final UserGql expectedUser = UserGql.userWithId(UUID.randomUUID().toString());

    when(jwt.getClaim("sub")).thenReturn(username);
    when(userService.updateUser(username, emptyUserInput)).thenReturn(expectedUser);

    final UserGql actualUser = userMutationController.user(jwt, emptyUserInput);

    verify(userService).updateUser(username, emptyUserInput);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  void friendship_WithREJECTAction_ShouldProcessFriendshipREJECT() {
    final String principalUsername = "user1";
    final UUID targetUserId = UUID.randomUUID();
    final String targetUsername = "user2";
    final FriendshipInputGql friendshipInput = new FriendshipInputGql(
      targetUserId,
      FriendshipAction.REJECT
    );
    final UserGql targetUser = new UserGql(
      targetUserId,
      targetUsername,
      "John",
      "Doe",
      null,
      null,
      null
    );
    final UserGql expectedUser = new UserGql(
      UUID.randomUUID(),
      "user1",
      "First",
      "Last",
      null,
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(principalUsername);
    when(userService.findUser(null, targetUserId)).thenReturn(targetUser);
    when(userService.friendship(principalUsername, targetUsername, FriendshipAction.REJECT))
      .thenReturn(expectedUser);

    final UserGql actualUser = userMutationController.friendship(jwt, friendshipInput);

    verify(userService).findUser(null, targetUserId);
    verify(userService).friendship(principalUsername, targetUsername, FriendshipAction.REJECT);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  void user_WithNullUsernameInJwt_ShouldCallServiceWithNull() {
    final UserInputGql userInput = new UserInputGql(
      "John",
      "Doe",
      new byte[]{},
      null
    );
    final UserGql expectedUser = new UserGql(
      UUID.randomUUID(),
      null,
      "First",
      "Last",
      null,
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(null);
    when(userService.updateUser(null, userInput)).thenReturn(expectedUser);

    final UserGql actualUser = userMutationController.user(jwt, userInput);

    verify(userService).updateUser(null, userInput);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  void friendship_WithNullUsernameInJwt_ShouldCallServiceWithNull() {
    final UUID targetUserId = UUID.randomUUID();
    final String targetUsername = "user2";
    final FriendshipInputGql friendshipInput = new FriendshipInputGql(
      targetUserId,
      FriendshipAction.ADD
    );
    final UserGql targetUser = new UserGql(
      targetUserId,
      targetUsername,
      "John",
      "Doe",
      null,
      null,
      null
    );
    final UserGql expectedUser = new UserGql(
      UUID.randomUUID(),
      null,
      "First",
      "Last",
      null,
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(null);
    when(userService.findUser(null, targetUserId)).thenReturn(targetUser);
    when(userService.friendship(null, targetUsername, FriendshipAction.ADD))
      .thenReturn(expectedUser);

    final UserGql actualUser = userMutationController.friendship(jwt, friendshipInput);

    verify(userService).findUser(null, targetUserId);
    verify(userService).friendship(null, targetUsername, FriendshipAction.ADD);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  void user_ShouldReturnSameInstanceFromService() {
    final String username = "testuser";
    final UserInputGql userInput = new UserInputGql(
      "John",
      "Doe",
      new byte[]{},
      null
    );
    final UserGql expectedUser = new UserGql(
      UUID.randomUUID(),
      username,
      "John",
      "Doe",
      null,
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(username);
    when(userService.updateUser(username, userInput)).thenReturn(expectedUser);

    final UserGql actualUser = userMutationController.user(jwt, userInput);

    verify(userService).updateUser(username, userInput);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  void friendship_ShouldReturnSameInstanceFromService() {
    final String principalUsername = "user1";
    final UUID targetUserId = UUID.randomUUID();
    final String targetUsername = "user2";
    final FriendshipInputGql friendshipInput = new FriendshipInputGql(
      targetUserId,
      FriendshipAction.ADD
    );
    final UserGql targetUser = new UserGql(
      targetUserId,
      targetUsername,
      "John",
      "Doe",
      null,
      null,
      null
    );
    final UserGql expectedUser = new UserGql(
      UUID.randomUUID(),
      principalUsername,
      "First",
      "Last",
      null,
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(principalUsername);
    when(userService.findUser(null, targetUserId)).thenReturn(targetUser);
    when(userService.friendship(principalUsername, targetUsername, FriendshipAction.ADD))
      .thenReturn(expectedUser);

    final UserGql actualUser = userMutationController.friendship(jwt, friendshipInput);

    verify(userService).findUser(null, targetUserId);
    verify(userService).friendship(principalUsername, targetUsername, FriendshipAction.ADD);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  void friendship_WithDifferentActions_ShouldCallCorrectServiceMethod() {
    final String principalUsername = "user1";
    final UUID targetUserId = UUID.randomUUID();
    final String targetUsername = "user2";
    final UserGql targetUser = new UserGql(
      targetUserId,
      targetUsername,
      "John",
      "Doe",
      null,
      null,
      null
    );

    final FriendshipAction[] actions = {
      FriendshipAction.ADD,
      FriendshipAction.DELETE,
      FriendshipAction.ACCEPT,
      FriendshipAction.REJECT
    };

    when(jwt.getClaim("sub")).thenReturn(principalUsername);
    when(userService.findUser(null, targetUserId)).thenReturn(targetUser);

    for (FriendshipAction action : actions) {
      final FriendshipInputGql friendshipInput = new FriendshipInputGql(
        targetUserId,
        action
      );
      final UserGql expectedUser = new UserGql(
        UUID.randomUUID(),
        principalUsername,
        "First",
        "Last",
        null,
        null,
        null
      );

      when(userService.friendship(principalUsername, targetUsername, action))
        .thenReturn(expectedUser);

      final UserGql actualUser = userMutationController.friendship(jwt, friendshipInput);

      verify(userService).friendship(principalUsername, targetUsername, action);
      assertEquals(expectedUser, actualUser);
    }
  }

  @Test
  void user_WithNullUserInput_ShouldCallServiceWithNull() {
    final String username = "testuser";
    final UserGql expectedUser = new UserGql(
      UUID.randomUUID(),
      username,
      "First",
      "Last",
      null,
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(username);
    when(userService.updateUser(username, null)).thenReturn(expectedUser);

    final UserGql actualUser = userMutationController.user(jwt, null);

    verify(userService).updateUser(username, null);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  void friendship_WithNullFriendshipInput_ShouldThrowException() {
    final String principalUsername = "user1";

    when(jwt.getClaim("sub")).thenReturn(principalUsername);

    try {
      userMutationController.friendship(jwt, null);
    } catch (NullPointerException e) {
      //NOP
    }
  }
}