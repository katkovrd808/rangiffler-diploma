package guru.qa.rangiffler.page.base;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.page.element.SideBarElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class BasePage <T extends BasePage<?>>{

  protected final SideBarElement sideBar = new SideBarElement();
  protected final SelenideElement alert = $("#root [role='presentation']");

  protected static final Config CFG = Config.getInstance();

  @SuppressWarnings("unchecked")
  @Step("Asserting that alert should have text {text}")
  public T checkAlert(String text) {
    alert.shouldHave(text(text));
    return (T) this;
  }
}
