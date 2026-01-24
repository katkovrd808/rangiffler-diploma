package guru.qa.rangiffler.jupiter.extension;

import com.github.jknack.handlebars.internal.lang3.ArrayUtils;
import com.google.protobuf.ByteString;
import guru.qa.rangiffler.data.projection.PhotoWithLikes;
import guru.qa.rangiffler.grpc.PhotoRequest;
import guru.qa.rangiffler.grpc.UserRequest;
import guru.qa.rangiffler.jupiter.annotation.Photo;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.UdUserJson;
import guru.qa.rangiffler.service.PhotoClient;
import guru.qa.rangiffler.service.UserdataClient;
import guru.qa.rangiffler.service.impl.api.PhotoGrpcClient;
import guru.qa.rangiffler.service.impl.api.UserdataGrpcClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static guru.qa.rangiffler.jupiter.extension.TestMethodContextExtension.context;
import static guru.qa.rangiffler.utils.RandomDataUtils.randomPhotoDescription;

@ParametersAreNonnullByDefault
public class PhotoExtension implements BeforeEachCallback, ParameterResolver {
  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(PhotoExtension.class);
  private final PhotoClient photoClient = new PhotoGrpcClient();
  private final UserdataClient userdataClient = new UserdataGrpcClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
      .ifPresent(userAnno -> {
        if (ArrayUtils.isNotEmpty(userAnno.photos())) {
          final @Nullable UdUserJson createdUser = UserExtension.createdUser();
          final String username = createdUser != null ? createdUser.username() : userAnno.username();

          final List<PhotoWithLikes> result = new ArrayList<>();
          for (Photo photoAnno : userAnno.photos()) {
            PhotoRequest request;
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(photoAnno.photoPath())) {
              request = PhotoRequest.newBuilder()
                .setUserId(userdataClient.currentUser(username).id().toString())
                .setSrc(ByteString.readFrom(is))
                .setCountryCode(photoAnno.countryCode())
                .setDescription(
                  photoAnno.withDescription() ? randomPhotoDescription() : ""
                )
                .build();
            } catch (IOException e) {
              throw new RuntimeException(e);
            }

            PhotoWithLikes created = PhotoWithLikes.fromProto(photoClient.create(request));

            result.add(created);
          }

          if (createdUser != null) {
            createdUser.testData().photos().addAll(result);
          } else {
            context.getStore(NAMESPACE).put(
              context.getUniqueId(),
              result.toArray(PhotoWithLikes[]::new)
            );
          }
        }
      });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(PhotoWithLikes[].class);
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return createdPhotos();
  }

  public static PhotoWithLikes[] createdPhotos() {
    final ExtensionContext methodContext = context();
    return methodContext.getStore(NAMESPACE)
      .get(methodContext.getUniqueId(), PhotoWithLikes[].class);
  }
}
