package guru.qa.rangiffler.model.graphql;

import java.util.UUID;

public record CountryGql(
  UUID id,
  String name,
  String code,
  byte[] flag
) {
}
