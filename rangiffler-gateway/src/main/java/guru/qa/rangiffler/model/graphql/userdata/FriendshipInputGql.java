package guru.qa.rangiffler.model.graphql.userdata;

import guru.qa.rangiffler.service.FriendshipAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record FriendshipInputGql(
  @NotBlank(message = "User id can't be blank")
  UUID user,
  FriendshipAction action
) {
}
