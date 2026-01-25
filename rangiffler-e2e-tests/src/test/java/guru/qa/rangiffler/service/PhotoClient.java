package guru.qa.rangiffler.service;

import guru.qa.rangiffler.grpc.*;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface PhotoClient {
  @Nonnull PhotoResponse create(PhotoRequest request);

  @Nonnull PhotoResponse update(PhotoUpdateRequest request);

  @Nonnull PhotoDeleteResponse delete(PhotoDeleteRequest request);

  @Nonnull FeedResponse feed(FeedRequest request);
}
