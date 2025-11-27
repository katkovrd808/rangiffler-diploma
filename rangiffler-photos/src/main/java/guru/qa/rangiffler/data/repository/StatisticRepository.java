package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.StatisticEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface StatisticRepository extends JpaRepository<StatisticEntity, UUID> {
  @Nonnull
  List<StatisticEntity> findByUserId(UUID userId);
}
