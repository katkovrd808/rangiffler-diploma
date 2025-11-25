package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.PhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface PhotoRepository extends JpaRepository<PhotoEntity, UUID> {
}
