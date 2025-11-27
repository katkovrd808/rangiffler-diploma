package guru.qa.rangiffler.service.mapper;

import com.google.protobuf.ByteString;
import guru.qa.rangiffler.data.PhotoLikeEntity;
import guru.qa.rangiffler.data.projection.PhotoWithLikes;
import guru.qa.rangiffler.grpc.CountryPhoto;
import guru.qa.rangiffler.grpc.Like;
import guru.qa.rangiffler.grpc.PhotoResponse;
import guru.qa.rangiffler.model.CountryDto;
import org.mapstruct.Mapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
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

    return photo == null ? PhotoResponse.getDefaultInstance() :
      PhotoResponse.newBuilder()
        .setId(photo.id().toString())
        .setUserId(photo.userId().toString())
        .setDescription(photo.description())
        .setSrc(ByteString.copyFrom(photo.photo()))
        .setCountry(countryPhoto)
        .addAllLikes(
          photo.likes().stream()
            .map(this::toProtoLike)
            .collect(Collectors.toList())
        )
        .build();
  }

  private @Nonnull Like toProtoLike(PhotoLikeEntity photoLikeEntity) {
    return photoLikeEntity == null ? Like.getDefaultInstance() :
      Like.newBuilder()
        .setUserId(photoLikeEntity.getUserId().toString())
        .build();
  }
}
