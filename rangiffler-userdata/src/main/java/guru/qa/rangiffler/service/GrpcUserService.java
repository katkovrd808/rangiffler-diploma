package guru.qa.rangiffler.service;

import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.data.repository.UserRepository;
import guru.qa.rangiffler.grpc.*;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.service.mapper.UserMapper;
import io.grpc.stub.StreamObserver;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GrpcUserService extends RangifflerUserdataServiceGrpc.RangifflerUserdataServiceImplBase {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcUserService.class);

  public static final String COUNTRY_ID = "03a90efc-9212-49f5-bcea-3347a6f1d77b";

  private final UserRepository userRepository;
  private final UserService userService;

  @Autowired
  public GrpcUserService(UserRepository userRepository, UserService userService) {
    this.userRepository = userRepository;
    this.userService = userService;
  }

  @Transactional
  @KafkaListener(topics = "users", groupId = "userdata")
  public void listener(@Payload UserJson user, ConsumerRecord<String, UserJson> cr) {
    userRepository.findByUsername(user.username())
      .ifPresentOrElse(
        u -> LOG.info("### User already exist in DB, kafka event will be skipped: {}", cr.toString()),
        () -> {
          LOG.info("### Kafka consumer record: {}", cr.toString());

          UserEntity userDataEntity = new UserEntity();
          userDataEntity.setUsername(user.username());
          userDataEntity.setCountryId(UUID.fromString(COUNTRY_ID));
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
  public void allUsers(UserRequest request, StreamObserver<UsersResponse> responseObserver) {
    UsersResponse response = userService.getAllUsers(request.getUsername());
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
    UserResponse response = userService.getUser(request.getUsername());
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void updateUser(UserRequest request, StreamObserver<UpdateUserResponse> responseObserver) {
    UpdateUserResponse response = userService.updateUser(request);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
