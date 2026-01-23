package guru.qa.rangiffler.jupiter.annotation;

import guru.qa.rangiffler.jupiter.extension.ApiLoginExtension;
import guru.qa.rangiffler.jupiter.extension.UserExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith({
  UserExtension.class,
  ApiLoginExtension.class
})
public @interface ApiLogin {
  String username() default "";

  String password() default "";
}