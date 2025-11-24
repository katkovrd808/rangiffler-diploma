package guru.qa.rangiffler.domain.graphql.userdata;

import guru.qa.rangiffler.domain.graphql.countries.CountryInputGql;

public record UserInputGql(
  String firstname,
  String surname,
  byte[] avatar,
  CountryInputGql location
) {
}
