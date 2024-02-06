package uk.ac.rhul.cs.csle.art.old.v3.value;

import java.math.BigInteger;

public class ARTValueIntegerArbitrary extends ARTValueInteger {
  BigInteger payload;

  @Override
  public BigInteger getPayload() {
    return payload;
  }

  public ARTValueIntegerArbitrary(BigInteger value) {
    this.payload = value;
  }

  public ARTValueIntegerArbitrary(int value) {
    this.payload = new BigInteger(Integer.toString(value));
  }

  public ARTValueIntegerArbitrary(long value) {
    this.payload = new BigInteger(Long.toString(value));
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
    if (!(obj instanceof ARTValueIntegerArbitrary)) return false;
    ARTValueIntegerArbitrary other = (ARTValueIntegerArbitrary) obj;
    if (payload == null) {
      if (other.payload != null) return false;
    } else if (!payload.equals(other.payload)) return false;
    return true;
  }

  @Override
  public ARTValue gt(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityIntegerArbitrary) return r.gt(this);
    return new ARTValueBoolean(payload.compareTo(r.castToIntegerArbitrary().payload) > 0);
  }

  @Override
  public ARTValue lt(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityIntegerArbitrary) return r.lt(this);
    return new ARTValueBoolean(payload.compareTo(r.castToIntegerArbitrary().payload) < 0);
  }

  @Override
  public ARTValue ge(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityIntegerArbitrary) return r.ge(this);
    return new ARTValueBoolean(payload.compareTo(r.castToIntegerArbitrary().payload) >= 0);
  }

  @Override
  public ARTValue le(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityIntegerArbitrary) return r.le(this);
    return new ARTValueBoolean(payload.compareTo(r.castToIntegerArbitrary().payload) <= 0);
  }

  @Override
  public ARTValue eq(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityIntegerArbitrary) return r.eq(this);
    return new ARTValueBoolean(payload.compareTo(r.castToIntegerArbitrary().payload) == 0);
  }

  @Override
  public ARTValue ne(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityIntegerArbitrary) return r.ne(this);
    return new ARTValueBoolean(payload.compareTo(r.castToIntegerArbitrary().payload) != 0);
  }

  @Override
  public ARTValue add(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityIntegerArbitrary) return r.add(this);
    return new ARTValueIntegerArbitrary(payload.add(r.castToIntegerArbitrary().payload));
  }

  @Override
  public ARTValue sub(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityIntegerArbitrary) return r.sub(this);
    return new ARTValueIntegerArbitrary(payload.subtract(r.castToIntegerArbitrary().payload));
  }

  @Override
  public ARTValue mul(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityIntegerArbitrary) return r.mul(this);
    return new ARTValueIntegerArbitrary(payload.multiply(r.castToIntegerArbitrary().payload));
  }

  @Override
  public ARTValue div(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityIntegerArbitrary) return r.div(this);
    return new ARTValueIntegerArbitrary(payload.divide(r.castToIntegerArbitrary().payload));
  }

  @Override
  public ARTValue mod(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityIntegerArbitrary) return r.mod(this);
    return new ARTValueIntegerArbitrary(payload.mod(r.castToIntegerArbitrary().payload));
  }

  @Override
  public ARTValue exp(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityIntegerArbitrary) return r.exp(this);
    return new ARTValueIntegerArbitrary(payload.pow(r.castToInteger32().payload));
  }

  @Override
  public ARTValue neg() {
    return new ARTValueIntegerArbitrary(payload.negate());
  }

  /****
   * Cast operations
   */

  @Override
  public ARTValueInteger32 castToInteger32() {
    return new ARTValueInteger32((int) payload.longValue());
  }

  @Override
  public ARTValueIntegerArbitrary castToIntegerArbitrary() {
    return this;
  }

  @Override
  public ARTValueReal64 castToReal64() {
    return new ARTValueReal64(payload.doubleValue());
  }

  @Override
  public ARTValueRealArbitrary castToRealArbitrary() {
    return new ARTValueRealArbitrary(payload.doubleValue());
  }

  @Override
  public ARTValueString castToString() {
    return new ARTValueString("" + payload);
  }

  @Override
  protected int getCoercionPriority() {
    return coercionPriorityIntegerArbitrary;
  }

}
