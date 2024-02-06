package uk.ac.rhul.cs.csle.art.old.v3.value;

import java.math.BigDecimal;

public class ARTValueRealArbitrary extends ARTValueReal {
  BigDecimal payload;

  @Override
  public BigDecimal getPayload() {
    return payload;
  }

  public ARTValueRealArbitrary(BigDecimal value) {
    this.payload = value;
  }

  public ARTValueRealArbitrary(double value) {
    this.payload = new BigDecimal(Double.toString(value));
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
    if (!(obj instanceof ARTValueRealArbitrary)) return false;
    ARTValueRealArbitrary other = (ARTValueRealArbitrary) obj;
    if (payload == null) {
      if (other.payload != null) return false;
    } else if (!payload.equals(other.payload)) return false;
    return true;
  }

  @Override
  public ARTValue gt(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityRealArbitrary) return r.gt(this);
    return new ARTValueBoolean(payload.compareTo(r.castToRealArbitrary().payload) > 0);
  }

  @Override
  public ARTValue lt(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityRealArbitrary) return r.lt(this);
    return new ARTValueBoolean(payload.compareTo(r.castToRealArbitrary().payload) < 0);
  }

  @Override
  public ARTValue ge(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityRealArbitrary) return r.ge(this);
    return new ARTValueBoolean(payload.compareTo(r.castToRealArbitrary().payload) >= 0);
  }

  @Override
  public ARTValue le(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityRealArbitrary) return r.le(this);
    return new ARTValueBoolean(payload.compareTo(r.castToRealArbitrary().payload) <= 0);
  }

  @Override
  public ARTValue eq(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityRealArbitrary) return r.eq(this);
    return new ARTValueBoolean(payload.compareTo(r.castToRealArbitrary().payload) == 0);
  }

  @Override
  public ARTValue ne(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityRealArbitrary) return r.ne(this);
    return new ARTValueBoolean(payload.compareTo(r.castToRealArbitrary().payload) != 0);
  }

  @Override
  public ARTValue add(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityRealArbitrary) return r.add(this);
    return new ARTValueRealArbitrary(payload.add(r.castToRealArbitrary().payload));
  }

  @Override
  public ARTValue sub(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityRealArbitrary) return r.sub(this);
    return new ARTValueRealArbitrary(payload.subtract(r.castToRealArbitrary().payload));
  }

  @Override
  public ARTValue mul(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityRealArbitrary) return r.mul(this);
    return new ARTValueRealArbitrary(payload.multiply(r.castToRealArbitrary().payload));
  }

  @Override
  public ARTValue div(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityRealArbitrary) return r.div(this);
    return new ARTValueRealArbitrary(payload.divide(r.castToRealArbitrary().payload));
  }

  @Override
  public ARTValue mod(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityRealArbitrary) return r.mod(this);
    return new ARTValueRealArbitrary(payload.remainder(r.castToRealArbitrary().payload));
  }

  @Override
  public ARTValue exp(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityRealArbitrary) return r.exp(this);
    return new ARTValueRealArbitrary(payload.pow(r.castToInteger32().payload));
  }

  @Override
  public ARTValue neg() {
    return new ARTValueRealArbitrary(payload.negate());
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
    return new ARTValueIntegerArbitrary(payload.longValue());
  }

  @Override
  public ARTValueReal64 castToReal64() {
    return new ARTValueReal64(payload.doubleValue());
  }

  @Override
  public ARTValueRealArbitrary castToRealArbitrary() {
    return this;
  }

  @Override
  public ARTValueString castToString() {
    return new ARTValueString("" + payload);
  }

  @Override
  protected int getCoercionPriority() {
    return coercionPriorityRealArbitrary;
  }

}
