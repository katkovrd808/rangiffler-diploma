package guru.qa.rangiffler.grpc;

import guru.qa.rangiffler.ex.InvalidPhotoLikeOperationException;
import guru.qa.rangiffler.ex.PhotoLikeNotFoundException;
import guru.qa.rangiffler.ex.PhotoNotFoundException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.stereotype.Component;

import static io.grpc.Status.*;

@Component
public class GlobalGrpcExceptionHandler implements GrpcExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalGrpcExceptionHandler.class);

  @Override
  public Status handleException(Throwable exception) {
    Status status = switch (exception) {
      case PhotoLikeNotFoundException e -> INVALID_ARGUMENT;
      case PhotoNotFoundException e -> NOT_FOUND;
      case InvalidPhotoLikeOperationException e -> PERMISSION_DENIED;
      case IllegalArgumentException e -> INVALID_ARGUMENT;
      case UnsupportedOperationException e -> Status.UNIMPLEMENTED;
      case StatusRuntimeException e -> Status.UNAVAILABLE;
      default -> INTERNAL;
    };

    logException(exception, status);

    return status.withDescription(exception.getMessage());
  }

  private void logException(Throwable exception, Status status) {
    Code statusCode = status.getCode();

    switch (statusCode) {
      case INTERNAL -> LOG.error("Internal error: {}", exception.getMessage(), exception);
      case NOT_FOUND, PERMISSION_DENIED, INVALID_ARGUMENT, UNIMPLEMENTED, UNAVAILABLE ->
        LOG.warn("Business exception [{}]: {}", statusCode, exception.getMessage());
      default -> LOG.info("Handled exception [{}]: {}", statusCode, exception.getMessage());
    }
  }
}
