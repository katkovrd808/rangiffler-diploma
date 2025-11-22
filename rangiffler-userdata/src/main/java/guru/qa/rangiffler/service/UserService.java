package guru.qa.rangiffler.service;

import guru.qa.rangiffler.grpc.UpdateUserResponse;
import guru.qa.rangiffler.grpc.UserRequest;
import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.grpc.UsersResponse;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface UserService {
  @Nonnull
  UserResponse getUser(String username);

  @Nonnull
  UsersResponse getAllUsers(String username);

  @Nonnull
  UpdateUserResponse updateUser(UserRequest user);
}
