package guru.qa.rangiffler.controller;


import guru.qa.rangiffler.model.SessionJson;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.Objects;

@Controller
public class SessionQueryController {

  @QueryMapping
  public SessionJson session(@AuthenticationPrincipal Jwt principal) {
    if (principal != null) {
      return new SessionJson(
          principal.getClaim("sub"),
          Date.from(Objects.requireNonNull(principal.getIssuedAt())),
          Date.from(Objects.requireNonNull(principal.getExpiresAt()))
      );
    } else {
      return SessionJson.empty();
    }
  }
}
