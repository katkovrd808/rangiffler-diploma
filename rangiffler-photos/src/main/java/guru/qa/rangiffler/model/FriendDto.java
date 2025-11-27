package guru.qa.rangiffler.model;

import java.util.UUID;

public record FriendDto(
  UUID id,
  String username
) {
}
