package guru.qa.rangiffler.service.impl;

import guru.qa.rangiffler.api.GrpcUserdataClient;
import guru.qa.rangiffler.data.StatisticEntity;
import guru.qa.rangiffler.data.repository.StatisticRepository;
import guru.qa.rangiffler.grpc.FeedRequest;
import guru.qa.rangiffler.model.FriendDto;
import guru.qa.rangiffler.model.UserDto;
import guru.qa.rangiffler.service.StatisticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@ParametersAreNonnullByDefault
public class DbStatisticService implements StatisticService {

  private static final Logger LOG = LoggerFactory.getLogger(DbStatisticService.class);

  private final StatisticRepository statisticRepository;
  private final GrpcUserdataClient grpcUserdataClient;

  @Autowired
  public DbStatisticService(StatisticRepository statisticRepository,
                            GrpcUserdataClient grpcUserdataClient) {
    this.statisticRepository = statisticRepository;
    this.grpcUserdataClient = grpcUserdataClient;
  }

  @Override
  @Transactional(readOnly = true)
  public @Nonnull List<StatisticEntity> getUserCountriesStatistic(FeedRequest request) {
    final String userId = assertUserIsNotEmpty(request);
    return statisticRepository.findByUserId(UUID.fromString(userId));
  }

  @Nonnull
  @Override
  public List<StatisticEntity> getUserCountriesStatisticWithFriends(FeedRequest request) {
    final String userId = assertUserIsNotEmpty(request);
    final List<UUID> friendIds = grpcUserdataClient.getAllFriendsByUserId(userId).stream()
      .map(FriendDto::id)
      .toList();
    return statisticRepository.findByUserIdWithFriends(UUID.fromString(userId), friendIds);
  }

  @Nonnull
  private String assertUserIsNotEmpty(FeedRequest request) {
    final String userId = request.getUserId();
    Optional<UserDto> user = grpcUserdataClient.getUserById(userId);
    if (user.isEmpty()) {
      LOG.info("### Requested user with id: {} was not found in userdata-db ###", userId);
      throw new IllegalArgumentException("User with id " + userId + " was not found.");
    }
    LOG.info("### Received user with id: {} and username: {} from userdata service###", user.get().id(), user.get().username());
    return userId;
  }
}
