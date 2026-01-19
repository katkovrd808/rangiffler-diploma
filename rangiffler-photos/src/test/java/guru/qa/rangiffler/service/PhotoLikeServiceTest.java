package guru.qa.rangiffler.service;

import guru.qa.rangiffler.api.GrpcCountriesClient;
import guru.qa.rangiffler.api.GrpcUserdataClient;
import guru.qa.rangiffler.data.PhotoEntity;
import guru.qa.rangiffler.data.PhotoLikeEntity;
import guru.qa.rangiffler.data.projection.PhotoWithLikes;
import guru.qa.rangiffler.data.repository.PhotoLikeRepository;
import guru.qa.rangiffler.data.repository.PhotoRepository;
import guru.qa.rangiffler.ex.InvalidPhotoLikeOperationException;
import guru.qa.rangiffler.ex.PhotoLikeNotFoundException;
import guru.qa.rangiffler.ex.PhotoNotFoundException;
import guru.qa.rangiffler.grpc.Like;
import guru.qa.rangiffler.grpc.PhotoLikeRequest;
import guru.qa.rangiffler.grpc.PhotoResponse;
import guru.qa.rangiffler.model.CountryDto;
import guru.qa.rangiffler.model.UserDto;
import guru.qa.rangiffler.service.impl.DbPhotoLikeService;
import guru.qa.rangiffler.service.mapper.PhotoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PhotoLikeServiceTest {

  @Mock
  private PhotoRepository photoRepository;

  @Mock
  private PhotoLikeRepository photoLikeRepository;

  @Mock
  private GrpcUserdataClient grpcUserdataClient;

  @Mock
  private GrpcCountriesClient grpcCountriesClient;

  @Mock
  private PhotoMapper photoMapper;

  private DbPhotoLikeService dbPhotoLikeService;

  @BeforeEach
  void setUp() {
    dbPhotoLikeService = new DbPhotoLikeService(
      photoRepository, photoLikeRepository, grpcUserdataClient,
      grpcCountriesClient, photoMapper
    );
  }

  @Test
  void likePhoto_WithValidDataAndNoExistingLike_ShouldAddLike() {
    final String userId = UUID.randomUUID().toString();
    final String photoId = UUID.randomUUID().toString();
    final String countryId = UUID.randomUUID().toString();

    final PhotoLikeRequest request = PhotoLikeRequest.newBuilder()
      .setPhotoId(photoId)
      .setLike(Like.newBuilder().setUserId(userId).build())
      .build();

    final PhotoEntity photo = new PhotoEntity();
    photo.setId(UUID.fromString(photoId));
    photo.setCountryId(UUID.fromString(countryId));
    photo.setUserId(UUID.randomUUID());

    final CountryDto countryDto = new CountryDto(
      UUID.fromString(countryId), "Test Country", "TC", new byte[]{}, 0
    );

    final PhotoWithLikes photoWithLikes = PhotoWithLikes.fromEntity(photo);
    final PhotoResponse expectedResponse = PhotoResponse.newBuilder()
      .setId(photoId)
      .build();

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.of(new UserDto(UUID.fromString(userId), "testuser")));
    when(photoRepository.findById(UUID.fromString(photoId))).thenReturn(Optional.of(photo));
    when(photoLikeRepository.findByPhotoIdAndUserId(UUID.fromString(photoId), UUID.fromString(userId)))
      .thenReturn(Optional.empty());
    when(grpcCountriesClient.getCountryById(countryId)).thenReturn(Optional.of(countryDto));
    when(photoLikeRepository.save(any(PhotoLikeEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(photoRepository.findById(UUID.fromString(photoId))).thenReturn(Optional.of(photo));
    when(photoMapper.toProto(any(PhotoWithLikes.class), eq(countryDto))).thenReturn(expectedResponse);

    final PhotoResponse actualResponse = dbPhotoLikeService.likePhoto(request);

    verify(grpcUserdataClient).getUserById(userId);
    verify(photoRepository, atLeastOnce()).findById(UUID.fromString(photoId));
    verify(photoLikeRepository).findByPhotoIdAndUserId(UUID.fromString(photoId), UUID.fromString(userId));
    verify(photoLikeRepository).save(any(PhotoLikeEntity.class));
    verify(photoMapper).toProto(any(PhotoWithLikes.class), eq(countryDto));
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void likePhoto_WithExistingLike_ShouldRemoveLike() {
    final String userId = UUID.randomUUID().toString();
    final String photoId = UUID.randomUUID().toString();
    final String countryId = UUID.randomUUID().toString();

    final PhotoLikeRequest request = PhotoLikeRequest.newBuilder()
      .setPhotoId(photoId)
      .setLike(Like.newBuilder().setUserId(userId).build())
      .build();

    final PhotoEntity photo = new PhotoEntity();
    photo.setId(UUID.fromString(photoId));
    photo.setCountryId(UUID.fromString(countryId));
    photo.setUserId(UUID.randomUUID());

    final PhotoLikeEntity existingLike = new PhotoLikeEntity();
    existingLike.setId(UUID.randomUUID());
    existingLike.setUserId(UUID.fromString(userId));
    existingLike.setPhoto(photo);

    final CountryDto countryDto = new CountryDto(
      UUID.fromString(countryId), "Test Country", "TC", new byte[]{}, 0
    );

    final PhotoWithLikes photoWithLikes = PhotoWithLikes.fromEntity(photo);
    final PhotoResponse expectedResponse = PhotoResponse.newBuilder()
      .setId(photoId)
      .build();

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.of(new UserDto(UUID.fromString(userId), "testuser")));
    when(photoRepository.findById(UUID.fromString(photoId))).thenReturn(Optional.of(photo));
    when(photoLikeRepository.findByPhotoIdAndUserId(UUID.fromString(photoId), UUID.fromString(userId)))
      .thenReturn(Optional.of(existingLike));
    when(photoRepository.findById(UUID.fromString(photoId))).thenReturn(Optional.of(photo));
    when(grpcCountriesClient.getCountryById(countryId)).thenReturn(Optional.of(countryDto));
    when(photoMapper.toProto(any(PhotoWithLikes.class), eq(countryDto))).thenReturn(expectedResponse);

    final PhotoResponse actualResponse = dbPhotoLikeService.likePhoto(request);

    verify(photoLikeRepository).delete(existingLike);
    verify(photoLikeRepository).flush();
    verify(photoMapper).toProto(any(PhotoWithLikes.class), eq(countryDto));
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void likePhoto_WithEmptyUserId_ShouldThrowException() {
    final String photoId = UUID.randomUUID().toString();
    final PhotoLikeRequest request = PhotoLikeRequest.newBuilder()
      .setPhotoId(photoId)
      .setLike(Like.newBuilder().setUserId("").build())
      .build();

    final IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> dbPhotoLikeService.likePhoto(request)
    );

    assertEquals("User can't be null.", exception.getMessage());
    verify(grpcUserdataClient, never()).getUserById(any());
    verify(photoRepository, never()).findById(any());
  }

  @Test
  void likePhoto_WhenUserNotFound_ShouldThrowException() {
    final String userId = UUID.randomUUID().toString();
    final String photoId = UUID.randomUUID().toString();

    final PhotoLikeRequest request = PhotoLikeRequest.newBuilder()
      .setPhotoId(photoId)
      .setLike(Like.newBuilder().setUserId(userId).build())
      .build();

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.empty());

    final IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> dbPhotoLikeService.likePhoto(request)
    );

    assertTrue(exception.getMessage().contains("User with id " + userId + " was not found."));
    verify(grpcUserdataClient).getUserById(userId);
    verify(photoRepository, never()).findById(any());
  }

  @Test
  void likePhoto_WhenPhotoNotFound_ShouldThrowException() {
    final String userId = UUID.randomUUID().toString();
    final String photoId = UUID.randomUUID().toString();

    final PhotoLikeRequest request = PhotoLikeRequest.newBuilder()
      .setPhotoId(photoId)
      .setLike(Like.newBuilder().setUserId(userId).build())
      .build();

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.of(new UserDto(UUID.fromString(userId), "testuser")));
    when(photoRepository.findById(UUID.fromString(photoId))).thenReturn(Optional.empty());

    final PhotoNotFoundException exception = assertThrows(
      PhotoNotFoundException.class,
      () -> dbPhotoLikeService.likePhoto(request)
    );

    assertEquals("Can't find photo with id: " + photoId, exception.getMessage());
    verify(grpcUserdataClient).getUserById(userId);
    verify(photoRepository).findById(UUID.fromString(photoId));
  }

  @Test
  void deleteLike_WithValidData_ShouldDeleteLike() {
    final String userId = UUID.randomUUID().toString();
    final String photoId = UUID.randomUUID().toString();
    final String countryId = UUID.randomUUID().toString();

    final PhotoLikeRequest request = PhotoLikeRequest.newBuilder()
      .setPhotoId(photoId)
      .setLike(Like.newBuilder().setUserId(userId).build())
      .build();

    final PhotoEntity photo = new PhotoEntity();
    photo.setId(UUID.fromString(photoId));
    photo.setCountryId(UUID.fromString(countryId));
    photo.setUserId(UUID.randomUUID());

    final PhotoLikeEntity photoLike = new PhotoLikeEntity();
    photoLike.setId(UUID.randomUUID());
    photoLike.setUserId(UUID.fromString(userId));
    photoLike.setPhoto(photo);

    final CountryDto countryDto = new CountryDto(
      UUID.fromString(countryId), "Test Country", "TC", new byte[]{}, 0
    );

    final PhotoWithLikes photoWithLikes = PhotoWithLikes.fromEntity(photo);
    final PhotoResponse expectedResponse = PhotoResponse.newBuilder()
      .setId(photoId)
      .build();

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.of(new UserDto(UUID.fromString(userId), "testuser")));
    when(photoLikeRepository.findByPhotoIdAndUserId(UUID.fromString(photoId), UUID.fromString(userId)))
      .thenReturn(Optional.of(photoLike));
    when(photoRepository.findById(UUID.fromString(photoId))).thenReturn(Optional.of(photo));
    when(grpcCountriesClient.getCountryById(countryId)).thenReturn(Optional.of(countryDto));
    when(photoMapper.toProto(any(PhotoWithLikes.class), eq(countryDto))).thenReturn(expectedResponse);

    final PhotoResponse actualResponse = dbPhotoLikeService.deleteLike(request);

    verify(grpcUserdataClient).getUserById(userId);
    verify(photoLikeRepository).findByPhotoIdAndUserId(UUID.fromString(photoId), UUID.fromString(userId));
    verify(photoLikeRepository).delete(photoLike);
    verify(photoLikeRepository).flush();
    verify(photoRepository).findById(UUID.fromString(photoId));
    verify(grpcCountriesClient).getCountryById(countryId);
    verify(photoMapper).toProto(any(PhotoWithLikes.class), eq(countryDto));
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void deleteLike_WithEmptyUserId_ShouldThrowException() {
    final String photoId = UUID.randomUUID().toString();
    final PhotoLikeRequest request = PhotoLikeRequest.newBuilder()
      .setPhotoId(photoId)
      .setLike(Like.newBuilder().setUserId("").build())
      .build();

    final IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> dbPhotoLikeService.deleteLike(request)
    );

    assertEquals("User can't be null.", exception.getMessage());
    verify(grpcUserdataClient, never()).getUserById(any());
    verify(photoLikeRepository, never()).findByPhotoIdAndUserId(any(), any());
  }

  @Test
  void deleteLike_WhenUserNotFound_ShouldThrowException() {
    final String userId = UUID.randomUUID().toString();
    final String photoId = UUID.randomUUID().toString();

    final PhotoLikeRequest request = PhotoLikeRequest.newBuilder()
      .setPhotoId(photoId)
      .setLike(Like.newBuilder().setUserId(userId).build())
      .build();

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.empty());

    final IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> dbPhotoLikeService.deleteLike(request)
    );

    assertTrue(exception.getMessage().contains("User with id " + userId + " was not found."));
    verify(grpcUserdataClient).getUserById(userId);
    verify(photoLikeRepository, never()).findByPhotoIdAndUserId(any(), any());
  }

  @Test
  void deleteLike_WhenLikeNotFound_ShouldThrowException() {
    final String userId = UUID.randomUUID().toString();
    final String photoId = UUID.randomUUID().toString();

    final PhotoLikeRequest request = PhotoLikeRequest.newBuilder()
      .setPhotoId(photoId)
      .setLike(Like.newBuilder().setUserId(userId).build())
      .build();

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.of(new UserDto(UUID.fromString(userId), "testuser")));
    when(photoLikeRepository.findByPhotoIdAndUserId(UUID.fromString(photoId), UUID.fromString(userId)))
      .thenReturn(Optional.empty());

    final PhotoLikeNotFoundException exception = assertThrows(
      PhotoLikeNotFoundException.class,
      () -> dbPhotoLikeService.deleteLike(request)
    );

    assertEquals("Photo like with photo id: " + photoId + " and user id: " + userId + " was not found.", exception.getMessage());
    verify(grpcUserdataClient).getUserById(userId);
    verify(photoLikeRepository).findByPhotoIdAndUserId(UUID.fromString(photoId), UUID.fromString(userId));
    verify(photoLikeRepository, never()).delete(any());
  }

  @Test
  void deleteLike_WhenUserHasNoPermission_ShouldThrowException() {
    final String userId = UUID.randomUUID().toString();
    final String anotherUserId = UUID.randomUUID().toString();
    final String photoId = UUID.randomUUID().toString();

    final PhotoLikeRequest request = PhotoLikeRequest.newBuilder()
      .setPhotoId(photoId)
      .setLike(Like.newBuilder().setUserId(userId).build())
      .build();

    final PhotoLikeEntity photoLike = new PhotoLikeEntity();
    photoLike.setId(UUID.randomUUID());
    photoLike.setUserId(UUID.fromString(anotherUserId));

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.of(new UserDto(UUID.fromString(userId), "testuser")));
    when(photoLikeRepository.findByPhotoIdAndUserId(UUID.fromString(photoId), UUID.fromString(userId)))
      .thenReturn(Optional.of(photoLike));

    final InvalidPhotoLikeOperationException exception = assertThrows(
      InvalidPhotoLikeOperationException.class,
      () -> dbPhotoLikeService.deleteLike(request)
    );

    assertEquals("User with id: " + userId + " don't have permissions to operate this like.", exception.getMessage());
    verify(grpcUserdataClient).getUserById(userId);
    verify(photoLikeRepository).findByPhotoIdAndUserId(UUID.fromString(photoId), UUID.fromString(userId));
    verify(photoLikeRepository, never()).delete(any());
  }

  @Test
  void deleteLike_WhenCountryNotFound_ShouldThrowException() {
    final String userId = UUID.randomUUID().toString();
    final String photoId = UUID.randomUUID().toString();
    final String countryId = UUID.randomUUID().toString();

    final PhotoLikeRequest request = PhotoLikeRequest.newBuilder()
      .setPhotoId(photoId)
      .setLike(Like.newBuilder().setUserId(userId).build())
      .build();

    final PhotoEntity photo = new PhotoEntity();
    photo.setId(UUID.fromString(photoId));
    photo.setCountryId(UUID.fromString(countryId));

    final PhotoLikeEntity photoLike = new PhotoLikeEntity();
    photoLike.setId(UUID.randomUUID());
    photoLike.setUserId(UUID.fromString(userId));
    photoLike.setPhoto(photo);

    when(grpcUserdataClient.getUserById(userId)).thenReturn(Optional.of(new UserDto(UUID.fromString(userId), "testuser")));
    when(photoLikeRepository.findByPhotoIdAndUserId(UUID.fromString(photoId), UUID.fromString(userId)))
      .thenReturn(Optional.of(photoLike));
    when(photoRepository.findById(UUID.fromString(photoId))).thenReturn(Optional.of(photo));
    when(grpcCountriesClient.getCountryById(countryId)).thenReturn(Optional.empty());

    assertThrows(
      RuntimeException.class,
      () -> dbPhotoLikeService.deleteLike(request)
    );

    verify(grpcUserdataClient).getUserById(userId);
    verify(photoLikeRepository).findByPhotoIdAndUserId(UUID.fromString(photoId), UUID.fromString(userId));
    verify(photoLikeRepository).delete(photoLike);
    verify(photoLikeRepository).flush();
    verify(photoRepository).findById(UUID.fromString(photoId));
    verify(grpcCountriesClient).getCountryById(countryId);
    verify(photoMapper, never()).toProto(any(), any());
  }
}