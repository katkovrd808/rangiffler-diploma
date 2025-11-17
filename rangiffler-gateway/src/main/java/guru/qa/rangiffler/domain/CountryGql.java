package guru.qa.rangiffler.domain;

import java.util.UUID;

public record CountryGql(
  UUID id,
  String name,
  String code,
  byte[] flag
) {
}
