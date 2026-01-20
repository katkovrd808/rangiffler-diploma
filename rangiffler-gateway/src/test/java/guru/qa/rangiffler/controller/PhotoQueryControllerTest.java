package guru.qa.rangiffler.controller;

import guru.qa.rangiffler.controller.photos.PhotoQueryController;
import guru.qa.rangiffler.model.graphql.photos.FeedGql;
import guru.qa.rangiffler.service.PhotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PhotoQueryControllerTest {
  @Mock
  private PhotoService photoService;
  @Mock
  private Jwt jwt;

  private PhotoQueryController photoQueryController;

  @BeforeEach
  void setUp() {
    photoQueryController = new PhotoQueryController(photoService);
  }

  @Test
  void feed_ShouldReturnFeedForAuthenticatedUser() {
    final String username = "testuser";
    final int page = 0;
    final int size = 10;
    final boolean withFriends = true;
    final FeedGql expectedFeed = new FeedGql(
      username,
      withFriends,
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(username);
    when(photoService.feed(PageRequest.of(page, size), username, withFriends)).thenReturn(expectedFeed);

    final FeedGql actualFeed = photoQueryController.feed(jwt, page, size, withFriends);

    verify(photoService).feed(PageRequest.of(page, size), username, withFriends);
    assertEquals(expectedFeed, actualFeed);
  }

  @Test
  void feed_WithFriendsFalse_ShouldReturnFeedWithoutFriends() {
    final String username = "testuser";
    final int page = 1;
    final int size = 20;
    final boolean withFriends = false;
    final FeedGql expectedFeed = new FeedGql(
      username,
      withFriends,
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(username);
    when(photoService.feed(PageRequest.of(page, size), username, withFriends)).thenReturn(expectedFeed);

    final FeedGql actualFeed = photoQueryController.feed(jwt, page, size, withFriends);

    verify(photoService).feed(PageRequest.of(page, size), username, withFriends);
    assertEquals(expectedFeed, actualFeed);
  }

  @Test
  void feed_WithPageZeroSizeFive_ShouldReturnPagedFeed() {
    final String username = "testuser";
    final int page = 0;
    final int size = 5;
    final boolean withFriends = true;
    final FeedGql expectedFeed = new FeedGql(
      username,
      withFriends,
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(username);
    when(photoService.feed(PageRequest.of(page, size), username, withFriends)).thenReturn(expectedFeed);

    final FeedGql actualFeed = photoQueryController.feed(jwt, page, size, withFriends);

    verify(photoService).feed(PageRequest.of(page, size), username, withFriends);
    assertEquals(expectedFeed, actualFeed);
  }

  @Test
  void feed_WithLargePageNumber_ShouldHandleGracefully() {
    final String username = "testuser";
    final int page = 100;
    final int size = 10;
    final boolean withFriends = true;
    final FeedGql expectedFeed = new FeedGql(
      username,
      withFriends,
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(username);
    when(photoService.feed(PageRequest.of(page, size), username, withFriends)).thenReturn(expectedFeed);

    final FeedGql actualFeed = photoQueryController.feed(jwt, page, size, withFriends);

    verify(photoService).feed(PageRequest.of(page, size), username, withFriends);
    assertEquals(expectedFeed, actualFeed);
  }

  @Test
  void feed_WithNullJwt_ShouldThrowException() {
    final int page = 0;
    final int size = 10;
    final boolean withFriends = true;

    assertThrows(NullPointerException.class, () ->
      photoQueryController.feed(null, page, size, withFriends)
    );
  }

  @Test
  void feed_WithEmptyUsername_ShouldHandleGracefully() {
    final String username = "";
    final int page = 0;
    final int size = 10;
    final boolean withFriends = true;
    final FeedGql expectedFeed = new FeedGql(
      username,
      withFriends,
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(username);
    when(photoService.feed(PageRequest.of(page, size), username, withFriends)).thenReturn(expectedFeed);

    final FeedGql actualFeed = photoQueryController.feed(jwt, page, size, withFriends);

    verify(photoService).feed(PageRequest.of(page, size), username, withFriends);
    assertEquals(expectedFeed, actualFeed);
  }

  @Test
  void feed_ShouldUseCorrectPageRequestParameters() {
    final String username = "testuser";
    final int page = 2;
    final int size = 15;
    final boolean withFriends = true;
    final FeedGql expectedFeed = new FeedGql(
      username,
      withFriends,
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(username);
    when(photoService.feed(PageRequest.of(page, size), username, withFriends)).thenReturn(expectedFeed);

    final FeedGql actualFeed = photoQueryController.feed(jwt, page, size, withFriends);

    verify(photoService).feed(PageRequest.of(page, size), username, withFriends);
    assertEquals(expectedFeed, actualFeed);
  }

  @Test
  void feed_ShouldReturnSameInstanceFromService() {
    final String username = "testuser";
    final int page = 0;
    final int size = 10;
    final boolean withFriends = true;
    final FeedGql expectedFeed = new FeedGql(
      username,
      withFriends,
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(username);
    when(photoService.feed(PageRequest.of(page, size), username, withFriends)).thenReturn(expectedFeed);

    final FeedGql actualFeed = photoQueryController.feed(jwt, page, size, withFriends);

    verify(photoService).feed(PageRequest.of(page, size), username, withFriends);
    assertEquals(expectedFeed, actualFeed);
  }

  @Test
  void feed_WithDifferentUsers_ShouldReturnDifferentFeeds() {
    final String username1 = "user1";
    final String username2 = "user2";
    final int page = 0;
    final int size = 10;
    final boolean withFriends = true;
    final FeedGql expectedFeed1 = new FeedGql(
      username1,
      withFriends,
      null,
      null
    );
    final FeedGql expectedFeed2 = new FeedGql(
      username2,
      withFriends,
      null,
      null
    );

    final Jwt jwt1 = mock(Jwt.class);
    final Jwt jwt2 = mock(Jwt.class);

    when(jwt1.getClaim("sub")).thenReturn(username1);
    when(jwt2.getClaim("sub")).thenReturn(username2);
    when(photoService.feed(PageRequest.of(page, size), username1, withFriends)).thenReturn(expectedFeed1);
    when(photoService.feed(PageRequest.of(page, size), username2, withFriends)).thenReturn(expectedFeed2);

    final FeedGql actualFeed1 = photoQueryController.feed(jwt1, page, size, withFriends);
    final FeedGql actualFeed2 = photoQueryController.feed(jwt2, page, size, withFriends);

    verify(photoService).feed(PageRequest.of(page, size), username1, withFriends);
    verify(photoService).feed(PageRequest.of(page, size), username2, withFriends);
    assertEquals(expectedFeed1, actualFeed1);
    assertEquals(expectedFeed2, actualFeed2);
  }
}
