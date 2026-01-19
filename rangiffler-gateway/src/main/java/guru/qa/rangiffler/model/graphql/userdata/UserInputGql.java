package guru.qa.rangiffler.model.graphql.userdata;

import guru.qa.rangiffler.model.graphql.countries.CountryInputGql;
import jakarta.validation.constraints.Size;

public record UserInputGql(
  @Size(min = 2, max = 55, message = "User firstname must be from 2 to 55 characters")
  String firstname,
  @Size(min = 2, max = 55, message = "User surname must be from 2 to 55 characters")
  String surname,
  byte[] avatar,
  CountryInputGql location
) {
}
