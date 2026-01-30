package guru.qa.rangiffler.config;

import guru.qa.rangiffler.grpc.RangifflerCountriesServiceGrpc;
import io.grpc.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.grpc.client.GrpcChannelFactory;

import javax.annotation.ParametersAreNonnullByDefault;

@Slf4j
@Configuration
@ParametersAreNonnullByDefault
@Profile({"local", "docker"})
public class GrpcCountriesClientConfiguration {
  @Bean
  public RangifflerCountriesServiceGrpc.RangifflerCountriesServiceBlockingStub rangifflerCountriesServiceBlockingStub(
    GrpcChannelFactory grpcChannelFactory
  ) {
    Channel channel = grpcChannelFactory.createChannel("countries-service");
    return RangifflerCountriesServiceGrpc.newBlockingStub(channel)
      .withMaxInboundMessageSize(1024 * 1024);
  }
}