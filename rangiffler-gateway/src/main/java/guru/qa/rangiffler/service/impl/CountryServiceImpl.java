package guru.qa.rangiffler.service.impl;

import guru.qa.rangiffler.api.GrpcCountriesClient;
import guru.qa.rangiffler.grpc.CountriesResponse;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.model.graphql.countries.CountryGql;
import guru.qa.rangiffler.service.CountryService;
import guru.qa.rangiffler.service.mapper.CountryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@Component
@ParametersAreNonnullByDefault
public class CountryServiceImpl implements CountryService {
  private final GrpcCountriesClient grpcCountriesClient;
  private final CountryMapper countryMapper;

  @Autowired
  public CountryServiceImpl(GrpcCountriesClient grpcCountriesClient, CountryMapper countryMapper) {
    this.grpcCountriesClient = grpcCountriesClient;
    this.countryMapper = countryMapper;
  }

  @Nonnull
  @Override
  public CountryGql country(@Nullable String code, @Nullable String id) {
    CountryResponse response = grpcCountriesClient.getCountry(code, id);
    return countryMapper.toCountryGql(response);
  }

  @Nonnull
  @Override
  public List<CountryGql> allCountries() {
    CountriesResponse response = grpcCountriesClient.allCountries();
    return countryMapper.toCountriesGqlList(response);
  }
}
