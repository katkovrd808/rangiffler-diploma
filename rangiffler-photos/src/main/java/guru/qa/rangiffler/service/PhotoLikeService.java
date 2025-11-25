package guru.qa.rangiffler.service;

import com.google.protobuf.Empty;
import guru.qa.rangiffler.grpc.PhotoLikeRequest;
import guru.qa.rangiffler.grpc.PhotoResponse;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface PhotoLikeService {
  @Nonnull PhotoResponse likePhoto(PhotoLikeRequest request);

  @Nonnull Empty deleteLike(PhotoLikeRequest request);
}
