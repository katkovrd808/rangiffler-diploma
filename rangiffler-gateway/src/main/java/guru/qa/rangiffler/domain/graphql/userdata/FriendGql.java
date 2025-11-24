package guru.qa.rangiffler.domain.graphql.userdata;

import guru.qa.rangiffler.domain.graphql.countries.CountryGql;

import java.util.UUID;

public record FriendGql(
  UUID id,
  String username,
  String firstname,
  String surname,
  CountryGql location,
  FriendStatus friendStatus
) {
}
