package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.PhotoLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface PhotoLikeRepository extends JpaRepository<PhotoLikeEntity, UUID> {
  @Nonnull
  Optional<PhotoLikeEntity> findByPhotoIdAndUserId(UUID photoId, UUID userId);
}
