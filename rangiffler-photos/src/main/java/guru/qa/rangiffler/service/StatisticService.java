package guru.qa.rangiffler.service;

import guru.qa.rangiffler.data.StatisticEntity;
import guru.qa.rangiffler.grpc.FeedRequest;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface StatisticService {
  @Nonnull List<StatisticEntity> getUserCountriesStatistic(FeedRequest request);

  @Nonnull List<StatisticEntity> getUserCountriesStatisticWithFriends(FeedRequest request);
}
