package guru.qa.rangiffler.service.mapper;

import guru.qa.rangiffler.grpc.UserRequest;
import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.model.graphql.UserGql;
import org.mapstruct.Mapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Slice;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
@ParametersAreNonnullByDefault
public interface UserMapper {
  @Nonnull
  default UserGql toUserGql(UserResponse response) {
    return UserGql.noCountryUser(
      UUID.fromString(response.getId()),
      response.getUsername(),
      response.getFirstname(),
      response.getSurname(),
      response.getPhoto().toByteArray()
    );
  }

  @Nonnull
  default UserRequest toProtoRequest(@Nullable String username, @Nullable UUID id) {
    if (username == null && id == null) {
      return UserRequest.getDefaultInstance();
    }

    UserRequest.Builder builder = UserRequest.newBuilder();

    if (username != null && !username.isEmpty()) {
      builder.setUsername(username);
    }

    if(id != null) {
      builder.setId(id.toString());
    }

    return builder.build();
  }

  @Nonnull
  private Slice<UserGql> friendsToSlice(List<UserResponse> friends) {
    return new PageImpl<>(friends.stream()
      .map(this::toUserGql)
      .toList());
  }
}
