package guru.qa.rangiffler.config;

import guru.qa.rangiffler.service.GrpcExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {
  @Bean
  public GrpcExceptionHandler grpcExceptionHandler() {
    return new GrpcExceptionHandler();
  }
}
