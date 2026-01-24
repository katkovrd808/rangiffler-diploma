package guru.qa.rangiffler.utils;

import com.github.javafaker.Faker;

import javax.annotation.Nonnull;

public class RandomDataUtils {
  private static final Faker faker = new Faker();

  @Nonnull
  public static String randomUsername() {
    return faker.name().username();
  }

  @Nonnull
  public static String randomPassword() {
    return faker.internet().password();
  }

  @Nonnull
  public static String randomName() {
    return faker.name().firstName();
  }

  @Nonnull
  public static String randomSurname() {
    return faker.name().lastName();
  }

  @Nonnull
  public static String randomPhotoDescription() {
    return faker.lorem().sentence(1);
  }

  @Nonnull
  public static String randomLongPhotoDescription() {
    return faker.lorem().sentence(15);
  }

  @Nonnull
  public static String randomSentence(int wordsCount) {
    return faker.lorem().sentence(wordsCount);
  }
}
