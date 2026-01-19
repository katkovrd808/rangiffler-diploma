package guru.qa.rangiffler.service.impl;

import guru.qa.rangiffler.api.GrpcCountriesClient;
import guru.qa.rangiffler.api.GrpcUserdataClient;
import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.model.graphql.userdata.UserGql;
import guru.qa.rangiffler.model.graphql.userdata.UserInputGql;
import guru.qa.rangiffler.model.graphql.userdata.UsersSliceGql;
import guru.qa.rangiffler.service.FriendshipAction;
import guru.qa.rangiffler.service.UserService;
import guru.qa.rangiffler.service.mapper.FriendshipMapper;
import guru.qa.rangiffler.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@Component
@ParametersAreNonnullByDefault
public class UserServiceImpl implements UserService {

  private final GrpcUserdataClient grpcUserdataClient;
  private final GrpcCountriesClient grpcCountriesClient;
  private final UserMapper userMapper;
  private final FriendshipMapper friendshipMapper;

  @Autowired
  public UserServiceImpl(GrpcUserdataClient grpcUserdataClient,
                         GrpcCountriesClient grpcCountriesClient,
                         UserMapper userMapper,
                         FriendshipMapper friendshipMapper) {
    this.grpcUserdataClient = grpcUserdataClient;
    this.grpcCountriesClient = grpcCountriesClient;
    this.userMapper = userMapper;
    this.friendshipMapper = friendshipMapper;
  }

  @Nonnull
  @Override
  public UserGql findUser(@Nullable String username, @Nullable UUID userId) {
    UserResponse response = grpcUserdataClient.findUser(username, userId);
    CountryResponse countryResponse = grpcCountriesClient.getCountry(null, response.getCountryId());
    return userMapper.toUserGql(response, countryResponse);
  }

  @Nonnull
  public UserGql findUserWithFriendStatus(String targetUserId, String currentUserId) {
    UserResponse response = grpcUserdataClient.findUserWithFriendStatus(targetUserId, currentUserId);
    CountryResponse countryResponse = grpcCountriesClient.getCountry(null, response.getCountryId());
    return userMapper.toUserGql(response, countryResponse);
  }

  @Nonnull
  @Override
  public UsersSliceGql allUsers(Pageable pageable, String username) {
    UsersPaginatedResponse response = grpcUserdataClient.findAllExceptCurrent(pageable, username);
    CountriesResponse countriesResponse = grpcCountriesClient.allCountries();
    return userMapper.toUsersListGql(response, countriesResponse);
  }

  @Nonnull
  @Override
  public UserGql updateUser(String username, UserInputGql user) {
    CountryResponse countryResponse = grpcCountriesClient.getCountry(user.location().code(), null);
    UserUpdateResponse response = grpcUserdataClient.updateUser(username, user, countryResponse.getId());
    return userMapper.toUserGql(response, countryResponse);
  }

  @Nonnull
  @Override
  public UsersSliceGql friends(Pageable pageable, String username, @Nullable String searchQuery) {
    AllFriendsPaginatedResponse response = grpcUserdataClient.findFriends(pageable, username, searchQuery);
    CountriesResponse countriesResponse = grpcCountriesClient.allCountries();
    return friendshipMapper.toFriendsListGql(response, countriesResponse);
  }

  @Nonnull
  @Override
  public UsersSliceGql incomeInvitations(Pageable pageable, String username, @Nullable String searchQuery) {
    InvitationsPaginatedResponse response = grpcUserdataClient.getIncomeInvitations(pageable, username, searchQuery);
    CountriesResponse countriesResponse = grpcCountriesClient.allCountries();
    return friendshipMapper.toInvitationsListGql(response, countriesResponse);
  }

  @Nonnull
  @Override
  public UsersSliceGql outcomeInvitations(Pageable pageable, String username, @Nullable String searchQuery) {
    InvitationsPaginatedResponse response = grpcUserdataClient.getOutcomeInvitations(pageable, username, searchQuery);
    CountriesResponse countriesResponse = grpcCountriesClient.allCountries();
    return friendshipMapper.toInvitationsListGql(response, countriesResponse);
  }

  @Nonnull
  @Override
  public UserGql friendship(String currentUser, String targetUser, FriendshipAction friendshipAction) {
    FriendshipResponse response = grpcUserdataClient.friendship(currentUser, targetUser, friendshipAction);
    final String targetUserId = findUser(response.getUsername(), null).id().toString();
    final String currentUserId = findUser(currentUser, null).id().toString();
    return findUserWithFriendStatus(targetUserId, currentUserId);
  }
}
