package guru.qa.rangiffler.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
public class GeoLocationService {

  private static final String DEFAULT_COUNTRY_CODE = "RU";

  private final DatabaseReader databaseReader;

  @Autowired
  @SneakyThrows
  public GeoLocationService(DatabaseReader databaseReader) {
    this.databaseReader = databaseReader;
  }

  public String countryCountryCodeByIP(String ip) {
    try {
      InetAddress ipAddress = InetAddress.getByName(ip);
      CountryResponse response = databaseReader.country(ipAddress);
      return response.country().isoCode();
    } catch (Exception e) {
      return DEFAULT_COUNTRY_CODE;
    }
  }
}
