package guru.qa.rangiffler.service.impl;

import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.data.repository.UserRepository;
import guru.qa.rangiffler.ex.UserNotFoundException;
import guru.qa.rangiffler.grpc.UpdateUserResponse;
import guru.qa.rangiffler.grpc.UserRequest;
import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.grpc.UsersResponse;
import guru.qa.rangiffler.service.UserService;
import guru.qa.rangiffler.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

@Component
@ParametersAreNonnullByDefault
public class DbUserService implements UserService {

  public static final String COUNTRY_ID = "03a90efc-9212-49f5-bcea-3347a6f1d77b";

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Autowired
  public DbUserService(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  @Override
  @Transactional(readOnly = true)
  public @Nonnull UserResponse getUser(String username) {
    return userRepository.findByUsername(username)
      .map(userMapper::toProto)
      .orElseThrow(() -> new UserNotFoundException("Can't find user with username " + username));
  }

  @Override
  @Transactional(readOnly = true)
  public @Nonnull UsersResponse getAllUsers(String username) {
    List<UserEntity> users = userRepository.findByUsernameNot(username);
    return userMapper.toProtoList(users);
  }

  //TODO FIX PHOTO UPDATE
  @Override
  @Transactional
  public @Nonnull UpdateUserResponse updateUser(UserRequest user) {
    UserEntity ue = userRepository.findByUsername(user.getUsername())
      .orElseGet(() -> {
        UserEntity empty = new UserEntity();
        empty.setUsername(user.getUsername());
        empty.setCountryId(UUID.fromString(COUNTRY_ID));
        return empty;
      });
    ue.setFirstname(user.getFirstname());
    ue.setSurname(user.getSurname());
    ue.setCountryId(UUID.fromString(user.getLocation()));
    ue.setPhoto(user.getPhoto().toByteArray());

    return userMapper.toUpdateProto(userRepository.save(ue));
  }
}
