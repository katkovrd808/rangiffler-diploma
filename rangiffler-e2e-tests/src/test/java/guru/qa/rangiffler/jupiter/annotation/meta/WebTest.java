package guru.qa.rangiffler.jupiter.annotation.meta;

import guru.qa.rangiffler.jupiter.extension.ApiLoginExtension;
import guru.qa.rangiffler.jupiter.extension.BrowserExtension;
import guru.qa.rangiffler.jupiter.extension.PhotoExtension;
import guru.qa.rangiffler.jupiter.extension.UserExtension;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({
  BrowserExtension.class,
  UserExtension.class,
  PhotoExtension.class,
  ApiLoginExtension.class,
  AllureJunit5.class
})
public @interface WebTest {
}
