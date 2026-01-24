package guru.qa.rangiffler.page.element;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.page.PhotoPage;
import guru.qa.rangiffler.page.base.BaseElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class PhotoModal extends BaseElement<PhotoModal> {
  private final CountriesSelectorElement countriesSelector = new CountriesSelectorElement();

  private final SelenideElement
    imageInput = $("#image__input"),
    countrySelect = $("#country"),
    descriptionInput = $("#description"),
    saveBtn = $("button[type='submit']"),
    closeBtn = $("button[type='submit'] + button"),
    imageError = $("label[for='image__input'] + div"),
    descriptionError = $("#description-helper-text");

  public PhotoModal() {
    super($("div[role='dialog']"));
  }

  @Nonnull
  @Step("Uploading image with path: {path}")
  public PhotoModal uploadImage(String path) {
    imageInput.uploadFromClasspath(path);
    return this;
  }

  @Nonnull
  @Step("Setting country: {country} for photo")
  public PhotoModal selectCountry(String country) {
    countrySelect.click();
    try {
      countriesSelector.setCountry(country);
    } catch (Exception e) {
      throw new IllegalArgumentException("Country with name: " + country + " was not found.");
    }
    return this;
  }

  @Nonnull
  @Step("Setting description: {description} for photo")
  public PhotoModal setDescription(String description) {
    descriptionInput.val(description);
    return this;
  }

  @Nonnull
  @Step("Asserting image error text is equal to: {error}")
  public PhotoModal assertImageErrorHaveText(String error) {
    imageError.shouldHave(text(error));
    return this;
  }

  @Nonnull
  @Step("Asserting description error text is equal to: {error}")
  public PhotoModal assertDescriptionErrorHaveText(String error) {
    descriptionError.shouldHave(text(error));
    return this;
  }

  @Nonnull
  @Step("Saving new photo")
  public PhotoPage save() {
    saveBtn.click();
    return new PhotoPage();
  }

  @Nonnull
  @Step("Closing photo modal")
  public PhotoPage close() {
    closeBtn.click();
    return new PhotoPage();
  }
}
