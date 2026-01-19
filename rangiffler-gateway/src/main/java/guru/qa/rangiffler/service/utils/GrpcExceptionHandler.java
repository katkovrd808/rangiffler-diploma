package guru.qa.rangiffler.service.utils;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class GrpcExceptionHandler {
  private final Logger logger;
  private final String serviceName;

  public GrpcExceptionHandler(Class<?> clazz, String serviceName) {
    this.logger = LoggerFactory.getLogger(clazz);
    this.serviceName = serviceName;
  }

  @Nonnull
  public ResponseStatusException handleGraphQLError(StatusRuntimeException e) {
    logger.error("Error while calling {} gRPC service", serviceName, e);

    final Status status = e.getStatus();
    final String description = status.getDescription() != null
      ? status.getDescription()
      : "No additional details";

    return switch (status.getCode()) {
      case NOT_FOUND -> new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        String.format("Not found in %s. Details: %s", serviceName, description),
        e
      );
      case INVALID_ARGUMENT -> new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        String.format("Invalid request to %s: %s", serviceName, description),
        e
      );
      case PERMISSION_DENIED -> new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        String.format("Access denied to %s", serviceName),
        e
      );
      case UNAVAILABLE, DEADLINE_EXCEEDED -> new ResponseStatusException(
        HttpStatus.SERVICE_UNAVAILABLE,
        String.format("%s is temporarily unavailable", serviceName),
        e
      );
      default -> new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR,
        String.format("%s error: %s", serviceName, description),
        e
      );
    };
  }
}
