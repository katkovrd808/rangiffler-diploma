package guru.qa.rangiffler.service.impl;

import guru.qa.rangiffler.api.GrpcCountriesClient;
import guru.qa.rangiffler.api.GrpcUserdataClient;
import guru.qa.rangiffler.data.PhotoEntity;
import guru.qa.rangiffler.data.PhotoLikeEntity;
import guru.qa.rangiffler.data.projection.PhotoWithLikes;
import guru.qa.rangiffler.data.repository.PhotoLikeRepository;
import guru.qa.rangiffler.data.repository.PhotoRepository;
import guru.qa.rangiffler.ex.PhotoNotFoundException;
import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.model.CountryDto;
import guru.qa.rangiffler.model.FriendDto;
import guru.qa.rangiffler.model.UserDto;
import guru.qa.rangiffler.service.PhotoService;
import guru.qa.rangiffler.service.mapper.PhotoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@ParametersAreNonnullByDefault
public class DbPhotoService implements PhotoService {

  private static final Logger LOG = LoggerFactory.getLogger(DbPhotoService.class);

  private final PhotoRepository photoRepository;
  private final PhotoLikeRepository photoLikeRepository;
  private final GrpcCountriesClient grpcCountriesClient;
  private final GrpcUserdataClient grpcUserdataClient;
  private final PhotoMapper photoMapper;

  @Autowired
  public DbPhotoService(PhotoRepository photoRepository,
                        GrpcCountriesClient grpcCountriesClient,
                        GrpcUserdataClient grpcUserdataClient,
                        PhotoMapper photoMapper,
                        PhotoLikeRepository photoLikeRepository) {
    this.photoRepository = photoRepository;
    this.grpcCountriesClient = grpcCountriesClient;
    this.grpcUserdataClient = grpcUserdataClient;
    this.photoMapper = photoMapper;
    this.photoLikeRepository = photoLikeRepository;
  }


  @Override
  @Transactional
  public @Nonnull PhotoResponse createPhoto(PhotoRequest request) {
    final String userId = request.getUserId();
    if (userId == null || userId.isEmpty()) {
      LOG.info("### Attempting to create photo with null userId was rejected ###");
      throw new IllegalArgumentException("User can't be null.");
    }

    final CountryDto country = getRequiredCountry(request.getCountryCode());
    assertUserCreated(userId);

    PhotoEntity pe = new PhotoEntity();
    pe.setUserId(UUID.fromString(userId));
    pe.setCountryId(country.id());
    pe.setDescription(request.getDescription());
    pe.setPhoto(request.getSrc().toByteArray());

    return photoMapper.toProto(PhotoWithLikes.fromEntity(photoRepository.save(pe)), country);
  }

  @Override
  @Transactional
  public @Nonnull PhotoResponse updatePhoto(PhotoUpdateRequest request) {
    final String userId = assertUserIdNotEmpty(request.getUserId());

    PhotoEntity pe = getRequiredPhoto(request.getId());
    if (!Objects.equals(pe.getUserId(), UUID.fromString(userId))) {
      LOG.info("### Attempting to update photo of other user. ###");
      throw new SecurityException("User can only update their own photos.");
    }

    final CountryDto country = getRequiredCountry(request.getCountry().getCode());
    assertUserCreated(request.getUserId());

    pe.setCountryId(country.id());
    pe.setDescription(request.getDescription());
    pe.setPhoto(request.getSrc().toByteArray());

    final PhotoWithLikes photoWithLikes = PhotoWithLikes.fromEntity(photoRepository.save(pe));

    return photoMapper.toProto(photoWithLikes, country);
  }

  @Override
  @Transactional
  public @Nonnull PhotoDeleteResponse deletePhoto(PhotoDeleteRequest request) {
    final String userId = assertUserIdNotEmpty(request.getUserId());
    final String photoId = request.getId();
    PhotoEntity pe = getRequiredPhoto(photoId);

    if (!Objects.equals(pe.getUserId(), UUID.fromString(userId))) {
      LOG.info("### Attempting to delete photo of other user. ###");
      throw new SecurityException("User can only update their own photos.");
    }

    photoRepository.delete(pe);
    photoRepository.flush();

    return photoMapper.toPhotoDeleteResponse(photoId);
  }

  @Override
  @Transactional(readOnly = true)
  public @Nonnull PhotoResponse getPhotoWithLikes(PhotoWithLikesRequest request) {
    final PhotoEntity pe = getRequiredPhoto(request.getId());
    CountryDto country = grpcCountriesClient.getCountryById(pe.getCountryId().toString())
      .orElseThrow(() -> new IllegalArgumentException("Can't find selected photo with country: " + pe.getCountryId()));
    final List<PhotoLikeEntity> photoLikes = photoLikeRepository.findPhotoLikesByPhotoId(pe.getId());
    return photoMapper.toProto(PhotoWithLikes.fromEntity(pe, photoLikes), country);
  }

