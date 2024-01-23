package uk.ac.rhul.cs.csle.art.v3.value;

import java.util.Map;

public class ARTValueStoreAddress extends ARTValue {
  int payload = 0;
  private final ARTValueMap store;

  public ARTValueStoreAddress(ARTValueMap store, int l) {
    this.payload = l;
    this.store = store;
  }

  @Override
  public String toString() {
    return "SA" + payload;
  }

  @Override
  public String toLatexString(Map<String, String> map) {
    return mapString("" + payload, map);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + payload;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTValueStoreAddress)) return false;
    ARTValueStoreAddress other = (ARTValueStoreAddress) obj;
    if (payload != other.payload) return false;
    return true;
  }

  public ARTValueStoreAddress inc() {
    return new ARTValueStoreAddress(store, payload + 1);
  }

  @Override
  public Object getPayload() {
    return payload;
  }
}
