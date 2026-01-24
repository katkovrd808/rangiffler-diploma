package guru.qa.rangiffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.type;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class LoginPage {
  private final SelenideElement
    usernameInput = $("input[name='username']"),
    passwordInput = $("input[name='password']"),
    submitButton = $("button[type='submit']"),
    loginForm = $(".main__form form"),
    registrationButton = $(".main__form form a"),
    formError = $(".form__error");

  @Nonnull
  @Step("Logging in user profile")
  public LoginPage fillLoginPage(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    return this;
  }

  @Nonnull
  @Step("Submitting log in and asserting the error")
  public LoginPage submitAndCheckErrorText(String error) {
    submitButton.click();
    formError.shouldHave(text(error));
    return this;
  }

  @Nonnull
  @Step("Display password")
  public LoginPage showPassword() {
    loginForm.$$("label").find(text("Password"))
      .$("button").click();
    return this;
  }

  @Nonnull
  @Step("Asserting password input data type")
  public LoginPage checkPasswordInputType(boolean isShown) {
    final String type = isShown ? "text" : "password";

    loginForm.$$("label").find(text("Password"))
      .$("input").shouldHave(type(type));
    return this;
  }

  @Nonnull
  @Step("Going to registration page")
  public RegistrationPage openRegistrationPage() {
    registrationButton.click();
    return new RegistrationPage();
  }

  @Nonnull
  @Step("Logging in")
  public PhotoPage submit() {
    submitButton.click();
    return new PhotoPage();
  }
}
