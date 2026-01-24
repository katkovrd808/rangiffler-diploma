package guru.qa.rangiffler.jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Photo {
  String photoPath() default "img/russia.jpg";

  String countryCode() default "RU";

  boolean withDescription() default false;
}
