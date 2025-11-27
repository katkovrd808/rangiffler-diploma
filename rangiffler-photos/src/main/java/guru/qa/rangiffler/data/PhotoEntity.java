package guru.qa.rangiffler.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Stream;

@Getter
@Setter
@Entity
@Table(name = "photo")
public class PhotoEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "country_id", nullable = false)
  private UUID countryId;

  @Column
  private String description;

  @Column(columnDefinition = "bytea")
  private byte[] photo;

  @Column(name = "created_date", columnDefinition = "DATE", insertable = false)
  private Date createdDate;

  @OneToMany(mappedBy = "photo", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PhotoLikeEntity> photoLikes = new ArrayList<>();

  public void addLikes(PhotoLikeEntity... photos) {
    List<PhotoLikeEntity> photoLikesEntities = Stream.of(photos)
      .map(p -> {
        PhotoLikeEntity pe = new PhotoLikeEntity();
        pe.setId(p.getId());
        pe.setPhoto(p.getPhoto());
        pe.setUserId(p.getUserId());
        pe.setCreatedDate(p.getCreatedDate());
        return pe;
      }).toList();
    this.photoLikes.addAll(photoLikesEntities);
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PhotoEntity that = (PhotoEntity) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return Objects.hash(getId());
  }
}
