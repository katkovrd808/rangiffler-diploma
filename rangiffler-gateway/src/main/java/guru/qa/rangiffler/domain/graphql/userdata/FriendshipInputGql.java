package guru.qa.rangiffler.domain.graphql.userdata;

import java.util.UUID;

public record FriendshipInputGql(
  UUID userId,
  FriendshipAction action
) {
}
