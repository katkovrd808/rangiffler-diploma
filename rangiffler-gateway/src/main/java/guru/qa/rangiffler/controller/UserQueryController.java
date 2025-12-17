package guru.qa.rangiffler.controller;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.SelectedField;
import guru.qa.rangiffler.TooManySubQueriesException;
import guru.qa.rangiffler.model.graphql.UserGql;
import guru.qa.rangiffler.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Nonnull;
import java.util.List;

@Controller
public class UserQueryController {

  private final UserService userService;

  @Autowired
  public UserQueryController(UserService userService) {
    this.userService = userService;
  }

  @QueryMapping
  @Nonnull
  @ResponseStatus(HttpStatus.OK)
  Slice<UserGql> allUsers(@AuthenticationPrincipal Jwt principal,
                         @Argument int size,
                         @Argument int page) {
    return userService.allUsers(PageRequest.of(
      page, size
    ));
  }
  
  @QueryMapping
  @Nonnull
  @ResponseStatus(HttpStatus.OK)
  UserGql user(@AuthenticationPrincipal Jwt principal,
               @Nonnull DataFetchingEnvironment env) {
    checkSubQueries(env, 2, "friends");
    final String principalUsername = principal.getClaim("sub");
    return userService.findByUsername(principalUsername);
  }
  
  private void checkSubQueries(@Nonnull DataFetchingEnvironment env, int depth, @Nonnull String... queryKeys) {
    for (String queryKey : queryKeys) {
      List<SelectedField> selectors = env.getSelectionSet().getFieldsGroupedByResultKey().get(queryKey);
      if (selectors != null && selectors.size() > depth) {
        throw new TooManySubQueriesException("Can`t fetch over 2 " + queryKey + " sub-queries");
      }
    }
  }
}
