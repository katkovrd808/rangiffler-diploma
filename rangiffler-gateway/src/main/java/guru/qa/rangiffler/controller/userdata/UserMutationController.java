package guru.qa.rangiffler.controller.userdata;

import guru.qa.rangiffler.model.graphql.userdata.FriendshipInputGql;
import guru.qa.rangiffler.model.graphql.userdata.UserGql;
import guru.qa.rangiffler.model.graphql.userdata.UserInputGql;
import guru.qa.rangiffler.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
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
public class UserMutationController {

  private final UserService userService;

  @Autowired
  public UserMutationController(UserService userService) {
    this.userService = userService;
  }

  @MutationMapping
  @ResponseStatus(HttpStatus.OK)
  public @Nonnull UserGql user(@AuthenticationPrincipal Jwt principal,
               @Argument @Valid UserInputGql input) {
    final String principalUsername = principal.getClaim("sub");
    return userService.updateUser(principalUsername, input);
  }

  @MutationMapping
  @ResponseStatus(HttpStatus.OK)
  public @Nonnull UserGql friendship(@AuthenticationPrincipal Jwt principal,
                     @Argument @Valid FriendshipInputGql input) {
    final String principalUsername = principal.getClaim("sub");
    final String targetUsername = userService.findUser(null, input.user()).username();
    return userService.friendship(principalUsername, targetUsername, input.action());
  }
}
