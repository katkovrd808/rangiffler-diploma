package guru.qa.rangiffler;

import guru.qa.rangiffler.grpc.GrpcCountryService;
import guru.qa.rangiffler.service.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RangifflerGrpcCountriesServiceTest {
  private GrpcCountryService grpcCountryService;

  @BeforeEach
  void setUp(@Mock CountryService countryService) {

  }
}
