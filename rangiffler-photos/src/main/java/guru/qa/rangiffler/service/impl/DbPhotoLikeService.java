package guru.qa.rangiffler.service.impl;

import guru.qa.rangiffler.api.GrpcCountriesClient;
import guru.qa.rangiffler.api.GrpcUserdataClient;
import guru.qa.rangiffler.data.PhotoEntity;
import guru.qa.rangiffler.data.PhotoLikeEntity;
import guru.qa.rangiffler.data.projection.PhotoWithLikes;
import guru.qa.rangiffler.data.repository.PhotoLikeRepository;
import guru.qa.rangiffler.data.repository.PhotoRepository;
import guru.qa.rangiffler.ex.InvalidPhotoLikeOperationException;
import guru.qa.rangiffler.ex.PhotoLikeNotFoundException;
import guru.qa.rangiffler.ex.PhotoNotFoundException;
import guru.qa.rangiffler.grpc.PhotoLikeRequest;
import guru.qa.rangiffler.grpc.PhotoResponse;
import guru.qa.rangiffler.grpc.PhotoWithLikesRequest;
import guru.qa.rangiffler.model.CountryDto;
import guru.qa.rangiffler.service.PhotoLikeService;
import guru.qa.rangiffler.service.mapper.PhotoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@ParametersAreNonnullByDefault
public class DbPhotoLikeService implements PhotoLikeService {

  private static final Logger LOG = LoggerFactory.getLogger(DbPhotoLikeService.class);

  private final PhotoRepository photoRepository;
  private final PhotoLikeRepository photoLikeRepository;
  private final GrpcUserdataClient grpcUserdataClient;
  private final GrpcCountriesClient grpcCountriesClient;

  private final PhotoMapper photoMapper;

  @Autowired
  public DbPhotoLikeService(PhotoRepository photoRepository,
                            PhotoLikeRepository photoLikeRepository,
                            GrpcUserdataClient grpcUserdataClient,
                            GrpcCountriesClient grpcCountriesClient,
                            PhotoMapper photoMapper) {
    this.photoRepository = photoRepository;
    this.photoLikeRepository = photoLikeRepository;
    this.grpcUserdataClient = grpcUserdataClient;
    this.grpcCountriesClient = grpcCountriesClient;
    this.photoMapper = photoMapper;
  }

  @Override
  @Transactional
  public @Nonnull PhotoResponse likePhoto(PhotoLikeRequest request) {
    final String userId = request.getLike().getUserId();
    final String photoId = request.getPhotoId();

    if (userId.isEmpty()) {
      LOG.info("### Attempting to create photo like with null userId was rejected ###");
      throw new IllegalArgumentException("User can't be null.");
    }

    assertUserCreated(userId);

    PhotoEntity photo = getRequiredPhoto(photoId);
    final CountryDto country = getCountryDto(photo.getCountryId().toString());

    final Optional<PhotoLikeEntity> existing = checkExistingLike(photoId, userId);

    if (existing.isPresent()) {
      final PhotoLikeEntity like = existing.get();
      photoLikeRepository.delete(like);
      photoLikeRepository.flush();

      photo = getRequiredPhoto(photoId);

      return photoMapper.toProto(PhotoWithLikes.fromEntity(photo), country);
    }

    PhotoLikeEntity ple = new PhotoLikeEntity();
    ple.setPhoto(photo);
    ple.setUserId(UUID.fromString(userId));

    photo.getPhotoLikes().add(ple);

    photoLikeRepository.save(ple);

    return photoMapper.toProto(PhotoWithLikes.fromEntity(photo), country);
  }

  @Nonnull
  @Override
  public PhotoResponse deleteLike(PhotoLikeRequest request) {
    final String userId = request.getLike().getUserId();
    final String photoId = request.getPhotoId();

    if (userId.isEmpty()) {
      LOG.info("### Attempting to delete photo like with null userId was rejected ###");
      throw new IllegalArgumentException("User can't be null.");
    }

    assertUserCreated(userId);

    final PhotoLikeEntity photoLike = checkExistingLike(photoId, userId)
      .orElseThrow(() -> new PhotoLikeNotFoundException("Photo like with photo id: " + photoId + " and user id: " + userId + " was not found."));

    if (!Objects.equals(userId, photoLike.getUserId().toString())) {
      throw new InvalidPhotoLikeOperationException("User with id: " + userId + " don't have permissions to operate this like.");
    }

    photoLikeRepository.delete(photoLike);
    photoLikeRepository.flush();

    final PhotoEntity photo = getRequiredPhoto(photoId);
    final CountryDto country = getCountryDto(photo.getCountryId().toString());

    return photoMapper.toProto(PhotoWithLikes.fromEntity(photo), country);
  }

  private @Nonnull CountryDto getCountryDto(String countryId) {
    return grpcCountriesClient.getCountryById(countryId)
      .map(c -> {
        return new CountryDto(
          c.id(),
          c.name(),
          c.code(),
          c.flag(),
          0
        );
      })
      .orElseThrow();
  }

  @Transactional(readOnly = true)
  private @Nonnull PhotoEntity getRequiredPhoto(String photoId) {
    return photoRepository.findById(UUID.fromString(photoId))
      .orElseThrow(() -> new PhotoNotFoundException("Can't find photo with id: " + photoId));
  }

  @Transactional(readOnly = true)
  private Optional<PhotoLikeEntity> checkExistingLike(String photoId, String userId) {
    return photoLikeRepository.findByPhotoIdAndUserId(UUID.fromString(photoId), UUID.fromString(userId));
  }

  private void assertUserCreated(String id) {
    grpcUserdataClient.getUserById(id).orElseThrow(
      () -> new IllegalArgumentException("User with id " + id + " was not found.")
    );
  }
}
