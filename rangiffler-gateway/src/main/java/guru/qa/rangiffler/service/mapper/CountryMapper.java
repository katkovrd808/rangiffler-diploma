package guru.qa.rangiffler.service.mapper;

import guru.qa.rangiffler.grpc.CountriesResponse;
import guru.qa.rangiffler.grpc.CountryRequest;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.model.graphql.countries.CountryGql;
import org.mapstruct.Mapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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
  default CountryGql toCountryGql(CountryResponse response) {
    String photo = Base64.getEncoder().encodeToString(response.getFlag().toByteArray());
    final boolean photoString = isPhotoString(photo);
    if (response.isInitialized()) {
      return new CountryGql(
        UUID.fromString(response.getId()),
        response.getName(),
        response.getCode(),
        photoString ? photo : "data:image/png;base64," + photo
      );
    } else return CountryGql.empty();
  }

  @Nonnull
  default List<CountryGql> toCountriesGqlList(CountriesResponse response) {
    if (response.getCountriesList().isEmpty()) {
      return new ArrayList<>();
    }
    return response.getCountriesList().stream()
      .map(this::toCountryGql)
      .toList();
  }

  private boolean isPhotoString(String photo) {
    return photo != null && photo.startsWith("data:image");
  }
}
