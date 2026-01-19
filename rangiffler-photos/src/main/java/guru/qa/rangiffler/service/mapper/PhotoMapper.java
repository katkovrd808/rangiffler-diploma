package guru.qa.rangiffler.service.mapper;

import com.google.protobuf.ByteString;
import com.google.type.Date;
import guru.qa.rangiffler.data.PhotoLikeEntity;
import guru.qa.rangiffler.data.projection.PhotoWithLikes;
import guru.qa.rangiffler.grpc.CountryPhoto;
import guru.qa.rangiffler.grpc.Like;
import guru.qa.rangiffler.grpc.PhotoDeleteResponse;
import guru.qa.rangiffler.grpc.PhotoResponse;
import guru.qa.rangiffler.model.CountryDto;
import org.mapstruct.Mapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
@ParametersAreNonnullByDefault
public interface PhotoMapper {
  default @Nonnull PhotoResponse toProto(PhotoWithLikes photo, CountryDto country) {
    final CountryPhoto countryPhoto = CountryPhoto.newBuilder()
      .setName(country.name())
      .setCode(country.code())
      .setFlag(ByteString.copyFrom(country.flag()))
      .build();

    PhotoResponse.Builder builder = PhotoResponse.newBuilder();
    builder
      .setId(photo.id().toString())
      .setUserId(photo.userId().toString())
      .setDescription(photo.description())
      .setSrc(ByteString.copyFrom(photo.photo()))
      .setCountry(countryPhoto)
      .addAllLikes(
        photo.likes().stream()
          .map(this::toProtoLike)
          .collect(Collectors.toList())
      );

    if (photo.dateCreated() != null) {
      builder.setDateCreated(toGoogleDate(photo.dateCreated()));
    }

    return photo == null
      ? PhotoResponse.getDefaultInstance()
      : builder.build();
  }

  private @Nonnull Like toProtoLike(PhotoLikeEntity photoLikeEntity) {
    return photoLikeEntity == null ? Like.getDefaultInstance() :
      Like.newBuilder()
        .setUserId(photoLikeEntity.getUserId().toString())
        .build();
  }

  @Nonnull
  private com.google.type.Date toGoogleDate(java.util.Date javaDate) {
    if (javaDate == null) {
      return null;
    }

    LocalDate localDate = javaDate.toInstant()
      .atZone(ZoneId.systemDefault())
      .toLocalDate();

    return Date.newBuilder()
      .setYear(localDate.getYear())
      .setMonth(localDate.getMonthValue())
      .setDay(localDate.getDayOfMonth())
      .build();
  }

  @Nonnull
  default PhotoDeleteResponse toPhotoDeleteResponse(String photoId) {
    if (photoId == null || photoId.isEmpty()) {
      return PhotoDeleteResponse.getDefaultInstance();
    }

    return PhotoDeleteResponse.newBuilder()
      .setId(photoId)
      .build();
  }
}
