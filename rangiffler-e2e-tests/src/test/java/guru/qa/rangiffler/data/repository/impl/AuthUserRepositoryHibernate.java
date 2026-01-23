package guru.qa.rangiffler.data.repository.impl;
import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.data.entity.auth.AuthUserEntity;
import guru.qa.rangiffler.data.jpa.EntityManagers;
import guru.qa.rangiffler.data.repository.AuthUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthUserRepositoryHibernate implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = EntityManagers.em(CFG.authJdbcUrl());

    @Nonnull
    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        entityManager.joinTransaction();
        entityManager.persist(user);
        return user;
    }

    @Nonnull
    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(AuthUserEntity.class, id)
        );
    }

    @Nonnull
    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        try {
            return Optional.of(
                    entityManager.createQuery("select u from AuthUserEntity u where u.username =: username", AuthUserEntity.class)
                            .setParameter("username", username)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Nonnull
    @Override
    public List<AuthUserEntity> findAll() {
        return entityManager.createQuery("select u from AuthUserEntity u", AuthUserEntity.class)
                .getResultList();
    }

    @Override
    public void delete(AuthUserEntity user) {
        entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
    }
}
