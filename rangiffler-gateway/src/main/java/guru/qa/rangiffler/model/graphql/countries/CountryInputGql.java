package guru.qa.rangiffler.model.graphql.countries;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CountryInputGql(
  @NotBlank(message = "Country code can not be blank")
  @Size(min = 2, max = 3, message = "Allowed country code length should be from 2 to 3 characters")
  String code
) {
}
