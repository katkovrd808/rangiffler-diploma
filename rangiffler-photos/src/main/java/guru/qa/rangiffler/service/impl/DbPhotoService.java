package guru.qa.rangiffler.service.impl;

import com.google.protobuf.Empty;
import guru.qa.rangiffler.api.GrpcCountriesClient;
import guru.qa.rangiffler.api.GrpcUserdataClient;
import guru.qa.rangiffler.data.PhotoEntity;
import guru.qa.rangiffler.data.repository.PhotoRepository;
import guru.qa.rangiffler.ex.PhotoNotFoundException;
import guru.qa.rangiffler.grpc.PhotoDeleteRequest;
import guru.qa.rangiffler.grpc.PhotoRequest;
import guru.qa.rangiffler.grpc.PhotoResponse;
import guru.qa.rangiffler.grpc.PhotoUpdateRequest;
import guru.qa.rangiffler.model.CountryDto;
import guru.qa.rangiffler.model.UserDto;
import guru.qa.rangiffler.service.PhotoService;
import guru.qa.rangiffler.service.mapper.PhotoMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@ParametersAreNonnullByDefault
public class DbPhotoService implements PhotoService {

  private static final Logger LOG = LoggerFactory.getLogger(DbPhotoService.class);

  private final PhotoRepository photoRepository;
  private final GrpcCountriesClient grpcCountriesClient;
  private final GrpcUserdataClient grpcUserdataClient;
  private final PhotoMapper photoMapper;

  @Autowired
  public DbPhotoService(PhotoRepository photoRepository,
                        GrpcCountriesClient grpcCountriesClient,
                        GrpcUserdataClient grpcUserdataClient,
                        PhotoMapper photoMapper) {
    this.photoRepository = photoRepository;
    this.grpcCountriesClient = grpcCountriesClient;
    this.grpcUserdataClient = grpcUserdataClient;
    this.photoMapper = photoMapper;
  }


  @Override
  @Transactional
  public @Nonnull PhotoResponse createPhoto(PhotoRequest request) {
    if (request.getUserId().isEmpty()) {
      LOG.info("### Attempting to create photo with null userId was rejected ###");
      throw new IllegalArgumentException("User can't be null.");
    }

    CountryDto country = getCountryDto(request.getCountryCode());
    getUserDto(request.getUserId());

    PhotoEntity pe = new PhotoEntity();
    pe.setUserId(UUID.fromString(request.getUserId()));
    pe.setCountryId(country.id());
    pe.setDescription(request.getDescription());
    pe.setPhoto(request.getSrc().toByteArray());

    return photoMapper.toProto(photoRepository.save(pe));
  }

  @Nonnull
  @Override
  public PhotoResponse updatePhoto(PhotoUpdateRequest request) {
    PhotoEntity pe = getRequiredPhoto(request.getId());
    if (!Objects.equals(pe.getUserId(), UUID.fromString(request.getUserId()))) {
      throw new SecurityException("User can only update their own photos.");
    }

    CountryDto country = getCountryDto(request.getCountryCode());
    getUserDto(request.getUserId());

    pe.setCountryId(country.id());
    pe.setDescription(request.getDescription());
    pe.setPhoto(request.getSrc().toByteArray());

    return photoMapper.toProto(photoRepository.save(pe));
  }

  @Override
  public Empty deletePhoto(PhotoDeleteRequest request) {
    PhotoEntity pe = getRequiredPhoto(request.getId());
    if (!Objects.equals(pe.getUserId(), UUID.fromString(request.getUserId()))) {
      throw new SecurityException("User can only update their own photos.");
    }

    photoRepository.delete(pe);
    photoRepository.flush();

    return Empty.getDefaultInstance();
  }

  @Nonnull
  private CountryDto getCountryDto(String code) {
    final Optional<CountryDto> country = grpcCountriesClient.getCountryByCode(code);
    if (country.isEmpty()) {
      LOG.info("### Requested country with code: {} was not found in userdata-db ###", code);
      throw new IllegalArgumentException("Country with code " + code + " was not found.");
    }
    LOG.info("### Received country with id: {} and code: {} from countries service###", country.get().id(), country.get().code());

    return country.get();
  }

  @Nonnull
  private UserDto getUserDto(String id) {
    Optional<UserDto> user = grpcUserdataClient.getUserById(id);
    if (user.isEmpty()) {
      LOG.info("### Requested user with id: {} was not found in userdata-db ###", id);
      throw new IllegalArgumentException("User with id " + id + " was not found.");
    }
    LOG.info("### Received user with id: {} and username: {} from userdata service###", user.get().id(), user.get().username());
    return user.get();
  }

  @Nonnull
  private PhotoEntity getRequiredPhoto(String id) {
    return photoRepository.findById(UUID.fromString(id))
      .orElseThrow(() -> new PhotoNotFoundException("Can't find photo with id " + id));
  }
}
