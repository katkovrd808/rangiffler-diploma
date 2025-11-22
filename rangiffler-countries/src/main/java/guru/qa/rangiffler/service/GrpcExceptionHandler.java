package guru.qa.rangiffler.service;

import guru.qa.rangiffler.ex.CountryNotFoundException;
import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcExceptionHandler implements ServerInterceptor {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcExceptionHandler.class);

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
                                                               Metadata headers,
                                                               ServerCallHandler<ReqT, RespT> next) {
    ServerCall.Listener<ReqT> listener = next.startCall(call, headers);
    return new ExceptionHandlingListener<>(listener, call, headers);
  }

  private static class ExceptionHandlingListener<ReqT, RespT>
    extends ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT> {
    private final ServerCall<ReqT, RespT> call;
    private final Metadata headers;

    ExceptionHandlingListener(ServerCall.Listener<ReqT> listener,
                              ServerCall<ReqT, RespT> call,
                              Metadata headers) {
      super(listener);
      this.call = call;
      this.headers = headers;
    }

    @Override
    public void onHalfClose() {
      try {
        super.onHalfClose();
      } catch (Exception e) {
        handleException(e, call, headers);
      }
    }

    @Override
    public void onReady() {
      try {
        super.onReady();
      } catch (Exception e) {
        handleException(e, call, headers);
      }
    }

    private void handleException(Exception e, ServerCall<ReqT, RespT> call, Metadata headers) {
      Status status = mapExceptionToStatus(e);
      LOG.warn("### Resolve Exception in gRPC Interceptor", e);
      call.close(status, headers);
    }

    private Status mapExceptionToStatus(Exception e) {
      if (e instanceof CountryNotFoundException) {
        return Status.NOT_FOUND
          .withDescription(e.getMessage())
          .withCause(e);
      }
      return Status.INTERNAL
        .withDescription("Internal server error")
        .withCause(e);
    }
  }
}
