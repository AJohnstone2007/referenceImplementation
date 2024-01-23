package uk.ac.rhul.cs.csle.art.v3.value;

public class ARTValueInteger32 extends ARTValueInteger {
  int payload = 0;

  @Override
  public Integer getPayload() {
    return payload;
  }

  public ARTValueInteger32(int l) {
    this.payload = l;
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
    if (!(obj instanceof ARTValueInteger32)) return false;
    ARTValueInteger32 other = (ARTValueInteger32) obj;
    if (payload != other.payload) return false;
    return true;
  }

  @Override
  public ARTValue gt(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityInteger32) return r.gt(this);
    return new ARTValueBoolean(payload > r.castToInteger32().payload);
  }

  @Override
  public ARTValue lt(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityInteger32) return r.lt(this);
    return new ARTValueBoolean(payload < r.castToInteger32().payload);
  }

  @Override
  public ARTValue ge(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityInteger32) return r.ge(this);
    return new ARTValueBoolean(payload >= r.castToInteger32().payload);
  }

  @Override
  public ARTValue le(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityInteger32) return r.le(this);
    return new ARTValueBoolean(payload <= r.castToInteger32().payload);
  }

  @Override
  public ARTValue eq(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityInteger32) return r.eq(this);

    return new ARTValueBoolean(payload == r.castToInteger32().payload);
  }

  @Override
  public ARTValue ne(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityInteger32) return r.ne(this);
    return new ARTValueBoolean(payload != r.castToInteger32().payload);
  }

  @Override
  public ARTValue add(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityInteger32) return r.add(this);
    return new ARTValueInteger32(payload + r.castToInteger32().payload);
  }

  @Override
  public ARTValue sub(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityInteger32) return r.sub(this);
    return new ARTValueInteger32(payload - r.castToInteger32().payload);
  }

  @Override
  public ARTValue mul(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityInteger32) return r.mul(this);
    return new ARTValueInteger32(payload * r.castToInteger32().payload);
  }

  @Override
  public ARTValue div(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityInteger32) return r.div(this);
    return new ARTValueInteger32(payload / r.castToInteger32().payload);
  }

  @Override
  public ARTValue mod(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityInteger32) return r.mod(this);
    return new ARTValueInteger32(payload % r.castToInteger32().payload);
  }

  @Override
  public ARTValue exp(ARTValue r) {
    if (r.getCoercionPriority() > coercionPriorityInteger32) return r.exp(this);
    return new ARTValueInteger32((int) (Math.pow(payload, r.castToReal64().payload)));
  }

  @Override
  public ARTValue neg() {
    return new ARTValueInteger32(-payload);
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
    return this;
  }

  @Override
  public ARTValueIntegerArbitrary castToIntegerArbitrary() {
    return new ARTValueIntegerArbitrary(payload);
  }

  @Override
  public ARTValueReal64 castToReal64() {
    return new ARTValueReal64(payload);
  }

  @Override
  public ARTValueRealArbitrary castToRealArbitrary() {
    return new ARTValueRealArbitrary(payload);
  }

  @Override
  public ARTValueString castToString() {
    return new ARTValueString("" + payload);
  }

  @Override
  protected int getCoercionPriority() {
    return coercionPriorityInteger32;
  }
}
