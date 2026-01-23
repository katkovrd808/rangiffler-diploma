package guru.qa.rangiffler.service.impl;

import guru.qa.rangiffler.api.GrpcCountriesClient;
import guru.qa.rangiffler.data.StatisticEntity;
import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.model.CountryDto;
import guru.qa.rangiffler.service.FeedService;
import guru.qa.rangiffler.service.PhotoService;
import guru.qa.rangiffler.service.StatisticService;
import guru.qa.rangiffler.service.mapper.FeedMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@Component
@ParametersAreNonnullByDefault
public class FeedServiceImpl implements FeedService {

  private static final Logger LOG = LoggerFactory.getLogger(FeedServiceImpl.class);

  private final PhotoService photoService;
  private final StatisticService statisticService;
  private final GrpcCountriesClient grpcCountriesClient;
  private final FeedMapper feedMapper;

  @Autowired
  public FeedServiceImpl(PhotoService photoService,
                         StatisticService statisticService,
                         GrpcCountriesClient grpcCountriesClient,
                         FeedMapper feedMapper) {
    this.photoService = photoService;
    this.statisticService = statisticService;
    this.feedMapper = feedMapper;
    this.grpcCountriesClient = grpcCountriesClient;
  }

  @Transactional(readOnly = true)
  public @Nonnull FeedResponse getFeed(FeedRequest request, Pageable pageable) {
    if (pageable == null) {
      throw new NullPointerException("Pageable can't be null for method execution.");
    }
    final Page<PhotoResponse> photoPage = request.getWithFriends()
      ? photoService.getFriendsPhotos(request, pageable)
      : photoService.getUserPhotos(request, pageable);

    final List<StatisticEntity> countriesStatistic = request.getWithFriends()
      ? statisticService.getUserCountriesStatisticWithFriends(request)
      : statisticService.getUserCountriesStatistic(request);

    final List<CountryDto> countries = getCountriesFromStatistic(countriesStatistic);

    return feedMapper.toProto(photoPage, countries);
  }

  private @Nonnull List<CountryDto> getCountriesFromStatistic(List<StatisticEntity> statisticEntities) {
    if (statisticEntities.isEmpty()) {
      return Collections.emptyList();
    }

    return statisticEntities.stream()
      .map(se -> {
        CountryDto country = grpcCountriesClient.getCountryById(se.getCountryId().toString())
          .orElseThrow();
        return CountryDto.addCount(country, se);
      })
      .toList();
  }
}
