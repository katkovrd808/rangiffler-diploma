package guru.qa.rangiffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.page.base.BasePage;
import guru.qa.rangiffler.page.element.CountriesSelectorElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class ProfilePage extends BasePage<ProfilePage> {
  private final CountriesSelectorElement countriesSelector = new CountriesSelectorElement();

  public final static String URL = CFG.frontUrl() + "profile";

  private final SelenideElement
    firstnameInput = $("#firstname"),
    surnameInput = $("#surname"),
    usernameInput = $("#username"),
    locationSelect = $("#location"),
    uploadProfilePhotoBtn = $("#root input#image__input"),
    saveBtn = $("#root button[type='submit']"),
    resetBtn = $$("form button[type='button']").find(text("Reset"));

  @Nonnull
  @Step("Asserting that Profile page loaded")
  public ProfilePage checkThatPageLoaded() {
    title.should(visible);
    return this;
  }

  @Nonnull
  @Step("Asserting that username input has value: {username} and input in disabled")
  public ProfilePage checkUsername(String username) {
    usernameInput.shouldHave(attribute("value", username))
      .shouldBe(disabled);
    return this;
  }

  @Nonnull
  @Step("Asserting that firstname input has value: {firstname}")
  public ProfilePage checkFirstname(String firstname) {
    firstnameInput.shouldHave(attribute("value", firstname));
    return this;
  }

  @Nonnull
  @Step("Asserting that surname input has value: {surname}")
  public ProfilePage checkSurname(String surname) {
    surnameInput.shouldHave(attribute("value", surname));
    return this;
  }

  @Nonnull
  @Step("Setting user firstname: {firstname}")
  public ProfilePage setFirstname(String firstname) {
    firstnameInput.val(firstname);
    return this;
  }

  @Nonnull
  @Step("Setting user surname: {surname}")
  public ProfilePage setSurname(String surname) {
    surnameInput.val(surname);
    return this;
  }

  @Nonnull
  @Step("Setting user country: {country}")
  public ProfilePage setUserLocation(String country) {
    locationSelect.click();
    countriesSelector.setCountry(country);
    return this;
  }

  @Nonnull
  @Step("Setting user profile photo from path: {path}")
  public ProfilePage setUserPhoto(String path) {
    uploadProfilePhotoBtn.uploadFromClasspath(path);
    saveBtn.click();
    return this;
  }

  @Nonnull
  @Step("Saving changes in user profile")
  public ProfilePage save() {
    saveBtn.click();
    return this;
  }

  @Nonnull
  @Step("Resetting form inputs to statement before any changes")
  public ProfilePage resetEnteredDataWithoutUpdatingProfile() {
    resetBtn.click();
    return this;
  }
}
