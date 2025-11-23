package guru.qa.rangiffler.data.projection;

import guru.qa.rangiffler.data.FriendStatus;
import guru.qa.rangiffler.data.UserEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public record FriendWithStatus(
  UUID id,
  String username,
  String firstname,
  String surname,
  byte[] photo,
  UUID countryId,
  FriendStatus status
) {
  public FriendWithStatus {
  }

  public static FriendWithStatus fromEntity(UserEntity entity, FriendStatus status) {
    return new FriendWithStatus(
      entity.getId(),
      entity.getUsername(),
      entity.getFirstname(),
      entity.getSurname(),
      entity.getPhoto(),
      entity.getCountryId(),
      status
    );
  }
}
