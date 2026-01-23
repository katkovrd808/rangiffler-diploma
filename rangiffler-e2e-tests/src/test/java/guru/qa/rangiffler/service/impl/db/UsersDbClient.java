package guru.qa.rangiffler.service.impl.db;

import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.data.entity.auth.AuthUserEntity;
import guru.qa.rangiffler.data.entity.auth.Authority;
import guru.qa.rangiffler.data.entity.auth.AuthorityEntity;
import guru.qa.rangiffler.data.entity.userdata.FriendshipStatus;
import guru.qa.rangiffler.data.entity.userdata.UdUserEntity;
import guru.qa.rangiffler.data.repository.AuthUserRepository;
import guru.qa.rangiffler.data.repository.UserdataUserRepository;
import guru.qa.rangiffler.data.repository.impl.AuthUserRepositoryHibernate;
import guru.qa.rangiffler.data.repository.impl.UserdataUserRepositoryHibernate;
import guru.qa.rangiffler.data.tpl.XaTransactionTemplate;
import guru.qa.rangiffler.model.UdUserJson;
import guru.qa.rangiffler.service.UsersClient;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

import static guru.qa.rangiffler.utils.RandomDataUtils.randomUsername;

@ParametersAreNonnullByDefault
public class UsersDbClient implements UsersClient {

  private static final Config CFG = Config.getInstance();
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  private static final String DEFAULT_COUNTRY_ID = "9415a235-6d91-484e-a92a-afa68c55bcb2";

  private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
  private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryHibernate();

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
    CFG.authJdbcUrl(),
    CFG.userdataJdbcUrl()
  );

  @Nonnull
  @Override
  public UdUserJson create(String username, String password) {
    return xaTransactionTemplate.execute(() -> {
        AuthUserEntity authUser = authUserEntity(username, password);
        authUserRepository.create(authUser);
        return UdUserJson.fromEntity(
          userdataUserRepository.create(userEntity(username)),
          null
        );
      }
    );
  }

  @Nonnull
  @Override
  public Optional<UdUserJson> findById(UUID id) {
    return userdataUserRepository.findById(id)
      .map(user -> UdUserJson.fromEntity(user, null));
  }

  @Nonnull
  @Override
  public List<UdUserJson> findAll() {
    List<UdUserEntity> userEntities = userdataUserRepository.findAll();
    return userEntities.stream().map(
        user -> UdUserJson.fromEntity(user, null))
      .toList();
  }

  @Nonnull
  @Override
  public Optional<UdUserJson> findByUsername(String username) {
    return userdataUserRepository.findByUsername(username)
      .map(user -> UdUserJson.fromEntity(user, null));
  }

  @Nonnull
  @Override
  public UdUserJson update(UdUserJson user) {
    UdUserEntity ue = UdUserEntity.fromJson(user);
    return UdUserJson.fromEntity(userdataUserRepository.update(ue), null);
  }

  //TODO пофиксить инвайты в друзья: все инвайты улетают в Income
  @Nonnull
  @Override
  public List<UdUserJson> addInvitation(UdUserJson targetUser, int count) {
    final List<UdUserJson> result = new ArrayList<>();
    if (count > 0) {
      UdUserEntity targetEntity = userdataUserRepository.findById(
        targetUser.id()
      ).orElseThrow();

      for (int i = 0; i < count; i++) {
        xaTransactionTemplate.execute(() -> {
            final String username = randomUsername();
            final AuthUserEntity authUser = authUserEntity(username, "12345");
            authUserRepository.create(authUser);
            final UdUserEntity addressee = userdataUserRepository.create(userEntity(username));
            userdataUserRepository.sendInvitation(targetEntity, addressee);
            result.add(UdUserJson.fromEntity(
              addressee,
              FriendshipStatus.PENDING
            ));
            return null;
          }
        );
      }
    }
    return result;
  }

  @Nonnull
  @Override
  public List<UdUserJson> addFriend(UdUserJson targetUser, int count) {
    final List<UdUserJson> result = new ArrayList<>();
    if (count > 0) {
      UdUserEntity targetEntity = userdataUserRepository.findById(
        targetUser.id()
      ).orElseThrow();
      for (int i = 0; i < count; i++) {
        xaTransactionTemplate.execute(() -> {
            String username = randomUsername();
            AuthUserEntity authUser = authUserEntity(username, "12345");
            authUserRepository.create(authUser);
            UdUserEntity adressee = userdataUserRepository.create(userEntity(username));
            userdataUserRepository.addFriend(adressee, targetEntity);
            result.add(UdUserJson.fromEntity(
              adressee,
              FriendshipStatus.ACCEPTED
            ));
            return null;
          }
        );
      }
    }
    return result;
  }

  @Nonnull
  @Override
  public void delete(UdUserJson user) {
    xaTransactionTemplate.execute(() -> {
        userdataUserRepository.delete(
          UdUserEntity.fromJson(user));
      }
    );
  }

  @Nonnull
  private UdUserEntity userEntity(String username) {
    UdUserEntity ue = new UdUserEntity();
    ue.setUsername(username);
    ue.setCountryId(UUID.fromString(DEFAULT_COUNTRY_ID));
    return ue;
  }

  @Nonnull
  private AuthUserEntity authUserEntity(String username, String password) {
    AuthUserEntity authUser = new AuthUserEntity();
    authUser.setUsername(username);
    authUser.setPassword(pe.encode(password));
    authUser.setEnabled(true);
    authUser.setAccountNonExpired(true);
    authUser.setAccountNonLocked(true);
    authUser.setCredentialsNonExpired(true);
    authUser.setAuthorities(
      Arrays.stream(Authority.values()).map(
        e -> {
          AuthorityEntity ae = new AuthorityEntity();
          ae.setUser(authUser);
          ae.setAuthority(e);
          return ae;
        }
      ).toList()
    );
    return authUser;
  }
}