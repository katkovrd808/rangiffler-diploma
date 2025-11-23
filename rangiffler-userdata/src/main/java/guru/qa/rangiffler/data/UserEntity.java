package guru.qa.rangiffler.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Stream;

@Getter
@Setter
@Entity
@Table(name = "\"user\"")
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, columnDefinition = "UUID default uuid_generate_v1()")
  private UUID id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column
  private String firstname;

  @Column
  private String surname;

  @Column(name = "country_id")
  private UUID countryId;

  @Column(columnDefinition = "bytea")
  private byte[] photo;

  @OneToMany(mappedBy = "requester", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<FriendshipEntity> friendshipRequests = new ArrayList<>();

  @OneToMany(mappedBy = "addressee", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<FriendshipEntity> friendshipAddressees = new ArrayList<>();

  public void addFriends(FriendshipStatus status, UserEntity... friends) {
    List<FriendshipEntity> friendsEntities = Stream.of(friends)
      .map(f -> {
        FriendshipEntity fe = new FriendshipEntity();
        fe.setRequester(this);
        fe.setAddressee(f);
        fe.setStatus(status);
        fe.setCreatedDate(new Date());
        return fe;
      }).toList();
    this.friendshipRequests.addAll(friendsEntities);
  }

  public void addInvitations(UserEntity... invitations) {
    List<FriendshipEntity> invitationsEntities = Stream.of(invitations)
      .map(i -> {
        FriendshipEntity fe = new FriendshipEntity();
        fe.setRequester(i);
        fe.setAddressee(this);
        fe.setStatus(FriendshipStatus.PENDING);
        fe.setCreatedDate(new Date());
        return fe;
      }).toList();
    this.friendshipAddressees.addAll(invitationsEntities);
  }

  public void removeFriends(UserEntity... friends) {
    List<UUID> idsToBeRemoved = Arrays.stream(friends).map(UserEntity::getId).toList();
    for (Iterator<FriendshipEntity> i = getFriendshipRequests().iterator(); i.hasNext(); ) {
      FriendshipEntity friendsEntity = i.next();
      if (idsToBeRemoved.contains(friendsEntity.getAddressee().getId())) {
        friendsEntity.setAddressee(null);
        i.remove();
      }
    }
  }

  public void removeInvites(UserEntity... invitations) {
    List<UUID> idsToBeRemoved = Arrays.stream(invitations).map(UserEntity::getId).toList();
    for (Iterator<FriendshipEntity> i = getFriendshipAddressees().iterator(); i.hasNext(); ) {
      FriendshipEntity friendsEntity = i.next();
      if (idsToBeRemoved.contains(friendsEntity.getRequester().getId())) {
        friendsEntity.setRequester(null);
        i.remove();
      }
    }
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    UserEntity that = (UserEntity) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return Objects.hash(getId());
  }
}
