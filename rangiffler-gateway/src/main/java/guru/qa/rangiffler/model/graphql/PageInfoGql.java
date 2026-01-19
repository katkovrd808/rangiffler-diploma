package guru.qa.rangiffler.model.graphql;

public record PageInfoGql(
  boolean hasNextPage,
  boolean hasPreviousPage
) {
  public static PageInfoGql unpaged(){
    return new PageInfoGql(false, false);
  }
}
