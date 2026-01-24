package guru.qa.rangiffler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rangiffler.data.entity.userdata.FriendshipStatus;
import guru.qa.rangiffler.data.entity.userdata.UdUserEntity;
import guru.qa.rangiffler.grpc.Friend;
import guru.qa.rangiffler.grpc.UserResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@ParametersAreNonnullByDefault
public record UdUserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("surname")
        String surname,
        @JsonProperty("photo")
        String photo,
        @JsonProperty("friendshipStatus")
        FriendshipStatus friendshipStatus,
        @JsonIgnore
        TestData testData)
{
  public UdUserJson(@Nonnull String username, @Nullable TestData testData) {
    this(null, username, null, null, null, null, testData);
  }

  @Nonnull
  public static UdUserJson fromEntity(UdUserEntity entity, @Nullable FriendshipStatus friendshipStatus) {
    return new UdUserJson(
            entity.getId(),
            entity.getUsername(),
            entity.getFirstname(),
            entity.getSurname(),
            entity.getPhoto() != null && entity.getPhoto().length > 0 ? new String(entity.getPhoto(), StandardCharsets.UTF_8) : null,
            friendshipStatus,
            null
    );
  }

  @Nonnull
  public static UdUserJson fromProto(UserResponse user) {
    return new UdUserJson(
      UUID.fromString(user.getId()),
      user.getUsername(),
      user.getFirstname(),
      user.getSurname(),
      user.getPhoto() != null ? user.getPhoto().toString() : null,
      null,
      null
    );
  }

  @Nonnull
  public static UdUserJson fromProto(Friend user) {
    return new UdUserJson(
      UUID.fromString(user.getId()),
      user.getUsername(),
      user.getFirstname(),
      user.getSurname(),
      user.getPhoto() != null ? user.getPhoto().toString() : null,
      null,
      null
    );
  }

  @Nonnull
  public UdUserJson addTestData(TestData testData) {
    return new UdUserJson(
            id,
            username,
            firstname,
            surname,
            photo,
            friendshipStatus,
            testData
    );
  }
}