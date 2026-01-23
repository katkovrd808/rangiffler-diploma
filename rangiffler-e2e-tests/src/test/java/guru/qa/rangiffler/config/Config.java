package guru.qa.rangiffler.config;

import javax.annotation.Nonnull;

public interface Config {

  @Nonnull
  static Config getInstance() {
    return "docker".equals(System.getProperty("test.env"))
      ? DockerConfig.INSTANCE
      : LocalConfig.INSTANCE;
  }

  default String testDatabaseUsername() {
    return "postgres";
  }

  default String testDatabasePassword() {
    return "secret";
  }

  @Nonnull
  String frontUrl();

  @Nonnull
  String authUrl();

  @Nonnull
  String authJdbcUrl();

  @Nonnull
  String countriesGrpcAddress();

  default int countriesGrpcPort() {
    return 9090;
  }

  @Nonnull
  String countriesJdbcUrl();

  @Nonnull
  String userdataGrpcAddress();

  default int userdataGrpcPort() {
    return 9091;
  }

  @Nonnull
  String userdataJdbcUrl();

  @Nonnull
  String photosGrpcAddress();

  default int photosGrpcPort() {
    return 9093;
  }

  @Nonnull
  String photosJdbcUrl();

  default String gatewayUrl() {
    return "http://127.0.0.1:8080/graphql";
  };
}
