package guru.qa.rangiffler.service.mapper;

import guru.qa.rangiffler.grpc.CountryRequest;
import org.mapstruct.Mapper;

import javax.annotation.ParametersAreNonnullByDefault;

@Mapper(componentModel = "spring")
@ParametersAreNonnullByDefault
public interface CountryMapper {
  default CountryRequest toProto(String isoCode) {
    return isoCode == null ? CountryRequest.getDefaultInstance() :
      CountryRequest.newBuilder()
        .setCode(isoCode)
        .build();
  }
}
