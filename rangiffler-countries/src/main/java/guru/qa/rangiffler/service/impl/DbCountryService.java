package guru.qa.rangiffler.service.impl;

import guru.qa.rangiffler.data.CountryEntity;
import guru.qa.rangiffler.data.repository.CountryRepository;
import guru.qa.rangiffler.grpc.CountriesResponse;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.ex.CountryNotFoundException;
import guru.qa.rangiffler.service.CountryService;
import guru.qa.rangiffler.service.mapper.CountryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@Component
@ParametersAreNonnullByDefault
public class DbCountryService implements CountryService {

  private final CountryMapper countryMapper;
  private final CountryRepository countryRepository;

  @Autowired
  public DbCountryService(CountryRepository countryRepository, CountryMapper countryMapper) {
    this.countryRepository = countryRepository;
    this.countryMapper = countryMapper;
  }

  @Override
  @Transactional(readOnly = true)
  public @Nonnull CountryResponse findByIsoCode(String isoCode) {
    return countryRepository.findByIsoCode(isoCode)
      .map(countryMapper::toProto)
      .orElseThrow(() -> new CountryNotFoundException("Can't find country with iso code: " + isoCode));
  }

  @Override
  @Transactional(readOnly = true)
  public @Nonnull CountriesResponse allCountries() {
    List<CountryEntity> countries = countryRepository.findAll();
    return countryMapper.toProtoList(countries);
  }
}
