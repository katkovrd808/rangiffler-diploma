package guru.qa.rangiffler.service.mapper;

import guru.qa.rangiffler.domain.UserGql;
import guru.qa.rangiffler.grpc.User;
import guru.qa.rangiffler.grpc.UserRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
  User toProto(UserGql userGql);

  default UserRequest toUserRequest(UserGql userGql) {
    return UserRequest.newBuilder()
      .setUsername(userGql.username())
      .build();
  }

  UserGql toGql(User user);
}
