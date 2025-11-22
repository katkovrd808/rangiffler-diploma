package guru.qa.rangiffler.controller;

import guru.qa.rangiffler.domain.UserGql;
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
  UserGql user(@AuthenticationPrincipal Jwt principal,
               @Argument String username) {
    return userService.findByUsername(username);
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
}
