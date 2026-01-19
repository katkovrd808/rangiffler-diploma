package guru.qa.rangiffler.service.mapper;

import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.model.CountryDto;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@Mapper(componentModel = "spring")
@ParametersAreNonnullByDefault
public interface FeedMapper {

  default @Nonnull FeedResponse toProto(Page<PhotoResponse> photoPage, List<CountryDto> statistic) {
    return FeedResponse.newBuilder()
      .setPhotos(toStatisticPhotos(photoPage))
      .setStatistic(toCountryStatisticProtoList(statistic))
      .build();
  }

  private @Nonnull FeedPhotos toStatisticPhotos(Page<PhotoResponse> photos) {
    if (photos == null || photos.isEmpty()) {
      return FeedPhotos.getDefaultInstance();
    }

    return FeedPhotos.newBuilder()
      .addAllPhotos(photos.getContent().stream().toList())
      .setPaginationResponse(createPaginationResponse(photos))
      .build();
  }

  private @Nonnull UserCountryStatistic toCountryStatisticProtoList(List<CountryDto> statistic) {
    if (statistic == null || statistic.isEmpty()) {
      return UserCountryStatistic.getDefaultInstance();
    }

    return UserCountryStatistic.newBuilder()
      .addAllCountries(statistic.stream()
        .map(c -> CountryStatistic.newBuilder()
          .setCode(c.code())
          .setCount(c.count())
          .build())
        .toList())
      .build();
  }

  private @Nonnull PaginationResponse createPaginationResponse(Page<?> page) {
    return PaginationResponse.newBuilder()
      .setCurrentPage(page.getNumber())
      .setPageSize(page.getSize())
      .setTotalPages(page.getTotalPages())
      .setTotalElements(page.getTotalElements())
      .setHasNext(page.hasNext())
      .setHasPrevious(page.hasPrevious())
      .build();
  }
}
