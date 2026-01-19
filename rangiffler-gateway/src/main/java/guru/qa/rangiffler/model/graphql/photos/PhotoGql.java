package guru.qa.rangiffler.model.graphql.photos;

import guru.qa.rangiffler.model.graphql.countries.CountryGql;
import guru.qa.rangiffler.model.graphql.userdata.UserGql;

import java.util.Date;
import java.util.UUID;

public record PhotoGql(
  UUID id,
  UserGql user,
  String src,
  CountryGql country,
  String description,
  Date creationDate,
  LikesGql likes
) {
}
