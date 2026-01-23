package guru.qa.rangiffler.data.entity.photos;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class StatisticId {
  private UUID userId;
  private UUID countryId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StatisticId that = (StatisticId) o;
    return Objects.equals(userId, that.userId) &&
      Objects.equals(countryId, that.countryId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, countryId);
  }
}
