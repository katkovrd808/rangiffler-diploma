package guru.qa.rangiffler.service.mapper;

import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.model.graphql.PageInfoGql;
import guru.qa.rangiffler.model.graphql.countries.CountryGql;
import guru.qa.rangiffler.model.graphql.photos.*;
import guru.qa.rangiffler.model.graphql.userdata.UserGql;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
@ParametersAreNonnullByDefault
public interface FeedMapper {

  String DEFAULT_IMAGE_TYPE = "png";

  @Nonnull
  default FeedGql toFeedGql(FeedResponse response, String username, boolean withFriends) {
    final List<PhotoGql> photos = response.getPhotos().getPhotosList()
      .stream()
      .map(this::toPhotoGql)
      .toList();

    final PaginationResponse paginationResponse = response.getPhotos().getPaginationResponse();

    final List<StatisticGql> statistic = response.getStatistic().getCountriesList().stream()
      .map(c -> {
        return new StatisticGql(
          c.getCount(),
          new CountryStatGql(
            c.getCode()
          )
        );
      })
      .toList();

    if (!paginationResponse.isInitialized()) {
      return new FeedGql(
        username,
        withFriends,
        new PhotoSliceGql(photos, PageInfoGql.unpaged()),
        statistic
      );
    }

    return new FeedGql(
      username,
      withFriends,
      new PhotoSliceGql(photos, new PageInfoGql(paginationResponse.getHasNext(), paginationResponse.getHasPrevious())),
      statistic
    );
  }

  @Nonnull
  default FeedRequest toProtoFeedRequest(Pageable pageable, String userId, boolean withFriends) {
    if (userId == null || userId.isEmpty()) {
      return FeedRequest.getDefaultInstance();
    }

    FeedRequest.Builder builder = FeedRequest.newBuilder();
    builder.setUserId(userId);
    builder.setWithFriends(withFriends);

    if (pageable.isPaged()) {
      PaginationRequest paginationRequest = PaginationRequest.newBuilder()
        .setSize(pageable.getPageSize())
        .setPage(pageable.getPageNumber())
        .build();
      builder.setPaginationRequest(paginationRequest);
    } else {
      PaginationRequest defaultInstance = PaginationRequest.getDefaultInstance();
      builder.setPaginationRequest(defaultInstance);
    }

    return builder.build();
  }

  @Nonnull
  default PhotoGql toPhotoGql(PhotoResponse response) {
    return new PhotoGql(
      UUID.fromString(response.getId()),
      UserGql.userWithId(response.getUserId()),
      bytesToDataUrl(response.getSrc().toByteArray()),
      new CountryGql(
        null,
        response.getCountry().getName(),
        response.getCountry().getCode(),
        bytesToDataUrl(response.getCountry().getFlag().toByteArray())
      ),
      response.getDescription(),
      toJavaDate(response.getDateCreated()),
      new LikesGql(
        response.getLikesCount(),
        response.getLikesList().stream()
          .map(l -> new LikeGql(UUID.fromString(l.getUserId())))
          .toList()
      )
    );
  }

  @Nonnull
  private java.util.Date toJavaDate(com.google.type.Date googleDate) {
    if (googleDate == null) {
      return null;
    }

    int year = googleDate.getYear();
    int month = googleDate.getMonth();
    int day = googleDate.getDay();

    if (year == 0) year = LocalDate.now().getYear();
    if (month == 0) month = 1;
    if (day == 0) day = 1;

    month = Math.max(1, Math.min(month, 12));

    try {
      LocalDate date = LocalDate.of(year, month, day);
      return java.util.Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    } catch (DateTimeException e) {
      return null;
    }
  }

  @Nonnull
  private String bytesToDataUrl(byte[] photoBytes) {
    if (photoBytes == null || photoBytes.length == 0) {
      return "";
    }

    try {
      String photoString = new String(photoBytes, StandardCharsets.UTF_8);

      if (isDataUrl(photoString)) {
        return cleanDataUrl(photoString);
      }
    } catch (Exception e) {
      //NOP
    }

    try {
      String base64 = Base64.getEncoder().encodeToString(photoBytes);
      String possibleDataUrl = "data:image/" + DEFAULT_IMAGE_TYPE + ";base64," + base64;
      if (isBase64Data(photoBytes)) {
        return possibleDataUrl;
      }
    } catch (Exception e) {
      //NOP
    }

    String base64 = Base64.getEncoder().encodeToString(photoBytes);
    return "data:image/" + DEFAULT_IMAGE_TYPE + ";base64," + base64;
  }

  @Nonnull
  private String cleanDataUrl(String dataUrl) {
    if (dataUrl == null) {
      return "";
    }

    String cleaned = dataUrl
      .replaceAll("\\x00", "")
      .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "")
      .trim();

    if (isDataUrl(cleaned)) {
      return cleaned;
    }

    return dataUrl.replace("\u0000", "").trim();
  }

  private boolean isBase64Data(byte[] bytes) {
    if (bytes == null || bytes.length == 0) {
      return false;
    }

    try {
      String str = new String(bytes, StandardCharsets.US_ASCII);
      return str.matches("^[A-Za-z0-9+/]*={0,2}$");
    } catch (Exception e) {
      return false;
    }
  }

  private boolean isDataUrl(String str) {
    if (str == null || str.length() < 20) {
      return false;
    }

    String lowerStr = str.toLowerCase();
    return lowerStr.startsWith("data:image/") &&
      lowerStr.contains(";base64,") &&
      lowerStr.length() > lowerStr.indexOf(";base64,") + 8;
  }
}
