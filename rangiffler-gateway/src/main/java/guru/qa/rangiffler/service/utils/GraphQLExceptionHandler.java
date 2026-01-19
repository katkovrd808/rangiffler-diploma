package guru.qa.rangiffler.service.utils;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@Component
@ParametersAreNonnullByDefault
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

  @Nonnull
  @Override
  protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
    if (ex instanceof ResponseStatusException rse) {
      return convertResponseStatusException(rse, env);
    }

    return GraphqlErrorBuilder.newError()
      .message(ex.getMessage())
      .errorType(ErrorType.INTERNAL_ERROR)
      .path(env.getExecutionStepInfo().getPath())
      .location(env.getField().getSourceLocation())
      .build();
  }

  @Nonnull
  private GraphQLError convertResponseStatusException(ResponseStatusException ex,
                                                      DataFetchingEnvironment env) {
    HttpStatus status = (HttpStatus) ex.getStatusCode();
    ErrorType errorType = mapToErrorType(status);

    String code = extractErrorCode(ex);
    String message = ex.getReason() != null ? ex.getReason() : ex.getMessage();

    return GraphqlErrorBuilder.newError()
      .message(message)
      .errorType(errorType)
      .path(env.getExecutionStepInfo().getPath())
      .location(env.getField().getSourceLocation())
      .extensions(
        Map.of(
          "code", code,
          "error", message)
      )
      .build();
  }

  @Nonnull
  private ErrorType mapToErrorType(HttpStatus status) {
    return switch (status) {
      case NOT_FOUND -> ErrorType.NOT_FOUND;
      case BAD_REQUEST -> ErrorType.BAD_REQUEST;
      case FORBIDDEN -> ErrorType.FORBIDDEN;
      case UNAUTHORIZED -> ErrorType.UNAUTHORIZED;
      default -> ErrorType.INTERNAL_ERROR;
    };
  }

  @Nonnull
  private String extractErrorCode(ResponseStatusException ex) {
    return String.valueOf(ex.getStatusCode().value());
  }
}
