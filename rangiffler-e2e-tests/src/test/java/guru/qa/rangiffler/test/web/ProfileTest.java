package guru.qa.rangiffler.test.web;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.annotation.meta.WebTest;
import guru.qa.rangiffler.model.UdUserJson;
import guru.qa.rangiffler.page.ProfilePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.open;
import static guru.qa.rangiffler.utils.RandomDataUtils.randomName;
import static guru.qa.rangiffler.utils.RandomDataUtils.randomSurname;

@Tags({@Tag("WEB")})
@WebTest
@ParametersAreNonnullByDefault
public class ProfileTest {
  @Test
  @User
  @ApiLogin
  @DisplayName("Username input should have valid username and field should be disabled")
  void usernameInputShouldHaveValidUsernameAndShouldBeDisabledInUserProfile(UdUserJson user) {
    open(ProfilePage.URL, ProfilePage.class)
      .checkThatPageLoaded()
      .checkUsername(user.username());
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("User should be able to change personal data on profile page")
  void userShouldBeAbleToChangePersonalDataInProfile() {
    final String firstname = randomName();
    final String surname = randomSurname();

    open(ProfilePage.URL, ProfilePage.class)
      .checkThatPageLoaded()
      .setFirstname(firstname)
      .setSurname(surname)
      .save()
      .checkFirstname(firstname)
      .checkSurname(surname);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("User should be able to change location on profile page")
  void userShouldBeAbleToChangeLocationInProfile() {
    open(ProfilePage.URL, ProfilePage.class)
      .checkThatPageLoaded()
      .setUserLocation("Canada")
      .save();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("User should be able to change photo on profile page")
  void userShouldBeAbleToChangePhotoInProfile() {
    open(ProfilePage.URL, ProfilePage.class)
      .checkThatPageLoaded()
      .setUserPhoto("img/cat.png");
  }
}
