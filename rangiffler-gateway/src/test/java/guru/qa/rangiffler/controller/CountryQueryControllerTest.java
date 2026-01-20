package guru.qa.rangiffler.controller;

import guru.qa.rangiffler.controller.countries.CountryQueryController;
import guru.qa.rangiffler.model.graphql.countries.CountryGql;
import guru.qa.rangiffler.service.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CountryQueryControllerTest {
  @Mock
  private CountryService countryService;

  private CountryQueryController countryQueryController;

  @BeforeEach
  void setUp() {
    countryQueryController = new CountryQueryController(countryService);
  }

  @Test
  void countries_ShouldReturnAllCountries() {
    final List<CountryGql> expectedCountries = List.of(
      new CountryGql(UUID.randomUUID(), "Country1", "C1", ""),
      new CountryGql(UUID.randomUUID(), "Country2", "C2", "")
    );

    when(countryService.allCountries()).thenReturn(expectedCountries);

    final List<CountryGql> actualCountries = countryQueryController.countries();

    verify(countryService).allCountries();
    assertEquals(expectedCountries, actualCountries);
  }

  @Test
  void country_ByCode_ShouldReturnCountry() {
    final String code = "US";
    final CountryGql expectedCountry = new CountryGql(
      UUID.randomUUID(),
      "United States",
      code,
      ""
    );

    when(countryService.country(code, null)).thenReturn(expectedCountry);

    final CountryGql actualCountry = countryQueryController.country(code, null);

    verify(countryService).country(code, null);
    assertEquals(expectedCountry, actualCountry);
  }

  @Test
  void country_ById_ShouldReturnCountry() {
    final String id = UUID.randomUUID().toString();
    final CountryGql expectedCountry = new CountryGql(
      UUID.randomUUID(),
      "Test Country",
      "TC",
      ""
    );

    when(countryService.country(null, id)).thenReturn(expectedCountry);

    final CountryGql actualCountry = countryQueryController.country(null, id);

    verify(countryService).country(null, id);
    assertEquals(expectedCountry, actualCountry);
  }

  @Test
  void country_ByCodeAndId_ShouldReturnCountry() {
    final String code = "US";
    final String id = UUID.randomUUID().toString();
    final CountryGql expectedCountry = new CountryGql(
      UUID.randomUUID(),
      "United States",
      code,
      ""
    );

    when(countryService.country(code, id)).thenReturn(expectedCountry);

    final CountryGql actualCountry = countryQueryController.country(code, id);

    verify(countryService).country(code, id);
    assertEquals(expectedCountry, actualCountry);
  }

  @Test
  void countries_WithEmptyList_ShouldReturnEmptyList() {
    final List<CountryGql> expectedEmptyList = List.of();

    when(countryService.allCountries()).thenReturn(expectedEmptyList);

    final List<CountryGql> actualCountries = countryQueryController.countries();

    verify(countryService).allCountries();
    assertEquals(expectedEmptyList, actualCountries);
  }

  @Test
  void countries_WithSingleCountry_ShouldReturnSingleElementList() {
    final List<CountryGql> expectedSingleList = List.of(
      new CountryGql(UUID.randomUUID(), "Single Country", "SC", "")
    );

    when(countryService.allCountries()).thenReturn(expectedSingleList);

    final List<CountryGql> actualCountries = countryQueryController.countries();

    verify(countryService).allCountries();
    assertEquals(expectedSingleList, actualCountries);
  }

  @Test
  void country_WithNullParameters_ShouldReturnCountry() {
    final CountryGql expectedCountry = new CountryGql(
      UUID.randomUUID(),
      "Default Country",
      "DC",
      ""
    );

    when(countryService.country(null, null)).thenReturn(expectedCountry);

    final CountryGql actualCountry = countryQueryController.country(null, null);

    verify(countryService).country(null, null);
    assertEquals(expectedCountry, actualCountry);
  }

  @Test
  void country_WithEmptyCode_ShouldReturnCountry() {
    final String emptyCode = "";
    final CountryGql expectedCountry = new CountryGql(
      UUID.randomUUID(),
      "Default",
      "DF",
      ""
    );

    when(countryService.country(emptyCode, null)).thenReturn(expectedCountry);

    final CountryGql actualCountry = countryQueryController.country(emptyCode, null);

    verify(countryService).country(emptyCode, null);
    assertEquals(expectedCountry, actualCountry);
  }

  @Test
  void country_WithEmptyId_ShouldReturnCountry() {
    final String emptyId = "";
    final CountryGql expectedCountry = new CountryGql(
      UUID.randomUUID(),
      "Default",
      "DF",
      ""
    );

    when(countryService.country(null, emptyId)).thenReturn(expectedCountry);

    final CountryGql actualCountry = countryQueryController.country(null, emptyId);

    verify(countryService).country(null, emptyId);
    assertEquals(expectedCountry, actualCountry);
  }

  @Test
  void country_WithEmptyCodeAndId_ShouldReturnCountry() {
    final String emptyCode = "";
    final String emptyId = "";
    final CountryGql expectedCountry = new CountryGql(
      UUID.randomUUID(),
      "Default",
      "DF",
      ""
    );

    when(countryService.country(emptyCode, emptyId)).thenReturn(expectedCountry);

    final CountryGql actualCountry = countryQueryController.country(emptyCode, emptyId);

    verify(countryService).country(emptyCode, emptyId);
    assertEquals(expectedCountry, actualCountry);
  }

  @Test
  void countries_ShouldReturnSameInstanceFromService() {
    final List<CountryGql> expectedCountries = List.of(
      new CountryGql(UUID.randomUUID(), "Country1", "C1", ""),
      new CountryGql(UUID.randomUUID(), "Country2", "C2", "")
    );

    when(countryService.allCountries()).thenReturn(expectedCountries);

    final List<CountryGql> actualCountries = countryQueryController.countries();

    verify(countryService).allCountries();
    assertEquals(expectedCountries, actualCountries);
  }

  @Test
  void country_ShouldReturnSameInstanceFromService() {
    final String code = "US";
    final CountryGql expectedCountry = new CountryGql(
      UUID.randomUUID(),
      "United States",
      code,
      ""
    );

    when(countryService.country(code, null)).thenReturn(expectedCountry);

    final CountryGql actualCountry = countryQueryController.country(code, null);

    verify(countryService).country(code, null);
    assertEquals(expectedCountry, actualCountry);
  }
}
