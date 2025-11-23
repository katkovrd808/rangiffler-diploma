package guru.qa.rangiffler.config;

import com.maxmind.geoip2.DatabaseReader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
@Slf4j
public class GeoIPConfiguration {
  @Bean
  @SneakyThrows
  public DatabaseReader databaseReader(@Value("${geoip.country.database}") String countryDatabase) {
    try {
      String resourcePath = countryDatabase.replace("classpath:", "");
      Resource resource = new ClassPathResource(resourcePath);

      if (!resource.exists()) {
        log.error("GeoIP database not found at: {}", resourcePath);
        throw new IllegalStateException("GeoIP database not found: " + resourcePath);
      }

      log.info("Loading GeoIP database from: {}", resourcePath);
      DatabaseReader reader = new DatabaseReader.Builder(resource.getInputStream()).build();
      log.info("GeoIP database loaded successfully");

      return reader;

    } catch (Exception e) {
      log.error("Failed to load GeoIP database from: {}", countryDatabase, e);
      throw e;
    }
  }
}
