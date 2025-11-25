package guru.qa.rangiffler.api;

import guru.qa.rangiffler.grpc.RangifflerUserdataServiceGrpc;
import guru.qa.rangiffler.grpc.UserRequest;
import guru.qa.rangiffler.model.UserDto;
import guru.qa.rangiffler.service.mapper.UserMapper;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@Component
@ParametersAreNonnullByDefault
public class GrpcUserdataClient {

  private final static Logger LOG = LoggerFactory.getLogger(GrpcUserdataClient.class);

  private final RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub rangifflerUserdataServiceBlockingStub;
  private final UserMapper userMapper;

  @Autowired
  public GrpcUserdataClient(RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub rangifflerUserdataServiceBlockingStub,
                            UserMapper userMapper) {
    this.rangifflerUserdataServiceBlockingStub = rangifflerUserdataServiceBlockingStub;
    this.userMapper = userMapper;
  }

  @Nonnull
  public Optional<UserDto> getUserById(String id) {
    try {
      UserRequest request = userMapper.toProto(UUID.fromString(id));
      return userMapper.toDto(rangifflerUserdataServiceBlockingStub.getUser(request));
    } catch (StatusRuntimeException e) {
      LOG.error("### Error while calling gRPC server ", e);
      Status.Code code = e.getStatus().getCode();
      if (code == Status.Code.NOT_FOUND || code == Status.Code.INVALID_ARGUMENT) {
        return Optional.empty();
      } else {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled or Userdata service unavailable", e);
      }
    }
  }
}
