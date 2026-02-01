package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.entity.countries.CountryEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public interface CountriesRepository {
  @Nonnull
  Optional<CountryEntity> findByCode(String code);
}
