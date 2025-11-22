package guru.qa.rangiffler.service;

import guru.qa.rangiffler.ex.UserNotFoundException;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.stereotype.Component;

@Component
public class GlobalGrpcExceptionHandler implements GrpcExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalGrpcExceptionHandler.class);

  @Override
  public Status handleException(Throwable exception) {
    Status status =
      exception instanceof UserNotFoundException ? Status.NOT_FOUND :
        exception instanceof IllegalArgumentException ? Status.INVALID_ARGUMENT :
          exception instanceof UnsupportedOperationException ? Status.UNIMPLEMENTED :
            Status.INTERNAL;

    if (status == Status.INTERNAL) {
      LOG.error("Internal error: {}", exception.getMessage(), exception);
    } else {
      LOG.warn("Business exception [{}]: {}", status.getCode(), exception.getMessage());
    }

    return status.withDescription(exception.getMessage());
  }
}
