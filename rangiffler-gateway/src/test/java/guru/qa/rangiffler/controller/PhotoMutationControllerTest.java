package guru.qa.rangiffler.controller;

import guru.qa.rangiffler.controller.photos.PhotoMutationController;
import guru.qa.rangiffler.model.graphql.photos.LikeInputGql;
import guru.qa.rangiffler.model.graphql.photos.PhotoGql;
import guru.qa.rangiffler.model.graphql.photos.PhotoInputGql;
import guru.qa.rangiffler.service.PhotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PhotoMutationControllerTest {
  @Mock
  private PhotoService photoService;
  @Mock
  private Jwt jwt;

  private PhotoMutationController photoMutationController;

  @BeforeEach
  void setUp() {
    photoMutationController = new PhotoMutationController(photoService);
  }

  @Test
  void photo_WithNullId_ShouldSavePhoto() {
    final String username = "testuser";
    final PhotoInputGql photoInput = new PhotoInputGql(
      null,
      new byte[]{},
      null,
      "Test description",
      null
    );
    final PhotoGql expectedPhoto = new PhotoGql(
      UUID.randomUUID(),
      null,
      "",
      null,
      "",
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(username);
    when(photoService.save(username, photoInput)).thenReturn(expectedPhoto);

    final PhotoGql actualPhoto = photoMutationController.photo(jwt, photoInput);

    verify(photoService).save(username, photoInput);
    assertEquals(expectedPhoto, actualPhoto);
  }

  @Test
  void photo_WithIdAndNoLike_ShouldUpdatePhoto() {
    final String username = "testuser";
    final UUID photoId = UUID.randomUUID();
    final PhotoInputGql photoInput = new PhotoInputGql(
      photoId,
      new byte[]{},
      null,
      "Updated description",
      null
    );
    final PhotoGql expectedPhoto = new PhotoGql(
      photoId,
      null,
      "",
      null,
      "",
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(username);
    when(photoService.update(username, photoInput)).thenReturn(expectedPhoto);

    final PhotoGql actualPhoto = photoMutationController.photo(jwt, photoInput);

    verify(photoService).update(username, photoInput);
    assertEquals(expectedPhoto, actualPhoto);
  }

  @Test
  void photo_WithIdAndLike_ShouldUpdateWithLike() {
    final String username = "testuser";
    final UUID photoId = UUID.randomUUID();
    final UUID likedUserId = UUID.randomUUID();
    final PhotoInputGql photoInput = new PhotoInputGql(
      photoId,
      null,
      null,
      null,
      new LikeInputGql(likedUserId)
    );
    final PhotoGql expectedPhoto = new PhotoGql(
      photoId,
      null,
      "",
      null,
      "",
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(username);
    when(photoService.updateWithLike(username, photoInput)).thenReturn(expectedPhoto);

    final PhotoGql actualPhoto = photoMutationController.photo(jwt, photoInput);

    verify(photoService).updateWithLike(username, photoInput);
    assertEquals(expectedPhoto, actualPhoto);
  }

  @Test
  void photo_WithIdAndNullLike_ShouldUpdatePhoto() {
    final String username = "testuser";
    final UUID photoId = UUID.randomUUID();
    final PhotoInputGql photoInput = new PhotoInputGql(
      photoId,
      new byte[]{},
      null,
      "Updated description",
      null
    );
    final PhotoGql expectedPhoto = new PhotoGql(
      photoId,
      null,
      "",
      null,
      "",
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(username);
    when(photoService.update(username, photoInput)).thenReturn(expectedPhoto);

    final PhotoGql actualPhoto = photoMutationController.photo(jwt, photoInput);

    verify(photoService).update(username, photoInput);
    assertEquals(expectedPhoto, actualPhoto);
  }

  @Test
  void deletePhoto_ShouldDeletePhotoAndReturnId() {
    final String username = "testuser";
    final String photoId = UUID.randomUUID().toString();
    final String expectedDeletedId = photoId;

    when(jwt.getClaim("sub")).thenReturn(username);
    when(photoService.delete(username, photoId)).thenReturn(expectedDeletedId);

    final String actualDeletedId = photoMutationController.deletePhoto(jwt, photoId);

    verify(photoService).delete(username, photoId);
    assertEquals(expectedDeletedId, actualDeletedId);
  }

  @Test
  void photo_WithEmptyPhotoInput_ShouldSavePhoto() {
    final String username = "testuser";
    final PhotoInputGql emptyPhotoInput = new PhotoInputGql(
      null,
      null,
      null,
      null,
      null
    );
    final PhotoGql expectedPhoto = new PhotoGql(
      UUID.randomUUID(),
      null,
      "",
      null,
      "",
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(username);
    when(photoService.save(username, emptyPhotoInput)).thenReturn(expectedPhoto);

    final PhotoGql actualPhoto = photoMutationController.photo(jwt, emptyPhotoInput);

    verify(photoService).save(username, emptyPhotoInput);
    assertEquals(expectedPhoto, actualPhoto);
  }

  @Test
  void photo_WithIdAndLikeWithNullUser_ShouldUpdatePhoto() {
    final String username = "testuser";
    final UUID photoId = UUID.randomUUID();
    final PhotoInputGql photoInput = new PhotoInputGql(
      photoId,
      null,
      null,
      null,
      new LikeInputGql(null)
    );
    final PhotoGql expectedPhoto = new PhotoGql(
      photoId,
      null,
      "",
      null,
      "",
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(username);
    when(photoService.update(username, photoInput)).thenReturn(expectedPhoto);

    final PhotoGql actualPhoto = photoMutationController.photo(jwt, photoInput);

    verify(photoService).update(username, photoInput);
    assertEquals(expectedPhoto, actualPhoto);
  }

  @Test
  void deletePhoto_WithEmptyPhotoId_ShouldDeletePhoto() {
    final String username = "testuser";
    final String emptyPhotoId = "";
    final String expectedDeletedId = "";

    when(jwt.getClaim("sub")).thenReturn(username);
    when(photoService.delete(username, emptyPhotoId)).thenReturn(expectedDeletedId);

    final String actualDeletedId = photoMutationController.deletePhoto(jwt, emptyPhotoId);

    verify(photoService).delete(username, emptyPhotoId);
    assertEquals(expectedDeletedId, actualDeletedId);
  }

  @Test
  void photo_WithNullJwt_ShouldThrowException() {
    final PhotoInputGql photoInput = new PhotoInputGql(
      null,
      null,
      null,
      null,
      null
    );

    assertThrows(NullPointerException.class, () ->
      photoMutationController.photo(null, photoInput)
    );
  }

  @Test
  void deletePhoto_WithNullJwt_ShouldThrowException() {
    final String photoId = UUID.randomUUID().toString();

    assertThrows(NullPointerException.class, () ->
      photoMutationController.deletePhoto(null, photoId)
    );
  }

  @Test
  void photo_WithLikeAndNullId_ShouldSavePhoto() {
    final String username = "testuser";
    final UUID likedUserId = UUID.randomUUID();
    final PhotoInputGql photoInput = new PhotoInputGql(
      null,
      null,
      null,
      null,
      new LikeInputGql(likedUserId)
    );
    final PhotoGql expectedPhoto = new PhotoGql(
      UUID.randomUUID(),
      null,
      "",
      null,
      "",
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(username);
    when(photoService.save(username, photoInput)).thenReturn(expectedPhoto);

    final PhotoGql actualPhoto = photoMutationController.photo(jwt, photoInput);

    verify(photoService).save(username, photoInput);
    assertEquals(expectedPhoto, actualPhoto);
  }

  @Test
  void deletePhoto_WithNullPhotoId_ShouldDeletePhoto() {
    final String username = "testuser";
    final String expectedDeletedId = "";

    when(jwt.getClaim("sub")).thenReturn(username);
    when(photoService.delete(username, null)).thenReturn(expectedDeletedId);

    final String actualDeletedId = photoMutationController.deletePhoto(jwt, null);

    verify(photoService).delete(username, null);
    assertEquals(expectedDeletedId, actualDeletedId);
  }

  @Test
  void photo_ShouldReturnSameInstanceFromService() {
    final String username = "testuser";
    final UUID photoId = UUID.randomUUID();
    final PhotoInputGql photoInput = new PhotoInputGql(
      photoId,
      new byte[]{},
      null,
      "Updated description",
      null
    );
    final PhotoGql expectedPhoto = new PhotoGql(
      photoId,
      null,
      "",
      null,
      "",
      null,
      null
    );

    when(jwt.getClaim("sub")).thenReturn(username);
    when(photoService.update(username, photoInput)).thenReturn(expectedPhoto);

    final PhotoGql actualPhoto = photoMutationController.photo(jwt, photoInput);

    verify(photoService).update(username, photoInput);
    assertEquals(expectedPhoto, actualPhoto);
  }

  @Test
  void deletePhoto_ShouldReturnSameStringFromService() {
    final String username = "testuser";
    final String photoId = UUID.randomUUID().toString();
    final String expectedDeletedId = photoId;

    when(jwt.getClaim("sub")).thenReturn(username);
    when(photoService.delete(username, photoId)).thenReturn(expectedDeletedId);

    final String actualDeletedId = photoMutationController.deletePhoto(jwt, photoId);

    verify(photoService).delete(username, photoId);
    assertEquals(expectedDeletedId, actualDeletedId);
  }

  @Test
  void photo_WithDifferentUsers_ShouldCallServiceWithCorrectUsername() {
    final String username1 = "user1";
    final String username2 = "user2";
    final UUID photoId = UUID.randomUUID();
    final PhotoInputGql photoInput = new PhotoInputGql(
      photoId,
      new byte[]{},
      null,
      "Description",
      null
    );
    final PhotoGql expectedPhoto1 = new PhotoGql(
      photoId,
      null,
      "",
      null,
      "",
      null,
      null
    );
    final PhotoGql expectedPhoto2 = new PhotoGql(
      photoId,
      null,
      "",
      null,
      "",
      null,
      null
    );

    final Jwt jwt1 = org.mockito.Mockito.mock(Jwt.class);
    final Jwt jwt2 = org.mockito.Mockito.mock(Jwt.class);

    when(jwt1.getClaim("sub")).thenReturn(username1);
    when(jwt2.getClaim("sub")).thenReturn(username2);
    when(photoService.update(username1, photoInput)).thenReturn(expectedPhoto1);
    when(photoService.update(username2, photoInput)).thenReturn(expectedPhoto2);

    final PhotoGql actualPhoto1 = photoMutationController.photo(jwt1, photoInput);
    final PhotoGql actualPhoto2 = photoMutationController.photo(jwt2, photoInput);

    verify(photoService).update(username1, photoInput);
    verify(photoService).update(username2, photoInput);
    assertEquals(expectedPhoto1, actualPhoto1);
    assertEquals(expectedPhoto2, actualPhoto2);
  }
}
