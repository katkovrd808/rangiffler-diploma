package guru.qa.rangiffler.controller.countries;

import guru.qa.rangiffler.model.graphql.countries.CountryGql;
import guru.qa.rangiffler.service.CountryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Controller
public class CountryQueryController {
  private final static Logger LOG = LoggerFactory.getLogger(CountryQueryController.class);

  private final CountryService countryService;

  @Autowired
  public CountryQueryController(CountryService countryService) {
    this.countryService = countryService;
  }

  @QueryMapping
  @ResponseStatus(HttpStatus.OK)
  public @Nonnull List<CountryGql> countries() {
    return countryService.allCountries();
  }

  @QueryMapping
  @ResponseStatus(HttpStatus.OK)
  public @Nonnull CountryGql country(@Argument @Nullable String code,
                                     @Argument @Nullable String id) {
    return countryService.country(code, id);
  }

}
