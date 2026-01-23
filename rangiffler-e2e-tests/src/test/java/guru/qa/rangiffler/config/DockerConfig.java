package guru.qa.rangiffler.config;

import org.jetbrains.annotations.NotNull;

enum DockerConfig implements Config {
  INSTANCE;

  @NotNull
  @Override
  public String frontUrl() {
    return "";
  }

  @NotNull
  @Override
  public String authUrl() {
    return "";
  }

  @NotNull
  @Override
  public String authJdbcUrl() {
    return "";
  }

  @NotNull
  @Override
  public String countriesGrpcAddress() {
    return "";
  }

  @NotNull
  @Override
  public String countriesJdbcUrl() {
    return "";
  }

  @NotNull
  @Override
  public String userdataGrpcAddress() {
    return "";
  }

  @NotNull
  @Override
  public String userdataJdbcUrl() {
    return "";
  }

  @NotNull
  @Override
  public String photosGrpcAddress() {
    return "";
  }

  @NotNull
  @Override
  public String photosJdbcUrl() {
    return "";
  }
}
