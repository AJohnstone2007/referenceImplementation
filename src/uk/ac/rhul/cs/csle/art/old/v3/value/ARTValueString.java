package uk.ac.rhul.cs.csle.art.old.v3.value;

import java.util.Iterator;

public class ARTValueString extends ARTValueCollection implements Comparable {
  protected String payload;

  @Override
  // todo: make this class more sophisticated so that it can construct an iterator object
  public Iterator<ARTValue> iterator() {
    return null;
  }

  public ARTValueString(String l) {
    this.payload = l;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((payload == null) ? 0 : payload.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTValueString)) return false;
    ARTValueString other = (ARTValueString) obj;
    if (payload == null) {
      if (other.payload != null) return false;
    } else if (!payload.equals(other.payload)) return false;
    return true;
  }

  @Override
  public ARTValue cat(ARTValue r) {
    return new ARTValueString(payload + r.castToString().payload);
  }

  @Override
  public ARTValue gt(ARTValue r) {
    return new ARTValueBoolean(payload.compareTo(r.castToString().payload) > 0);
  }

  @Override
  public ARTValue lt(ARTValue r) {
    return new ARTValueBoolean(payload.compareTo(r.castToString().payload) < 0);
  }

  @Override
  public ARTValue ge(ARTValue r) {
    return new ARTValueBoolean(payload.compareTo(r.castToString().payload) >= 0);
  }

  @Override
  public ARTValue le(ARTValue r) {
    return new ARTValueBoolean(payload.compareTo(r.castToString().payload) <= 0);
  }

  @Override
  public ARTValue eq(ARTValue r) {
    return new ARTValueBoolean(payload.compareTo(r.castToString().payload) == 0);
  }

  @Override
  public ARTValue ne(ARTValue r) {
    return new ARTValueBoolean(payload.compareTo(r.castToString().payload) != 0);
  }

  /****
   * Cast operations
   */

  @Override
  public ARTValueBoolean castToBoolean() {
    return new ARTValueBoolean(Boolean.parseBoolean(payload));
  }

  @Override
  public ARTValueInteger32 castToInteger32() {
    return new ARTValueInteger32(Integer.parseInt(payload));
  }

  @Override
  public ARTValueReal64 castToReal64() {
    return new ARTValueReal64(Double.parseDouble(payload));
  }

  @Override
  public ARTValueString castToString() {
    return this;
  }

  @Override
  public int compareTo(Object that) {
    if (!(that instanceof ARTValueString)) return -1;
    return payload.compareTo(((ARTValueString) that).payload);
  }

  @Override
  public String toString() {
    return payload;
  }

  @Override
  public String getPayload() {
    return toString();
  }
}
