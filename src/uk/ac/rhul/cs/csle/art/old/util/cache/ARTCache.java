package uk.ac.rhul.cs.csle.art.old.util.cache;

import java.util.HashMap;
import java.util.Map;

public class ARTCache<T> {
  public Map<T, Integer> getToIndexMap() {
    return toIndexMap;
  }

  public Map<Integer, T> getFromIndexMap() {
    return fromIndexMap;
  }

  private final Map<T, Integer> toIndexMap = new HashMap<>();
  private final Map<Integer, T> fromIndexMap = new HashMap<>();
  private Integer nextFreeIndexNumber = 1; // Reserve zero for illegals or empty set in application

  Integer getIndex(T chiSet) {
    return toIndexMap.get(chiSet);
  }

  public T get(Integer indexValue) {
    return fromIndexMap.get(indexValue);
  }

  public Integer find(T t) {
    if (getIndex(t) == null) {
      toIndexMap.put(t, nextFreeIndexNumber);
      fromIndexMap.put(nextFreeIndexNumber, t);
      nextFreeIndexNumber++;
    }

    return getIndex(t);
  }
}
