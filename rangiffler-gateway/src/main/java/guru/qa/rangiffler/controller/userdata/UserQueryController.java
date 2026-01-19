package guru.qa.rangiffler.controller.userdata;

import guru.qa.rangiffler.model.graphql.userdata.UserGql;
import guru.qa.rangiffler.model.graphql.userdata.UsersSliceGql;
import guru.qa.rangiffler.service.UserService;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@Controller
@PreAuthorize("isAuthenticated()")
@ParametersAreNonnullByDefault
public class UserQueryController {

  private final UserService userService;

  @Autowired
  public UserQueryController(UserService userService) {
    this.userService = userService;
  }

  @QueryMapping
  @ResponseStatus(HttpStatus.OK)
  @Nonnull
  UserGql user(@AuthenticationPrincipal Jwt principal) {
    final String principalUsername = principal.getClaim("sub");
    return userService.findUser(principalUsername, null);
  }

  @QueryMapping
  @ResponseStatus(HttpStatus.OK)
  @Nonnull
  UsersSliceGql users(@AuthenticationPrincipal Jwt principal,
                      @Argument int size,
                      @Argument int page) {
    final String principalUsername = principal.getClaim("sub");
    return userService.allUsers(PageRequest.of(page, size), principalUsername);
  }

  @SchemaMapping(typeName = "User", field = "friends")
  @Nonnull
  UsersSliceGql friends(UserGql user,
                         @Argument int page,
                         @Argument int size,
                         @Argument @Nullable String searchQuery) {
    return userService.friends(
      PageRequest.of(page, size),
      user.username(),
      searchQuery
    );
  }

  @SchemaMapping(typeName = "User", field = "incomeInvitations")
  @Nonnull
  UsersSliceGql incomeInvitations(UserGql user,
                                   @Argument int page,
                                   @Argument int size,
                                   @Argument @Nullable String searchQuery) {
    return userService.incomeInvitations(
      PageRequest.of(page, size),
      user.username(),
      searchQuery
    );
  }

  @SchemaMapping(typeName = "User", field = "outcomeInvitations")
  @Nonnull
  UsersSliceGql outcomeInvitations(UserGql user,
                                    @Argument int page,
                                    @Argument int size,
                                    @Argument @Nullable String searchQuery) {
    return userService.outcomeInvitations(
      PageRequest.of(page, size),
      user.username(),
      searchQuery
    );
  }
}
