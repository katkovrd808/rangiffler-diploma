package guru.qa.rangiffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.page.base.BasePage;
import guru.qa.rangiffler.page.element.PhotoModal;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public class PhotoPage extends BasePage<PhotoPage> {
  public final static String URL = CFG.frontUrl() + "my-travels";

  private final PhotoModal photoModal = new PhotoModal();

  private final SelenideElement
    feedTypeButtonsGroup = $("div[role='group']"),
    addPhotoBtn = $x("//button[contains(text(),'Add photo')]"),
    nextPageBtn = $("//button[contains(text(),'Next')]"),
    previousPageBtn = $("//button[contains(text(),'Previous')]"),
    photoGrid = $x("//main/div[2]/div[2]/div[1]");

  @Nonnull
  @Step("Asserting that My Travels page is loaded")
  public PhotoPage checkThatPageLoaded() {
    title.should(visible);
    return this;
  }

  @Nonnull
  @Step("Opening create photo modal")
  public PhotoModal openModal() {
    addPhotoBtn.click();
    return new PhotoModal();
  }

  @Nonnull
  @Step("Creating new photo with image: {path}, country: {country} and description: {description}")
  public PhotoPage createPhoto(String path, String country, String description) {
    addPhotoBtn.click();
    photoModal.uploadImage(path);
    photoModal.selectCountry(country);
    photoModal.setDescription(description);
    photoModal.save();
    return this;
  }

  @Nonnull
  @Step("Creating new photo with image: {path}, country: {country} and description: {description}")
  public PhotoModal createPhotoWithError(@Nullable String path, String country, String description) {
    addPhotoBtn.click();
    if (path != null) photoModal.uploadImage(path);
    photoModal.selectCountry(country);
    photoModal.setDescription(description);
    photoModal.save();
    return new PhotoModal();
  }

  @Nonnull
  @Step("Opening Only my Travels page section")
  public PhotoPage selectOnlyMyPhotos() {
    feedTypeButtonsGroup.$$("button").get(0).click();
    return this;
  }

  @Nonnull
  @Step("Opening With friends Travels page section")
  public PhotoPage selectPhotosWithFriends() {
    feedTypeButtonsGroup.$$("button").get(1).click();
    return this;
  }

  @Nonnull
  @Step("Asserting that photo exists")
  public PhotoPage assertPhotoExists(int index) {
    photoGrid.$$("div").get(index).shouldBe(visible);
    return this;
  }
}
