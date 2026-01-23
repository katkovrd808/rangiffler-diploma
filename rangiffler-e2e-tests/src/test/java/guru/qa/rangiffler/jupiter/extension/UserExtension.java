package guru.qa.rangiffler.jupiter.extension;

import guru.qa.rangiffler.data.projection.PhotoWithLikes;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.TestData;
import guru.qa.rangiffler.model.UdUserJson;
import guru.qa.rangiffler.service.PhotoClient;
import guru.qa.rangiffler.service.UserdataClient;
import guru.qa.rangiffler.service.UsersClient;
import guru.qa.rangiffler.service.impl.api.PhotoApiClient;
import guru.qa.rangiffler.service.impl.api.UserdataApiClient;
import guru.qa.rangiffler.service.impl.db.UsersDbClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;

import static guru.qa.rangiffler.jupiter.extension.TestMethodContextExtension.context;
import static guru.qa.rangiffler.utils.RandomDataUtils.randomUsername;

public class UserExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
  public static final String DEFAULT_PASSWORD = "secret";

  private final UsersClient usersClient = new UsersDbClient();
  private final UserdataClient userdataClient = new UserdataApiClient();
  private final PhotoClient photoClient = new PhotoApiClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
      .ifPresent(userAnno -> {
        UdUserJson user = "".equals(userAnno.username())
          ? usersClient.create(randomUsername(), DEFAULT_PASSWORD)
          : usersClient.findByUsername(userAnno.username()).orElseThrow(
          () -> new IllegalArgumentException("User is empty")
        );
        final List<UdUserJson> incomes = usersClient.addInvitation(user, userAnno.incomeInvitations());
        final List<UdUserJson> outcomes = usersClient.addInvitation(user, userAnno.outcomeInvitations());
        final List<UdUserJson> friends = usersClient.addFriend(user, userAnno.friends());

        final List<PhotoWithLikes> photos = new ArrayList<>();

        if (user.username().equals(userAnno.username())) {
          photos.addAll(photoClient.findUserPhotos(user.username()));

          incomes.addAll(userdataClient.findIncomeInvitations(user.username()));
          outcomes.addAll(userdataClient.findOutcomeInvitations(user.username()));
          friends.addAll(userdataClient.findAllFriends(user.username(), null));
        }

        TestData testData = new TestData(
          DEFAULT_PASSWORD,
          photos,
          friends,
          incomes,
          outcomes
        );

        setUser(user.addTestData(testData));
      });
  }

  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
    context.getStore(NAMESPACE).remove(context.getUniqueId());
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
    ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(UdUserJson.class);
  }

  @Override
  public UdUserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
    ParameterResolutionException {
    return createdUser();
  }

  public static void setUser(UdUserJson testUser) {
    final ExtensionContext context = context();
    context.getStore(NAMESPACE).put(
      context.getUniqueId(),
      testUser
    );
  }

  public static UdUserJson createdUser() {
    final ExtensionContext methodContext = context();
    return methodContext.getStore(NAMESPACE)
      .get(methodContext.getUniqueId(), UdUserJson.class);
  }
}
