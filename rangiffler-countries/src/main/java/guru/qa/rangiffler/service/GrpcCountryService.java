package guru.qa.rangiffler.service;

import com.google.protobuf.Empty;
import guru.qa.rangiffler.grpc.CountriesResponse;
import guru.qa.rangiffler.grpc.CountryRequest;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.RangifflerCountriesServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@Service
@ParametersAreNonnullByDefault
public class GrpcCountryService extends RangifflerCountriesServiceGrpc.RangifflerCountriesServiceImplBase {

  private final CountryService countryService;

  @Autowired
  public GrpcCountryService(CountryService countryService) {
    this.countryService = countryService;
  }

  @Override
  public void getCountry(CountryRequest request, StreamObserver<CountryResponse> responseObserver) {
    CountryResponse response = countryService.findByIsoCode(request.getCode());
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void allCountries(Empty request, StreamObserver<CountriesResponse> responseObserver) {
    CountriesResponse response = countryService.allCountries();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
