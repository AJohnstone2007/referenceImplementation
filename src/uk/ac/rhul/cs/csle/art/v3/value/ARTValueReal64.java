package uk.ac.rhul.cs.csle.art.v3.value;

public class ARTValueReal64 extends ARTValueReal {
  double payload = 0.0;

  @Override
  public Double getPayload() {
    return payload;
  }

  public ARTValueReal64(double l) {
    this.payload = l;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(payload);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTValueReal64)) return false;
    ARTValueReal64 other = (ARTValueReal64) obj;
    if (Double.doubleToLongBits(payload) != Double.doubleToLongBits(other.payload)) return false;
    return true;
  }

  @Override
  public ARTValue gt(ARTValue r) {
    return new ARTValueBoolean(payload > r.castToReal64().payload);
  }

  @Override
  public ARTValue lt(ARTValue r) {
    return new ARTValueBoolean(payload < r.castToReal64().payload);
  }

  @Override
  public ARTValue ge(ARTValue r) {
    return new ARTValueBoolean(payload >= r.castToReal64().payload);
  }

  @Override
  public ARTValue le(ARTValue r) {
    return new ARTValueBoolean(payload <= r.castToReal64().payload);
  }

  @Override
  public ARTValue eq(ARTValue r) {
    return new ARTValueBoolean(payload == r.castToReal64().payload);
  }

  @Override
  public ARTValue ne(ARTValue r) {
    return new ARTValueBoolean(payload != r.castToReal64().payload);
  }

  @Override
  public ARTValue add(ARTValue r) {
    return new ARTValueReal64(payload + r.castToReal64().payload);
  }

  @Override
  public ARTValue sub(ARTValue r) {
    return new ARTValueReal64(payload - r.castToReal64().payload);
  }

  @Override
  public ARTValue mul(ARTValue r) {
    return new ARTValueReal64(payload * r.castToReal64().payload);
  }

  @Override
  public ARTValue div(ARTValue r) {
    return new ARTValueReal64(payload / r.castToReal64().payload);
  }

  @Override
  public ARTValue mod(ARTValue r) {
    return new ARTValueReal64(payload % r.castToReal64().payload);
  }

  @Override
  public ARTValue exp(ARTValue r) {
    return new ARTValueReal64((int) (Math.pow(payload, r.castToReal64().payload)));
  }

  @Override
  public ARTValue neg() {
    return new ARTValueReal64(-payload);
  }

  /****
   * Cast operations
   */

  @Override
  public ARTValueBoolean castToBoolean() {
    return new ARTValueBoolean(payload != 0);
  }

  @Override
  public ARTValueCharacter castToCharacter() {
    return new ARTValueCharacter((char) payload);
  }

  @Override
  public ARTValueInteger32 castToInteger32() {
    return new ARTValueInteger32((int) payload);
  }

  @Override
  public ARTValueReal64 castToReal64() {
    return this;
  }

  @Override
  public ARTValueString castToString() {
    return new ARTValueString("" + payload);
  }

  @Override
  protected int getCoercionPriority() {
    return coercionPriorityReal64;
  }
}
