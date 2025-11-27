package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.PhotoLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface PhotoLikeRepository extends JpaRepository<PhotoLikeEntity, UUID> {
  @Nonnull
  Optional<PhotoLikeEntity> findByPhotoIdAndUserId(UUID photoId, UUID userId);

  @Query(
    """
      SELECT ple FROM PhotoLikeEntity ple WHERE ple.photo.id = :photoId
      """
  )
  @Nonnull
  List<PhotoLikeEntity> findPhotoLikesByPhotoId(UUID photoId);

  @Query(
    """
      SELECT ple FROM PhotoLikeEntity ple WHERE ple.photo.id IN :photoIds
      """
  )
  List<PhotoLikeEntity> findLikesByPhotoIds(@Param("photoIds") List<UUID> photoIds);
}
