package guru.qa.rangiffler.test.web;

import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.UdUserJson;
import guru.qa.rangiffler.page.LoginPage;
import guru.qa.rangiffler.page.WelcomePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.open;
import static guru.qa.rangiffler.utils.RandomDataUtils.randomPassword;
import static guru.qa.rangiffler.utils.RandomDataUtils.randomUsername;

@Tags({@Tag("WEB")})
@ParametersAreNonnullByDefault
public class LoginTest {
  private static final String FRONT_URL = Config.getInstance().frontUrl();
  private static final String CREDENTIALS_ERROR_TEXT = "Bad credentials";

  @User
  @Test
  @DisplayName("Main page should be present after successful login")
  void mainPageShouldBeDisplayedAfterSuccessLogin(UdUserJson user) {
    open(FRONT_URL, WelcomePage.class)
      .openLoginPage()
      .fillLoginPage(user.username(), user.testData().password())
      .submit()
      .checkThatPageLoaded();
  }

  @Test
  @DisplayName("Unregistered user should get error when trying to login")
  void errorShouldBeShownIfUserNotRegistered(){
    open(FRONT_URL, LoginPage.class)
      .fillLoginPage(randomUsername(), "12345")
      .submitAndCheckErrorText(CREDENTIALS_ERROR_TEXT);
  }

  @User
  @Test
  @DisplayName("Error should be present if user trying to login with incorrect password")
  void errorShouldBeShownWithIncorrectRegisteredUserPassword(UdUserJson user){
    open(FRONT_URL, WelcomePage.class)
      .openLoginPage()
      .fillLoginPage(user.username(), randomPassword())
      .submitAndCheckErrorText(CREDENTIALS_ERROR_TEXT);
  }

  @User
  @Test
  @DisplayName("Password should be visible after changing visibility")
  void passwordShouldBeShownAfterVisibilityChanging(UdUserJson user){
    open(FRONT_URL, WelcomePage.class)
      .openLoginPage()
      .fillLoginPage(user.username(), user.testData().password())
      .showPassword()
      .checkPasswordInputType(true);
  }
}