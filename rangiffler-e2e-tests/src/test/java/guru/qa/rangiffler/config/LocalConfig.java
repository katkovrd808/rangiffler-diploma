package guru.qa.rangiffler.config;

import org.jetbrains.annotations.NotNull;

enum LocalConfig implements Config {
  INSTANCE;

  @NotNull
  @Override
  public String frontUrl() {
    return "http://127.0.0.1:3000/";
  }

  @NotNull
  @Override
  public String authUrl() {
    return "http://127.0.0.1:9000/";
  }

  @NotNull
  @Override
  public String authJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5432/rangiffler-auth";
  }

  @NotNull
  @Override
  public String countriesGrpcAddress() {
    return "127.0.0.1";
  }

  @NotNull
  @Override
  public String countriesJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5432/rangiffler-countries";
  }

  @NotNull
  @Override
  public String userdataGrpcAddress() {
    return "127.0.0.1";
  }

  @NotNull
  @Override
  public String userdataJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5432/rangiffler-userdata";
  }

  @NotNull
  @Override
  public String photosGrpcAddress() {
    return "127.0.0.1";
  }

  @NotNull
  @Override
  public String photosJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5432/rangiffler-photos";
  }
}
