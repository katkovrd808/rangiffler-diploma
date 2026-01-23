package guru.qa.rangiffler.test.web;

import com.github.javafaker.Faker;
import guru.qa.rangiffler.page.RegistrationPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.open;
import static guru.qa.rangiffler.utils.RandomDataUtils.randomUsername;

@Tags({@Tag("WEB")})
@ParametersAreNonnullByDefault
public class RegistrationTest {
  private final Faker faker = new Faker();
  private static final String DEFAULT_PASSWORD = "12345";

  @Test
  @DisplayName("User should be registered with valid username and password")
  void userWithUniqueUsernameAndValidPasswordShouldBeRegistered() {
    open(RegistrationPage.URL, RegistrationPage.class)
      .fillRegistrationForm(randomUsername(), DEFAULT_PASSWORD, DEFAULT_PASSWORD)
      .submitRegistration()
      .checkSucceedRegistrationPageTitle();
  }

  @Test
  @DisplayName("User should be registered with valid uppercase username and password")
  void userWithUpperCaseUsernameShouldBeRegistered() {
    open(RegistrationPage.URL, RegistrationPage.class)
      .fillRegistrationForm(randomUsername().toUpperCase(), DEFAULT_PASSWORD, DEFAULT_PASSWORD)
      .submitRegistration()
      .checkSucceedRegistrationPageTitle();
  }

  @Test
  @DisplayName("User should be registered with valid username which contains digits and password")
  void userUsernameContainsDigitsShouldBeRegistered() {
    open(RegistrationPage.URL, RegistrationPage.class)
      .fillRegistrationForm(randomUsername() + "1", DEFAULT_PASSWORD, DEFAULT_PASSWORD)
      .submitRegistration()
      .checkSucceedRegistrationPageTitle();
  }

  @Test
  @DisplayName("User can open login page and login to account after registration")
  void userCanMakeLoginAfterRegistration() {
    final String username = randomUsername();

    open(RegistrationPage.URL, RegistrationPage.class)
      .fillRegistrationForm(username, DEFAULT_PASSWORD, DEFAULT_PASSWORD)
      .submitRegistration()
      .checkSucceedRegistrationPageTitle()
      .loginAfterRegistration()
      .openLoginPage()
      .fillLoginPage(username, DEFAULT_PASSWORD)
      .submit()
      .checkThatPageLoaded();
  }

  @Test
  @DisplayName("User with short username should be not registered")
  void userShouldBeNotRegisteredIfUsernameIsShort() {
    open(RegistrationPage.URL, RegistrationPage.class)
      .fillRegistrationForm("us", DEFAULT_PASSWORD, DEFAULT_PASSWORD)
      .submitRegistrationAndCheckUsernameError("Allowed username length should be from 3 to 50 characters");
  }

  @Test
  @DisplayName("User with username more than 50 characters should be not registered")
  void userShouldBeNotRegisteredIfUsernameIsLong() {
    final String username = faker.lorem().sentence(20)
      .replaceAll("\\s", "");

    open(RegistrationPage.URL, RegistrationPage.class)
      .fillRegistrationForm(username, DEFAULT_PASSWORD, DEFAULT_PASSWORD)
      .submitRegistrationAndCheckUsernameError("Allowed username length should be from 3 to 50 characters");
  }

  @Test
  @DisplayName("User with blank username should be not registered")
  void userShouldBeNotRegisteredIfUsernameIsBlank() {
    open(RegistrationPage.URL, RegistrationPage.class)
      .fillRegistrationForm("     ", DEFAULT_PASSWORD, DEFAULT_PASSWORD)
      .submitRegistrationAndCheckUsernameError("Username can not be blank");
  }

  @Test
  @DisplayName("User with username contains whitespaces should be not registered")
  void userShouldBeNotRegisteredIfUsernameContainsWhitespaces() {
    final String username = "test username";

    open(RegistrationPage.URL, RegistrationPage.class)
      .fillRegistrationForm(username, DEFAULT_PASSWORD, DEFAULT_PASSWORD)
      .submitRegistrationAndCheckUsernameError("Username must not contain whitespaces");
  }

  @Test
  @DisplayName("Error should be present if passwords don't match")
  void errorShouldBeShownWhenPasswordsNotMatch() {
    open(RegistrationPage.URL, RegistrationPage.class)
      .fillRegistrationForm(randomUsername(), DEFAULT_PASSWORD, DEFAULT_PASSWORD + "1")
      .submitRegistrationAndCheckPasswordError("Passwords should be equal");
  }

  @Test
  @DisplayName("Error should be present if password shorter than 3 characters")
  void errorShouldBeShownWhenPasswordTooShort() {
    final String password = "12";

    open(RegistrationPage.URL, RegistrationPage.class)
      .fillRegistrationForm(randomUsername(), password, password)
      .submitRegistrationAndCheckPasswordError("Allowed password length should be from 3 to 12 characters");
  }

  @Test
  @DisplayName("Error should be present if password longer than 12 characters")
  void errorShouldBeShownWhenPasswordTooLong() {
    final String password = "1234567890123";

    open(RegistrationPage.URL, RegistrationPage.class)
      .fillRegistrationForm(randomUsername(), password, password)
      .submitRegistrationAndCheckPasswordError("Allowed password length should be from 3 to 12 characters");
  }

}
