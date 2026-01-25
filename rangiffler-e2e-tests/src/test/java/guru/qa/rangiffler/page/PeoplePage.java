package guru.qa.rangiffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.data.entity.userdata.FriendshipStatus;
import guru.qa.rangiffler.page.base.BasePage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class PeoplePage extends BasePage<PeoplePage> {

  public static final String URL = Config.getInstance().frontUrl() + "people";

  private final SelenideElement
    peopleTabsButtons = $("div[aria-label='People tabs']"),
    friendsTabPanel = $("#simple-tabpanel-friends"),
    allPeopleTabPanel = $("#simple-tabpanel-all"),
    outcomeInvitationsTabPanel = $("#simple-tabpanel-outcome"),
    incomeInvitationsTabPanel = $("#simple-tabpanel-income");

  @Nonnull
  @Step("Opening Friends page")
  public PeoplePage openFriendsPage() {
    peopleTabsButtons.$$("button").get(0).click();
    return this;
  }

  @Nonnull
  @Step("Opening All People page")
  public PeoplePage openAllPeoplePage() {
    peopleTabsButtons.$$("button").get(1).click();
    return this;
  }

  @Nonnull
  @Step("Opening Outcome Invitations page")
  public PeoplePage openOutcomeInvitationsPage() {
    peopleTabsButtons.$$("button").get(2).click();
    return this;
  }

  @Nonnull
  @Step("Opening Income Invitations page")
  public PeoplePage openIncomeInvitationsPage() {
    peopleTabsButtons.$$("button").get(3).click();
    return this;
  }

  @Nonnull
  @Step("Searching for friend with username: {username} in table Friends")
  public PeoplePage findFriend(String username) {
    friendsTabPanel.$("input").val(username).pressEnter();
    return this;
  }

  @Nonnull
  @Step("Asserting that friend with username: {username} exists in table Friends")
  public PeoplePage assertFriendExists(String username) {
    friendsTabPanel.$$("tbody td").find(text(username))
      .should(exist);
    return this;
  }

  @Nonnull
  @Step("Asserting that friend with username: {username} exists in table Friends")
  public PeoplePage assertFriendNotExists(String username) {
    friendsTabPanel.$$("tbody td").find(text(username))
      .should(not(exist));
    return this;
  }

  @Nonnull
  @Step("Deleting friend with username: {username}")
  public PeoplePage deleteFriend(String username) {
    ElementsCollection rows = friendsTabPanel.$$("tbody tr");
    for (SelenideElement row : rows) {
      ElementsCollection cells = row.$$("td");
      if (cells.size() > 1 && cells.get(1).getText().equals(username)) {
        cells.last().$("button").click();
        break;
      }
    }
    return this;
  }

  @Nonnull
  @Step("Searching for user with username: {username} in table All people")
  public PeoplePage findPeople(String username) {
    allPeopleTabPanel.$("input").val(username).pressEnter();
    return this;
  }

  @Nonnull
  @Step("Asserting that user with username: {username} exists in table All people")
  public PeoplePage assertPeopleExists(String username) {
    allPeopleTabPanel.$$("tbody td").find(text(username))
      .should(exist);
    return this;
  }

  @Nonnull
  @Step("Creating friendship with user with username: {username}")
  public PeoplePage createFriendshipWithUser(String username) {
    ElementsCollection rows = allPeopleTabPanel.$$("tbody tr");
    for (SelenideElement row : rows) {
      ElementsCollection cells = row.$$("td");
      if (cells.size() > 1 && cells.get(1).getText().equals(username)) {
        cells.last().$("button").click();
        break;
      }
    }
    return this;
  }

  @Nonnull
  @Step("Asserting status of friendship request to user with username: {username}")
  public PeoplePage assertFriendshipRequestStatus(String username) {
    SelenideElement userRow = friendsTabPanel.$x(
      String.format(".//tr[.//td[text()='%s']]", username)
    );
    SelenideElement statusChip = userRow.$x(".//td[last()]//div[contains(@class, 'MuiChip-root')]");
    statusChip.should(exist).shouldHave(text("Waiting..."));
    return this;
  }

  @Nonnull
  @Step("Searching for income invitation from user with username: {username} in table Outcome Invitations")
  public PeoplePage findOutcomeInvitation(String username) {
    outcomeInvitationsTabPanel.$("input").val(username).pressEnter();
    return this;
  }

  @Nonnull
  @Step("Asserting income invitation from user with username: {username} exists in table Outcome Invitations")
  public PeoplePage assertOutcomeInvitationExists(String username) {
    outcomeInvitationsTabPanel.$$("tbody td").find(text(username))
      .should(visible);
    return this;
  }

  @Nonnull
  @Step("Searching for income invitation from user with username: {username} in table Income Invitations")
  public PeoplePage findIncomeInvitation(String username) {
    incomeInvitationsTabPanel.$("input").val(username).pressEnter();
    return this;
  }

  @Nonnull
  @Step("Asserting income invitation from user with username: {username} exists in table Income Invitations")
  public PeoplePage assertIncomeInvitationExists(String username) {
    incomeInvitationsTabPanel.$$("tbody td").find(text(username))
      .should(exist);
    return this;
  }

  @Nonnull
  @Step("Asserting income invitation from user with username: {username} exists in table Income Invitations")
  public PeoplePage assertIncomeInvitationNotExists(String username) {
    incomeInvitationsTabPanel.$$("tbody td").find(text(username))
      .should(not(visible));
    return this;
  }

  @Nonnull
  @Step("Changing status of income friendship request from user: {username} to status: {targetStatus}")
  public PeoplePage changeFriendshipRequestStatus(String username, FriendshipStatus targetStatus) {
    ElementsCollection rows = $$("table.MuiTable-root tbody tr");
    for (SelenideElement row : rows) {
      ElementsCollection cells = row.$$("td");
      if (cells.size() > 1 && cells.get(1).getText().equals(username)) {
        SelenideElement actionsCell = cells.last();
        switch (targetStatus) {
          case ACCEPTED:
            actionsCell.$$("button").findBy(text("Accept")).click();
            break;
          case DECLINED:
            actionsCell.$$("button").findBy(text("Decline")).click();
            break;
          default:
            throw new IllegalArgumentException("Unknown friendship status: " + targetStatus);
        }
        break;
      }
    }
    return this;
  }
}
