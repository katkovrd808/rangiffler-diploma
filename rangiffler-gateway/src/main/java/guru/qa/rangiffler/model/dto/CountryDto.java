package guru.qa.rangiffler.model.dto;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record CountryDto(
  String name,
  String code,
  byte[] flag
) {
}
