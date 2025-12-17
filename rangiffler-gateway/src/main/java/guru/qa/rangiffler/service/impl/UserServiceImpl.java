package guru.qa.rangiffler.service.impl;

import guru.qa.rangiffler.api.GrpcUserdataClient;
import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.model.graphql.UserGql;
import guru.qa.rangiffler.service.UserService;
import guru.qa.rangiffler.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@Component
@ParametersAreNonnullByDefault
public class UserServiceImpl implements UserService {

  private final GrpcUserdataClient grpcUserdataClient;
  private final UserMapper userMapper;

  @Autowired
  public UserServiceImpl(GrpcUserdataClient grpcUserdataClient, UserMapper userMapper) {
    this.grpcUserdataClient = grpcUserdataClient;
    this.userMapper = userMapper;
  }

  @Nonnull
  @Override
  public UserGql findByUsername(String username) {
    UserResponse response = grpcUserdataClient.findByUsername(username);
    return userMapper.toUserGql(response);
  }

  @Nonnull
  @Override
  public Slice<UserGql> allUsers(Pageable pageable) {
    return null;
  }

  @Nonnull
  @Override
  public UserGql updateUser(UserGql user) {
    return null;
  }

  @Nonnull
  @Override
  public Slice<UserGql> friends(String username, Pageable pageable) {
    return null;
  }

  @Nonnull
  @Override
  public Slice<UserGql> incomeInvitations(String username, Pageable pageable) {
    return null;
  }

  @Nonnull
  @Override
  public Slice<UserGql> outcomeInvitations(String username, Pageable pageable) {
    return null;
  }
}
