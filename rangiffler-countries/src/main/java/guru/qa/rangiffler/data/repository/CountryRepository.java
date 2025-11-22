package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {
  @Nonnull
  Optional<CountryEntity> findByIsoCode(String isoCode);
}
