package guru.qa.rangiffler.service;

import guru.qa.rangiffler.api.GrpcCountriesClient;
import guru.qa.rangiffler.api.GrpcUserdataClient;
import guru.qa.rangiffler.data.PhotoEntity;
import guru.qa.rangiffler.data.PhotoLikeEntity;
import guru.qa.rangiffler.data.projection.PhotoWithLikes;
import guru.qa.rangiffler.data.repository.PhotoLikeRepository;
import guru.qa.rangiffler.data.repository.PhotoRepository;
import guru.qa.rangiffler.ex.PhotoNotFoundException;
import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.model.CountryDto;
import guru.qa.rangiffler.model.FriendDto;
import guru.qa.rangiffler.model.UserDto;
import guru.qa.rangiffler.service.impl.DbPhotoService;
import guru.qa.rangiffler.service.mapper.PhotoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PhotoServiceTest {
  @Mock
  private PhotoRepository photoRepository;

  @Mock
  private GrpcCountriesClient grpcCountriesClient;

  @Mock
  private GrpcUserdataClient grpcUserdataClient;

  @Mock
  private PhotoMapper photoMapper;

  @Mock
  private PhotoLikeRepository photoLikeRepository;

  @Mock
  private Pageable pageable;

  private DbPhotoService dbPhotoService;

  @BeforeEach
  void setUp() {
    dbPhotoService = new DbPhotoService(
      photoRepository, grpcCountriesClient, grpcUserdataClient,
      photoMapper, photoLikeRepository
    );
  }

  @Test
  void createPhoto_WithValidData_ShouldCreatePhoto() {
    final String userId = UUID.randomUUID().toString();
    final String countryCode = "US";
    final byte[] photoData = "photo_data".getBytes();

    final PhotoRequest request = PhotoRequest.newBuilder()
      .setUserId(userId)
      .setCountryCode(countryCode)
      .setDescription("Test photo")
      .setSrc(com.google.protobuf.ByteString.copyFrom(photoData))
      .build();

    final CountryDto countryDto = new CountryDto(UUID.randomUUID(), "United States", "US", new byte[]{}, 0);
    final PhotoEntity savedPhoto = new PhotoEntity();
    savedPhoto.setId(UUID.randomUUID());
    savedPhoto.setUserId(UUID.fromString(userId));
    savedPhoto.setCountryId(countryDto.id());
    savedPhoto.setDescription("Test photo");
    savedPhoto.setPhoto(photoData);

    final PhotoResponse expectedResponse = PhotoResponse.newBuilder()
      .setId(savedPhoto.getId().toString())
      .build();

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.of(new UserDto(UUID.fromString(userId), "testuser")));
    when(grpcCountriesClient.getCountryByCode(countryCode)).thenReturn(Optional.of(countryDto));
    when(photoRepository.save(any(PhotoEntity.class))).thenReturn(savedPhoto);
    when(photoMapper.toProto(any(PhotoWithLikes.class), eq(countryDto))).thenReturn(expectedResponse);

    final PhotoResponse actualResponse = dbPhotoService.createPhoto(request);

    verify(grpcUserdataClient).getUserById(userId);
    verify(grpcCountriesClient).getCountryByCode(countryCode);
    verify(photoRepository).save(any(PhotoEntity.class));
    verify(photoMapper).toProto(any(PhotoWithLikes.class), eq(countryDto));
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void createPhoto_WithEmptyUserId_ShouldThrowException() {
    final PhotoRequest request = PhotoRequest.newBuilder()
      .setUserId("")
      .setCountryCode("US")
      .build();

    final IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> dbPhotoService.createPhoto(request)
    );

    assertEquals("User can't be null.", exception.getMessage());
    verify(grpcUserdataClient, never()).getUserById(any());
    verify(photoRepository, never()).save(any());
  }

  @Test
  void updatePhoto_WithValidData_ShouldUpdatePhoto() {
    final String userId = UUID.randomUUID().toString();
    final String photoId = UUID.randomUUID().toString();
    final String countryCode = "US";

    final PhotoUpdateRequest request = PhotoUpdateRequest.newBuilder()
      .setId(photoId)
      .setUserId(userId)
      .setDescription("Updated description")
      .setSrc(com.google.protobuf.ByteString.copyFrom("updated_photo".getBytes()))
      .setCountry(CountryPhoto.newBuilder()
        .setCode(countryCode)
        .build())
      .build();

    final PhotoEntity existingPhoto = new PhotoEntity();
    existingPhoto.setId(UUID.fromString(photoId));
    existingPhoto.setUserId(UUID.fromString(userId));
    existingPhoto.setCountryId(UUID.randomUUID());

    final CountryDto countryDto = new CountryDto(UUID.randomUUID(), "United States", "US", new byte[]{}, 0);
    final PhotoEntity savedPhoto = new PhotoEntity();
    savedPhoto.setId(UUID.fromString(photoId));

    final PhotoResponse expectedResponse = PhotoResponse.newBuilder()
      .setId(photoId)
      .build();

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.of(new UserDto(UUID.fromString(userId), "testuser")));
    when(photoRepository.findById(UUID.fromString(photoId))).thenReturn(Optional.of(existingPhoto));
    when(grpcCountriesClient.getCountryByCode(countryCode)).thenReturn(Optional.of(countryDto));
    when(photoRepository.save(existingPhoto)).thenReturn(savedPhoto);
    when(photoMapper.toProto(any(PhotoWithLikes.class), eq(countryDto))).thenReturn(expectedResponse);

    final PhotoResponse actualResponse = dbPhotoService.updatePhoto(request);

    verify(grpcUserdataClient).getUserById(userId);
    verify(photoRepository).findById(UUID.fromString(photoId));
    verify(grpcCountriesClient).getCountryByCode(countryCode);
    verify(photoRepository).save(existingPhoto);
    verify(photoMapper).toProto(any(PhotoWithLikes.class), eq(countryDto));
    assertEquals(expectedResponse, actualResponse);
    assertEquals(countryDto.id(), existingPhoto.getCountryId());
    assertEquals("Updated description", existingPhoto.getDescription());
  }

  @Test
  void updatePhoto_WhenNotOwnPhoto_ShouldThrowException() {
    final String userId = UUID.randomUUID().toString();
    final String otherUserId = UUID.randomUUID().toString();
    final String photoId = UUID.randomUUID().toString();

    final PhotoUpdateRequest request = PhotoUpdateRequest.newBuilder()
      .setId(photoId)
      .setUserId(userId)
      .setCountry(CountryPhoto.newBuilder().setCode("US").build())
      .build();

    final PhotoEntity existingPhoto = new PhotoEntity();
    existingPhoto.setId(UUID.fromString(photoId));
    existingPhoto.setUserId(UUID.fromString(otherUserId));

    when(photoRepository.findById(UUID.fromString(photoId))).thenReturn(Optional.of(existingPhoto));

    final SecurityException exception = assertThrows(
      SecurityException.class,
      () -> dbPhotoService.updatePhoto(request)
    );

    assertEquals("User can only update their own photos.", exception.getMessage());
    verify(photoRepository).findById(UUID.fromString(photoId));
    verify(photoRepository, never()).save(any());
    verify(grpcUserdataClient, never()).getUserById(any());
  }

  @Test
  void deletePhoto_WithValidData_ShouldDeletePhoto() {
    final String userId = UUID.randomUUID().toString();
    final String photoId = UUID.randomUUID().toString();

    final PhotoDeleteRequest request = PhotoDeleteRequest.newBuilder()
      .setId(photoId)
      .setUserId(userId)
      .build();

    final PhotoEntity photo = new PhotoEntity();
    photo.setId(UUID.fromString(photoId));
    photo.setUserId(UUID.fromString(userId));

    final PhotoDeleteResponse expectedResponse = PhotoDeleteResponse.newBuilder()
      .setId(photoId)
      .build();

    when(photoRepository.findById(UUID.fromString(photoId))).thenReturn(Optional.of(photo));
    when(photoMapper.toPhotoDeleteResponse(photoId)).thenReturn(expectedResponse);

    final PhotoDeleteResponse actualResponse = dbPhotoService.deletePhoto(request);

    verify(photoRepository).findById(UUID.fromString(photoId));
    verify(photoRepository).delete(photo);
    verify(photoRepository).flush();
    verify(photoMapper).toPhotoDeleteResponse(photoId);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void deletePhoto_WhenNotOwnPhoto_ShouldThrowException() {
    final String userId = UUID.randomUUID().toString();
    final String otherUserId = UUID.randomUUID().toString();
    final String photoId = UUID.randomUUID().toString();

    final PhotoDeleteRequest request = PhotoDeleteRequest.newBuilder()
      .setId(photoId)
      .setUserId(userId)
      .build();

    final PhotoEntity photo = new PhotoEntity();
    photo.setId(UUID.fromString(photoId));
    photo.setUserId(UUID.fromString(otherUserId));

    when(photoRepository.findById(UUID.fromString(photoId))).thenReturn(Optional.of(photo));

    final SecurityException exception = assertThrows(
      SecurityException.class,
      () -> dbPhotoService.deletePhoto(request)
    );

    assertEquals("User can only update their own photos.", exception.getMessage());
    verify(photoRepository).findById(UUID.fromString(photoId));
    verify(photoRepository, never()).delete(any());
    verify(photoRepository, never()).flush();
  }

  @Test
  void deletePhoto_WhenPhotoNotFound_ShouldThrowException() {
    final String userId = UUID.randomUUID().toString();
    final String photoId = UUID.randomUUID().toString();

    final PhotoDeleteRequest request = PhotoDeleteRequest.newBuilder()
      .setId(photoId)
      .setUserId(userId)
      .build();

    when(photoRepository.findById(UUID.fromString(photoId))).thenReturn(Optional.empty());

    final PhotoNotFoundException exception = assertThrows(
      PhotoNotFoundException.class,
      () -> dbPhotoService.deletePhoto(request)
    );

    assertEquals("Can't find photo with id " + photoId, exception.getMessage());
    verify(photoRepository).findById(UUID.fromString(photoId));
    verify(photoRepository, never()).delete(any());
  }

  @Test
  void getPhotoWithLikes_ShouldReturnPhotoWithLikes() {
    final String photoId = UUID.randomUUID().toString();
    final String countryId = UUID.randomUUID().toString();

    final PhotoWithLikesRequest request = PhotoWithLikesRequest.newBuilder()
      .setId(photoId)
      .build();

    final PhotoEntity photo = new PhotoEntity();
    photo.setId(UUID.fromString(photoId));
    photo.setCountryId(UUID.fromString(countryId));

    final List<PhotoLikeEntity> photoLikes = List.of(
      new PhotoLikeEntity(),
      new PhotoLikeEntity()
    );

    final CountryDto countryDto = new CountryDto(UUID.fromString(countryId), "Test Country", "TC", new byte[]{}, 0);
    final PhotoResponse expectedResponse = PhotoResponse.newBuilder()
      .setId(photoId)
      .build();

    when(photoRepository.findById(UUID.fromString(photoId))).thenReturn(Optional.of(photo));
    when(photoLikeRepository.findPhotoLikesByPhotoId(UUID.fromString(photoId))).thenReturn(photoLikes);
    when(grpcCountriesClient.getCountryById(countryId)).thenReturn(Optional.of(countryDto));
    when(photoMapper.toProto(any(PhotoWithLikes.class), eq(countryDto))).thenReturn(expectedResponse);

    final PhotoResponse actualResponse = dbPhotoService.getPhotoWithLikes(request);

    verify(photoRepository).findById(UUID.fromString(photoId));
    verify(photoLikeRepository).findPhotoLikesByPhotoId(UUID.fromString(photoId));
    verify(grpcCountriesClient).getCountryById(countryId);
    verify(photoMapper).toProto(any(PhotoWithLikes.class), eq(countryDto));
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void getUserPhotos_ShouldReturnUserPhotos() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .build();

    final UUID countryId1 = UUID.randomUUID();
    final UUID countryId2 = UUID.randomUUID();
    final List<PhotoEntity> photos = List.of(
      createPhotoEntity(UUID.randomUUID(), UUID.fromString(userId), countryId1),
      createPhotoEntity(UUID.randomUUID(), UUID.fromString(userId), countryId2)
    );
    final Page<PhotoEntity> photoPage = new PageImpl<>(photos);

    final List<CountryDto> countries = List.of(
      new CountryDto(countryId1, "Country1", "C1", new byte[]{}, 0),
      new CountryDto(countryId2, "Country2", "C2", new byte[]{}, 0)
    );

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.of(new UserDto(UUID.fromString(userId), "testuser")));
    when(photoRepository.findPhotosByUserId(UUID.fromString(userId), pageable)).thenReturn(photoPage);
    when(photoLikeRepository.findLikesByPhotoIds(anyList())).thenReturn(List.of());
    when(grpcCountriesClient.getCountriesByIds(List.of(countryId1, countryId2))).thenReturn(countries);
    when(photoMapper.toProto(any(PhotoWithLikes.class), any(CountryDto.class)))
      .thenAnswer(invocation -> {
        CountryDto country = invocation.getArgument(1);
        return PhotoResponse.newBuilder()
          .setId("test-id")
          .setCountry(CountryPhoto.newBuilder()
            .setCode(country.code())
            .build())
          .build();
      });

    final Page<PhotoResponse> actualPage = dbPhotoService.getUserPhotos(request, pageable);

    verify(grpcUserdataClient).getUserById(userId);
    verify(photoRepository).findPhotosByUserId(UUID.fromString(userId), pageable);
    verify(photoLikeRepository).findLikesByPhotoIds(anyList());
    verify(grpcCountriesClient).getCountriesByIds(List.of(countryId1, countryId2));
    assertEquals(2, actualPage.getContent().size());
  }

  @Test
  void getAllPhotos_ShouldReturnAllPhotos() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .build();

    final UUID countryId1 = UUID.randomUUID();
    final UUID countryId2 = UUID.randomUUID();
    final List<PhotoEntity> photos = List.of(
      createPhotoEntity(UUID.randomUUID(), UUID.randomUUID(), countryId1),
      createPhotoEntity(UUID.randomUUID(), UUID.randomUUID(), countryId2)
    );
    final Page<PhotoEntity> photoPage = new PageImpl<>(photos);

    final List<CountryDto> countries = List.of(
      new CountryDto(countryId1, "Country1", "C1", new byte[]{}, 0),
      new CountryDto(countryId2, "Country2", "C2", new byte[]{}, 0)
    );

    when(photoRepository.findAll(pageable)).thenReturn(photoPage);
    when(photoLikeRepository.findLikesByPhotoIds(anyList())).thenReturn(List.of());
    when(grpcCountriesClient.getCountriesByIds(List.of(countryId1, countryId2))).thenReturn(countries);
    when(photoMapper.toProto(any(PhotoWithLikes.class), any(CountryDto.class)))
      .thenAnswer(invocation -> {
        CountryDto country = invocation.getArgument(1);
        return PhotoResponse.newBuilder()
          .setId("test-id")
          .setCountry(CountryPhoto.newBuilder()
            .setCode(country.code())
            .build())
          .build();
      });

    final Page<PhotoResponse> actualPage = dbPhotoService.getAllPhotos(request, pageable);

    verify(photoRepository).findAll(pageable);
    verify(photoLikeRepository).findLikesByPhotoIds(anyList());
    verify(grpcCountriesClient).getCountriesByIds(List.of(countryId1, countryId2));
    assertEquals(2, actualPage.getContent().size());
  }

  @Test
  void getFriendsPhotos_ShouldReturnFriendsPhotos() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .build();

    final UUID friendId1 = UUID.randomUUID();
    final UUID friendId2 = UUID.randomUUID();
    final List<FriendDto> friends = List.of(
      new FriendDto(friendId1, "friend1"),
      new FriendDto(friendId2, "friend2")
    );

    final UUID countryId1 = UUID.randomUUID();
    final UUID countryId2 = UUID.randomUUID();
    final List<PhotoEntity> photos = List.of(
      createPhotoEntity(UUID.randomUUID(), friendId1, countryId1),
      createPhotoEntity(UUID.randomUUID(), friendId2, countryId2)
    );
    final Page<PhotoEntity> photoPage = new PageImpl<>(photos);

    final List<CountryDto> countries = List.of(
      new CountryDto(countryId1, "Country1", "C1", new byte[]{}, 0),
      new CountryDto(countryId2, "Country2", "C2", new byte[]{}, 0)
    );

    when(grpcUserdataClient.getAllFriendsByUserId(userId)).thenReturn(friends);
    when(photoRepository.findFriendsPhoto(UUID.fromString(userId), List.of(friendId1, friendId2), pageable))
      .thenReturn(photoPage);
    when(photoLikeRepository.findLikesByPhotoIds(anyList())).thenReturn(List.of());
    when(grpcCountriesClient.getCountriesByIds(List.of(countryId1, countryId2))).thenReturn(countries);
    when(photoMapper.toProto(any(PhotoWithLikes.class), any(CountryDto.class)))
      .thenAnswer(invocation -> {
        CountryDto country = invocation.getArgument(1);
        return PhotoResponse.newBuilder()
          .setId("test-id")
          .setCountry(CountryPhoto.newBuilder()
            .setCode(country.code())
            .build())
          .build();
      });

    final Page<PhotoResponse> actualPage = dbPhotoService.getFriendsPhotos(request, pageable);

    verify(grpcUserdataClient).getAllFriendsByUserId(userId);
    verify(photoRepository).findFriendsPhoto(UUID.fromString(userId), List.of(friendId1, friendId2), pageable);
    verify(photoLikeRepository).findLikesByPhotoIds(anyList());
    verify(grpcCountriesClient).getCountriesByIds(List.of(countryId1, countryId2));
    assertEquals(2, actualPage.getContent().size());
  }

  @Test
  void getFriendsPhotos_WhenNoFriends_ShouldReturnEmptyPage() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .build();

    final UUID photoId = UUID.randomUUID();
    final UUID countryId = UUID.randomUUID();
    final PhotoEntity photo = createPhotoEntity(photoId, UUID.fromString(userId), countryId);
    final Page<PhotoEntity> photoPage = new PageImpl<>(List.of(photo));

    final CountryDto countryDto = new CountryDto(countryId, "Test Country", "TC", new byte[]{}, 0);
    final PhotoResponse photoResponse = PhotoResponse.newBuilder()
      .setId(photoId.toString())
      .build();

    when(grpcUserdataClient.getAllFriendsByUserId(userId)).thenReturn(List.of());
    when(photoRepository.findFriendsPhoto(UUID.fromString(userId), List.of(), pageable)).thenReturn(photoPage);
    when(photoLikeRepository.findLikesByPhotoIds(anyList())).thenReturn(List.of());
    when(grpcCountriesClient.getCountriesByIds(List.of(countryId))).thenReturn(List.of(countryDto));
    when(photoMapper.toProto(any(PhotoWithLikes.class), eq(countryDto))).thenReturn(photoResponse);

    final Page<PhotoResponse> actualPage = dbPhotoService.getFriendsPhotos(request, pageable);

    verify(grpcUserdataClient).getAllFriendsByUserId(userId);
    verify(photoRepository).findFriendsPhoto(UUID.fromString(userId), List.of(), pageable);
    assertEquals(1, actualPage.getContent().size());
  }

  @Test
  void createPhoto_WithNullUserId_ShouldThrowException() {
    final PhotoRequest request = PhotoRequest.newBuilder()
      .setUserId("")
      .build();

    final IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> dbPhotoService.createPhoto(request)
    );

    assertEquals("User can't be null.", exception.getMessage());
  }

  @Test
  void updatePhoto_WithEmptyUserId_ShouldThrowException() {
    final PhotoUpdateRequest request = PhotoUpdateRequest.newBuilder()
      .setUserId("")
      .build();

    final IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> dbPhotoService.updatePhoto(request)
    );

    assertEquals("User id can't be null value.", exception.getMessage());
    verify(photoRepository, never()).findById(any());
  }

  @Test
  void deletePhoto_WithEmptyUserId_ShouldThrowException() {
    final PhotoDeleteRequest request = PhotoDeleteRequest.newBuilder()
      .setUserId("")
      .build();

    final IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> dbPhotoService.deletePhoto(request)
    );

    assertEquals("User id can't be null value.", exception.getMessage());
    verify(photoRepository, never()).findById(any());
  }

  @Test
  void getAllPhotos_WithEmptyUserId_ShouldThrowException() {
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId("")
      .build();

    final IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> dbPhotoService.getAllPhotos(request, pageable)
    );

    assertEquals("User id can't be null value.", exception.getMessage());
    verify(photoRepository, never()).findAll((Pageable) any());
  }

  @Test
  void getFriendsPhotos_WithEmptyUserId_ShouldThrowException() {
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId("")
      .build();

    final IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> dbPhotoService.getFriendsPhotos(request, pageable)
    );

    assertEquals("User id can't be null value.", exception.getMessage());
    verify(grpcUserdataClient, never()).getAllFriendsByUserId(any());
  }

  @Test
  void getUserPhotos_WhenUserNotFound_ShouldThrowException() {
    final String userId = UUID.randomUUID().toString();
    final FeedRequest request = FeedRequest.newBuilder()
      .setUserId(userId)
      .build();

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.empty());

    final IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> dbPhotoService.getUserPhotos(request, pageable)
    );

    assertTrue(exception.getMessage().contains("User with id " + userId + " was not found."));
    verify(grpcUserdataClient).getUserById(userId);
    verify(photoRepository, never()).findPhotosByUserId(any(), any());
  }

  @Nonnull
  private PhotoEntity createPhotoEntity(UUID photoId, UUID userId, UUID countryId) {
    final PhotoEntity photo = new PhotoEntity();
    photo.setId(photoId);
    photo.setUserId(userId);
    photo.setCountryId(countryId);
    photo.setDescription("Test photo");
    photo.setPhoto("photo_data".getBytes());

    photo.setPhotoLikes(new ArrayList<>());

    return photo;
  }
}
