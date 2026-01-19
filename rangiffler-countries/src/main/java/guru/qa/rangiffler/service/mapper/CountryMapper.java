package guru.qa.rangiffler.service.mapper;

import com.google.protobuf.ByteString;
import guru.qa.rangiffler.data.CountryEntity;
import guru.qa.rangiffler.grpc.CountriesResponse;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.NeededCountriesResponse;
import org.mapstruct.Mapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
@ParametersAreNonnullByDefault
public interface CountryMapper {
  @Nonnull
  default CountryResponse toProto(CountryEntity country) {
    return CountryResponse.newBuilder()
      .setId(country.getId().toString())
      .setCode(country.getIsoCode())
      .setName(country.getName())
      .setFlag(map(country.getFlag()))
      .build();
  }

  @Nonnull
  default CountriesResponse toProtoList(List<CountryEntity> countries) {
    return countries.isEmpty() ?
      CountriesResponse.getDefaultInstance() :
      CountriesResponse.newBuilder()
        .addAllCountries(countries.stream()
          .map(this::toProto)
          .collect(Collectors.toList()))
        .build();
  }

  default NeededCountriesResponse toNeededCountriesProto(List<CountryEntity> countries) {
    return countries.isEmpty() ? NeededCountriesResponse.getDefaultInstance() :
      NeededCountriesResponse.newBuilder()
        .addAllCountries(countries.stream()
          .map(this::toProto)
          .collect(Collectors.toList()))
        .build();
  }

  @Nonnull
  private ByteString map(byte[] value) {
    return ByteString.copyFrom(value);
  }
}
