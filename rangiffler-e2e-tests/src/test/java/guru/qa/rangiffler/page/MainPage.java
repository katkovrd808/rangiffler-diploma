package guru.qa.rangiffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.page.base.BasePage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage extends BasePage<MainPage> {

  private final SelenideElement statistics = $("");

  public final static String URL = CFG.frontUrl() + "my-travel";

  @Nonnull
  @Step("Asserting that My Travel page is loaded")
  public MainPage checkThatPageLoaded() {
    statistics.should(visible);
    return this;
  }
}
