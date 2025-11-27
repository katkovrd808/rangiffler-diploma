package guru.qa.rangiffler.service;

import guru.qa.rangiffler.grpc.CountriesResponse;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.NeededCountriesResponse;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface CountryService {
  @Nonnull CountryResponse findByIsoCode(String isoCode);

  @Nonnull CountryResponse findById(String id);

  @Nonnull CountriesResponse allCountries();

  @Nonnull NeededCountriesResponse findNeededCountries(List<UUID> neededCountriesIds);
}
