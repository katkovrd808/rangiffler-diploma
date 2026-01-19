package guru.qa.rangiffler.model.graphql.countries;

import java.util.UUID;

public record CountryGql(
  UUID id,
  String name,
  String code,
  String flag
) {
  public static CountryGql empty() {
    return new CountryGql(
      UUID.randomUUID(),
      "Empty",
      "Empty",
      null
    );
  }
}
