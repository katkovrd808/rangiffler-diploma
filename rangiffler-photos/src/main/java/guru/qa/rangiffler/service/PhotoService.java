package guru.qa.rangiffler.service;

import guru.qa.rangiffler.grpc.PhotoRequest;
import guru.qa.rangiffler.grpc.PhotoResponse;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface PhotoService {
  PhotoResponse createPhoto(PhotoRequest photo);
}
