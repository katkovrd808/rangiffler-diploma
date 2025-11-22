package guru.qa.rangiffler.config;

import guru.qa.rangiffler.grpc.RangifflerCountriesServiceGrpc;
import guru.qa.rangiffler.grpc.RangifflerUserdataServiceGrpc;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.service.GrpcService;

@Configuration
public class GrpcServerConfiguration {
  @Bean
  @GrpcService
  @ConditionalOnBean(RangifflerUserdataServiceGrpc.RangifflerUserdataServiceImplBase.class)
  public RangifflerUserdataServiceGrpc.RangifflerUserdataServiceImplBase userdataService(
    ObjectProvider<RangifflerUserdataServiceGrpc.RangifflerUserdataServiceImplBase> userdataServiceProvider) {
    return userdataServiceProvider.getIfAvailable();
  }

  @Bean
  @GrpcService
  @ConditionalOnBean(RangifflerCountriesServiceGrpc.RangifflerCountriesServiceImplBase.class)
  public RangifflerCountriesServiceGrpc.RangifflerCountriesServiceImplBase countriesService(
    ObjectProvider<RangifflerCountriesServiceGrpc.RangifflerCountriesServiceImplBase> countriesServiceProvider) {
    return countriesServiceProvider.getIfAvailable();
  }
}
