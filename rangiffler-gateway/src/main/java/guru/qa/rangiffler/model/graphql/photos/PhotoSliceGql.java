package guru.qa.rangiffler.model.graphql.photos;

import guru.qa.rangiffler.model.graphql.PageInfoGql;

import java.util.List;

public record PhotoSliceGql(
  List<PhotoGql> content,
  PageInfoGql pageInfo
) {
}
