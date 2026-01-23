package guru.qa.rangiffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.page.base.BasePage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage extends BasePage<MainPage> {

  private final SelenideElement title = $("#root h2");

  public final static String URL = CFG.frontUrl() + "my-travels";

  @Nonnull
  @Step("Asserting that My Travels page is loaded")
  public MainPage checkThatPageLoaded() {
    title.should(visible);
    return this;
  }
}
