package guru.qa.rangiffler.api;

import guru.qa.rangiffler.grpc.CountryRequest;
import guru.qa.rangiffler.grpc.RangifflerCountriesServiceGrpc;
import guru.qa.rangiffler.model.CountryDto;
import guru.qa.rangiffler.service.mapper.CountryMapper;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@Component
@ParametersAreNonnullByDefault
public class GrpcCountriesClient {

  private final static Logger LOG = LoggerFactory.getLogger(GrpcCountriesClient.class);
  private final RangifflerCountriesServiceGrpc.RangifflerCountriesServiceBlockingStub rangifflerCountriesServiceBlockingStub;
  private final CountryMapper countryMapper;

  @Autowired
  public GrpcCountriesClient(RangifflerCountriesServiceGrpc.RangifflerCountriesServiceBlockingStub rangifflerCountriesServiceBlockingStub,
                             CountryMapper countryMapper) {
    this.rangifflerCountriesServiceBlockingStub = rangifflerCountriesServiceBlockingStub;
    this.countryMapper = countryMapper;
  }

  @Nonnull
  public Optional<CountryDto> getCountryByCode(String countryCode) {
    try {
      CountryRequest request = countryMapper.toProto(null, countryCode);
      return countryMapper.toDto(rangifflerCountriesServiceBlockingStub.getCountry(request));
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

  @Nonnull
  public Optional<CountryDto> getCountryById(String id) {
    try {
      CountryRequest request = countryMapper.toProto(id, null);
      return countryMapper.toDto(rangifflerCountriesServiceBlockingStub.getCountry(request));
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
