package guru.qa.rangiffler.config;

import guru.qa.rangiffler.service.cors.CorsCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.security.GrpcSecurity;
import org.springframework.grpc.server.security.RequestMapperConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfiguration {

  private final CorsCustomizer corsCustomizer;

  @Autowired
  public SecurityConfiguration(CorsCustomizer corsCustomizer) {
    this.corsCustomizer = corsCustomizer;
  }

  @Bean
  public GrpcSecurity grpcSecurity(ObjectPostProcessor<Object> objectPostProcessor,
                                   AuthenticationManagerBuilder authenticationManagerBuilder,
                                   ApplicationContext context) throws Exception {
    GrpcSecurity grpcSecurity = new GrpcSecurity(objectPostProcessor, authenticationManagerBuilder, context);
    grpcSecurity.authorizeRequests(RequestMapperConfigurer::allRequests);
    return grpcSecurity;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    corsCustomizer.corsCustomizer(http);

    return http
      .authorizeHttpRequests(authorize -> authorize
        .requestMatchers(
          antMatcher("/api/session/current"),
          antMatcher("/vendor/graphiql/**"),
          antMatcher("/static/**"),
          antMatcher("/webjars/**"),
          antMatcher("/graphiql/**"),
          antMatcher("/actuator/health"),
          antMatcher(HttpMethod.POST, "/graphql"),
          antMatcher(HttpMethod.GET, "/graphql")
        ).permitAll()
        .anyRequest().authenticated()
      )
      .oauth2ResourceServer(oauth2 -> oauth2
        .jwt(Customizer.withDefaults())
      )
      .csrf(csrf -> csrf
        .ignoringRequestMatchers("/graphql", "/graphiql/**")
      )
      .build();
  }
}
