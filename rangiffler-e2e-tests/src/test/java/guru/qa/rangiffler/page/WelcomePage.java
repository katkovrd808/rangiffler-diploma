package guru.qa.rangiffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.page.base.BasePage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class WelcomePage extends BasePage<WelcomePage> {
  private final SelenideElement
    title = $("#root h2"),
    loginBtn = $("#root button"),
    registrationBtn = $("#root a");

  @Nonnull
  @Step("Asserting that Welcome page is loaded")
  public WelcomePage checkThatPageLoaded() {
    title.shouldBe(visible);
    return this;
  }

  @Nonnull
  @Step("Going to login page from welcome page")
  public LoginPage openLoginPage() {
    loginBtn.click();
    return new LoginPage();
  }

  @Nonnull
  @Step("Going to login page from welcome page")
  public RegistrationPage openRegistrationPage() {
    registrationBtn.click();
    return new RegistrationPage();
  }
}
