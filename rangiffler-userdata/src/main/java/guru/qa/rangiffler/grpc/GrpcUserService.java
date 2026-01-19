package guru.qa.rangiffler.grpc;

import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.data.repository.UserRepository;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.api.GrpcCountriesClient;
import guru.qa.rangiffler.service.UserService;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GrpcUserService extends RangifflerUserdataServiceGrpc.RangifflerUserdataServiceImplBase {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcUserService.class);

  private final UserRepository userRepository;
  private final GrpcCountriesClient grpcCountriesClient;
  private final UserService userService;

  @Autowired
  public GrpcUserService(UserRepository userRepository,
                         UserService userService,
                         GrpcCountriesClient grpcCountriesClient) {
    this.userRepository = userRepository;
    this.grpcCountriesClient = grpcCountriesClient;
    this.userService = userService;
  }

  @Transactional
  @KafkaListener(topics = "users", groupId = "userdata")
  public void listener(@Payload UserJson user, ConsumerRecord<String, UserJson> cr) {
    UUID countryId = grpcCountriesClient.getCountryId(user.countryCode());
    LOG.info("### User with username {} and country id {} received from Kafka ###", user.username(), countryId);
    userRepository.findByUsername(user.username())
      .ifPresentOrElse(
        u -> LOG.info("### User already exist in DB, kafka event will be skipped: {}", cr.toString()),
        () -> {
          LOG.info("### Kafka consumer record: {}", cr.toString());

          UserEntity userDataEntity = new UserEntity();
          userDataEntity.setUsername(user.username());
          userDataEntity.setCountryId(countryId);
          UserEntity userEntity = userRepository.save(userDataEntity);

          LOG.info(
            "### User '{}' successfully saved to database with id: {}",
            user.username(),
            userEntity.getId()
          );
        }
      );
  }

  @Override
  public void allUsers(UsersPaginatedRequest request, StreamObserver<UsersPaginatedResponse> responseObserver) {
    UsersPaginatedResponse response = userService.getAllUsers(
      createPageable(request.getPaginationRequest()),
      request.getExcludeUsername());
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
    UserResponse response = request.hasId()
      ? userService.findById(request.getId())
      : userService.findByUsername(request.getUsername());
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getUserWithFriendStatus(UserWithStatusRequest request, StreamObserver<UserResponse> responseObserver) {
    UserResponse response = userService.findUserWithFriendStatus(request.getTargetUserId(), request.getCurrentUserId());
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void updateUser(UserUpdateRequest request, StreamObserver<UserUpdateResponse> responseObserver) {
    UserUpdateResponse response = userService.updateUser(request);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void allFriendsPaginated(AllFriendsPaginatedRequest request, StreamObserver<AllFriendsPaginatedResponse> responseObserver) {
    AllFriendsPaginatedResponse response = userService.allFriends(
      createPageable(request.getPaginationRequest()),
      request.getTargetUsername(),
      request.getSearchQuery());
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void allFriends(AllFriendsRequest request, StreamObserver<AllFriendsResponse> responseObserver) {
    AllFriendsResponse response = userService.allFriends(request.getTargetUsername());
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getIncomeInvitations(InvitationsPaginatedRequest request, StreamObserver<InvitationsPaginatedResponse> responseObserver) {
    InvitationsPaginatedResponse response = userService.incomeInvitations(
      createPageable(request.getPaginationRequest()),
      request.getTargetUsername(),
      request.getSearchQuery());
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getOutcomeInvitations(InvitationsPaginatedRequest request, StreamObserver<InvitationsPaginatedResponse> responseObserver) {
    InvitationsPaginatedResponse response = userService.outcomeInvitations(
      createPageable(request.getPaginationRequest()),
      request.getTargetUsername(),
      request.getSearchQuery());
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void createFriendshipRequest(FriendshipRequest request, StreamObserver<FriendshipResponse> responseObserver) {
    FriendshipResponse response = userService.sendFriendshipRequest(request.getUsername(), request.getTargetUsername());
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void acceptFriendshipRequest(FriendshipRequest request, StreamObserver<FriendshipResponse> responseObserver) {
    FriendshipResponse response = userService.acceptFriendship(request.getUsername(), request.getTargetUsername());
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void declineFriendshipRequest(FriendshipRequest request, StreamObserver<FriendshipResponse> responseObserver) {
    FriendshipResponse response = userService.declineFriendship(request.getUsername(), request.getTargetUsername());
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void deleteFriend(FriendshipRequest request, StreamObserver<FriendshipResponse> responseObserver) {
    FriendshipResponse response = userService.deleteFriend(request.getUsername(), request.getTargetUsername());
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Nonnull
  private Pageable createPageable(PaginationRequest paginationRequest) {
    final int DEFAULT_PAGE = 0;
    final int DEFAULT_SIZE = 20;
    final int MAX_PAGE_SIZE = 100;

    if (paginationRequest == null) {
      LOG.info("### Using default pagination: page = {}, size = {}", DEFAULT_PAGE, DEFAULT_SIZE);
      return PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE);
    }

    int originalPage = paginationRequest.getPage();
    int originalSize = paginationRequest.getSize();

    int page = Math.max(DEFAULT_PAGE, originalPage);
    int size = Math.min(MAX_PAGE_SIZE, Math.max(1, originalSize));

    if (originalPage != page || originalSize != size) {
      LOG.debug("### Pagination parameters adjusted from page={}, size={} to page={}, size={} ###",
        originalPage, originalSize, page, size);
    }

    return PageRequest.of(page, size);
  }
}
