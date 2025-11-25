package guru.qa.rangiffler.service.impl;

import guru.qa.rangiffler.api.GrpcCountriesClient;
import guru.qa.rangiffler.api.GrpcUserdataClient;
import guru.qa.rangiffler.data.PhotoEntity;
import guru.qa.rangiffler.data.repository.PhotoRepository;
import guru.qa.rangiffler.grpc.PhotoRequest;
import guru.qa.rangiffler.grpc.PhotoResponse;
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

    final CountryDto country = grpcCountriesClient.getCountryByCode(request.getCountryCode());
    LOG.info("### Received country with id: {} and code: {} from countries service###", country.id(), country.code());

    Optional<UserDto> user = grpcUserdataClient.getUserById(request.getUserId());

    if (user.isEmpty()) {
      LOG.info("### Requested user with id {} was not found in userdata-db ###", request.getUserId());
      throw new IllegalArgumentException("User with id " + request.getUserId() + " was not found.");
    }

    PhotoEntity pe = new PhotoEntity();
    pe.setUserId(UUID.fromString(request.getUserId()));
    pe.setCountryId(country.id());
    pe.setDescription(request.getDescription());
    pe.setPhoto(request.getSrc().toByteArray());

    return photoMapper.toProto(photoRepository.save(pe));
  }
}
