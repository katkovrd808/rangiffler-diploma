package guru.qa.rangiffler.controller;

import guru.qa.rangiffler.service.OauthSessionValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class LoginController {

  private static final String LOGIN_VIEW_NAME = "login";

  private final String rangifflerFrontUri;
  private final OauthSessionValidator sessionValidator;

  public LoginController(@Value("${rangiffler-front.base-uri}") String rangifflerFrontUri, OauthSessionValidator sessionValidator) {
    this.rangifflerFrontUri = rangifflerFrontUri;
    this.sessionValidator = sessionValidator;
  }

  @GetMapping("/login")
  public String login(HttpSession session) {
    if (sessionValidator.isWebOauthSession(session)) {
      return LOGIN_VIEW_NAME;
    }
    return "redirect:" + rangifflerFrontUri;
  }

  @GetMapping("/")
  @ResponseStatus(HttpStatus.FOUND)
  public String root(Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()) {
      return "redirect:" + rangifflerFrontUri;
    }
    return "redirect:/" + LOGIN_VIEW_NAME;
  }
}
