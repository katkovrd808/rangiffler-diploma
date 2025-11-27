package guru.qa.rangiffler.service.mapper;

import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.model.CountryDto;
import org.mapstruct.Mapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper(componentModel = "spring")
@ParametersAreNonnullByDefault
public interface CountryMapper {
  @Nonnull
  default CountryRequest toProto(@Nullable String id, @Nullable String code) {
    boolean hasId = id != null && !id.trim().isEmpty();
    boolean hasCode = code != null && !code.trim().isEmpty();

    if (!hasId && !hasCode) {
      throw new IllegalArgumentException("Either id or code must be provided and not empty");
    }

    CountryRequest.Builder builder = CountryRequest.newBuilder();

    if (hasId) {
      builder.setId(id.trim());
    }
    if (hasCode) {
      builder.setCode(code.trim());
    }

    return builder.build();
  }

  @Nonnull
  default NeededCountriesRequest toNeededRequest(List<UUID> neededCountries) {
    return neededCountries.isEmpty() ? NeededCountriesRequest.getDefaultInstance() :
      NeededCountriesRequest.newBuilder()
        .addAllId(neededCountries.stream()
          .map(UUID::toString)
          .toList()
        )
        .build();
  }

  @Nonnull
  default Optional<CountryDto> toDto(CountryResponse response) {
    return Optional.of(new CountryDto(
      UUID.fromString(response.getId()),
      response.getName(),
      response.getCode(),
      response.getFlag().toByteArray(),
      0
    ));
  }

  @Nonnull
  default List<CountryDto> toDtoList(CountriesResponse response) {
    if (response.getCountriesList().isEmpty()) {
      return new ArrayList<>();
    }
    return response.getCountriesList().stream()
      .map(c -> {
        return new CountryDto(
          UUID.fromString(c.getId()),
          c.getName(),
          c.getCode(),
          c.getFlag().toByteArray(),
          0
        );
      })
      .toList();
  }
}
