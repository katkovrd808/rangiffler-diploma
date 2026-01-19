package guru.qa.rangiffler.service.mapper;

import com.google.protobuf.ByteString;
import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.model.graphql.countries.CountryGql;
import guru.qa.rangiffler.model.graphql.photos.LikeGql;
import guru.qa.rangiffler.model.graphql.photos.LikesGql;
import guru.qa.rangiffler.model.graphql.photos.PhotoGql;
import guru.qa.rangiffler.model.graphql.photos.PhotoInputGql;
import guru.qa.rangiffler.model.graphql.userdata.UserGql;
import org.mapstruct.Mapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;
import java.util.UUID;

@Mapper(componentModel = "spring")
@ParametersAreNonnullByDefault
public interface PhotoMapper {

  String DEFAULT_IMAGE_TYPE = "png";

  @Nonnull
  default PhotoGql toPhotoGql(PhotoResponse response, String username) {
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
  default PhotoRequest toProtoPhotoSaveRequest(String userId, PhotoInputGql photo) {
    if (userId == null || userId.isEmpty() || photo == null) {
      return PhotoRequest.getDefaultInstance();
    }

    PhotoRequest.Builder builder = PhotoRequest.newBuilder();
    builder.setUserId(userId);

    if (photo.country() != null && photo.country().code() != null) {
      builder.setCountryCode(photo.country().code());
    }

    if (photo.description() != null) {
      builder.setDescription(photo.description());
    }

    if (photo.src() != null) {
      builder.setSrc(dataUrlToByteString(photo.src()));
    }

    if (photo.like() != null
      && photo.like().user() != null
      && photo.like().user().toString() != null) {
      builder.setLike(
        Like.newBuilder()
          .setUserId(photo.like().user().toString())
          .build()
      );
    }

    return builder.build();
  }

  @Nonnull
  default PhotoUpdateRequest toProtoPhotoUpdateRequest(String userId, PhotoInputGql photo) {
    if (userId == null || userId.isEmpty() || photo == null) {
      return PhotoUpdateRequest.getDefaultInstance();
    }

    PhotoUpdateRequest.Builder builder = PhotoUpdateRequest.newBuilder();
    builder.setUserId(userId);
    builder.setId(photo.id().toString());

    if (photo.country() != null && photo.country().code() != null) {
      builder.setCountry(CountryPhoto.newBuilder()
        .setCode(photo.country().code())
      );
    }

    if (photo.description() != null) {
      builder.setDescription(photo.description());
    }

    if (photo.src() != null) {
      builder.setSrc(dataUrlToByteString(photo.src()));
    }

    return builder.build();
  }

  @Nonnull
  default PhotoDeleteRequest toProtoPhotoDeleteRequest(String userId, String photoId) {
    if (userId == null || photoId == null) {
      return PhotoDeleteRequest.getDefaultInstance();
    }

    return PhotoDeleteRequest.newBuilder()
      .setUserId(userId)
      .setId(photoId)
      .build();
  }

  @Nonnull
  default PhotoLikeRequest toProtoPhotoLikeRequest(String userId, PhotoInputGql photo) {
    if (userId == null || userId.isEmpty() || photo == null) {
      return PhotoLikeRequest.getDefaultInstance();
    }

    final String photoId = photo.id().toString();

    return PhotoLikeRequest.newBuilder()
      .setPhotoId(photoId)
      .setLike(
        Like.newBuilder()
          .setUserId(userId)
          .build()
      )
      .build();
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

  @Nonnull
  private ByteString dataUrlToByteString(byte[] dataUrlBytes) {
    if (dataUrlBytes == null || dataUrlBytes.length == 0) {
      return ByteString.EMPTY;
    }

    try {
      String dataUrl = new String(dataUrlBytes, StandardCharsets.UTF_8);

      if (!isDataUrl(dataUrl)) {
        String base64 = Base64.getEncoder().encodeToString(dataUrlBytes);
        dataUrl = "data:image/" + DEFAULT_IMAGE_TYPE + ";base64," + base64;
      }

      String cleanedDataUrl = cleanDataUrl(dataUrl);
      return ByteString.copyFromUtf8(cleanedDataUrl);

    } catch (Exception e) {
      String base64 = Base64.getEncoder().encodeToString(dataUrlBytes);
      String dataUrl = "data:image/" + DEFAULT_IMAGE_TYPE + ";base64," + base64;
      return ByteString.copyFromUtf8(dataUrl);
    }
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
