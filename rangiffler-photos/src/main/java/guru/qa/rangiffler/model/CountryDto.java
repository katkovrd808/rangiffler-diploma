package guru.qa.rangiffler.model;

import java.util.UUID;

public record CountryDto(
  UUID id,
  String name,
  String code
) {
}
