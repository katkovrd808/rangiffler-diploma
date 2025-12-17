package guru.qa.rangiffler.api;

import guru.qa.rangiffler.grpc.RangifflerUserdataServiceGrpc;
import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.model.graphql.UserGql;
import guru.qa.rangiffler.service.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.ParametersAreNonnullByDefault;

@Component
@ParametersAreNonnullByDefault
public class GrpcUserdataClient {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcUserdataClient.class);
  private final RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub rangifflerUserdataServiceBlockingStub;
  private final UserMapper userMapper;

  @Autowired
  public GrpcUserdataClient(RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub rangifflerUserdataServiceBlockingStub,
                            UserMapper userMapper) {
    this.rangifflerUserdataServiceBlockingStub = rangifflerUserdataServiceBlockingStub;
    this.userMapper = userMapper;
  }

  public UserResponse findByUsername(String username) {
    rangifflerUserdataServiceBlockingStub.getUser();
  }

}
