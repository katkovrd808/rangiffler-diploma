package guru.qa.rangiffler.model;

import guru.qa.rangiffler.data.StatisticEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public record CountryDto(
  UUID id,
  String name,
  String code,
  byte[] flag,
  int count
) {
  public static CountryDto addCount(CountryDto country, StatisticEntity statistic) {
    return new CountryDto(
      country.id,
      country.name,
      country.code,
      country.flag,
      statistic.getCount()
    );
  }
}
