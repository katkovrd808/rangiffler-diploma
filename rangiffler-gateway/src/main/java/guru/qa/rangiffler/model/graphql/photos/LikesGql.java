package guru.qa.rangiffler.model.graphql.photos;

import java.util.List;

public record LikesGql(
  int total,
  List<LikeGql> likes
) {
}
