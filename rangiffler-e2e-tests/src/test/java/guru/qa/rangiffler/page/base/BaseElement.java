package guru.qa.rangiffler.page.base;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

public class BaseElement <T extends BaseElement<?>>{

  @Getter
  protected final SelenideElement self;

  public BaseElement(SelenideElement self) {
    this.self = self;
  }
}
