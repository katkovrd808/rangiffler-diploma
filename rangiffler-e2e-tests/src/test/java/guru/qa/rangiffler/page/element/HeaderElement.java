package guru.qa.rangiffler.page.element;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.page.WelcomePage;
import guru.qa.rangiffler.page.base.BaseElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class HeaderElement extends BaseElement<HeaderElement> {
  private final SelenideElement
    sideBarBtn = self.$("button[aria-label='open drawer']"),
    logoutBtn = $("button[aria-label='Logout']");

  public HeaderElement() {
    super($("#root header"));
  }

  @Nonnull
  @Step("Going to side bar from header")
  public SideBarElement openSideBarFromHeader() {
    sideBarBtn.click();
    return new SideBarElement();
  }

  @Nonnull
  @Step("Logging out from user profile")
  public WelcomePage logoutUserProfileFromHeader() {
    sideBarBtn.click();
    return new WelcomePage();
  }
}
