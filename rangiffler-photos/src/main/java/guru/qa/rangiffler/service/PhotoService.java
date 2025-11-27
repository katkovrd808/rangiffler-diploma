package guru.qa.rangiffler.service;

import com.google.protobuf.Empty;
import guru.qa.rangiffler.grpc.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface PhotoService {
  @Nonnull PhotoResponse createPhoto(PhotoRequest photo);

  @Nonnull PhotoResponse updatePhoto(PhotoUpdateRequest photo);

  @Nonnull Empty deletePhoto(PhotoDeleteRequest photo);

  @Nonnull PhotoResponse getPhotoWithLikes(PhotoWithLikesRequest photo);

  @Nonnull Page<PhotoResponse> getAllPhotos(FeedRequest request, Pageable pageable);

  @Nonnull Page<PhotoResponse> getFriendsPhotos(FeedRequest request, Pageable pageable);
}
