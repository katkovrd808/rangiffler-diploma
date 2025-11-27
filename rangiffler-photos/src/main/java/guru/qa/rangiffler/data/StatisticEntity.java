package guru.qa.rangiffler.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_country_statistic")
@IdClass(StatisticId.class)
public class StatisticEntity {

  @Id
  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Id
  @Column(name = "country_id", nullable = false)
  private UUID countryId;

  @Column(name = "count", nullable = false)
  private Integer count;

  @Column(name = "updated_at", columnDefinition = "DATE", nullable = false)
  private Date updatedAt;

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    StatisticEntity that = (StatisticEntity) o;
    return getUserId() != null && Objects.equals(getUserId(), that.getUserId())
      && getCountryId() != null && Objects.equals(getCountryId(), that.getCountryId());
  }

  @Override
  public final int hashCode() {
    return Objects.hash(userId, countryId);
  }
}
