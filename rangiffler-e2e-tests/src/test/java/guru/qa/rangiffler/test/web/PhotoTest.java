package guru.qa.rangiffler.test.web;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.page.PhotoPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.open;
import static guru.qa.rangiffler.utils.RandomDataUtils.*;

@Tags({@Tag("WEB")})
@ParametersAreNonnullByDefault
public class PhotoTest {

  private static final String DEFAULT_IMAGE_PATH = "img/cat.png";
  private static final String DEFAULT_COUNTRY = "Russian Federation";

  @Test
  @User
  @ApiLogin
  @DisplayName("User should be able to create photo with valid description")
  void userShouldBeAbleToCreatePhotoWithDescription() {
    open(PhotoPage.URL, PhotoPage.class)
      .checkThatPageLoaded()
      .createPhoto(DEFAULT_IMAGE_PATH, DEFAULT_COUNTRY, randomPhotoDescription())
      .assertPhotoExists(0);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("User should be able to create photo with valid description includes special numbers")
  void userShouldBeAbleToCreatePhotoWithDescriptionIncludeNumbers() {
    open(PhotoPage.URL, PhotoPage.class)
      .checkThatPageLoaded()
      .createPhoto(DEFAULT_IMAGE_PATH, DEFAULT_COUNTRY, randomPhotoDescription() + "12345")
      .assertPhotoExists(0);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("User should be able to create photo with valid description includes special characters")
  void userShouldBeAbleToCreatePhotoWithDescriptionIncludeSpecialCharacters() {
    open(PhotoPage.URL, PhotoPage.class)
      .checkThatPageLoaded()
      .createPhoto(DEFAULT_IMAGE_PATH, DEFAULT_COUNTRY, randomPhotoDescription() + "#$!@")
      .assertPhotoExists(0);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("User should be able to create photo with valid description includes spaces")
  void userShouldBeAbleToCreatePhotoWithDescriptionIncludeSpaces() {
    open(PhotoPage.URL, PhotoPage.class)
      .checkThatPageLoaded()
      .createPhoto(DEFAULT_IMAGE_PATH, DEFAULT_COUNTRY, "Test    description    with    spaces")
      .assertPhotoExists(0);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("User should be able to create photo without description")
  void userShouldBeAbleToCreatePhotoWithoutDescription() {
    open(PhotoPage.URL, PhotoPage.class)
      .checkThatPageLoaded()
      .createPhoto(DEFAULT_IMAGE_PATH, DEFAULT_COUNTRY, "")
      .assertPhotoExists(0);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Error should be displayed if user tries to create photo when the description length reaches limit")
  void errorShouldBeDisplayedIfDescriptionMoreThanLimit() {
    final String error = "Description length has to be not longer that 50 symbols";

    open(PhotoPage.URL, PhotoPage.class)
      .checkThatPageLoaded()
      .createPhotoWithError(DEFAULT_IMAGE_PATH, DEFAULT_COUNTRY, randomLongPhotoDescription())
      .assertDescriptionErrorHaveText(error);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Error should be displayed if user tries to create photo without uploading image")
  void errorShouldBeDisplayedIfPhotoNotUploaded() {
    final String error = "Please upload an image";

    open(PhotoPage.URL, PhotoPage.class)
      .checkThatPageLoaded()
      .createPhotoWithError(null, DEFAULT_COUNTRY, randomLongPhotoDescription())
      .assertImageErrorHaveText(error);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("User should be able to close modal before saving photo")
  void userShouldBeAbleToCloseModal() {
    open(PhotoPage.URL, PhotoPage.class)
      .checkThatPageLoaded()
      .openModal()
      .close();
  }
}
