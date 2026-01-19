package guru.qa.rangiffler.service;

import guru.qa.rangiffler.model.graphql.countries.CountryGql;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface CountryService {
  @Nonnull
  List<CountryGql> allCountries();

  @Nonnull
  CountryGql country(@Nullable String code, @Nullable String id);
}
