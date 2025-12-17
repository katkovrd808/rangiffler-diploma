package guru.qa.rangiffler.model.graphql;

import guru.qa.rangiffler.grpc.FriendStatus;
import guru.qa.rangiffler.model.dto.CountryDto;

import java.util.UUID;

public record UserGql(
  UUID id,
  String username,
  String firstname,
  String surname,
  byte[] avatar,
  FriendStatus friendStatus,
  CountryDto location
) {
  public static UserGql noCountryUser(UUID id, String username, String firstname, String surname, byte[] avatar, FriendStatus friendStatus) {
    return new UserGql(
      id,
      username,
      firstname,
      surname,
      avatar,
      friendStatus,
      null
    );
  }

  public static UserGql setCountry(UserGql user, CountryDto country) {
    return new UserGql(
      user.id,
      user.username,
      user.firstname,
      user.surname,
      user.avatar,
      user.friendStatus,
      country
    );
  }
}
