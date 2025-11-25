package guru.qa.rangiffler.service.mapper;

import com.google.protobuf.ByteString;
import guru.qa.rangiffler.data.PhotoEntity;
import guru.qa.rangiffler.grpc.CountryPhoto;
import guru.qa.rangiffler.grpc.PhotoResponse;
import guru.qa.rangiffler.model.CountryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PhotoMapper {
  default PhotoResponse toProto(PhotoEntity entity, CountryDto country) {
    final CountryPhoto countryPhoto = CountryPhoto.newBuilder()
      .setName(country.name())
      .setCode(country.code())
      .setFlag(ByteString.copyFrom(country.flag()))
      .build();

    return entity == null ? PhotoResponse.getDefaultInstance() :
      PhotoResponse.newBuilder()
        .setId(entity.getId().toString())
        .setUserId(entity.getUserId().toString())
        .setDescription(entity.getDescription())
        .setSrc(ByteString.copyFrom(entity.getPhoto()))
        .setCountry(countryPhoto)
        .build();
  }
}
