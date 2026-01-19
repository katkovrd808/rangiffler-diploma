package guru.qa.rangiffler.controller.photos;

import guru.qa.rangiffler.model.graphql.photos.FeedGql;
import guru.qa.rangiffler.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
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
public class PhotoQueryController {

  private final PhotoService photoService;

  @Autowired
  public PhotoQueryController(PhotoService photoService) {
    this.photoService = photoService;
  }

  @QueryMapping
  @ResponseStatus(HttpStatus.OK)
  @Nonnull
  FeedGql feed(@AuthenticationPrincipal Jwt principal,
               @Argument int page,
               @Argument int size,
               @Argument boolean withFriends) {
    final String principalUsername = principal.getClaim("sub");
    return photoService.feed(PageRequest.of(page, size), principalUsername, withFriends);
  }
}
