package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.PhotoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface PhotoRepository extends JpaRepository<PhotoEntity, UUID> {
  @Query(
    """
      SELECT p
      FROM PhotoEntity p
      WHERE p.userId IN :friendIds
      ORDER BY p.createdDate DESC
      """)
  @Nonnull
  Page<PhotoEntity> findFriendsPhoto(@Param("friendIds") List<UUID> friendIds, Pageable pageable);
}
