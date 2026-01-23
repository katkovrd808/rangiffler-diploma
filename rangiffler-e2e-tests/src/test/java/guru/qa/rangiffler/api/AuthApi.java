package guru.qa.rangiffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.rangiffler.model.UdUserJson;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface AuthApi {

  @GET("register")
  Call<ResponseBody> requestRegisterForm();

  @POST("register")
  Call<ResponseBody> register(@Query("username") String username,
                              @Query("password") String password,
                              @Query("passwordSubmit") String passwordSubmit,
                              @Query("_csrf") String token);

  @POST("update")
  Call<UdUserJson> update(@Body UdUserJson user);

  //OAUTH 2.0 Methods
  @GET("oauth2/authorize")
  Call<Void> authorizeOAuth(@Query("response_type") String responseType,
                            @Query("client_id") String clientId,
                            @Query("scope") String scope,
                            @Query(value = "redirect_uri", encoded = true) String redirectUri,
                            @Query(value = "code_challenge", encoded = true) String codeChallenge,
                            @Query(value = "code_challenge_method", encoded = true) String codeChallengeMethod);

  @FormUrlEncoded
  @POST("login")
  Call<Void> loginOAuth(@Field(value = "_csrf", encoded = true) String csrf,
                        @Field("username") String username,
                        @Field("password") String password);

  @FormUrlEncoded
  @POST("oauth2/token")
  Call<JsonNode> tokenOAuth(@Field(value = "code", encoded = true) String code,
                            @Field(value = "redirect_uri", encoded = true) String redirectUri,
                            @Field(value = "code_verifier", encoded = true) String codeVerifier,
                            @Field("grant_type") String grantType,
                            @Field("client_id") String clientId);
}
