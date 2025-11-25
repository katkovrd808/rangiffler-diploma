package guru.qa.rangiffler.config;

import guru.qa.rangiffler.grpc.RangifflerCountriesServiceGrpc;
import guru.qa.rangiffler.grpc.RangifflerUserdataServiceGrpc;
import io.grpc.Channel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcClientsConfiguration {
  @Bean
  public RangifflerCountriesServiceGrpc.RangifflerCountriesServiceBlockingStub rangifflerCountriesServiceBlockingStub(
    GrpcChannelFactory grpcChannelFactory
  ) {
    Channel channel = grpcChannelFactory.createChannel("countries-service");
    return RangifflerCountriesServiceGrpc.newBlockingStub(channel);
  }

  @Bean
  public RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub rangifflerUserdataServiceBlockingStub(
    GrpcChannelFactory grpcChannelFactory
  ) {
    Channel channel = grpcChannelFactory.createChannel("userdata-service");
    return RangifflerUserdataServiceGrpc.newBlockingStub(channel);
  }
}
