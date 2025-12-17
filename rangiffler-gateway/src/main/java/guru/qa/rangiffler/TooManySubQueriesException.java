package guru.qa.rangiffler;

public class TooManySubQueriesException extends RuntimeException {
  public TooManySubQueriesException(String message) {
    super(message);
  }
}
