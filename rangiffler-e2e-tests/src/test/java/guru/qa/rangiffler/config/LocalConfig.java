package guru.qa.rangiffler.config;


import javax.annotation.Nonnull;

enum LocalConfig implements Config {
  INSTANCE;

  @Nonnull
  @Override
  public String frontUrl() {
    return "http://127.0.0.1:3001/";
  }

  @Nonnull
  @Override
  public String authUrl() {
    return "http://127.0.0.1:9000/";
  }

  @Nonnull
  @Override
  public String authJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5432/rangiffler-auth";
  }

  @Nonnull
  @Override
  public String countriesGrpcAddress() {
    return "127.0.0.1";
  }

  @Nonnull
  @Override
  public String countriesJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5432/rangiffler-countries";
  }

  @Nonnull
  @Override
  public String userdataGrpcAddress() {
    return "127.0.0.1";
  }

  @Nonnull
  @Override
  public String userdataJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5432/rangiffler-userdata";
  }

  @Nonnull
  @Override
  public String photosGrpcAddress() {
    return "127.0.0.1";
  }

  @Nonnull
  @Override
  public String photosJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5432/rangiffler-photos";
  }

  @Nonnull
  @Override
  public String allureDockerUrl() {
    return "http://allure:5050/";
  }
}
