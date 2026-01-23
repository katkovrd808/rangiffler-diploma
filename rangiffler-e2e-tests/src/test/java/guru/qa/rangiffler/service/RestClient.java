package guru.qa.rangiffler.service;

import guru.qa.rangiffler.api.core.ThreadSafeCookieStore;
import guru.qa.rangiffler.config.Config;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.CookieManager;
import java.net.CookiePolicy;

@ParametersAreNonnullByDefault
public abstract class RestClient {

  protected static final Config CFG = Config.getInstance();

  private final OkHttpClient client;
  private final Retrofit retrofit;

  public RestClient(String baseUrl) {
    this(baseUrl, false, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.HEADERS, null);
  }

  public RestClient(String baseUrl, boolean followRedirects) {
    this(baseUrl, followRedirects, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.HEADERS, null);
  }

  public RestClient(String baseUrl, boolean followRedirects, Interceptor... interceptors) {
    this(baseUrl, followRedirects, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.HEADERS, interceptors);
  }

  public RestClient(String baseUrl, boolean followRedirects, Converter.Factory converterFactory) {
    this(baseUrl, followRedirects, converterFactory, HttpLoggingInterceptor.Level.HEADERS, null);
  }

  public RestClient(String baseUrl, Converter.Factory converterFactory) {
    this(baseUrl, false, converterFactory, HttpLoggingInterceptor.Level.HEADERS, null);
  }

  public RestClient(String baseUrl, boolean followRedirects, Converter.Factory converterFactory, Interceptor... interceptors) {
    this(baseUrl, followRedirects, converterFactory, HttpLoggingInterceptor.Level.HEADERS, interceptors);
  }

  public RestClient(String baseUrl, boolean followRedirects, Converter.Factory converterFactory, HttpLoggingInterceptor.Level level, @Nullable Interceptor... interceptors) {
    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
      .followRedirects(followRedirects);

    if (interceptors != null) {
      for (Interceptor interceptor : interceptors) {
        clientBuilder.addNetworkInterceptor(interceptor);
      }
    }

    clientBuilder
      .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(level))
      .addNetworkInterceptor(new AllureOkHttp3()
        .setRequestTemplate("http-request.ftl")
        .setResponseTemplate("http-response.ftl")
      )
      .cookieJar(
        new JavaNetCookieJar(
          new CookieManager(
            ThreadSafeCookieStore.INSTANCE,
            CookiePolicy.ACCEPT_ALL
          )
        )
      );

    this.client = clientBuilder.build();
    this.retrofit = new Retrofit.Builder()
      .client(this.client)
      .baseUrl(baseUrl)
      .addConverterFactory(converterFactory)
      .build();
  }

  protected <T> T create(final Class<T> service) {
    return this.retrofit.create(service);
  }
}