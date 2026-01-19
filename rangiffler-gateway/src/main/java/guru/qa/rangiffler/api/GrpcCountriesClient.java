package guru.qa.rangiffler.api;

import com.google.protobuf.Empty;
import guru.qa.rangiffler.grpc.CountriesResponse;
import guru.qa.rangiffler.grpc.CountryRequest;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.RangifflerCountriesServiceGrpc;
import guru.qa.rangiffler.service.mapper.CountryMapper;
import guru.qa.rangiffler.service.utils.GrpcExceptionHandler;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@Component
@ParametersAreNonnullByDefault
public class GrpcCountriesClient {
  private final RangifflerCountriesServiceGrpc.RangifflerCountriesServiceBlockingStub rangifflerCountriesServiceBlockingStub;
  private final CountryMapper countryMapper;
  private final GrpcExceptionHandler grpcExceptionHandler;

  @Autowired
  public GrpcCountriesClient(RangifflerCountriesServiceGrpc.RangifflerCountriesServiceBlockingStub rangifflerCountriesServiceBlockingStub,
                             CountryMapper countryMapper) {
    this.rangifflerCountriesServiceBlockingStub = rangifflerCountriesServiceBlockingStub;
    this.countryMapper = countryMapper;
    this.grpcExceptionHandler = new GrpcExceptionHandler(
      GrpcCountriesClient.class,
      "gRPC Countries service"
    );
  }

  @Nonnull
  public CountriesResponse allCountries() {
    try {
      return rangifflerCountriesServiceBlockingStub.allCountries(Empty.getDefaultInstance());
    } catch (StatusRuntimeException e) {
      throw grpcExceptionHandler.handleGraphQLError(e);
    }
  }

  @Nonnull
  public CountryResponse getCountry(@Nullable String countryCode, @Nullable String id) {
    try {
      CountryRequest request = countryMapper.toProto(id, countryCode);
      return rangifflerCountriesServiceBlockingStub.getCountry(request);
    } catch (StatusRuntimeException e) {
      throw grpcExceptionHandler.handleGraphQLError(e);
    }
  }
}