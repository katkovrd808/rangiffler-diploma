package guru.qa.rangiffler.service.mapper;

import guru.qa.rangiffler.grpc.UserRequest;
import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.model.UserDto;
import org.mapstruct.Mapper;

import java.util.Optional;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {
  default UserRequest toProto(UUID id) {
    return id == null ? UserRequest.getDefaultInstance() :
      UserRequest.newBuilder()
        .setId(id.toString())
        .build();
  }

  default Optional<UserDto> toDto(UserResponse user) {
    return Optional.of(
      new UserDto(
        UUID.fromString(user.getId()),
        user.getUsername()
      ));
  }
}
