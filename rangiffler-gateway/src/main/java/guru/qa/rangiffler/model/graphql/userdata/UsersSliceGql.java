package guru.qa.rangiffler.model.graphql.userdata;

import guru.qa.rangiffler.model.graphql.PageInfoGql;

import java.util.List;

public record UsersSliceGql(
  List<UserGql> content,
  PageInfoGql pageInfo
) {
}
