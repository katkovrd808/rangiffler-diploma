package guru.qa.rangiffler.service.mapper;

import com.google.protobuf.ByteString;
import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.grpc.UpdateUserResponse;
import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.grpc.UsersResponse;
import org.mapstruct.Mapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
@ParametersAreNonnullByDefault
public interface UserMapper {
  @Nonnull
  default UserResponse toProto(UserEntity user) {
    return UserResponse.newBuilder()
      .setId(user.getId().toString())
      .setUsername(user.getUsername())
      .setFirstname(user.getFirstname())
      .setSurname(user.getSurname())
      .setPhoto(map(user.getPhoto()))
      .build();
  }

  @Nonnull
  default UpdateUserResponse toUpdateProto(UserEntity user) {
    return UpdateUserResponse.newBuilder()
      .setUsername(user.getUsername())
      .setFirstname(user.getFirstname())
      .setSurname(user.getSurname())
      .setPhoto(map(user.getPhoto()))
      .build();
  }

  @Nonnull
  default UsersResponse toProtoList(List<UserEntity> users) {
    return users.isEmpty() ? UsersResponse.getDefaultInstance() :
      UsersResponse.newBuilder().addAllUser(
        users.stream()
          .map(this::toProto)
          .collect(Collectors.toList()))
      .build();
  }

  @Nonnull
  default ByteString map(byte[] value) {
    return value != null ? ByteString.copyFrom(value) : ByteString.EMPTY;
  }
}
