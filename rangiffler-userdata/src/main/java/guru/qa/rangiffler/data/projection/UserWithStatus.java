package guru.qa.rangiffler.data.projection;

import guru.qa.rangiffler.data.FriendshipStatus;
import guru.qa.rangiffler.data.UserEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public record UserWithStatus(
  UUID id,
  String username,
  String firstname,
  String surname,
  byte[] photo,
  UUID countryId,
  FriendshipStatus status,
  boolean isAddressee
) {
  public UserWithStatus {
  }

  public static UserWithStatus fromEntity(UserEntity entity, FriendshipStatus status) {
    return new UserWithStatus(
      entity.getId(),
      entity.getUsername(),
      entity.getFirstname(),
      entity.getSurname(),
      entity.getPhoto(),
      entity.getCountryId(),
      status,
      false
    );
  }

  public static UserWithStatus fromEntity(UserEntity entity, FriendshipStatus status, boolean isAddressee) {
    return new UserWithStatus(
      entity.getId(),
      entity.getUsername(),
      entity.getFirstname(),
      entity.getSurname(),
      entity.getPhoto(),
      entity.getCountryId(),
      status,
      isAddressee
    );
  }
}
