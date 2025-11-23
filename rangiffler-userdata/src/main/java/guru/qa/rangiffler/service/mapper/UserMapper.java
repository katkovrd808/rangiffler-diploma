package guru.qa.rangiffler.service.mapper;

import com.google.protobuf.ByteString;
import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.data.projection.FriendWithStatus;
import guru.qa.rangiffler.grpc.*;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
@ParametersAreNonnullByDefault
public interface UserMapper {

  guru.qa.rangiffler.grpc.FriendStatus mapStatus(guru.qa.rangiffler.data.FriendStatus status);

  default @Nonnull UserResponse toProto(UserEntity user) {
    return UserResponse.newBuilder()
      .setId(user.getId().toString())
      .setUsername(user.getUsername())
      .setFirstname(user.getFirstname() != null ? user.getFirstname() : "")
      .setSurname(user.getSurname() != null ? user.getSurname() : "")
      .setPhoto(user.getPhoto() != null ? map(user.getPhoto()) : ByteString.EMPTY)
      .setCountryId(user.getCountryId().toString())
      .build();
  }

  default @Nonnull UserUpdateResponse toUpdateProto(UserEntity user) {
    return UserUpdateResponse.newBuilder()
      .setId(user.getId().toString())
      .setUsername(user.getUsername())
      .setFirstname(user.getFirstname() != null ? user.getFirstname() : "")
      .setSurname(user.getSurname() != null ? user.getSurname() : "")
      .setPhoto(user.getPhoto() != null ? map(user.getPhoto()) : ByteString.EMPTY)
      .setCountryId(user.getCountryId().toString())
      .build();
  }

  default @Nonnull UsersPaginatedResponse toProtoList(Page<UserEntity> users) {
    return users.isEmpty() ? UsersPaginatedResponse.getDefaultInstance() :
      UsersPaginatedResponse.newBuilder().addAllUsers(
        users.stream()
          .map(this::toProto)
          .collect(Collectors.toList()))
        .setPaginationResponse(createPaginationResponse(users))
      .build();
  }

  default @Nonnull Friend toProtoFriend(FriendWithStatus user) {
    return user == null ? Friend.getDefaultInstance() :
      Friend.newBuilder()
        .setId(user.id().toString())
        .setUsername(user.username())
        .setFirstname(user.firstname() != null ? user.firstname() : "")
        .setSurname(user.surname() != null ? user.surname() : "")
        .setPhoto(user.photo() != null ? map(user.photo()) : ByteString.EMPTY)
        .setCountryId(!user.countryId().toString().isEmpty() ? user.countryId().toString() : "")
        .setStatus(user.status() != null ? mapStatus(user.status()) : FriendStatus.UNRECOGNIZED)
        .build();
  }

  default  @Nonnull FriendshipResponse toProtoFriendship(FriendWithStatus user) {
    return user == null ? FriendshipResponse.getDefaultInstance() :
      FriendshipResponse.newBuilder()
        .setId(user.id().toString())
        .setUsername(user.username())
        .setStatus(mapStatus(user.status()))
        .build();
  }

  default  @Nonnull AllFriendsPaginatedResponse toProtoFriendsList(Page<FriendWithStatus> friends) {
    return friends.isEmpty() ? AllFriendsPaginatedResponse.getDefaultInstance() :
      AllFriendsPaginatedResponse.newBuilder().addAllFriends(
          friends.stream()
            .map(this::toProtoFriend)
            .collect(Collectors.toList()))
        .setPaginationResponse(createPaginationResponse(friends))
        .build();
  }

  default  @Nonnull InvitationsPaginatedResponse toProtoInvitationsList(Page<FriendWithStatus> friends) {
    return friends.isEmpty() ? InvitationsPaginatedResponse.getDefaultInstance() :
      InvitationsPaginatedResponse.newBuilder().addAllInvitations(
          friends.stream()
            .map(this::toProtoFriend)
            .collect(Collectors.toList()))
        .setPaginationResponse(createPaginationResponse(friends))
        .build();
  }

  default @Nonnull UserEntity fromProtoRequest(UserUpdateRequest user) {
    UserEntity ue = new UserEntity();
    ue.setUsername(user.getUsername());
    ue.setFirstname(user.getFirstname());
    ue.setSurname(user.getSurname());
    ue.setPhoto(user.getPhoto().toByteArray());
    ue.setCountryId(UUID.fromString(user.getCountryId()));
    return ue;
  }

  default  @Nonnull ByteString map(@Nullable byte[] value) {
    return value == null ? ByteString.EMPTY : ByteString.copyFrom(value);
  }

  default @Nonnull PaginationResponse createPaginationResponse(Page<?> page) {
    return PaginationResponse.newBuilder()
      .setCurrentPage(page.getNumber())
      .setPageSize(page.getSize())
      .setTotalPages(page.getTotalPages())
      .setTotalElements(page.getTotalElements())
      .setHasNext(page.hasNext())
      .setHasPrevious(page.hasPrevious())
      .build();
  }
}
