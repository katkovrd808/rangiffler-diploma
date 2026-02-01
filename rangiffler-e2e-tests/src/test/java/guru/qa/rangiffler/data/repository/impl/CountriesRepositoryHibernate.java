package guru.qa.rangiffler.data.repository.impl;

import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.data.entity.countries.CountryEntity;
import guru.qa.rangiffler.data.repository.CountriesRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

import static guru.qa.rangiffler.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class CountriesRepositoryHibernate implements CountriesRepository {

  private static final Config CFG = Config.getInstance();

  private final EntityManager entityManager = em(CFG.countriesJdbcUrl());

  @NotNull
  @Override
  public Optional<CountryEntity> findByCode(String code) {
    try {
      return Optional.ofNullable(
        entityManager.createQuery("select c from CountryEntity c where c.isoCode =: code", CountryEntity.class)
          .setParameter("code", code)
          .getSingleResult()
      );
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }
}
