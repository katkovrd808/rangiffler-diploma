package guru.qa.rangiffler.service;

import guru.qa.rangiffler.api.GrpcCountriesClient;
import guru.qa.rangiffler.grpc.CountriesResponse;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.model.graphql.countries.CountryGql;
import guru.qa.rangiffler.service.impl.CountryServiceImpl;
import guru.qa.rangiffler.service.mapper.CountryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CountryServiceTest {
  @Mock
  private GrpcCountriesClient grpcCountriesClient;
  @Mock
  private CountryMapper countryMapper;

  private CountryServiceImpl countryServiceImpl;

  @BeforeEach
  void setUp() {
    countryServiceImpl = new CountryServiceImpl(grpcCountriesClient, countryMapper);
  }

  @Test
  void country_ByCode_ShouldReturnCountry() {
    final String code = "US";
    final CountryResponse countryResponse = CountryResponse.newBuilder()
      .setId(UUID.randomUUID().toString())
      .setName("United States")
      .setCode(code)
      .build();
    final CountryGql expectedCountryGql = new CountryGql(
      UUID.randomUUID(),
      "United States",
      code,
      ""
    );

    when(grpcCountriesClient.getCountry(code, null)).thenReturn(countryResponse);
    when(countryMapper.toCountryGql(countryResponse)).thenReturn(expectedCountryGql);

    final CountryGql actualCountryGql = countryServiceImpl.country(code, null);

    verify(grpcCountriesClient).getCountry(code, null);
    verify(countryMapper).toCountryGql(countryResponse);
    assertEquals(expectedCountryGql, actualCountryGql);
  }

  @Test
  void country_ById_ShouldReturnCountry() {
    final String id = UUID.randomUUID().toString();
    final CountryResponse countryResponse = CountryResponse.newBuilder()
      .setId(id)
      .setName("Test Country")
      .setCode("TC")
      .build();
    final CountryGql expectedCountryGql = new CountryGql(
      UUID.randomUUID(),
      "Test Country",
      "TC",
      ""
    );

    when(grpcCountriesClient.getCountry(null, id)).thenReturn(countryResponse);
    when(countryMapper.toCountryGql(countryResponse)).thenReturn(expectedCountryGql);

    final CountryGql actualCountryGql = countryServiceImpl.country(null, id);

    verify(grpcCountriesClient).getCountry(null, id);
    verify(countryMapper).toCountryGql(countryResponse);
    assertEquals(expectedCountryGql, actualCountryGql);
  }

  @Test
  void country_ByCodeAndId_ShouldPreferCode() {
    final String code = "US";
    final String id = UUID.randomUUID().toString();
    final CountryResponse countryResponse = CountryResponse.newBuilder()
      .setId(id)
      .setName("United States")
      .setCode(code)
      .build();
    final CountryGql expectedCountryGql = new CountryGql(
      UUID.randomUUID(),
      "United States",
      code,
      ""
    );

    when(grpcCountriesClient.getCountry(code, id)).thenReturn(countryResponse);
    when(countryMapper.toCountryGql(countryResponse)).thenReturn(expectedCountryGql);

    final CountryGql actualCountryGql = countryServiceImpl.country(code, id);

    verify(grpcCountriesClient).getCountry(code, id);
    verify(countryMapper).toCountryGql(countryResponse);
    assertEquals(expectedCountryGql, actualCountryGql);
  }

  @Test
  void allCountries_ShouldReturnAllCountries() {
    final CountriesResponse countriesResponse = CountriesResponse.newBuilder()
      .addCountries(CountryResponse.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setName("Country1")
        .setCode("C1")
        .build())
      .addCountries(CountryResponse.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setName("Country2")
        .setCode("C2")
        .build())
      .build();
    final List<CountryGql> expectedCountries = List.of(
      new CountryGql(UUID.randomUUID(), "Country1", "C1", ""),
      new CountryGql(UUID.randomUUID(), "Country2", "C2", "")
    );

    when(grpcCountriesClient.allCountries()).thenReturn(countriesResponse);
    when(countryMapper.toCountriesGqlList(countriesResponse)).thenReturn(expectedCountries);

    final List<CountryGql> actualCountries = countryServiceImpl.allCountries();

    verify(grpcCountriesClient).allCountries();
    verify(countryMapper).toCountriesGqlList(countriesResponse);
    assertEquals(expectedCountries, actualCountries);
  }

  @Test
  void allCountries_WithEmptyResponse_ShouldReturnEmptyList() {
    final CountriesResponse emptyResponse = CountriesResponse.newBuilder().build();
    final List<CountryGql> expectedEmptyList = List.of();

    when(grpcCountriesClient.allCountries()).thenReturn(emptyResponse);
    when(countryMapper.toCountriesGqlList(emptyResponse)).thenReturn(expectedEmptyList);

    final List<CountryGql> actualCountries = countryServiceImpl.allCountries();

    verify(grpcCountriesClient).allCountries();
    verify(countryMapper).toCountriesGqlList(emptyResponse);
    assertEquals(expectedEmptyList, actualCountries);
  }

  @Test
  void country_WithNullParameters_ShouldCallClientWithNulls() {
    final CountryResponse countryResponse = CountryResponse.newBuilder()
      .setId(UUID.randomUUID().toString())
      .setName("Default Country")
      .setCode("DC")
      .build();
    final CountryGql expectedCountryGql = new CountryGql(
      UUID.randomUUID(),
      "Default Country",
      "DC",
      ""
    );

    when(grpcCountriesClient.getCountry(null, null)).thenReturn(countryResponse);
    when(countryMapper.toCountryGql(countryResponse)).thenReturn(expectedCountryGql);

    final CountryGql actualCountryGql = countryServiceImpl.country(null, null);

    verify(grpcCountriesClient).getCountry(null, null);
    verify(countryMapper).toCountryGql(countryResponse);
    assertEquals(expectedCountryGql, actualCountryGql);
  }

  @Test
  void country_WithEmptyCodeAndId_ShouldHandleGracefully() {
    final String emptyCode = "";
    final String emptyId = "";
    final CountryResponse countryResponse = CountryResponse.newBuilder()
      .setId(UUID.randomUUID().toString())
      .setName("Default")
      .setCode("DF")
      .build();
    final CountryGql expectedCountryGql = new CountryGql(
      UUID.randomUUID(),
      "Default",
      "DF",
      ""
    );

    when(grpcCountriesClient.getCountry(emptyCode, emptyId)).thenReturn(countryResponse);
    when(countryMapper.toCountryGql(countryResponse)).thenReturn(expectedCountryGql);

    final CountryGql actualCountryGql = countryServiceImpl.country(emptyCode, emptyId);

    verify(grpcCountriesClient).getCountry(emptyCode, emptyId);
    verify(countryMapper).toCountryGql(countryResponse);
    assertEquals(expectedCountryGql, actualCountryGql);
  }

  @Test
  void country_WithNonExistentCode_ShouldHandleDefaultResponse() {
    final String nonExistentCode = "XYZ";
    final CountryResponse defaultResponse = CountryResponse.getDefaultInstance();
    final CountryGql expectedCountryGql = new CountryGql(
      null,
      null,
      null,
      null
    );

    when(grpcCountriesClient.getCountry(nonExistentCode, null)).thenReturn(defaultResponse);
    when(countryMapper.toCountryGql(defaultResponse)).thenReturn(expectedCountryGql);

    final CountryGql actualCountryGql = countryServiceImpl.country(nonExistentCode, null);

    verify(grpcCountriesClient).getCountry(nonExistentCode, null);
    verify(countryMapper).toCountryGql(defaultResponse);
    assertEquals(expectedCountryGql, actualCountryGql);
  }

  @Test
  void country_WithNonExistentId_ShouldHandleDefaultResponse() {
    final String nonExistentId = UUID.randomUUID().toString();
    final CountryResponse defaultResponse = CountryResponse.getDefaultInstance();
    final CountryGql expectedCountryGql = new CountryGql(
      null,
      null,
      null,
      null
    );

    when(grpcCountriesClient.getCountry(null, nonExistentId)).thenReturn(defaultResponse);
    when(countryMapper.toCountryGql(defaultResponse)).thenReturn(expectedCountryGql);

    final CountryGql actualCountryGql = countryServiceImpl.country(null, nonExistentId);

    verify(grpcCountriesClient).getCountry(null, nonExistentId);
    verify(countryMapper).toCountryGql(defaultResponse);
    assertEquals(expectedCountryGql, actualCountryGql);
  }

  @Test
  void allCountries_WithSingleCountry_ShouldReturnSingleElementList() {
    final CountriesResponse singleCountryResponse = CountriesResponse.newBuilder()
      .addCountries(CountryResponse.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setName("Single Country")
        .setCode("SC")
        .build())
      .build();
    final List<CountryGql> expectedSingleList = List.of(
      new CountryGql(UUID.randomUUID(), "Single Country", "SC", "")
    );

    when(grpcCountriesClient.allCountries()).thenReturn(singleCountryResponse);
    when(countryMapper.toCountriesGqlList(singleCountryResponse)).thenReturn(expectedSingleList);

    final List<CountryGql> actualCountries = countryServiceImpl.allCountries();

    verify(grpcCountriesClient).allCountries();
    verify(countryMapper).toCountriesGqlList(singleCountryResponse);
    assertEquals(expectedSingleList, actualCountries);
  }

  @Test
  void allCountries_WithMultipleCountries_ShouldReturnCorrectOrder() {
    final CountriesResponse countriesResponse = CountriesResponse.newBuilder()
      .addCountries(CountryResponse.newBuilder()
        .setId("1")
        .setName("Country A")
        .setCode("A")
        .build())
      .addCountries(CountryResponse.newBuilder()
        .setId("2")
        .setName("Country B")
        .setCode("B")
        .build())
      .addCountries(CountryResponse.newBuilder()
        .setId("3")
        .setName("Country C")
        .setCode("C")
        .build())
      .build();
    final List<CountryGql> expectedOrderedList = List.of(
      new CountryGql(UUID.randomUUID(), "Country A", "A", ""),
      new CountryGql(UUID.randomUUID(), "Country B", "B", ""),
      new CountryGql(UUID.randomUUID(), "Country C", "C", "")
    );

    when(grpcCountriesClient.allCountries()).thenReturn(countriesResponse);
    when(countryMapper.toCountriesGqlList(countriesResponse)).thenReturn(expectedOrderedList);

    final List<CountryGql> actualCountries = countryServiceImpl.allCountries();

    verify(grpcCountriesClient).allCountries();
    verify(countryMapper).toCountriesGqlList(countriesResponse);
    assertEquals(expectedOrderedList, actualCountries);
  }
}
