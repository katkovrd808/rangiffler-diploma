package guru.qa.rangiffler;

import guru.qa.rangiffler.service.utils.PropertiesLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RangifflerUserdataApplication {
  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(RangifflerUserdataApplication.class);
    springApplication.addListeners(new PropertiesLogger());
    springApplication.run(args);
  }
}