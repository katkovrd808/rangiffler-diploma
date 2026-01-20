package guru.qa.rangiffler.controller.photos;

import guru.qa.rangiffler.model.graphql.photos.LikeInputGql;
import guru.qa.rangiffler.model.graphql.photos.PhotoGql;
import guru.qa.rangiffler.model.graphql.photos.PhotoInputGql;
import guru.qa.rangiffler.service.PhotoService;
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
import java.util.Optional;

@Controller
@PreAuthorize("isAuthenticated()")
@ParametersAreNonnullByDefault
public class PhotoMutationController {

  private final PhotoService photoService;

  @Autowired
  public PhotoMutationController(PhotoService photoService) {
    this.photoService = photoService;
  }

  @MutationMapping
  @ResponseStatus(HttpStatus.OK)
  public @Nonnull PhotoGql photo(@AuthenticationPrincipal Jwt principal,
                 @Argument @Valid PhotoInputGql input) {
    final String principalUsername = principal.getClaim("sub");

    boolean hasLike = Optional.ofNullable(input.like())
      .map(LikeInputGql::user)
      .isPresent();

    if (input.id() == null) {
      return photoService.save(principalUsername, input);
    } else {
      return hasLike
        ? photoService.updateWithLike(principalUsername, input)
        : photoService.update(principalUsername, input);
    }
  }

  @MutationMapping
  @ResponseStatus(HttpStatus.OK)
  public @Nonnull String deletePhoto(@AuthenticationPrincipal Jwt principal,
                     @Argument String id) {
    final String principalUsername = principal.getClaim("sub");
    return photoService.delete(principalUsername, id);
  }
}
