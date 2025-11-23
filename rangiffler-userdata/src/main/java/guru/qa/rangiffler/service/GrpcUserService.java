package guru.qa.rangiffler.service;

import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.data.repository.UserRepository;
import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.service.api.GrpcCountriesClient;
import io.grpc.stub.StreamObserver;
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
    Pageable pageable = createPageable(request.getPaginationRequest());
    UsersPaginatedResponse response = userService.getAllUsers(pageable, request.getExcludeUsername());
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
    UserResponse response = userService.getCurrentUser(request.getUsername());
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
  public void allFriends(AllFriendsPaginatedRequest request, StreamObserver<AllFriendsPaginatedResponse> responseObserver) {
    AllFriendsPaginatedResponse response = userService.allFriends(createPageable(request.getPaginationRequest()), request.getTargetUsername());
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getIncomeInvitations(InvitationsPaginatedRequest request, StreamObserver<InvitationsPaginatedResponse> responseObserver) {
    InvitationsPaginatedResponse response = userService.incomeInvitations(createPageable(request.getPaginationRequest()), request.getTargetUsername());
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getOutcomeInvitations(InvitationsPaginatedRequest request, StreamObserver<InvitationsPaginatedResponse> responseObserver) {
    InvitationsPaginatedResponse response = userService.outcomeInvitations(createPageable(request.getPaginationRequest()), request.getTargetUsername());
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

  private Pageable createPageable(PaginationRequest paginationRequest) {
    return PageRequest.of(
      paginationRequest.getPage(),
      paginationRequest.getSize()
    );
  }
}
