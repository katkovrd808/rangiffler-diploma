package guru.qa.rangiffler.service;

import guru.qa.rangiffler.api.GrpcUserdataClient;
import guru.qa.rangiffler.data.StatisticEntity;
import guru.qa.rangiffler.data.repository.StatisticRepository;
import guru.qa.rangiffler.grpc.FeedRequest;
import guru.qa.rangiffler.model.FriendDto;
import guru.qa.rangiffler.model.UserDto;
import guru.qa.rangiffler.service.impl.DbStatisticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticServiceTest {

  @Mock
  private StatisticRepository statisticRepository;

  @Mock
  private GrpcUserdataClient grpcUserdataClient;

  private DbStatisticService dbStatisticService;

  @BeforeEach
  void setUp() {
    dbStatisticService = new DbStatisticService(statisticRepository, grpcUserdataClient);
  }

  @Test
  void getUserCountriesStatistic_WithValidUser_ShouldReturnStatistics() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .build();

    final UserDto userDto = new UserDto(UUID.fromString(userId), "testuser");
    final List<StatisticEntity> expectedStatistics = List.of(
      createStatisticEntity(userId, UUID.randomUUID(), 5),
      createStatisticEntity(userId, UUID.randomUUID(), 3)
    );

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.of(userDto));
    when(statisticRepository.findByUserId(UUID.fromString(userId))).thenReturn(expectedStatistics);

    final List<StatisticEntity> actualStatistics = dbStatisticService.getUserCountriesStatistic(request);

    verify(grpcUserdataClient).getUserById(userId);
    verify(statisticRepository).findByUserId(UUID.fromString(userId));
    assertEquals(expectedStatistics, actualStatistics);
  }

  @Test
  void getUserCountriesStatistic_WhenUserNotFound_ShouldThrowException() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .build();

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.empty());

    final IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> dbStatisticService.getUserCountriesStatistic(request)
    );

    assertTrue(exception.getMessage().contains("User with id " + userId + " was not found."));
    verify(grpcUserdataClient).getUserById(userId);
    verify(statisticRepository, never()).findByUserId(any());
  }

  @Test
  void getUserCountriesStatisticWithFriends_WithValidUserAndFriends_ShouldReturnStatistics() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .build();

    final UserDto userDto = new UserDto(UUID.fromString(userId), "testuser");
    final UUID friendId1 = UUID.randomUUID();
    final UUID friendId2 = UUID.randomUUID();
    final List<FriendDto> friends = List.of(
      new FriendDto(friendId1, "friend1"),
      new FriendDto(friendId2, "friend2")
    );

    final List<StatisticEntity> expectedStatistics = List.of(
      createStatisticEntity(userId, UUID.randomUUID(), 10),
      createStatisticEntity(userId, UUID.randomUUID(), 7)
    );

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.of(userDto));
    when(grpcUserdataClient.getAllFriendsByUserId(userId)).thenReturn(friends);
    when(statisticRepository.findByUserIdWithFriends(UUID.fromString(userId), List.of(friendId1, friendId2)))
      .thenReturn(expectedStatistics);

    final List<StatisticEntity> actualStatistics = dbStatisticService.getUserCountriesStatisticWithFriends(request);

    verify(grpcUserdataClient).getUserById(userId);
    verify(grpcUserdataClient).getAllFriendsByUserId(userId);
    verify(statisticRepository).findByUserIdWithFriends(UUID.fromString(userId), List.of(friendId1, friendId2));
    assertEquals(expectedStatistics, actualStatistics);
  }

  @Test
  void getUserCountriesStatisticWithFriends_WhenUserNotFound_ShouldThrowException() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .build();

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.empty());

    final IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> dbStatisticService.getUserCountriesStatisticWithFriends(request)
    );

    assertTrue(exception.getMessage().contains("User with id " + userId + " was not found."));
    verify(grpcUserdataClient).getUserById(userId);
    verify(grpcUserdataClient, never()).getAllFriendsByUserId(any());
    verify(statisticRepository, never()).findByUserIdWithFriends(any(), any());
  }

  @Test
  void getUserCountriesStatisticWithFriends_WithNoFriends_ShouldReturnStatisticsWithEmptyFriendList() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .build();

    final UserDto userDto = new UserDto(UUID.fromString(userId), "testuser");
    final List<FriendDto> emptyFriends = List.of();
    final List<StatisticEntity> expectedStatistics = List.of(
      createStatisticEntity(userId, UUID.randomUUID(), 5)
    );

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.of(userDto));
    when(grpcUserdataClient.getAllFriendsByUserId(userId)).thenReturn(emptyFriends);
    when(statisticRepository.findByUserIdWithFriends(UUID.fromString(userId), List.of()))
      .thenReturn(expectedStatistics);

    final List<StatisticEntity> actualStatistics = dbStatisticService.getUserCountriesStatisticWithFriends(request);

    verify(grpcUserdataClient).getUserById(userId);
    verify(grpcUserdataClient).getAllFriendsByUserId(userId);
    verify(statisticRepository).findByUserIdWithFriends(UUID.fromString(userId), List.of());
    assertEquals(expectedStatistics, actualStatistics);
  }

  @Test
  void getUserCountriesStatistic_WithEmptyStatistics_ShouldReturnEmptyList() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .build();

    final UserDto userDto = new UserDto(UUID.fromString(userId), "testuser");
    final List<StatisticEntity> emptyStatistics = List.of();

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.of(userDto));
    when(statisticRepository.findByUserId(UUID.fromString(userId))).thenReturn(emptyStatistics);

    final List<StatisticEntity> actualStatistics = dbStatisticService.getUserCountriesStatistic(request);

    verify(grpcUserdataClient).getUserById(userId);
    verify(statisticRepository).findByUserId(UUID.fromString(userId));
    assertTrue(actualStatistics.isEmpty());
  }

  @Test
  void getUserCountriesStatisticWithFriends_WithEmptyStatistics_ShouldReturnEmptyList() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .build();

    final UserDto userDto = new UserDto(UUID.fromString(userId), "testuser");
    final UUID friendId = UUID.randomUUID();
    final List<FriendDto> friends = List.of(new FriendDto(friendId, "friend"));
    final List<StatisticEntity> emptyStatistics = List.of();

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.of(userDto));
    when(grpcUserdataClient.getAllFriendsByUserId(userId)).thenReturn(friends);
    when(statisticRepository.findByUserIdWithFriends(UUID.fromString(userId), List.of(friendId)))
      .thenReturn(emptyStatistics);

    final List<StatisticEntity> actualStatistics = dbStatisticService.getUserCountriesStatisticWithFriends(request);

    verify(grpcUserdataClient).getUserById(userId);
    verify(grpcUserdataClient).getAllFriendsByUserId(userId);
    verify(statisticRepository).findByUserIdWithFriends(UUID.fromString(userId), List.of(friendId));
    assertTrue(actualStatistics.isEmpty());
  }

  @Test
  void getUserCountriesStatistic_WithMultipleStatistics_ShouldReturnAll() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .build();

    final UserDto userDto = new UserDto(UUID.fromString(userId), "testuser");
    final List<StatisticEntity> expectedStatistics = List.of(
      createStatisticEntity(userId, UUID.randomUUID(), 10),
      createStatisticEntity(userId, UUID.randomUUID(), 20),
      createStatisticEntity(userId, UUID.randomUUID(), 15),
      createStatisticEntity(userId, UUID.randomUUID(), 5)
    );

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.of(userDto));
    when(statisticRepository.findByUserId(UUID.fromString(userId))).thenReturn(expectedStatistics);

    final List<StatisticEntity> actualStatistics = dbStatisticService.getUserCountriesStatistic(request);

    verify(grpcUserdataClient).getUserById(userId);
    verify(statisticRepository).findByUserId(UUID.fromString(userId));
    assertEquals(4, actualStatistics.size());
    assertEquals(expectedStatistics, actualStatistics);
  }

  @Test
  void getUserCountriesStatisticWithFriends_WithManyFriends_ShouldHandleCorrectly() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .build();

    final UserDto userDto = new UserDto(UUID.fromString(userId), "testuser");

    final List<FriendDto> friends = List.of(
      new FriendDto(UUID.randomUUID(), "friend1"),
      new FriendDto(UUID.randomUUID(), "friend2"),
      new FriendDto(UUID.randomUUID(), "friend3"),
      new FriendDto(UUID.randomUUID(), "friend4"),
      new FriendDto(UUID.randomUUID(), "friend5")
    );

    final List<UUID> friendIds = friends.stream().map(FriendDto::id).toList();
    final List<StatisticEntity> expectedStatistics = List.of(
      createStatisticEntity(userId, UUID.randomUUID(), 25),
      createStatisticEntity(userId, UUID.randomUUID(), 18)
    );

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.of(userDto));
    when(grpcUserdataClient.getAllFriendsByUserId(userId)).thenReturn(friends);
    when(statisticRepository.findByUserIdWithFriends(UUID.fromString(userId), friendIds))
      .thenReturn(expectedStatistics);

    final List<StatisticEntity> actualStatistics = dbStatisticService.getUserCountriesStatisticWithFriends(request);

    verify(grpcUserdataClient).getUserById(userId);
    verify(grpcUserdataClient).getAllFriendsByUserId(userId);
    verify(statisticRepository).findByUserIdWithFriends(UUID.fromString(userId), friendIds);
    assertEquals(2, actualStatistics.size());
    assertEquals(expectedStatistics, actualStatistics);
  }

  @Test
  void getUserCountriesStatistic_ShouldLogUserInfo() {
    final String userId = UUID.randomUUID().toString();
    final String username = "testuser";
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .build();

    final UserDto userDto = new UserDto(UUID.fromString(userId), username);
    final List<StatisticEntity> statistics = List.of(
      createStatisticEntity(userId, UUID.randomUUID(), 5)
    );

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.of(userDto));
    when(statisticRepository.findByUserId(UUID.fromString(userId))).thenReturn(statistics);

    dbStatisticService.getUserCountriesStatistic(request);

    verify(grpcUserdataClient).getUserById(userId);
    verify(statisticRepository).findByUserId(UUID.fromString(userId));
  }

  @Test
  void getUserCountriesStatisticWithFriends_ShouldLogUserInfo() {
    final String userId = UUID.randomUUID().toString();
    final String username = "testuser";
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .build();

    final UserDto userDto = new UserDto(UUID.fromString(userId), username);
    final List<FriendDto> friends = List.of(new FriendDto(UUID.randomUUID(), "friend"));
    final List<StatisticEntity> statistics = List.of(
      createStatisticEntity(userId, UUID.randomUUID(), 10)
    );

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.of(userDto));
    when(grpcUserdataClient.getAllFriendsByUserId(userId)).thenReturn(friends);
    when(statisticRepository.findByUserIdWithFriends(eq(UUID.fromString(userId)), anyList()))
      .thenReturn(statistics);

    dbStatisticService.getUserCountriesStatisticWithFriends(request);

    verify(grpcUserdataClient).getUserById(userId);
    verify(grpcUserdataClient).getAllFriendsByUserId(userId);
    verify(statisticRepository).findByUserIdWithFriends(eq(UUID.fromString(userId)), anyList());
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