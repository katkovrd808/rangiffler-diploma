package guru.qa.rangiffler.service;

import com.google.protobuf.Empty;
import guru.qa.rangiffler.data.PhotoLikeEntity;
import guru.qa.rangiffler.grpc.PhotoLikeRequest;
import guru.qa.rangiffler.grpc.PhotoResponse;
import guru.qa.rangiffler.grpc.PhotoWithLikesRequest;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface PhotoLikeService {
  @Nonnull PhotoResponse likePhoto(PhotoLikeRequest request);

  @Nonnull Empty deleteLike(PhotoLikeRequest request);

  @Nonnull List<PhotoLikeEntity> getPhotoWithLikes(PhotoWithLikesRequest request);
}
