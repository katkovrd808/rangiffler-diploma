package guru.qa.rangiffler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RangifflerGrpcServerApplication {
  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(RangifflerGrpcServerApplication.class);
    springApplication.run(args);
  }
}