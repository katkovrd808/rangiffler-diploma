package guru.qa.rangiffler.data.projection;

import guru.qa.rangiffler.grpc.Like;
import guru.qa.rangiffler.grpc.PhotoResponse;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public record PhotoWithLikes(
  UUID id,
  UUID userId,
  String countryCode,
  String description,
  byte[] src,
  List<Like> likes,
  Date dateCreated
) {
  @Nonnull
  public static PhotoWithLikes fromProto(PhotoResponse photoResponse) {
    return new PhotoWithLikes(
      UUID.fromString(photoResponse.getId()),
      UUID.fromString(photoResponse.getUserId()),
      photoResponse.getCountry().getCode(),
      photoResponse.getDescription(),
      photoResponse.getSrc().toByteArray(),
      photoResponse.getLikesList(),
      toJavaDate(photoResponse.getDateCreated())
    );
  }

  @Nonnull
  private static java.util.Date toJavaDate(com.google.type.Date googleDate) {
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
}
