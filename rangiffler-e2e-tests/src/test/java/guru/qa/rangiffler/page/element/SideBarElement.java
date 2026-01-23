package guru.qa.rangiffler.page.element;

import com.codeborne.selenide.ElementsCollection;
import guru.qa.rangiffler.page.ProfilePage;
import guru.qa.rangiffler.page.base.BaseElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Selenide.$$;

public class SideBarElement extends BaseElement<SideBarElement> {
  private final ElementsCollection sideBarItems = self.$("ul").$$("li");

  public SideBarElement() {
    super($$("#root .MuiDrawer-root div").get(0));
  }

  @Nonnull
  @Step("Going to Profile page from side bar")
  public ProfilePage openProfilePageFromSideBar() {
    sideBarItems.get(0).click();
    return new ProfilePage();
  }
}
