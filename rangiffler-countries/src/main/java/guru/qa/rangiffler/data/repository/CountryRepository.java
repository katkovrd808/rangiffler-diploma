package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {
  @Query(
    """
      SELECT c
      FROM CountryEntity c
      WHERE c.isoCode = :isoCode
      """
  )
  @Nonnull
  Optional<CountryEntity> findByIsoCode(@Param("isoCode") String isoCode);

  @Query(
    """
      SELECT c
      FROM CountryEntity c
      WHERE c.id IN :neededCountries
      """
  )
  @Nonnull
  List<CountryEntity> findNeededCountries(@Param("neededCountries") List<UUID> neededCountries);

  @Nonnull
  List<CountryEntity> findAllByOrderByNameAsc();
}
