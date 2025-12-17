package guru.qa.rangiffler.service.mapper;

import guru.qa.rangiffler.grpc.CountryRequest;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.model.CountryDto;
import org.mapstruct.Mapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@Mapper(componentModel = "spring")
@ParametersAreNonnullByDefault
public interface CountryMapper {
  @Nonnull
  default CountryRequest toProtoRequest(@Nullable String code, @Nullable String id) {
    boolean hasId = id != null && !id.trim().isEmpty();
    boolean hasCode = code != null && !code.trim().isEmpty();

    if (!hasId && !hasCode) {
      throw new IllegalArgumentException("Either id or code must be provided and not empty");
    }

    CountryRequest.Builder builder = CountryRequest.newBuilder();

    if (hasId) {
      builder.setId(id);
    }
    if (hasCode) {
      builder.setCode(code.trim());
    }

    return builder.build();
  }

  @Nonnull
  default CountryDto toDto(CountryResponse response) {
    return new CountryDto(
      response.getName(),
      response.getCode(),
      response.getFlag().toByteArray()
    );
  }
}
