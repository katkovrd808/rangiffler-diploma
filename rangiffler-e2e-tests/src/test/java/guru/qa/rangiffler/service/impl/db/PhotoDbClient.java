package guru.qa.rangiffler.service.impl.db;

import guru.qa.rangiffler.data.projection.PhotoWithLikes;
import guru.qa.rangiffler.service.PhotoClient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class PhotoDbClient implements PhotoClient {
  @Override
  public List<PhotoWithLikes> findUserPhotos(String username) {
    return List.of();
  }
}
