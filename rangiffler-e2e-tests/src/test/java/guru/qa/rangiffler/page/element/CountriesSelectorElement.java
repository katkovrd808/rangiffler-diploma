package guru.qa.rangiffler.page.element;

import guru.qa.rangiffler.page.base.BaseElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class CountriesSelectorElement extends BaseElement<CountriesSelectorElement> {

  public CountriesSelectorElement() {
    super($("ul[role='listbox']"));
  }

  @Nonnull
  @Step("Setting country: {country}")
  public CountriesSelectorElement setCountry(String country) {
    if (!country.matches("^[A-Z][a-zA-Z\\s-']+$")) {
      throw new IllegalArgumentException("");
    }
    self.$$("li").find(text(country)).click();
    return this;
  }
}
