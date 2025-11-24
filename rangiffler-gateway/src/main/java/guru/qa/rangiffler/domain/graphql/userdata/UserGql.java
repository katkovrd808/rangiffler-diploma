package guru.qa.rangiffler.domain.graphql.userdata;

import guru.qa.rangiffler.domain.graphql.countries.CountryGql;

import java.util.List;
import java.util.UUID;

public record UserGql(
  UUID id,
  String username,
  String firstname,
  String surname,
  byte[] avatar,
  FriendStatus friendStatus,
  List<UserGql> friends,
  List<UserGql> incomeInvitations,
  List<UserGql> outcomeInvitations,
  CountryGql location
) {
}
