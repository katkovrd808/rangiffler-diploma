package guru.qa.rangiffler.model;

import guru.qa.rangiffler.data.PhotoEntity;

import java.util.UUID;

public record PhotoDto(
  UUID id,
  UUID userId,
  String description,
  UUID countryId
) {
  public static PhotoDto fromEntity(PhotoEntity entity, String countryCode) {
    return new PhotoDto(
      entity.getId(),
      entity.getUserId(),
      entity.getDescription(),
      entity.getCountryId()
    );
  }
}
