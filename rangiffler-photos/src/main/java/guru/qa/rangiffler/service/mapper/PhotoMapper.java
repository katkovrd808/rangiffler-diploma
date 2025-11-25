package guru.qa.rangiffler.service.mapper;

import guru.qa.rangiffler.data.PhotoEntity;
import guru.qa.rangiffler.grpc.PhotoResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PhotoMapper {
  default PhotoResponse toProto(PhotoEntity entity) {
    return entity == null ? PhotoResponse.getDefaultInstance() :
    PhotoResponse.newBuilder()
      .setId(entity.getId().toString())
      .build();
  }
}
