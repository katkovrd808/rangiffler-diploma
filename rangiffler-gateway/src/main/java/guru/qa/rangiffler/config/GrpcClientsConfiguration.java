package guru.qa.rangiffler.config;

import guru.qa.rangiffler.grpc.RangifflerCountriesServiceGrpc;
import guru.qa.rangiffler.grpc.RangifflerPhotosServiceGrpc;
import guru.qa.rangiffler.grpc.RangifflerUserdataServiceGrpc;
import io.grpc.Channel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcClientsConfiguration {
  @Bean
  public RangifflerPhotosServiceGrpc.RangifflerPhotosServiceBlockingStub rangifflerPhotosServiceBlockingStub(
    GrpcChannelFactory grpcChannelFactory
  ) {
    Channel channel = grpcChannelFactory.createChannel("photos-service");
    return RangifflerPhotosServiceGrpc.newBlockingStub(channel)
      .withMaxInboundMessageSize(10 * 1024 * 1024);
  }

  @Bean
  public RangifflerCountriesServiceGrpc.RangifflerCountriesServiceBlockingStub rangifflerCountriesServiceBlockingStub(
    GrpcChannelFactory grpcChannelFactory
  ) {
    Channel channel = grpcChannelFactory.createChannel("countries-service");
    return RangifflerCountriesServiceGrpc.newBlockingStub(channel)
      .withMaxInboundMessageSize(10 * 1024 * 1024);
  }

  @Bean
  public RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub rangifflerUserdataServiceBlockingStub(
    GrpcChannelFactory grpcChannelFactory
  ) {
    Channel channel = grpcChannelFactory.createChannel("userdata-service");
    return RangifflerUserdataServiceGrpc.newBlockingStub(channel)
      .withMaxInboundMessageSize(10 * 1024 * 1024);
  }
}
