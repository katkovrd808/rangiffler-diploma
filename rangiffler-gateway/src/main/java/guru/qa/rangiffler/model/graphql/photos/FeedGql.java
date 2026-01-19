package guru.qa.rangiffler.model.graphql.photos;

import java.util.List;

public record FeedGql(
  String username,
  boolean withFriends,
  PhotoSliceGql photos,
  List<StatisticGql> stat
) {
}
