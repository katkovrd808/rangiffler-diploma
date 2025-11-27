package guru.qa.rangiffler.data.projection;

import guru.qa.rangiffler.data.PhotoEntity;
import guru.qa.rangiffler.data.PhotoLikeEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public record PhotoWithLikes(
  UUID id,
  UUID userId,
  UUID countryId,
  String description,
  byte[] photo,
  List<PhotoLikeEntity> likes
) {
  @Nonnull
  public static PhotoWithLikes fromEntity(PhotoEntity photo) {
    return new PhotoWithLikes(
      photo.getId(),
      photo.getUserId(),
      photo.getCountryId(),
      photo.getDescription(),
      photo.getPhoto(),
      photo.getPhotoLikes()
    );
  }

  @Nonnull
  public static PhotoWithLikes fromEntity(PhotoEntity photo, List<PhotoLikeEntity> likes) {
    return new PhotoWithLikes(
      photo.getId(),
      photo.getUserId(),
      photo.getCountryId(),
      photo.getDescription(),
      photo.getPhoto(),
      likes
    );
  }

  @Nonnull
  public static PhotoWithLikes fromEntity(PhotoEntity photo, List<PhotoLikeEntity> likes, byte[] compressedPhoto) {
    return new PhotoWithLikes(
      photo.getId(),
      photo.getUserId(),
      photo.getCountryId(),
      photo.getDescription(),
      compressedPhoto,
      likes
    );
  }
}
