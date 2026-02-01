package guru.qa.rangiffler.config;

import guru.qa.rangiffler.grpc.RangifflerCountriesServiceGrpc;
import guru.qa.rangiffler.grpc.RangifflerPhotosServiceGrpc;
import guru.qa.rangiffler.grpc.RangifflerUserdataServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.grpc.client.GrpcChannelFactory;

import javax.annotation.ParametersAreNonnullByDefault;

@Slf4j
@Configuration
@ParametersAreNonnullByDefault
public class GrpcClientsDockerConfiguration {

  @Bean
  @Profile("docker")
  public RangifflerCountriesServiceGrpc.RangifflerCountriesServiceBlockingStub rangifflerCountriesServiceBlockingStubDocker(
    GrpcChannelFactory grpcChannelFactory
  ) {
    ManagedChannel channel = ManagedChannelBuilder
      .forAddress("countries.rangiffler.dc", 9099)
      .usePlaintext()
      .build();
    return RangifflerCountriesServiceGrpc.newBlockingStub(channel)
      .withMaxInboundMessageSize(1024 * 1024);
  }

  @Bean
  @Profile("docker")
  public RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub rangifflerUserdataServiceBlockingStubDocker(
    GrpcChannelFactory grpcChannelFactory
  ) {
    ManagedChannel channel = ManagedChannelBuilder
      .forAddress("userdata.rangiffler.dc", 9091)
      .usePlaintext()
      .build();
    return RangifflerUserdataServiceGrpc.newBlockingStub(channel)
      .withMaxInboundMessageSize(1024 * 1024);
  }

  @Bean
  @Profile("docker")
  public RangifflerPhotosServiceGrpc.RangifflerPhotosServiceBlockingStub rangifflerPhotosServiceBlockingStubDocker(
    GrpcChannelFactory grpcChannelFactory
  ) {
    ManagedChannel channel = ManagedChannelBuilder
      .forAddress("photos.rangiffler.dc", 9093)
      .usePlaintext()
      .build();
    return RangifflerPhotosServiceGrpc.newBlockingStub(channel)
      .withMaxInboundMessageSize(1024 * 1024);
  }
}
