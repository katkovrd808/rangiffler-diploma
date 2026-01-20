package guru.qa.rangiffler.service;

import guru.qa.rangiffler.api.GrpcPhotosClient;
import guru.qa.rangiffler.api.GrpcUserdataClient;
import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.model.graphql.PageInfoGql;
import guru.qa.rangiffler.model.graphql.photos.*;
import guru.qa.rangiffler.service.impl.PhotoServiceImpl;
import guru.qa.rangiffler.service.mapper.FeedMapper;
import guru.qa.rangiffler.service.mapper.PhotoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PhotoServiceTest {
  @Mock
  private GrpcPhotosClient grpcPhotosClient;
  @Mock
  private GrpcUserdataClient grpcUserdataClient;
  @Mock
  private PhotoMapper photoMapper;
  @Mock
  private FeedMapper feedMapper;
  @Mock
  private Pageable pageable;

  private PhotoServiceImpl photoServiceImpl;

  @BeforeEach
  void setUp() {
    photoServiceImpl = new PhotoServiceImpl(grpcPhotosClient, grpcUserdataClient, photoMapper, feedMapper);
  }

  @Test
  void save_ShouldSavePhotoAndReturnPhotoGql() {
    final String username = "testuser";
    final String userId = UUID.randomUUID().toString();
    final PhotoInputGql photoInput = new PhotoInputGql(
      UUID.randomUUID(),
      new byte[]{},
      null,
      "Test description",
      null
    );
    final PhotoResponse photoResponse = PhotoResponse.newBuilder()
      .setId(UUID.randomUUID().toString())
      .build();
    final PhotoGql expectedPhotoGql = new PhotoGql(
      UUID.randomUUID(),
      null,
      "",
      null,
      "",
      null,
      null
    );

    when(grpcUserdataClient.findUser(username, null)).thenReturn(
      UserResponse.newBuilder().setId(userId).build()
    );
    when(grpcPhotosClient.addPhoto(userId, photoInput)).thenReturn(photoResponse);
    when(photoMapper.toPhotoGql(photoResponse, username)).thenReturn(expectedPhotoGql);

    final PhotoGql actualPhotoGql = photoServiceImpl.save(username, photoInput);

    verify(grpcUserdataClient).findUser(username, null);
    verify(grpcPhotosClient).addPhoto(userId, photoInput);
    verify(photoMapper).toPhotoGql(photoResponse, username);
    assertEquals(expectedPhotoGql, actualPhotoGql);
  }

  @Test
  void update_ShouldUpdatePhotoAndReturnPhotoGql() {
    final String username = "testuser";
    final String userId = UUID.randomUUID().toString();
    final PhotoInputGql photoInput = new PhotoInputGql(
      UUID.randomUUID(),
      new byte[]{},
      null,
      "Updated description",
      null
    );
    final PhotoResponse photoResponse = PhotoResponse.newBuilder()
      .setId(UUID.randomUUID().toString())
      .build();
    final PhotoGql expectedPhotoGql = new PhotoGql(
      UUID.randomUUID(),
      null,
      "",
      null,
      "",
      null,
      null
    );

    when(grpcUserdataClient.findUser(username, null)).thenReturn(
      UserResponse.newBuilder().setId(userId).build()
    );
    when(grpcPhotosClient.updatePhoto(userId, photoInput)).thenReturn(photoResponse);
    when(photoMapper.toPhotoGql(photoResponse, username)).thenReturn(expectedPhotoGql);

    final PhotoGql actualPhotoGql = photoServiceImpl.update(username, photoInput);

    verify(grpcUserdataClient).findUser(username, null);
    verify(grpcPhotosClient).updatePhoto(userId, photoInput);
    verify(photoMapper).toPhotoGql(photoResponse, username);
    assertEquals(expectedPhotoGql, actualPhotoGql);
  }

  @Test
  void updateWithLike_ShouldLikePhotoAndReturnPhotoGql() {
    final String username = "testuser";
    final String userId = UUID.randomUUID().toString();
    final PhotoInputGql photoInput = new PhotoInputGql(
      UUID.randomUUID(),
      null,
      null,
      null,
      new LikeInputGql(UUID.randomUUID())
    );
    final PhotoResponse photoResponse = PhotoResponse.newBuilder()
      .setId(UUID.randomUUID().toString())
      .build();
    final PhotoGql expectedPhotoGql = new PhotoGql(
      UUID.randomUUID(),
      null,
      "",
      null,
      "",
      null,
      null
    );

    when(grpcUserdataClient.findUser(username, null)).thenReturn(
      UserResponse.newBuilder().setId(userId).build()
    );
    when(grpcPhotosClient.likePhoto(userId, photoInput)).thenReturn(photoResponse);
    when(photoMapper.toPhotoGql(photoResponse, username)).thenReturn(expectedPhotoGql);

    final PhotoGql actualPhotoGql = photoServiceImpl.updateWithLike(username, photoInput);

    verify(grpcUserdataClient).findUser(username, null);
    verify(grpcPhotosClient).likePhoto(userId, photoInput);
    verify(photoMapper).toPhotoGql(photoResponse, username);
    assertEquals(expectedPhotoGql, actualPhotoGql);
  }

  @Test
  void delete_ShouldDeletePhotoAndReturnPhotoId() {
    final String username = "testuser";
    final String userId = UUID.randomUUID().toString();
    final String photoId = UUID.randomUUID().toString();
    final String expectedDeletedId = photoId;

    when(grpcUserdataClient.findUser(username, null)).thenReturn(
      UserResponse.newBuilder().setId(userId).build()
    );
    when(grpcPhotosClient.deletePhoto(userId, photoId)).thenReturn(expectedDeletedId);

    final String actualDeletedId = photoServiceImpl.delete(username, photoId);

    verify(grpcUserdataClient).findUser(username, null);
    verify(grpcPhotosClient).deletePhoto(userId, photoId);
    assertEquals(expectedDeletedId, actualDeletedId);
  }

  @Test
  void feed_ShouldReturnFeedWithFriends() {
    final String username = "testuser";
    final String userId = UUID.randomUUID().toString();
    final boolean withFriends = true;
    final FeedResponse feedResponse = FeedResponse.newBuilder()
      .setPhotos(FeedPhotos.newBuilder()
        .addPhotos(PhotoResponse.newBuilder()
          .setId(UUID.randomUUID().toString())
          .build())
        .build())
      .setStatistic(UserCountryStatistic.newBuilder().build())
      .build();
    final FeedGql expectedFeedGql = new FeedGql(
      username,
      withFriends,
      new PhotoSliceGql(List.of(), PageInfoGql.unpaged()),
      List.of()
    );

    when(grpcUserdataClient.findUser(username, null)).thenReturn(
      UserResponse.newBuilder().setId(userId).build()
    );
    when(grpcPhotosClient.getFeed(pageable, userId, withFriends)).thenReturn(feedResponse);
    when(feedMapper.toFeedGql(feedResponse, username, withFriends)).thenReturn(expectedFeedGql);

    final FeedGql actualFeedGql = photoServiceImpl.feed(pageable, username, withFriends);

    verify(grpcUserdataClient).findUser(username, null);
    verify(grpcPhotosClient).getFeed(pageable, userId, withFriends);
    verify(feedMapper).toFeedGql(feedResponse, username, withFriends);
    assertEquals(expectedFeedGql, actualFeedGql);
  }

  @Test
  void feed_ShouldReturnFeedWithoutFriends() {
    final String username = "testuser";
    final String userId = UUID.randomUUID().toString();
    final boolean withFriends = false;
    final FeedResponse feedResponse = FeedResponse.newBuilder()
      .setPhotos(FeedPhotos.newBuilder().build())
      .setStatistic(UserCountryStatistic.newBuilder().build())
      .build();
    final FeedGql expectedFeedGql = new FeedGql(
      username,
      withFriends,
      new PhotoSliceGql(List.of(), PageInfoGql.unpaged()),
      List.of()
    );

    when(grpcUserdataClient.findUser(username, null)).thenReturn(
      UserResponse.newBuilder().setId(userId).build()
    );
    when(grpcPhotosClient.getFeed(pageable, userId, withFriends)).thenReturn(feedResponse);
    when(feedMapper.toFeedGql(feedResponse, username, withFriends)).thenReturn(expectedFeedGql);

    final FeedGql actualFeedGql = photoServiceImpl.feed(pageable, username, withFriends);

    verify(grpcUserdataClient).findUser(username, null);
    verify(grpcPhotosClient).getFeed(pageable, userId, withFriends);
    verify(feedMapper).toFeedGql(feedResponse, username, withFriends);
    assertEquals(expectedFeedGql, actualFeedGql);
  }

  @Test
  void feed_WithPagedRequest_ShouldReturnPagedFeed() {
    final String username = "testuser";
    final String userId = UUID.randomUUID().toString();
    final boolean withFriends = true;
    final FeedResponse feedResponse = FeedResponse.newBuilder()
      .setPhotos(FeedPhotos.newBuilder()
        .setPaginationResponse(PaginationResponse.newBuilder()
          .setHasNext(true)
          .setHasPrevious(false)
          .build())
        .build())
      .setStatistic(UserCountryStatistic.newBuilder().build())
      .build();
    final FeedGql expectedFeedGql = new FeedGql(
      username,
      withFriends,
      new PhotoSliceGql(
        List.of(),
        new PageInfoGql(true, false)
      ),
      List.of()
    );

    when(grpcUserdataClient.findUser(username, null)).thenReturn(
      UserResponse.newBuilder().setId(userId).build()
    );
    when(grpcPhotosClient.getFeed(pageable, userId, withFriends)).thenReturn(feedResponse);
    when(feedMapper.toFeedGql(feedResponse, username, withFriends)).thenReturn(expectedFeedGql);

    final FeedGql actualFeedGql = photoServiceImpl.feed(pageable, username, withFriends);

    verify(grpcUserdataClient).findUser(username, null);
    verify(grpcPhotosClient).getFeed(pageable, userId, withFriends);
    verify(feedMapper).toFeedGql(feedResponse, username, withFriends);
    assertEquals(expectedFeedGql, actualFeedGql);
  }

  @Test
  void findUserIdByUsername_WithNullUsername_ShouldThrowException() {
    try {
      photoServiceImpl.save(null,
        new PhotoInputGql(null, null, null, null, null));
    } catch (IllegalArgumentException e) {
      assertEquals("Username can't be null value.", e.getMessage());
    }
  }

  @Test
  void save_WithEmptyPhotoInput_ShouldHandleGracefully() {
    final String username = "testuser";
    final String userId = UUID.randomUUID().toString();
    final PhotoInputGql emptyPhotoInput = new PhotoInputGql(null, null, null, null, null);
    final PhotoResponse photoResponse = PhotoResponse.newBuilder()
      .setId(UUID.randomUUID().toString())
      .build();
    final PhotoGql expectedPhotoGql = new PhotoGql(
      UUID.randomUUID(),
      null,
      "",
      null,
      "",
      null,
      null
    );

    when(grpcUserdataClient.findUser(username, null)).thenReturn(
      UserResponse.newBuilder().setId(userId).build()
    );
    when(grpcPhotosClient.addPhoto(userId, emptyPhotoInput)).thenReturn(photoResponse);
    when(photoMapper.toPhotoGql(photoResponse, username)).thenReturn(expectedPhotoGql);

    final PhotoGql actualPhotoGql = photoServiceImpl.save(username, emptyPhotoInput);

    verify(grpcUserdataClient).findUser(username, null);
    verify(grpcPhotosClient).addPhoto(userId, emptyPhotoInput);
    verify(photoMapper).toPhotoGql(photoResponse, username);
    assertEquals(expectedPhotoGql, actualPhotoGql);
  }

  @Test
  void update_WithOnlyDescription_ShouldUpdateOnlyDescription() {
    final String username = "testuser";
    final String userId = UUID.randomUUID().toString();
    final PhotoInputGql photoInput = new PhotoInputGql(
      UUID.randomUUID(),
      null,
      null,
      "New description only",
      null
    );
    final PhotoResponse photoResponse = PhotoResponse.newBuilder()
      .setId(UUID.randomUUID().toString())
      .build();
    final PhotoGql expectedPhotoGql = new PhotoGql(
      UUID.randomUUID(),
      null,
      "",
      null,
      "",
      null,
      null
    );

    when(grpcUserdataClient.findUser(username, null)).thenReturn(
      UserResponse.newBuilder().setId(userId).build()
    );
    when(grpcPhotosClient.updatePhoto(userId, photoInput)).thenReturn(photoResponse);
    when(photoMapper.toPhotoGql(photoResponse, username)).thenReturn(expectedPhotoGql);

    final PhotoGql actualPhotoGql = photoServiceImpl.update(username, photoInput);

    verify(grpcUserdataClient).findUser(username, null);
    verify(grpcPhotosClient).updatePhoto(userId, photoInput);
    verify(photoMapper).toPhotoGql(photoResponse, username);
    assertEquals(expectedPhotoGql, actualPhotoGql);
  }

  @Test
  void updateWithLike_WithNullLike_ShouldHandleGracefully() {
    final String username = "testuser";
    final String userId = UUID.randomUUID().toString();
    final PhotoInputGql photoInput = new PhotoInputGql(
      UUID.randomUUID(),
      null,
      null,
      null,
      null
    );
    final PhotoResponse photoResponse = PhotoResponse.newBuilder()
      .setId(UUID.randomUUID().toString())
      .build();
    final PhotoGql expectedPhotoGql = new PhotoGql(
      UUID.randomUUID(),
      null,
      "",
      null,
      "",
      null,
      null
    );

    when(grpcUserdataClient.findUser(username, null)).thenReturn(
      UserResponse.newBuilder().setId(userId).build()
    );
    when(grpcPhotosClient.likePhoto(userId, photoInput)).thenReturn(photoResponse);
    when(photoMapper.toPhotoGql(photoResponse, username)).thenReturn(expectedPhotoGql);

    final PhotoGql actualPhotoGql = photoServiceImpl.updateWithLike(username, photoInput);

    verify(grpcUserdataClient).findUser(username, null);
    verify(grpcPhotosClient).likePhoto(userId, photoInput);
    verify(photoMapper).toPhotoGql(photoResponse, username);
    assertEquals(expectedPhotoGql, actualPhotoGql);
  }

  @Test
  void delete_WithNonExistentPhoto_ShouldReturnEmptyString() {
    final String username = "testuser";
    final String userId = UUID.randomUUID().toString();
    final String photoId = UUID.randomUUID().toString();
    final String expectedDeletedId = "";

    when(grpcUserdataClient.findUser(username, null)).thenReturn(
      UserResponse.newBuilder().setId(userId).build()
    );
    when(grpcPhotosClient.deletePhoto(userId, photoId)).thenReturn(expectedDeletedId);

    final String actualDeletedId = photoServiceImpl.delete(username, photoId);

    verify(grpcUserdataClient).findUser(username, null);
    verify(grpcPhotosClient).deletePhoto(userId, photoId);
    assertEquals(expectedDeletedId, actualDeletedId);
  }

  @Test
  void feed_WithEmptyPhotos_ShouldReturnEmptyFeed() {
    final String username = "testuser";
    final String userId = UUID.randomUUID().toString();
    final boolean withFriends = false;
    final FeedResponse emptyFeedResponse = FeedResponse.newBuilder()
      .setPhotos(FeedPhotos.newBuilder().build())
      .setStatistic(UserCountryStatistic.newBuilder().build())
      .build();
    final FeedGql expectedFeedGql = new FeedGql(
      username,
      withFriends,
      new PhotoSliceGql(List.of(), PageInfoGql.unpaged()),
      List.of()
    );

    when(grpcUserdataClient.findUser(username, null)).thenReturn(
      UserResponse.newBuilder().setId(userId).build()
    );
    when(grpcPhotosClient.getFeed(pageable, userId, withFriends)).thenReturn(emptyFeedResponse);
    when(feedMapper.toFeedGql(emptyFeedResponse, username, withFriends)).thenReturn(expectedFeedGql);

    final FeedGql actualFeedGql = photoServiceImpl.feed(pageable, username, withFriends);

    verify(grpcUserdataClient).findUser(username, null);
    verify(grpcPhotosClient).getFeed(pageable, userId, withFriends);
    verify(feedMapper).toFeedGql(emptyFeedResponse, username, withFriends);
    assertEquals(expectedFeedGql, actualFeedGql);
  }

  @Test
  void feed_WithStatistics_ShouldReturnFeedWithStatistics() {
    final String username = "testuser";
    final String userId = UUID.randomUUID().toString();
    final boolean withFriends = true;
    final FeedResponse feedResponse = FeedResponse.newBuilder()
      .setPhotos(FeedPhotos.newBuilder().build())
      .setStatistic(UserCountryStatistic.newBuilder()
        .addCountries(CountryStatistic.newBuilder()
          .setCode("US")
          .setCount(5)
          .build())
        .build())
      .build();
    final FeedGql expectedFeedGql = new FeedGql(
      username,
      withFriends,
      new PhotoSliceGql(List.of(), PageInfoGql.unpaged()),
      List.of(new StatisticGql(
        5,
        new CountryStatGql("US")
      ))
    );

    when(grpcUserdataClient.findUser(username, null)).thenReturn(
      UserResponse.newBuilder().setId(userId).build()
    );
    when(grpcPhotosClient.getFeed(pageable, userId, withFriends)).thenReturn(feedResponse);
    when(feedMapper.toFeedGql(feedResponse, username, withFriends)).thenReturn(expectedFeedGql);

    final FeedGql actualFeedGql = photoServiceImpl.feed(pageable, username, withFriends);

    verify(grpcUserdataClient).findUser(username, null);
    verify(grpcPhotosClient).getFeed(pageable, userId, withFriends);
    verify(feedMapper).toFeedGql(feedResponse, username, withFriends);
    assertEquals(expectedFeedGql, actualFeedGql);
  }
}
