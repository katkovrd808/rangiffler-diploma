package guru.qa.rangiffler.api.core;

import java.util.concurrent.ConcurrentHashMap;

public enum ThreadSafeQueryStore {
  INSTANCE;

  private final ConcurrentHashMap<String, String> queryStore = new ConcurrentHashMap<>();

  public void put(String param, String value) {
    queryStore.put(param, value);
  }

  public String get(String param) {
    return queryStore.get(param);
  }

  public void remove(String param) {
    queryStore.remove(param);
  }

  public String getParams() {
    return queryStore.keySet().toString();
  }
}
