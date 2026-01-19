package guru.qa.rangiffler.model.graphql.userdata;

import guru.qa.rangiffler.grpc.FriendStatus;
import guru.qa.rangiffler.model.graphql.countries.CountryGql;

import java.util.UUID;

public record UserGql(
  UUID id,
  String username,
  String firstname,
  String surname,
  String avatar,
  FriendStatus friendStatus,
  CountryGql location
) {
  public static UserGql userWithId(String id) {
    return new UserGql(
      UUID.fromString(id),
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
}
