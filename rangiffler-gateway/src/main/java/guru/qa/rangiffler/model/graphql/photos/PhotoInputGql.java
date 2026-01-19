package guru.qa.rangiffler.model.graphql.photos;

import guru.qa.rangiffler.model.graphql.countries.CountryInputGql;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.checkerframework.checker.formatter.qual.Format;

import java.util.UUID;

public record PhotoInputGql(
  UUID id,
  byte[] src,
  CountryInputGql country,
  @Size(max = 50, message = "Allowed description length should be up to 50 characters")
  String description,
  LikeInputGql like
) {
}
