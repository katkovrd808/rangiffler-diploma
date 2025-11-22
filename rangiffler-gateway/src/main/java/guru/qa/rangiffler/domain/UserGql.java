package guru.qa.rangiffler.domain;

import org.springframework.data.domain.Slice;

import java.util.UUID;

public record UserGql(
  UUID id,
  String username,
  String firstname,
  String surname,
  byte[] avatar,
  FriendshipStatus friendStatus,
  Slice<UserGql> friends,
  Slice<UserGql> incomeInvitations,
  Slice<UserGql> outcomeInvitations,
  CountryGql location
) {
}
