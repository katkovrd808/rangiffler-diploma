package guru.qa.rangiffler.page.element;

import guru.qa.rangiffler.page.base.BaseElement;

import static com.codeborne.selenide.Selenide.$;

public class SideBarElement extends BaseElement<SideBarElement> {

  public SideBarElement() {
    super($(""));
  }
}
