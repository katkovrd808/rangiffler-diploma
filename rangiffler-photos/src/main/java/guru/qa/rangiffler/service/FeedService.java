package guru.qa.rangiffler.service;

import guru.qa.rangiffler.grpc.FeedRequest;
import guru.qa.rangiffler.grpc.FeedResponse;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface FeedService {
  @Nonnull FeedResponse getFeed(FeedRequest request, Pageable pageable);
}
