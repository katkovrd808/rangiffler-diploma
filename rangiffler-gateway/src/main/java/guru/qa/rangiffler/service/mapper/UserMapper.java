package guru.qa.rangiffler.service.mapper;

import com.google.protobuf.ByteString;
import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.model.graphql.PageInfoGql;
import guru.qa.rangiffler.model.graphql.countries.CountryGql;
import guru.qa.rangiffler.model.graphql.userdata.UserGql;
import guru.qa.rangiffler.model.graphql.userdata.UserInputGql;
import guru.qa.rangiffler.model.graphql.userdata.UsersSliceGql;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
@ParametersAreNonnullByDefault
public interface UserMapper {

  String DEFAULT_IMAGE_TYPE = "png";

  @Nonnull
  default UserGql toUserGql(UserResponse user, CountryResponse country) {
    return new UserGql(
      UUID.fromString(user.getId()),
      user.getUsername(),
      user.getFirstname(),
      user.getSurname(),
      bytesToDataUrl(user.getPhoto().toByteArray()),
      user.getFriendStatus(),
      new CountryGql(
        UUID.fromString(country.getId()),
        country.getName(),
        country.getCode(),
        bytesToDataUrl(country.getFlag().toByteArray())
      )
    );
  }

  @Nonnull
  default UserGql toUserGql(UserUpdateResponse user, CountryResponse country) {
    return new UserGql(
      UUID.fromString(user.getId()),
      user.getUsername(),
      user.getFirstname(),
      user.getSurname(),
      bytesToDataUrl(user.getPhoto().toByteArray()),
      null,
      new CountryGql(
        UUID.fromString(country.getId()),
        country.getName(),
        country.getCode(),
        bytesToDataUrl(country.getFlag().toByteArray())
      )
    );
  }

  @Nonnull
  default UsersSliceGql toUsersListGql(UsersPaginatedResponse response, CountriesResponse countries) {
    final List<CountryResponse> countriesList = countries.getCountriesList();

    final Map<String, CountryResponse> countryMap = countriesList.stream()
      .collect(Collectors.toMap(
        CountryResponse::getId,
        country -> country
      ));

    final List<UserGql> users = response.getUsersList().stream()
      .map(user -> {
        CountryResponse country = countryMap.get(user.getCountryId());
        if (country == null) {
          throw new IllegalArgumentException("Country not found for user: " + user.getId());
        }
        return toUserGql(user, country);
      })
      .toList();

    final PaginationResponse pagination = response.getPaginationResponse();

    if (!pagination.isInitialized()) {
      return new UsersSliceGql(users, PageInfoGql.unpaged());
    }

    final int pageSize = pagination.getPageSize();
    if (pageSize < 1) {
      return new UsersSliceGql(users, PageInfoGql.unpaged());
    }

    return new UsersSliceGql(users, new PageInfoGql(
      pagination.getHasNext(),
      pagination.getHasPrevious()
    ));
  }

  @Nonnull
  default UserUpdateRequest toProtoUpdateUserRequest(String username, UserInputGql user, String countryId) {
    if (username == null || username.isEmpty()) {
      return UserUpdateRequest.getDefaultInstance();
    }

    final UserUpdateRequest.Builder builder = UserUpdateRequest.newBuilder();
    builder.setUsername(username);

    builder.setFirstname(user.firstname());
    builder.setSurname(user.surname());
    builder.setCountryId(countryId);
    if (user.avatar() != null && user.avatar().length > 0) {
      builder.setPhoto(dataUrlToByteString(user.avatar()));
    }

    return builder.build();
  }

