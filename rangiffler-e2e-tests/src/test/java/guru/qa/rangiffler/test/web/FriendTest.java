package guru.qa.rangiffler.test.web;

import guru.qa.rangiffler.data.entity.userdata.FriendshipStatus;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.annotation.meta.WebTest;
import guru.qa.rangiffler.model.UdUserJson;
import guru.qa.rangiffler.page.PeoplePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;

@Tags({@Tag("WEB")})
@WebTest
@ParametersAreNonnullByDefault
public class FriendTest {
  @Test
  @User(
    friends = 1
  )
  @ApiLogin
  @DisplayName("Search field should return matches result in Friends table")
  void searchFieldInFriendsTableShouldReturnListOfMatchesUsers(UdUserJson user) {
    final String friendUsername = user.testData().friends().getFirst().username();

    open(PeoplePage.URL, PeoplePage.class)
      .openFriendsPage()
      .findFriend(friendUsername)
      .assertFriendExists(friendUsername);
  }

  @Test
  @User(
    incomeInvitations = 1
  )
  @ApiLogin
  @DisplayName("Search field should return matches result in All People table")
  void searchFieldInPeopleTableShouldReturnListOfMatchesUsers(UdUserJson user) {
    final String username = user.testData().incomeInvitations().getFirst().username();

    open(PeoplePage.URL, PeoplePage.class)
      .openAllPeoplePage()
      .findPeople(username)
      .assertPeopleExists(username);
  }

  @Test
  @User(
    incomeInvitations = 1
  )
  @ApiLogin
  @DisplayName("Search field should return matches result in Income Invitations table")
  void searchFieldInIncomeInvitationsTableShouldReturnListOfMatchesUsers(UdUserJson user) {
    final String username = user.testData().incomeInvitations().getFirst().username();

    open(PeoplePage.URL, PeoplePage.class)
      .openIncomeInvitationsPage()
      .findIncomeInvitation(username)
      .assertIncomeInvitationExists(username);
  }

  @Test
  @User(
    friends = 1
  )
  @ApiLogin
  @DisplayName("Existed friends should be displayed in Friends table")
  void existedFriendShouldBeDisplayedInFriendsTable(UdUserJson user) {
    final String friendUsername = user.testData().friends().getFirst().username();

    open(PeoplePage.URL, PeoplePage.class)
      .openFriendsPage()
      .findFriend(friendUsername)
      .assertFriendExists(friendUsername);
  }

  @Test
  @User(
    friends = 1
  )
  @ApiLogin
  @DisplayName("User should be able to delete friend from Friends table")
  void userShouldBeAbleToDeleteFriendFromFriendsList(UdUserJson user) {
    final String friendUsername = user.testData().friends().getFirst().username();
    open(PeoplePage.URL, PeoplePage.class)
      .openFriendsPage()
      .findFriend(friendUsername)
      .deleteFriend(friendUsername)
      .assertFriendNotExists(friendUsername);
  }

  @Test
  @User(
    incomeInvitations = 1
  )
  @ApiLogin
  @DisplayName("User should be able to decline friendship request from Income Invitations table")
  void userShouldBeAbleToDeclineFriendshipRequest(UdUserJson user) {
    final String inviterUsername = user.testData().incomeInvitations().getFirst().username();

    open(PeoplePage.URL, PeoplePage.class)
      .openIncomeInvitationsPage()
      .findIncomeInvitation(inviterUsername)
      .assertIncomeInvitationExists(inviterUsername)
      .changeFriendshipRequestStatus(inviterUsername, FriendshipStatus.DECLINED);
    sleep(5000);
  }

  @Test
  @User(
    incomeInvitations = 1
  )
  @ApiLogin
  @DisplayName("User should be able to create friendship request from All People page")
  void userShouldBeAbleToCreateFriendshipRequestFromAllPeopleTable(UdUserJson user) {
    final String inviterUsername = user.testData().incomeInvitations().getFirst().username();

    open(PeoplePage.URL, PeoplePage.class)
      .openIncomeInvitationsPage()
      .findIncomeInvitation(inviterUsername)
      .changeFriendshipRequestStatus(inviterUsername, FriendshipStatus.DECLINED)
      .assertIncomeInvitationNotExists(inviterUsername)
      .openAllPeoplePage()
      .findPeople(inviterUsername)
      .createFriendshipWithUser(inviterUsername);
  }

  @Test
  @User(
    incomeInvitations = 1
  )
  @ApiLogin
  @DisplayName("Income invitations to current user should be displayed in Income Invitations table")
  void incomeInvitationShouldBeDisplayedInIncomeInvitationsTable(UdUserJson user) {
    final String inviterUsername = user.testData().incomeInvitations().getFirst().username();

    open(PeoplePage.URL, PeoplePage.class)
      .openIncomeInvitationsPage()
      .findIncomeInvitation(inviterUsername)
      .assertIncomeInvitationExists(inviterUsername);
  }

  @Test
  @User(
    incomeInvitations = 1
  )
  @ApiLogin
  @DisplayName("Declined friendship request should be not displayed in Friends table")
  void incomeInvitationShouldBeNotDisplayedInFriendsTableAfterDecline(UdUserJson user) {
    final String inviterUsername = user.testData().incomeInvitations().getFirst().username();

    open(PeoplePage.URL, PeoplePage.class)
      .openIncomeInvitationsPage()
      .findIncomeInvitation(inviterUsername)
      .assertIncomeInvitationExists(inviterUsername)
      .changeFriendshipRequestStatus(inviterUsername, FriendshipStatus.DECLINED)
      .openFriendsPage()
      .findFriend(inviterUsername)
      .assertFriendNotExists(inviterUsername);
  }
}
