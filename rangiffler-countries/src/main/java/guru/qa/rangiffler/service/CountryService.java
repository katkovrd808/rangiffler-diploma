package guru.qa.rangiffler.service;

import guru.qa.rangiffler.grpc.CountriesResponse;
import guru.qa.rangiffler.grpc.CountryResponse;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface CountryService {
  @Nonnull
  CountryResponse findByIsoCode(String isoCode);

  @Nonnull
  CountriesResponse allCountries();
}
