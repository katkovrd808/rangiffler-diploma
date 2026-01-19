package guru.qa.rangiffler.service;

import guru.qa.rangiffler.api.GrpcCountriesClient;
import guru.qa.rangiffler.data.StatisticEntity;
import guru.qa.rangiffler.grpc.FeedPhotos;
import guru.qa.rangiffler.grpc.FeedRequest;
import guru.qa.rangiffler.grpc.FeedResponse;
import guru.qa.rangiffler.grpc.PhotoResponse;
import guru.qa.rangiffler.model.CountryDto;
import guru.qa.rangiffler.service.impl.FeedServiceImpl;
import guru.qa.rangiffler.service.mapper.FeedMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

  @Mock
  private PhotoService photoService;

  @Mock
  private StatisticService statisticService;

  @Mock
  private GrpcCountriesClient grpcCountriesClient;

  @Mock
  private FeedMapper feedMapper;

  @Mock
  private Pageable pageable;

  private FeedServiceImpl feedServiceImpl;

  @BeforeEach
  void setUp() {
    feedServiceImpl = new FeedServiceImpl(
      photoService, statisticService, grpcCountriesClient, feedMapper
    );
  }

  @Test
  void getFeed_WithFriends_ShouldReturnFriendsFeed() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .setWithFriends(true)
      .build();

    final List<PhotoResponse> photos = List.of(
      PhotoResponse.newBuilder().setId(UUID.randomUUID().toString()).build(),
      PhotoResponse.newBuilder().setId(UUID.randomUUID().toString()).build()
    );
    final Page<PhotoResponse> photoPage = new PageImpl<>(photos);

    final UUID countryId1 = UUID.randomUUID();
    final UUID countryId2 = UUID.randomUUID();
    final List<StatisticEntity> statisticEntities = List.of(
      createStatisticEntity(userId, countryId1, 5),
      createStatisticEntity(userId, countryId2, 3)
    );

    final CountryDto countryDto1 = new CountryDto(countryId1, "Country1", "C1", new byte[]{}, 0);
    final CountryDto countryDto2 = new CountryDto(countryId2, "Country2", "C2", new byte[]{}, 0);

    final FeedResponse expectedResponse = FeedResponse.newBuilder()
      .setPhotos(FeedPhotos.newBuilder().addAllPhotos(photos).build())
      .build();

    when(photoService.getFriendsPhotos(request, pageable)).thenReturn(photoPage);
    when(statisticService.getUserCountriesStatisticWithFriends(request)).thenReturn(statisticEntities);

    when(grpcCountriesClient.getCountryById(anyString()))
      .thenAnswer(invocation -> {
        String countryId = invocation.getArgument(0);
        if (countryId.equals(countryId1.toString())) {
          return Optional.of(countryDto1);
        } else if (countryId.equals(countryId2.toString())) {
          return Optional.of(countryDto2);
        }
        return Optional.empty();
      });

    when(feedMapper.toProto(eq(photoPage), anyList())).thenReturn(expectedResponse);

    final FeedResponse actualResponse = feedServiceImpl.getFeed(request, pageable);

    verify(photoService).getFriendsPhotos(request, pageable);
    verify(statisticService).getUserCountriesStatisticWithFriends(request);
    verify(statisticService, never()).getUserCountriesStatistic(any());
    verify(grpcCountriesClient, atLeastOnce()).getCountryById(anyString());
    verify(feedMapper).toProto(eq(photoPage), anyList());
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void getFeed_WithoutFriends_ShouldReturnUserFeed() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .setWithFriends(false)
      .build();

    final List<PhotoResponse> photos = List.of(
      PhotoResponse.newBuilder().setId(UUID.randomUUID().toString()).build()
    );
    final Page<PhotoResponse> photoPage = new PageImpl<>(photos);

    final UUID countryId = UUID.randomUUID();
    final List<StatisticEntity> statisticEntities = List.of(
      createStatisticEntity(userId, countryId, 10)
    );

    final CountryDto countryDto = new CountryDto(countryId, "Country", "C", new byte[]{}, 0);

    final FeedResponse expectedResponse = FeedResponse.newBuilder()
      .setPhotos(FeedPhotos.newBuilder().addAllPhotos(photos).build())
      .build();

    when(photoService.getUserPhotos(request, pageable)).thenReturn(photoPage);
    when(statisticService.getUserCountriesStatistic(request)).thenReturn(statisticEntities);

    when(grpcCountriesClient.getCountryById(anyString())).thenReturn(Optional.of(countryDto));

    when(feedMapper.toProto(eq(photoPage), anyList())).thenReturn(expectedResponse);

    final FeedResponse actualResponse = feedServiceImpl.getFeed(request, pageable);

    verify(photoService).getUserPhotos(request, pageable);
    verify(statisticService).getUserCountriesStatistic(request);
    verify(statisticService, never()).getUserCountriesStatisticWithFriends(any());
    verify(grpcCountriesClient).getCountryById(anyString());
    verify(feedMapper).toProto(eq(photoPage), anyList());
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void getFeed_WithEmptyStatistic_ShouldReturnEmptyCountries() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .setWithFriends(true)
      .build();

    final List<PhotoResponse> photos = List.of(
      PhotoResponse.newBuilder().setId(UUID.randomUUID().toString()).build()
    );
    final Page<PhotoResponse> photoPage = new PageImpl<>(photos);

    final List<StatisticEntity> emptyStatistic = List.of();

    final FeedResponse expectedResponse = FeedResponse.newBuilder()
      .setPhotos(FeedPhotos.newBuilder().addAllPhotos(photos).build())
      .build();

    when(photoService.getFriendsPhotos(request, pageable)).thenReturn(photoPage);
    when(statisticService.getUserCountriesStatisticWithFriends(request)).thenReturn(emptyStatistic);
    when(feedMapper.toProto(eq(photoPage), eq(List.of()))).thenReturn(expectedResponse);

    final FeedResponse actualResponse = feedServiceImpl.getFeed(request, pageable);

    verify(photoService).getFriendsPhotos(request, pageable);
    verify(statisticService).getUserCountriesStatisticWithFriends(request);
    verify(grpcCountriesClient, never()).getCountryById(anyString());
    verify(feedMapper).toProto(eq(photoPage), eq(List.of()));
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void getFeed_WhenCountryNotFound_ShouldThrowException() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .setWithFriends(false)
      .build();

    final List<PhotoResponse> photos = List.of(
      PhotoResponse.newBuilder().setId(UUID.randomUUID().toString()).build()
    );
    final Page<PhotoResponse> photoPage = new PageImpl<>(photos);

    final UUID countryId = UUID.randomUUID();
    final List<StatisticEntity> statisticEntities = List.of(
      createStatisticEntity(userId, countryId, 5)
    );

    when(photoService.getUserPhotos(request, pageable)).thenReturn(photoPage);
    when(statisticService.getUserCountriesStatistic(request)).thenReturn(statisticEntities);
    when(grpcCountriesClient.getCountryById(anyString())).thenReturn(Optional.empty());

    assertThrows(
      RuntimeException.class,
      () -> feedServiceImpl.getFeed(request, pageable)
    );

    verify(photoService).getUserPhotos(request, pageable);
    verify(statisticService).getUserCountriesStatistic(request);
    verify(grpcCountriesClient).getCountryById(anyString());
    verify(feedMapper, never()).toProto(any(), any());
  }

  @Test
  void getFeed_WithFriendsAndMultipleCountries_ShouldReturnCorrectStatistics() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .setWithFriends(true)
      .build();

    final List<PhotoResponse> photos = List.of(
      PhotoResponse.newBuilder().setId(UUID.randomUUID().toString()).build(),
      PhotoResponse.newBuilder().setId(UUID.randomUUID().toString()).build(),
      PhotoResponse.newBuilder().setId(UUID.randomUUID().toString()).build()
    );
    final Page<PhotoResponse> photoPage = new PageImpl<>(photos);

    final UUID countryId1 = UUID.randomUUID();
    final UUID countryId2 = UUID.randomUUID();
    final UUID countryId3 = UUID.randomUUID();
    final List<StatisticEntity> statisticEntities = List.of(
      createStatisticEntity(userId, countryId1, 15),
      createStatisticEntity(userId, countryId2, 8),
      createStatisticEntity(userId, countryId3, 3)
    );

    final CountryDto countryDto1 = new CountryDto(countryId1, "Country1", "C1", new byte[]{}, 0);
    final CountryDto countryDto2 = new CountryDto(countryId2, "Country2", "C2", new byte[]{}, 0);
    final CountryDto countryDto3 = new CountryDto(countryId3, "Country3", "C3", new byte[]{}, 0);

    final FeedResponse expectedResponse = FeedResponse.newBuilder()
      .setPhotos(FeedPhotos.newBuilder().addAllPhotos(photos).build())
      .build();

    when(photoService.getFriendsPhotos(request, pageable)).thenReturn(photoPage);
    when(statisticService.getUserCountriesStatisticWithFriends(request)).thenReturn(statisticEntities);

    when(grpcCountriesClient.getCountryById(anyString()))
      .thenAnswer(invocation -> {
        String countryId = invocation.getArgument(0);
        if (countryId.equals(countryId1.toString())) {
          return Optional.of(countryDto1);
        } else if (countryId.equals(countryId2.toString())) {
          return Optional.of(countryDto2);
        } else if (countryId.equals(countryId3.toString())) {
          return Optional.of(countryDto3);
        }
        return Optional.empty();
      });

    when(feedMapper.toProto(eq(photoPage), anyList())).thenReturn(expectedResponse);

    final FeedResponse actualResponse = feedServiceImpl.getFeed(request, pageable);

    verify(photoService).getFriendsPhotos(request, pageable);
    verify(statisticService).getUserCountriesStatisticWithFriends(request);
    verify(grpcCountriesClient, times(3)).getCountryById(anyString());
    verify(feedMapper).toProto(eq(photoPage), anyList());
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void getFeed_WithEmptyPhotoPage_ShouldReturnEmptyFeed() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .setWithFriends(false)
      .build();

    final Page<PhotoResponse> emptyPhotoPage = Page.empty();

    final UUID countryId = UUID.randomUUID();
    final List<StatisticEntity> statisticEntities = List.of(
      createStatisticEntity(userId, countryId, 5)
    );

    final CountryDto countryDto = new CountryDto(countryId, "Country", "C", new byte[]{}, 0);

    final FeedResponse expectedResponse = FeedResponse.newBuilder().build();

    when(photoService.getUserPhotos(request, pageable)).thenReturn(emptyPhotoPage);
    when(statisticService.getUserCountriesStatistic(request)).thenReturn(statisticEntities);
    when(grpcCountriesClient.getCountryById(anyString())).thenReturn(Optional.of(countryDto));

    when(feedMapper.toProto(eq(emptyPhotoPage), anyList())).thenReturn(expectedResponse);

    final FeedResponse actualResponse = feedServiceImpl.getFeed(request, pageable);

    verify(photoService).getUserPhotos(request, pageable);
    verify(statisticService).getUserCountriesStatistic(request);
    verify(grpcCountriesClient).getCountryById(anyString());
    verify(feedMapper).toProto(eq(emptyPhotoPage), anyList());
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void getFeed_WithZeroCountInStatistic_ShouldHandleCorrectly() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .setWithFriends(true)
      .build();

    final List<PhotoResponse> photos = List.of(
      PhotoResponse.newBuilder().setId(UUID.randomUUID().toString()).build()
    );
    final Page<PhotoResponse> photoPage = new PageImpl<>(photos);

    final UUID countryId = UUID.randomUUID();
    final List<StatisticEntity> statisticEntities = List.of(
      createStatisticEntity(userId, countryId, 0)
    );

    final CountryDto countryDto = new CountryDto(countryId, "Country", "C", new byte[]{}, 0);

    final FeedResponse expectedResponse = FeedResponse.newBuilder()
      .setPhotos(FeedPhotos.newBuilder().addAllPhotos(photos).build())
      .build();

    when(photoService.getFriendsPhotos(request, pageable)).thenReturn(photoPage);
    when(statisticService.getUserCountriesStatisticWithFriends(request)).thenReturn(statisticEntities);
    when(grpcCountriesClient.getCountryById(anyString())).thenReturn(Optional.of(countryDto));

    when(feedMapper.toProto(eq(photoPage), anyList())).thenReturn(expectedResponse);

    final FeedResponse actualResponse = feedServiceImpl.getFeed(request, pageable);

    verify(photoService).getFriendsPhotos(request, pageable);
    verify(statisticService).getUserCountriesStatisticWithFriends(request);
    verify(grpcCountriesClient).getCountryById(anyString());
    verify(feedMapper).toProto(eq(photoPage), anyList());
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void getFeed_ShouldUseCorrectServiceBasedOnWithFriendsFlag() {
    final String userId = UUID.randomUUID().toString();

    final FeedRequest requestWithFriends = FeedRequest.newBuilder()
      .setUserId(userId)
      .setWithFriends(true)
      .build();

    final Page<PhotoResponse> photoPage = new PageImpl<>(List.of());
    final List<StatisticEntity> statisticEntities = List.of();

    when(photoService.getFriendsPhotos(requestWithFriends, pageable)).thenReturn(photoPage);
    when(statisticService.getUserCountriesStatisticWithFriends(requestWithFriends)).thenReturn(statisticEntities);
    when(feedMapper.toProto(eq(photoPage), eq(List.of()))).thenReturn(FeedResponse.newBuilder().build());

    feedServiceImpl.getFeed(requestWithFriends, pageable);

    verify(photoService).getFriendsPhotos(requestWithFriends, pageable);
    verify(statisticService).getUserCountriesStatisticWithFriends(requestWithFriends);
    verify(statisticService, never()).getUserCountriesStatistic(any());

    reset(photoService, statisticService, feedMapper);

    final FeedRequest requestWithoutFriends = FeedRequest.newBuilder()
      .setUserId(userId)
      .setWithFriends(false)
      .build();

    when(photoService.getUserPhotos(requestWithoutFriends, pageable)).thenReturn(photoPage);
    when(statisticService.getUserCountriesStatistic(requestWithoutFriends)).thenReturn(statisticEntities);
    when(feedMapper.toProto(eq(photoPage), eq(List.of()))).thenReturn(FeedResponse.newBuilder().build());

    feedServiceImpl.getFeed(requestWithoutFriends, pageable);

    verify(photoService).getUserPhotos(requestWithoutFriends, pageable);
    verify(statisticService).getUserCountriesStatistic(requestWithoutFriends);
    verify(statisticService, never()).getUserCountriesStatisticWithFriends(any());
  }

  @Test
  void getFeed_WithNullPageable_ShouldThrowNullPointerException() {
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(UUID.randomUUID().toString())
      .build();

    assertThrows(
      NullPointerException.class,
      () -> feedServiceImpl.getFeed(request, null)
    );
  }

  @Test
  void getFeed_ShouldPreservePhotoOrderFromService() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .setWithFriends(true)
      .build();

    final PhotoResponse photo1 = PhotoResponse.newBuilder().setId(UUID.randomUUID().toString()).build();
    final PhotoResponse photo2 = PhotoResponse.newBuilder().setId(UUID.randomUUID().toString()).build();
    final PhotoResponse photo3 = PhotoResponse.newBuilder().setId(UUID.randomUUID().toString()).build();

    final List<PhotoResponse> photos = List.of(photo1, photo2, photo3);
    final Page<PhotoResponse> photoPage = new PageImpl<>(photos);

    final UUID countryId = UUID.randomUUID();
    final List<StatisticEntity> statisticEntities = List.of(
      createStatisticEntity(userId, countryId, 5)
    );

    final CountryDto countryDto = new CountryDto(countryId, "Country", "C", new byte[]{}, 0);

    final FeedResponse expectedResponse = FeedResponse.newBuilder()
      .setPhotos(FeedPhotos.newBuilder().addAllPhotos(photos).build())
      .build();

    when(photoService.getFriendsPhotos(request, pageable)).thenReturn(photoPage);
    when(statisticService.getUserCountriesStatisticWithFriends(request)).thenReturn(statisticEntities);
    when(grpcCountriesClient.getCountryById(anyString())).thenReturn(Optional.of(countryDto));

    when(feedMapper.toProto(eq(photoPage), anyList())).thenReturn(expectedResponse);

    final FeedResponse actualResponse = feedServiceImpl.getFeed(request, pageable);

    verify(feedMapper).toProto(eq(photoPage), anyList());
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void getFeed_WithLargeStatistics_ShouldHandleManyCountries() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .setWithFriends(false)
      .build();

    final List<PhotoResponse> photos = List.of(
      PhotoResponse.newBuilder().setId(UUID.randomUUID().toString()).build()
    );
    final Page<PhotoResponse> photoPage = new PageImpl<>(photos);

    final List<StatisticEntity> statisticEntities = List.of(
      createStatisticEntity(userId, UUID.randomUUID(), 1),
      createStatisticEntity(userId, UUID.randomUUID(), 2),
      createStatisticEntity(userId, UUID.randomUUID(), 3),
      createStatisticEntity(userId, UUID.randomUUID(), 4),
      createStatisticEntity(userId, UUID.randomUUID(), 5),
      createStatisticEntity(userId, UUID.randomUUID(), 6),
      createStatisticEntity(userId, UUID.randomUUID(), 7),
      createStatisticEntity(userId, UUID.randomUUID(), 8),
      createStatisticEntity(userId, UUID.randomUUID(), 9),
      createStatisticEntity(userId, UUID.randomUUID(), 10)
    );

    final FeedResponse expectedResponse = FeedResponse.newBuilder()
      .setPhotos(FeedPhotos.newBuilder().addAllPhotos(photos).build())
      .build();

    when(photoService.getUserPhotos(request, pageable)).thenReturn(photoPage);
    when(statisticService.getUserCountriesStatistic(request)).thenReturn(statisticEntities);

    when(grpcCountriesClient.getCountryById(anyString())).thenAnswer(invocation -> {
      String countryIdStr = invocation.getArgument(0);
      UUID countryId = UUID.fromString(countryIdStr);
      return Optional.of(new CountryDto(countryId, "Country", "C", new byte[]{}, 0));
    });

    when(feedMapper.toProto(eq(photoPage), anyList())).thenReturn(expectedResponse);

    final FeedResponse actualResponse = feedServiceImpl.getFeed(request, pageable);

    verify(photoService).getUserPhotos(request, pageable);
    verify(statisticService).getUserCountriesStatistic(request);
    verify(grpcCountriesClient, times(10)).getCountryById(anyString());
    verify(feedMapper).toProto(eq(photoPage), anyList());
    assertEquals(expectedResponse, actualResponse);
  }

  @Nonnull
  private StatisticEntity createStatisticEntity(String userId, UUID countryId, int count) {
    final StatisticEntity entity = new StatisticEntity();
    entity.setUserId(UUID.fromString(userId));
    entity.setCountryId(countryId);
    entity.setCount(count);
    return entity;
  }
}