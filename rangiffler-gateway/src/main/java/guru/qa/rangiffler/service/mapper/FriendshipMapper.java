package guru.qa.rangiffler.service.mapper;

import com.google.protobuf.ByteString;
import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.model.graphql.PageInfoGql;
import guru.qa.rangiffler.model.graphql.countries.CountryGql;
import guru.qa.rangiffler.model.graphql.userdata.UserGql;
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
public interface FriendshipMapper {

  String DEFAULT_IMAGE_TYPE = "png";

  @Nonnull
  default UserGql toUserGql(Friend friend, CountryResponse country) {
    return new UserGql(
      UUID.fromString(friend.getId()),
      friend.getUsername(),
      friend.getFirstname(),
      friend.getSurname(),
      bytesToDataUrl(friend.getPhoto().toByteArray()),
      friend.getStatus(),
      new CountryGql(
        UUID.fromString(country.getId()),
        country.getName(),
        country.getCode(),
        bytesToDataUrl(country.getFlag().toByteArray())
      )
    );
  }

  @Nonnull
  default UsersSliceGql toFriendsListGql(AllFriendsPaginatedResponse response, CountriesResponse countries) {
    final List<CountryResponse> countriesList = countries.getCountriesList();

    final Map<String, CountryResponse> countryMap = countriesList.stream()
      .collect(Collectors.toMap(
        CountryResponse::getId,
        country -> country
      ));

    final List<UserGql> friends = response.getFriendsList().stream()
      .map(friend -> {
        CountryResponse country = countryMap.get(friend.getCountryId());
        if (country == null) {
          throw new IllegalArgumentException("Country not found for user: " + friend.getId());
        }
        return toUserGql(friend, country);
      })
      .toList();

    final PaginationResponse pagination = response.getPaginationResponse();

    if (!pagination.isInitialized()) {
      return new UsersSliceGql(friends, PageInfoGql.unpaged());
    }

    final int pageSize = pagination.getPageSize();
    if (pageSize < 1) {
      return new UsersSliceGql(friends, PageInfoGql.unpaged());
    }

    return new UsersSliceGql(
      friends,
      new PageInfoGql(pagination.getHasNext(), pagination.getHasPrevious())
    );
  }

  @Nonnull
  default UsersSliceGql toInvitationsListGql(InvitationsPaginatedResponse response, CountriesResponse countries) {
    final List<CountryResponse> countriesList = countries.getCountriesList();

    final Map<String, CountryResponse> countryMap = countriesList.stream()
      .collect(Collectors.toMap(
        CountryResponse::getId,
        country -> country
      ));

    final List<UserGql> invitations = response.getInvitationsList().stream()
      .map(friend -> {
        CountryResponse country = countryMap.get(friend.getCountryId());
        if (country == null) {
          throw new IllegalArgumentException("Country not found for user: " + friend.getId());
        }
        return toUserGql(friend, country);
      })
      .toList();

    final PaginationResponse pagination = response.getPaginationResponse();

    if (!pagination.isInitialized()) {
      return new UsersSliceGql(invitations, PageInfoGql.unpaged());
    }

    final int pageSize = pagination.getPageSize();
    if (pageSize < 1) {
      return new UsersSliceGql(invitations, PageInfoGql.unpaged());
    }

    return new UsersSliceGql(
      invitations,
      new PageInfoGql(pagination.getHasNext(), pagination.getHasPrevious())
    );
  }

  @Nonnull
  default AllFriendsPaginatedRequest toProtoFriendsRequest(Pageable pageable, String username, @Nullable String searchQuery) {
    if (username == null || username.isEmpty()) {
      return AllFriendsPaginatedRequest.getDefaultInstance();
    }

    final AllFriendsPaginatedRequest.Builder builder = AllFriendsPaginatedRequest.newBuilder();
    builder.setTargetUsername(username);

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
    if (searchQuery != null) {
      builder.setSearchQuery(searchQuery);
    }

    return builder.build();
  }

  @Nonnull
  default InvitationsPaginatedRequest toProtoInvitationsRequest(Pageable pageable, String username, @Nullable String searchQuery) {
    if (username == null || username.isEmpty()) {
      return InvitationsPaginatedRequest.getDefaultInstance();
    }

    final InvitationsPaginatedRequest.Builder builder = InvitationsPaginatedRequest.newBuilder();
    builder.setTargetUsername(username);

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
    if (searchQuery != null) {
      builder.setSearchQuery(searchQuery);
    }

    return builder.build();
  }

  @Nonnull
  default FriendshipRequest toFriendshipRequest(String username, String targetUsername) {
    if (username.isEmpty() || targetUsername.isEmpty()) {
      return FriendshipRequest.getDefaultInstance();
    }

    return FriendshipRequest.newBuilder()
      .setUsername(username)
      .setTargetUsername(targetUsername)
      .build();
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
