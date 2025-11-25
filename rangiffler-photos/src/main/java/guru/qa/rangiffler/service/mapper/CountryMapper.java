package guru.qa.rangiffler.service.mapper;

import guru.qa.rangiffler.grpc.CountryRequest;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.model.CountryDto;
import org.mapstruct.Mapper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@Mapper(componentModel = "spring")
@ParametersAreNonnullByDefault
public interface CountryMapper {
  default CountryRequest toProto(@Nullable String id, @Nullable String code) {
    return code == null && id == null ? CountryRequest.getDefaultInstance() :
      CountryRequest.newBuilder()
        .setId(id == null ? "" : id)
        .setCode(code == null ? "" : code)
        .build();
  }

  default Optional<CountryDto> toDto(CountryResponse response) {
    return Optional.of(new CountryDto(
      UUID.fromString(response.getId()),
      response.getName(),
      response.getCode(),
      response.getFlag().toByteArray()
    ));
  }
}
