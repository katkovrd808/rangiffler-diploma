package guru.qa.rangiffler.service.api;

import com.google.protobuf.Empty;
import guru.qa.rangiffler.domain.UserGql;
import guru.qa.rangiffler.grpc.RangifflerUserdataServiceGrpc;
import guru.qa.rangiffler.service.mapper.UserMapper;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@Component
@ParametersAreNonnullByDefault
public class GrpcUserdataClient extends RangifflerUserdataServiceGrpc.RangifflerUserdataServiceImplBase {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcUserdataClient.class);
  private static final Empty EMPTY = Empty.getDefaultInstance();

  private final UserMapper userMapper;

  @Autowired
  public GrpcUserdataClient(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @GrpcClient("grpcUserdataClient")
  private RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub rangifflerUserdataServiceBlockingStub;

  @Nonnull
  public UserGql getUser(UserGql user) {
    try {
      return userMapper.toGql(rangifflerUserdataServiceBlockingStub.getUser(userMapper.toUserRequest(user)));
    } catch (StatusRuntimeException e) {
      LOG.error("### Error while calling gRPC server ", e);
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
    }
  }

  @Nonnull
  public List<UserGql> getAllUsers() {
    try {
      return rangifflerUserdataServiceBlockingStub.allUsers(EMPTY).getUserList()
        .stream()
        .map(userMapper::toGql)
        .toList();
    } catch (StatusRuntimeException e) {
      LOG.error("### Error while calling gRPC server ", e);
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
    }
  }

}
