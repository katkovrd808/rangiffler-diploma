package guru.qa.rangiffler.model;

public record CountryDto(
  String name,
  String code,
  byte[] flag
) {
}