  @Override
  @Transactional(readOnly = true)
  public @Nonnull Page<PhotoResponse> getUserPhotos(FeedRequest request, Pageable pageable) {
    final String userId = assertUserCreated(request.getUserId());
    final Page<PhotoEntity> photoPage = photoRepository.findPhotosByUserId(UUID.fromString(userId), pageable);
    return buildPhotoPage(photoPage, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public @Nonnull Page<PhotoResponse> getAllPhotos(FeedRequest request, Pageable pageable) {
    assertUserIdNotEmpty(request.getUserId());
    final Page<PhotoEntity> photoPage = photoRepository.findAll(pageable);
    return buildPhotoPage(photoPage, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public @Nonnull Page<PhotoResponse> getFriendsPhotos(FeedRequest request, Pageable pageable) {
    final String userId = assertUserIdNotEmpty(request.getUserId());
    final List<UUID> friendIds = grpcUserdataClient.getAllFriendsByUserId(userId).stream()
      .map(FriendDto::id)
      .toList();
    final Page<PhotoEntity> photoPage = photoRepository.findFriendsPhoto(UUID.fromString(userId), friendIds, pageable);
    return buildPhotoPage(photoPage, pageable);
  }

  private @Nonnull Page<PhotoResponse> buildPhotoPage(Page<PhotoEntity> photoPage, Pageable pageable) {
    final List<PhotoEntity> photos = photoPage.getContent();

    if (photos.isEmpty()) {
      LOG.info("No photos found for friends");
      return Page.empty(pageable);
    }

    LOG.info("Found {} photos to process", photos.size());

    final List<UUID> photoIds = photos.stream().map(PhotoEntity::getId).toList();

    final Map<UUID, List<PhotoLikeEntity>> likesByPhotoId = photoLikeRepository.findLikesByPhotoIds(photoIds)
      .stream()
      .collect(Collectors.groupingBy(like -> like.getPhoto().getId()));

    LOG.info("Loaded likes for {} photos", likesByPhotoId.size());

    final List<UUID> neededCountryIds = photos.stream()
      .map(PhotoEntity::getCountryId)
      .distinct()
      .toList();

    final Map<UUID, CountryDto> countryMap = grpcCountriesClient.getCountriesByIds(neededCountryIds).stream()
      .collect(Collectors.toMap(CountryDto::id, Function.identity()));

    LOG.info("Loaded {} countries", countryMap.size());

    final List<PhotoResponse> photoResponses = photos.stream()
      .map(photo -> {
        final List<PhotoLikeEntity> likes = likesByPhotoId.getOrDefault(photo.getId(), Collections.emptyList());

        final PhotoWithLikes photoWithLikes = PhotoWithLikes.fromEntity(photo, likes);

        final CountryDto country = countryMap.get(photo.getCountryId());
        if (country == null) {
          LOG.info("### Can't find country. ###");
          throw new IllegalArgumentException("Country not found for id: " + photo.getCountryId());
        }
        return photoMapper.toProto(photoWithLikes, country);
      })
      .toList();

    return new PageImpl<>(
      photoResponses,
      pageable,
      photoPage.getTotalElements()
    );
  }

  @Transactional(readOnly = true)
  private @Nonnull CountryDto getRequiredCountry(String code) {
    final CountryDto country = grpcCountriesClient.getCountryByCode(code)
      .orElseThrow(() -> new IllegalArgumentException("Country with code " + code + " was not found."));

    LOG.info("### Received country with id: {} and code: {} from countries service###", country.id(), country.code());

    return country;
  }

  @Transactional(readOnly = true)
  private @Nonnull PhotoEntity getRequiredPhoto(String id) {
    return photoRepository.findById(UUID.fromString(id))
      .orElseThrow(() -> new PhotoNotFoundException("Can't find photo with id " + id));
  }

  private @Nonnull String assertUserIdNotEmpty(String userId) {
    if (userId.isEmpty()) {
      LOG.info("### Attempting to request photos without user_id param. ###");
      throw new IllegalArgumentException("User id can't be null value.");
    }
    return userId;
  }

  private @Nonnull String assertUserCreated(String userId) {
    UserDto user = grpcUserdataClient.getUserById(userId)
      .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " was not found."));

    LOG.info("### Received user with id: {} and username: {} from userdata service###", user.id(), user.username());
    return userId;
  }
}
