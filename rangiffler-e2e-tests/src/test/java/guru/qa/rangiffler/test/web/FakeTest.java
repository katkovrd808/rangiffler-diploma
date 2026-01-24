package guru.qa.rangiffler.test.web;

import guru.qa.rangiffler.jupiter.annotation.Photo;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.UdUserJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import javax.annotation.ParametersAreNonnullByDefault;

@Tags({@Tag("WEB")})
@ParametersAreNonnullByDefault
public class FakeTest {
  @Test
  @User(
    photos = @Photo(
      withDescription = true,
      countryCode = "US"
    )
  )
  void fakeTest(UdUserJson user) {
    System.out.println(user.username());
    Assertions.assertNotNull(user.testData().photos());
  }
}