package guru.qa.rangiffler.service.impl.api;

import guru.qa.rangiffler.data.projection.PhotoWithLikes;
import guru.qa.rangiffler.service.PhotoClient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class PhotoApiClient implements PhotoClient {

  @Override
  public List<PhotoWithLikes> findUserPhotos(String username) {
    return List.of();
  }
}
