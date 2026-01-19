package guru.qa.rangiffler.service;

import guru.qa.rangiffler.data.CountryEntity;
import guru.qa.rangiffler.data.repository.CountryRepository;
import guru.qa.rangiffler.ex.CountryNotFoundException;
import guru.qa.rangiffler.ex.IsoCodeMismatchException;
import guru.qa.rangiffler.grpc.CountriesResponse;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.NeededCountriesResponse;
import guru.qa.rangiffler.service.impl.DbCountryService;
import guru.qa.rangiffler.service.mapper.CountryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CountriesServiceTest {
  @Mock
  private CountryRepository countryRepository;

  @Mock
  private CountryMapper countryMapper;

  private DbCountryService dbCountryService;

  @BeforeEach
  void setUp() {
    dbCountryService = new DbCountryService(countryRepository, countryMapper);
  }

  @Test
  void findByIsoCode_WithValidCode_ShouldReturnCountry() {
    final String isoCode = "US";
    final String upperCaseCode = "US";
    final CountryEntity entity = new CountryEntity();
    final CountryResponse expectedResponse = CountryResponse.newBuilder()
      .setId(UUID.randomUUID().toString())
      .setCode(upperCaseCode)
      .setName("United States")
      .build();

    when(countryRepository.findByIsoCode(upperCaseCode)).thenReturn(Optional.of(entity));
    when(countryMapper.toProto(entity)).thenReturn(expectedResponse);

    final CountryResponse actualResponse = dbCountryService.findByIsoCode(isoCode);

    verify(countryRepository).findByIsoCode(upperCaseCode);
    verify(countryMapper).toProto(entity);
    assertEquals(expectedResponse, actualResponse);
  }

  @ValueSource(strings = {"A", "a-Z", "aBcD", "", "123", "ISO1"})
  @ParameterizedTest
  void findByIsoCode_WithInvalidCode_ShouldThrowException(String invalidCode) {
    final IsoCodeMismatchException exception = assertThrows(
      IsoCodeMismatchException.class,
      () -> dbCountryService.findByIsoCode(invalidCode)
    );

    assertEquals(
      "Iso code pattern mismatch. Accepts only ISO-2 code.",
      exception.getMessage()
    );
    verify(
      countryRepository,
      never()
    ).findByIsoCode(any());
    verify(
      countryMapper,
      never()
    ).toProto(any());
  }

  @Test
  void findByIsoCode_WithNullCode_ShouldThrowException() {
    final IsoCodeMismatchException exception = assertThrows(
      IsoCodeMismatchException.class,
      () -> dbCountryService.findByIsoCode(null)
    );

    assertEquals("Iso code pattern mismatch. Accepts only ISO-2 code.", exception.getMessage());
    verify(countryRepository, never()).findByIsoCode(any());
    verify(countryMapper, never()).toProto(any());
  }

  @Test
  void findByIsoCode_WhenCountryNotFound_ShouldThrowException() {
    final String isoCode = "XX";
    when(countryRepository.findByIsoCode(isoCode)).thenReturn(Optional.empty());

    final CountryNotFoundException exception = assertThrows(
      CountryNotFoundException.class,
      () -> dbCountryService.findByIsoCode(isoCode)
    );

    assertEquals("Can't find country with iso code: " + isoCode, exception.getMessage());
    verify(countryRepository).findByIsoCode(isoCode);
    verify(countryMapper, never()).toProto(any());
  }

  @Test
  void findByIsoCode_ShouldConvertToUpperCase() {
    final String lowerCaseCode = "us";
    final String upperCaseCode = "US";
    final CountryEntity entity = new CountryEntity();

    when(countryRepository.findByIsoCode(upperCaseCode)).thenReturn(Optional.of(entity));
    when(countryMapper.toProto(entity)).thenReturn(CountryResponse.getDefaultInstance());

    dbCountryService.findByIsoCode(lowerCaseCode);

    verify(countryRepository).findByIsoCode(upperCaseCode);
  }

  @Test
  void findById_WithValidId_ShouldReturnCountry() {
    final UUID id = UUID.randomUUID();
    final String idString = id.toString();
    final CountryEntity entity = new CountryEntity();
    final CountryResponse expectedResponse = CountryResponse.newBuilder()
      .setId(idString)
      .setCode("US")
      .setName("United States")
      .build();

    when(countryRepository.findById(id)).thenReturn(Optional.of(entity));
    when(countryMapper.toProto(entity)).thenReturn(expectedResponse);

    final CountryResponse actualResponse = dbCountryService.findById(idString);

    verify(countryRepository).findById(id);
    verify(countryMapper).toProto(entity);
    assertEquals(expectedResponse, actualResponse);
  }

  @ParameterizedTest
  @NullAndEmptySource
  void findById_WithNullOrEmptyId_ShouldThrowException(String invalidId) {
    final IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> dbCountryService.findById(invalidId)
    );

    assertEquals("Id can't be null value.", exception.getMessage());
    verify(countryRepository, never()).findById(any());
    verify(countryMapper, never()).toProto(any());
  }

  @Test
  void findById_WithInvalidUUIDFormat_ShouldThrowException() {
    final String invalidUuid = "not-a-uuid";

    assertThrows(
      IllegalArgumentException.class,
      () -> dbCountryService.findById(invalidUuid)
    );

    verify(countryRepository, never()).findById(any());
    verify(countryMapper, never()).toProto(any());
  }

  @Test
  void findById_WhenCountryNotFound_ShouldThrowException() {
    final UUID id = UUID.randomUUID();
    when(countryRepository.findById(id)).thenReturn(Optional.empty());

    final CountryNotFoundException exception = assertThrows(
      CountryNotFoundException.class,
      () -> dbCountryService.findById(id.toString())
    );

    assertEquals("Can't find country with id: " + id, exception.getMessage());
    verify(countryRepository).findById(id);
    verify(countryMapper, never()).toProto(any());
  }

  @Test
  void allCountries_ShouldReturnOrderedCountries() {
    final List<CountryEntity> entities = List.of(
      new CountryEntity(),
      new CountryEntity()
    );
    final CountriesResponse expectedResponse = CountriesResponse.newBuilder()
      .addCountries(CountryResponse.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setCode("US")
        .setName("United States"))
      .addCountries(CountryResponse.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setCode("GB")
        .setName("United Kingdom"))
      .build();

    when(countryRepository.findAllByOrderByNameAsc()).thenReturn(entities);
    when(countryMapper.toProtoList(entities)).thenReturn(expectedResponse);

    final CountriesResponse actualResponse = dbCountryService.allCountries();

    verify(countryRepository).findAllByOrderByNameAsc();
    verify(countryMapper).toProtoList(entities);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void allCountries_WhenNoCountries_ShouldReturnEmptyResponse() {
    final List<CountryEntity> entities = List.of();
    final CountriesResponse expectedResponse = CountriesResponse.newBuilder().build();

    when(countryRepository.findAllByOrderByNameAsc()).thenReturn(entities);
    when(countryMapper.toProtoList(entities)).thenReturn(expectedResponse);

    final CountriesResponse actualResponse = dbCountryService.allCountries();

    verify(countryRepository).findAllByOrderByNameAsc();
    verify(countryMapper).toProtoList(entities);
    assertEquals(expectedResponse, actualResponse);
    assertEquals(0, actualResponse.getCountriesCount());
  }

  @Test
  void findNeededCountries_WithIds_ShouldReturnCountries() {
    final UUID id1 = UUID.randomUUID();
    final UUID id2 = UUID.randomUUID();
    final UUID id3 = UUID.randomUUID();
    final List<UUID> ids = List.of(id1, id2, id3);

    final List<CountryEntity> entities = List.of(
      new CountryEntity(),
      new CountryEntity()
    );
    final NeededCountriesResponse expectedResponse = NeededCountriesResponse.newBuilder()
      .addCountries(CountryResponse.newBuilder()
        .setId(id1.toString())
        .setCode("US")
        .setName("United States"))
      .addCountries(CountryResponse.newBuilder()
        .setId(id2.toString())
        .setCode("GB")
        .setName("United Kingdom"))
      .build();

    when(countryRepository.findNeededCountries(ids)).thenReturn(entities);
    when(countryMapper.toNeededCountriesProto(entities)).thenReturn(expectedResponse);

    final NeededCountriesResponse actualResponse = dbCountryService.findNeededCountries(ids);

    verify(countryRepository).findNeededCountries(ids);
    verify(countryMapper).toNeededCountriesProto(entities);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void findNeededCountries_WhenRepositoryReturnsEmptyListForNonEmptyInput_ShouldReturnEmptyResponse() {
    final UUID id1 = UUID.randomUUID();
    final UUID id2 = UUID.randomUUID();
    final List<UUID> ids = List.of(id1, id2);

    final List<CountryEntity> emptyEntities = List.of();
    final NeededCountriesResponse expectedResponse = NeededCountriesResponse.newBuilder().build();

    when(countryRepository.findNeededCountries(ids)).thenReturn(emptyEntities);
    when(countryMapper.toNeededCountriesProto(emptyEntities)).thenReturn(expectedResponse);

    final NeededCountriesResponse actualResponse = dbCountryService.findNeededCountries(ids);

    verify(countryRepository).findNeededCountries(ids);
    verify(countryMapper).toNeededCountriesProto(emptyEntities);
    assertEquals(expectedResponse, actualResponse);
    assertEquals(0, actualResponse.getCountriesCount());
  }

  @Test
  void findNeededCountries_WithEmptyList_ShouldThrowException() {
    final List<UUID> emptyList = List.of();

    final IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> dbCountryService.findNeededCountries(emptyList)
    );

    assertEquals("Countries list can't be null or empty value", exception.getMessage());
    verify(countryRepository, never()).findNeededCountries(any());
    verify(countryMapper, never()).toNeededCountriesProto(any());
  }

  @Test
  void findNeededCountries_WithNullList_ShouldThrowException() {
    final IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> dbCountryService.findNeededCountries(null)
    );

    assertEquals("Countries list can't be null or empty value", exception.getMessage());
    verify(countryRepository, never()).findNeededCountries(any());
    verify(countryMapper, never()).toNeededCountriesProto(any());
  }

  @Test
  void findNeededCountries_WhenRepositoryReturnsEmptyList_ShouldReturnEmptyResponse() {
    final UUID id1 = UUID.randomUUID();
    final UUID id2 = UUID.randomUUID();
    final List<UUID> ids = List.of(id1, id2);

    final List<CountryEntity> emptyEntities = List.of();
    final NeededCountriesResponse expectedResponse = NeededCountriesResponse.newBuilder().build();

    when(countryRepository.findNeededCountries(ids)).thenReturn(emptyEntities);
    when(countryMapper.toNeededCountriesProto(emptyEntities)).thenReturn(expectedResponse);

    final NeededCountriesResponse actualResponse = dbCountryService.findNeededCountries(ids);

    verify(countryRepository).findNeededCountries(ids);
    verify(countryMapper).toNeededCountriesProto(emptyEntities);
    assertEquals(expectedResponse, actualResponse);
    assertEquals(0, actualResponse.getCountriesCount());
  }
}