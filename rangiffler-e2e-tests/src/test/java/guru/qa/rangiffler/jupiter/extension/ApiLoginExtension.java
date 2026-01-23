package guru.qa.rangiffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.rangiffler.api.core.ThreadSafeCookieStore;
import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.Token;
import guru.qa.rangiffler.model.TestData;
import guru.qa.rangiffler.model.UdUserJson;
import guru.qa.rangiffler.page.MainPage;
import guru.qa.rangiffler.service.impl.api.AuthApiClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {

  private static final Config CFG = Config.getInstance();
  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);

  private final AuthApiClient authApiClient = new AuthApiClient();
  private final boolean setupBrowser;

  private ApiLoginExtension(boolean setupBrowser) {
    this.setupBrowser = setupBrowser;
  }

  public ApiLoginExtension() {
    this.setupBrowser = true;
  }

  public static ApiLoginExtension restApiLoginExtension() {
    return new ApiLoginExtension(false);
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
      .ifPresent(apiLogin -> {
        final UdUserJson userToLogin;
        final UdUserJson userFromUserExtension = UserExtension.createdUser();
        if ("".equals(apiLogin.username()) || "".equals(apiLogin.password())) {
          if (userFromUserExtension == null) {
            throw new IllegalStateException("@User must be present in case when @ApiLogin is empty!");
          }
          userToLogin = userFromUserExtension;
        } else {
          UdUserJson fakeUser = new UdUserJson(
            apiLogin.username(),
            new TestData(apiLogin.password())
          );
          if (userFromUserExtension != null) {
            throw new IllegalStateException("@User must not be present in case when @ApiLogin contains username or password!");
          }
          UserExtension.setUser(fakeUser);
          userToLogin = fakeUser;
        }

        final String token = authApiClient.login(
          userToLogin.username(),
          userToLogin.testData().password()
        );
        setToken(token);
        if (setupBrowser) {
          Selenide.open(CFG.frontUrl());
          Selenide.localStorage().setItem("id_token", getToken());
          WebDriverRunner.getWebDriver().manage().addCookie(
            getJsessionIdCookie()
          );
          Selenide.open(MainPage.URL, MainPage.class).checkThatPageLoaded();
        }
      });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(String.class)
      && AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
  }

  @Override
  public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return getToken();
  }

  public static void setToken(String token) {
    TestMethodContextExtension.context().getStore(NAMESPACE).put("token", token);
  }

  public static String getToken() {
    return TestMethodContextExtension.context().getStore(NAMESPACE).get("token", String.class);
  }

  public static Cookie getJsessionIdCookie() {
    return new Cookie(
      "JSESSIONID",
      ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
    );
  }
}
