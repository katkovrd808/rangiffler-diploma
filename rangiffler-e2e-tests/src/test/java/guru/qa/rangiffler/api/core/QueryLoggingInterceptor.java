package guru.qa.rangiffler.api.core;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@ParametersAreNonnullByDefault
public class QueryLoggingInterceptor implements Interceptor {

  @NotNull
  @Override
  public Response intercept(@NotNull Chain chain) throws IOException {
    Request original = chain.request();
    Response response = chain.proceed(original);

    HttpUrl finalUrl = response.request().url();
    if (finalUrl.encodedPath().contains("/authorized")) {
      String code = finalUrl.queryParameter("code");
      if (code != null && !code.isEmpty()) {
        ThreadSafeQueryStore.INSTANCE.put("code", code);
      }
    }
    return response;
  }
}
