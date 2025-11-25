package guru.qa.rangiffler.service.mapper;

import guru.qa.rangiffler.grpc.CountryRequest;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.model.CountryDto;
import org.mapstruct.Mapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@Mapper(componentModel = "spring")
@ParametersAreNonnullByDefault
public interface CountryMapper {
  default CountryRequest toProto(String code) {
    return code == null ? CountryRequest.getDefaultInstance() :
      CountryRequest.newBuilder()
        .setCode(code)
        .build();
  }

  default CountryDto toDto(CountryResponse response) {
    return new CountryDto(
      UUID.fromString(response.getId()),
      response.getName(),
      response.getCode()
    );
  }
}
