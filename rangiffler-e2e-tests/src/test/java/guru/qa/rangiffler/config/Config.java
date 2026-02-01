package guru.qa.rangiffler.config;

import javax.annotation.Nonnull;

public interface Config {

  @Nonnull
  static Config getInstance() {
    return "docker".equals(System.getProperty("test.env"))
      ? DockerConfig.INSTANCE
      : LocalConfig.INSTANCE;
  }

  @Nonnull
  default String testDatabaseUsername() {
    return "postgres";
  }

  @Nonnull
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

  @Nonnull
  default int countriesGrpcPort() {
    return 9099;
  }

  @Nonnull
  String countriesJdbcUrl();

  @Nonnull
  String userdataGrpcAddress();

  @Nonnull
  default int userdataGrpcPort() {
    return 9091;
  }

  @Nonnull
  String userdataJdbcUrl();

  @Nonnull
  String photosGrpcAddress();

  @Nonnull
  default int photosGrpcPort() {
    return 9093;
  }

  @Nonnull
  String photosJdbcUrl();

  @Nonnull
  default String gatewayUrl() {
    return "http://127.0.0.1:8089/graphql";
  };

  @Nonnull
  String allureDockerUrl();
}
