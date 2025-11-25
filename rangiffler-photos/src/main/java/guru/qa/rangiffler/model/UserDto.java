package guru.qa.rangiffler.model;

import java.util.UUID;

public record UserDto(
  UUID id,
  String username
) {
}
