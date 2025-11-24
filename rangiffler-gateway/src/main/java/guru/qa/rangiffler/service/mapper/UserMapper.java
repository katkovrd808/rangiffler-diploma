package guru.qa.rangiffler.service.mapper;

import guru.qa.rangiffler.domain.UserGql;
import guru.qa.rangiffler.grpc.UserRequest;
import guru.qa.rangiffler.grpc.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserResponse toProto(UserGql userGql);

  default UserRequest toUserRequest(UserGql userGql) {
    return UserRequest.newBuilder()
      .setUsername(userGql.username())
      .build();
  }

  UserGql toGqlRequest(UserRequest user);
}
