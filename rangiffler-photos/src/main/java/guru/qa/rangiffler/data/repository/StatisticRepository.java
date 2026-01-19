package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.StatisticEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface StatisticRepository extends JpaRepository<StatisticEntity, UUID> {
  @Nonnull
  List<StatisticEntity> findByUserId(UUID userId);

  @Query(value = """
    SELECT s FROM StatisticEntity s
    WHERE s.userId IN :friendIds OR s.userId =:userId
    ORDER BY s.countryId DESC
    """)
  List<StatisticEntity> findByUserIdWithFriends(@Param("userId") UUID userId, @Param("friendIds") List<UUID> friendIds);
}
