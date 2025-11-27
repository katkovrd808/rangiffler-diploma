package guru.qa.rangiffler.service.mapper;

import guru.qa.rangiffler.grpc.AllFriendsRequest;
import guru.qa.rangiffler.grpc.AllFriendsResponse;
import guru.qa.rangiffler.grpc.UserRequest;
import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.model.FriendDto;
import guru.qa.rangiffler.model.UserDto;
import org.mapstruct.Mapper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {
  @Nonnull
  default UserRequest toProto(UUID id) {
    return id == null ? UserRequest.getDefaultInstance() :
      UserRequest.newBuilder()
        .setId(id.toString())
        .build();
  }

  @Nonnull
  default AllFriendsRequest toProtoFriendsRequest(String username) {
    return username == null ? AllFriendsRequest.getDefaultInstance() :
      AllFriendsRequest.newBuilder()
        .setTargetUsername(username)
        .build();
  }

  @Nonnull
  default Optional<UserDto> toUserDto(UserResponse user) {
    return Optional.of(
      new UserDto(
        UUID.fromString(user.getId()),
        user.getUsername()
      ));
  }

  @Nonnull
  default List<FriendDto> toListDto(AllFriendsResponse friends) {
    return friends.getFriendsList().stream()
      .map(f -> {
        return new FriendDto(
          UUID.fromString(f.getId()),
          f.getUsername()
        );
      })
      .toList();
  }
}
