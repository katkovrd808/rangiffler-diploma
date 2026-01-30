package guru.qa.rangiffler.config;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

enum DockerConfig implements Config {
  INSTANCE;

  @NotNull
  @Override
  public String frontUrl() {
    return "http://frontend.rangiffler.dc/";
  }

  @NotNull
  @Override
  public String authUrl() {
    return "http://auth.rangiffler.dc:9000/";
  }

  @NotNull
  @Override
  public String authJdbcUrl() {
    return "jdbc:postgresql://rangiffler-all-db:5432/rangiffler-auth";
  }

  @NotNull
  @Override
  public String countriesGrpcAddress() {
    return "countries.rangiffler.dc";
  }

  @NotNull
  @Override
  public String countriesJdbcUrl() {
    return "jdbc:postgresql://rangiffler-all-db:5432/rangiffler-countries";
  }

  @NotNull
  @Override
  public String userdataGrpcAddress() {
    return "userdata.rangiffler.dc";
  }

  @NotNull
  @Override
  public String userdataJdbcUrl() {
    return "jdbc:postgresql://rangiffler-all-db:5432/rangiffler-userdata";
  }

  @NotNull
  @Override
  public String photosGrpcAddress() {
    return "photos.rangiffler.dc";
  }

  @NotNull
  @Override
  public String photosJdbcUrl() {
    return "jdbc:postgresql://rangiffler-all-db:5432/rangiffler-photos";
  }

  @Nonnull
  @Override
  public String allureDockerUrl() {
    final String allureDockerApiFromEnv = System.getenv("ALLURE_DOCKER_API");
    return allureDockerApiFromEnv != null
      ? allureDockerApiFromEnv
      : "http://allure:5050/";
  }
}
