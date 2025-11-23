package guru.qa.rangiffler;

import guru.qa.rangiffler.service.utils.PropertiesLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RangifflerCountriesApplication {
  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(RangifflerCountriesApplication.class);
    springApplication.addListeners(new PropertiesLogger());
    springApplication.run(args);
  }
}