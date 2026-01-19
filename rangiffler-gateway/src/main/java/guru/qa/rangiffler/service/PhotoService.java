package guru.qa.rangiffler.service;

import guru.qa.rangiffler.model.graphql.photos.FeedGql;
import guru.qa.rangiffler.model.graphql.photos.PhotoGql;
import guru.qa.rangiffler.model.graphql.photos.PhotoInputGql;
import org.springframework.data.domain.Pageable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface PhotoService {
  PhotoGql save(String username, PhotoInputGql photo);

  PhotoGql update(String username, PhotoInputGql photo);

  PhotoGql updateWithLike(String username, PhotoInputGql photo);

  String delete(String username, String photoId);

  FeedGql feed(Pageable pageable, String username, boolean withFriends);
}
