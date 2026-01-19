package guru.qa.rangiffler.grpc;

import com.google.protobuf.Empty;
import guru.qa.rangiffler.service.CountryService;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@GrpcService
@ParametersAreNonnullByDefault
public class GrpcCountryService extends RangifflerCountriesServiceGrpc.RangifflerCountriesServiceImplBase {

  private final CountryService countryService;

  @Autowired
  public GrpcCountryService(CountryService countryService) {
    this.countryService = countryService;
  }

  @Override
  public void getCountry(CountryRequest request, StreamObserver<CountryResponse> responseObserver) {
    CountryResponse response = request.hasId()
      ? countryService.findById(request.getId())
      : countryService.findByIsoCode(request.getCode());
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void allCountries(Empty request, StreamObserver<CountriesResponse> responseObserver) {
    CountriesResponse response = countryService.allCountries();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getCountriesByIds(NeededCountriesRequest request, StreamObserver<NeededCountriesResponse> responseObserver) {
    NeededCountriesResponse response = countryService.findNeededCountries(request.getIdList().stream()
      .map(UUID::fromString)
      .toList()
    );
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
