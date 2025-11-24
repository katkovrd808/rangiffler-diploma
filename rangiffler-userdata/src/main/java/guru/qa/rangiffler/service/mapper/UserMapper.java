package guru.qa.rangiffler.service.mapper;

import com.google.protobuf.ByteString;
import guru.qa.rangiffler.data.FriendshipStatus;
import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.data.projection.UserWithStatus;
import guru.qa.rangiffler.grpc.*;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
@ParametersAreNonnullByDefault
public interface UserMapper {
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

  //TODO fix mapper for Invitation_Received status
  default @Nonnull Friend toProtoFriend(UserWithStatus user) {
    return user == null ? Friend.getDefaultInstance() :
      Friend.newBuilder()
        .setId(user.id().toString())
        .setUsername(user.username())
        .setFirstname(user.firstname() != null ? user.firstname() : "")
        .setSurname(user.surname() != null ? user.surname() : "")
        .setPhoto(user.photo() != null ? map(user.photo()) : ByteString.EMPTY)
        .setCountryId(!user.countryId().toString().isEmpty() ? user.countryId().toString() : "")
        .setStatus(resolveStatus(user))
        .build();
  }

  default @Nonnull FriendshipResponse toProtoFriendship(UserWithStatus user) {
    return user == null ? FriendshipResponse.getDefaultInstance() :
      FriendshipResponse.newBuilder()
        .setId(user.id().toString())
        .setUsername(user.username())
        .setStatus(resolveStatus(user))
        .build();
  }

  default @Nonnull AllFriendsPaginatedResponse toProtoFriendsList(Page<UserWithStatus> friends) {
    return friends.isEmpty() ? AllFriendsPaginatedResponse.getDefaultInstance() :
      AllFriendsPaginatedResponse.newBuilder().addAllFriends(
          friends.stream()
            .map(this::toProtoFriend)
            .collect(Collectors.toList()))
        .setPaginationResponse(createPaginationResponse(friends))
        .build();
  }

  default @Nonnull InvitationsPaginatedResponse toProtoInvitationsList(Page<UserWithStatus> friends) {
    return friends.isEmpty() ? InvitationsPaginatedResponse.getDefaultInstance() :
      InvitationsPaginatedResponse.newBuilder().addAllInvitations(
          friends.stream()
            .map(this::toProtoFriend)
            .collect(Collectors.toList()))
        .setPaginationResponse(createPaginationResponse(friends))
        .build();
  }

  private @Nonnull ByteString map(@Nullable byte[] value) {
    return value == null ? ByteString.EMPTY : ByteString.copyFrom(value);
  }

  private @Nonnull PaginationResponse createPaginationResponse(Page<?> page) {
    return PaginationResponse.newBuilder()
      .setCurrentPage(page.getNumber())
      .setPageSize(page.getSize())
      .setTotalPages(page.getTotalPages())
      .setTotalElements(page.getTotalElements())
      .setHasNext(page.hasNext())
      .setHasPrevious(page.hasPrevious())
      .build();
  }

  private @Nonnull guru.qa.rangiffler.grpc.FriendStatus resolveStatus(UserWithStatus user) {
    return switch (user.status()) {
      case ACCEPTED -> FriendStatus.FRIEND;
      case PENDING -> user.isRequester() ? FriendStatus.INVITATION_SENT : FriendStatus.INVITATION_RECEIVED;
      case DECLINED, DELETED -> FriendStatus.NOT_FRIEND;
    };
  }
}
