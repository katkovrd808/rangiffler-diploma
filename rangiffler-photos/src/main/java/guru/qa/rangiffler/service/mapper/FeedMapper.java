package guru.qa.rangiffler.service.mapper;

import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.model.CountryDto;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@Mapper(componentModel = "spring")
@ParametersAreNonnullByDefault
public interface FeedMapper {
  default FeedResponse toProto(Page<PhotoResponse> photoPage, List<CountryDto> statistic) {
    List<PhotoResponse> photosWithPagination = photoPage.getContent().stream()
      .map(photo -> addPaginationToPhoto(photo, photoPage))
      .toList();

    return FeedResponse.newBuilder()
      .addAllPhotos(photosWithPagination)
      .setStatistic(toCountryStatisticProtoList(statistic))
      .build();
  }

  private PhotoResponse addPaginationToPhoto(PhotoResponse photo, Page<?> page) {
    return photo.toBuilder()
      .setPaginationResponse(createPaginationResponse(page))
      .build();
  }

  private UserCountryStatistic toCountryStatisticProtoList(List<CountryDto> statistic) {
    if (statistic == null || statistic.isEmpty()) {
      return UserCountryStatistic.getDefaultInstance();
    }

    return UserCountryStatistic.newBuilder()
      .addAllCountry(statistic.stream()
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
