package guru.qa.rangiffler.service;

import guru.qa.rangiffler.data.projection.PhotoWithLikes;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface PhotoClient {
  List<PhotoWithLikes> findUserPhotos(String username);
}
