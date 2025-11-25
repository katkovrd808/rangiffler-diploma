package guru.qa.rangiffler.service;

import com.google.protobuf.Empty;
import guru.qa.rangiffler.grpc.PhotoDeleteRequest;
import guru.qa.rangiffler.grpc.PhotoRequest;
import guru.qa.rangiffler.grpc.PhotoResponse;
import guru.qa.rangiffler.grpc.PhotoUpdateRequest;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface PhotoService {
  @Nonnull PhotoResponse createPhoto(PhotoRequest photo);

  @Nonnull PhotoResponse updatePhoto(PhotoUpdateRequest photo);

  Empty deletePhoto(PhotoDeleteRequest photo);
}