  @Nonnull
  default UsersPaginatedRequest toProtoUsersRequest(Pageable pageable, String username, @Nullable String searchQuery) {
    if (username == null || username.isEmpty()) {
      return UsersPaginatedRequest.getDefaultInstance();
    }

    final UsersPaginatedRequest.Builder builder = UsersPaginatedRequest.newBuilder();
    builder.setExcludeUsername(username);
    builder.setSearchQuery(searchQuery);

    if (pageable.isPaged()) {
      PaginationRequest paginationRequest = PaginationRequest.newBuilder()
        .setSize(pageable.getPageSize())
        .setPage(pageable.getPageNumber())
        .build();
      builder.setPaginationRequest(paginationRequest);
    } else {
      PaginationRequest defaultInstance = PaginationRequest.getDefaultInstance();
      builder.setPaginationRequest(defaultInstance);
    }

    return builder.build();
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

    if (id != null) {
      builder.setId(id.toString());
    }

    return builder.build();
  }

  @Nonnull
  default UserWithStatusRequest toProtoRequest(String targetUserId, String currentUserId) {
    if (currentUserId == null || targetUserId == null) {
      return UserWithStatusRequest.getDefaultInstance();
    }

    UserWithStatusRequest.Builder builder = UserWithStatusRequest.newBuilder();

    if (!currentUserId.isEmpty()) {
      builder.setCurrentUserId(currentUserId);
    }

    if (!targetUserId.isEmpty()) {
      builder.setTargetUserId(targetUserId);
    }

    return builder.build();
  }

  @Nonnull
  private String bytesToDataUrl(byte[] photoBytes) {
    if (photoBytes == null || photoBytes.length == 0) {
      return "";
    }

    try {
      String photoString = new String(photoBytes, StandardCharsets.UTF_8);

      if (isDataUrl(photoString)) {
        return cleanDataUrl(photoString);
      }

      if (isBase64WithoutPrefix(photoString)) {
        return "data:image/" + DEFAULT_IMAGE_TYPE + ";base64," + photoString;
      }

    } catch (Exception e) {
      //NOP
    }

    String base64 = Base64.getEncoder().encodeToString(photoBytes);
    return "data:image/" + DEFAULT_IMAGE_TYPE + ";base64," + base64;
  }

  @Nonnull
  private ByteString dataUrlToByteString(byte[] dataUrlBytes) {
    if (dataUrlBytes == null || dataUrlBytes.length == 0) {
      return ByteString.EMPTY;
    }

    try {
      String dataUrl = new String(dataUrlBytes, StandardCharsets.UTF_8);

      if (!isDataUrl(dataUrl)) {
        String base64 = Base64.getEncoder().encodeToString(dataUrlBytes);
        dataUrl = "data:image/" + DEFAULT_IMAGE_TYPE + ";base64," + base64;
      }

      String cleanedDataUrl = cleanDataUrl(dataUrl);
      return ByteString.copyFromUtf8(cleanedDataUrl);

    } catch (Exception e) {
      String base64 = Base64.getEncoder().encodeToString(dataUrlBytes);
      String dataUrl = "data:image/" + DEFAULT_IMAGE_TYPE + ";base64," + base64;
      return ByteString.copyFromUtf8(dataUrl);
    }
  }

  private boolean isDataUrl(String str) {
    if (str == null || str.length() < 20) {
      return false;
    }

    String lowerStr = str.toLowerCase();
    return lowerStr.startsWith("data:image/") &&
      lowerStr.contains(";base64,") &&
      lowerStr.length() > lowerStr.indexOf(";base64,") + 8;
  }

  private boolean isBase64WithoutPrefix(String str) {
    if (str == null || str.isEmpty()) {
      return false;
    }

    String trimmed = str.trim();
    return trimmed.matches("^[A-Za-z0-9+/]*={0,2}$") &&
      trimmed.length() % 4 == 0 &&
      trimmed.length() > 20;
  }

  @Nonnull
  private String cleanDataUrl(String dataUrl) {
    if (dataUrl == null) {
      return "";
    }

    String cleaned = dataUrl
      .replaceAll("\\x00", "")
      .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "")
      .trim();

    if (isDataUrl(cleaned)) {
      return cleaned;
    }

    return dataUrl.replace("\u0000", "").trim();
  }
}
