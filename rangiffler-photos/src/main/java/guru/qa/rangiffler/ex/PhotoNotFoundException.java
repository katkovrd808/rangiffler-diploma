package guru.qa.rangiffler.ex;

public class PhotoNotFoundException extends RuntimeException {
  public PhotoNotFoundException(String message) {
    super(message);
  }
}
