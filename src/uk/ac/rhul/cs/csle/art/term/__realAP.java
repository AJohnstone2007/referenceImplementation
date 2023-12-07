package uk.ac.rhul.cs.csle.art.term;

import java.math.BigDecimal;

public class __realAP extends Value {
  private final BigDecimal javaValue;

  @Override
  public BigDecimal javaValue() {
    return javaValue;
  }

  public __realAP(int termIndex) {
    javaValue = null;
  }

  public __realAP(BigDecimal value) {
    this.javaValue = value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((javaValue == null) ? 0 : javaValue.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    __realAP other = (__realAP) obj;
    if (javaValue == null) {
      if (other.javaValue != null) return false;
    } else if (!javaValue.equals(other.javaValue)) return false;
    return true;
  }

  @Override
  public Value __gt(Value r) {
    return new __bool(javaValue.compareTo(((__realAP) r).javaValue()) > 0);
  }

  @Override
  public Value __ge(Value r) {
    return new __bool(javaValue.compareTo(((__realAP) r).javaValue()) >= 0);
  }

  @Override
  public Value __lt(Value r) {
    return new __bool(javaValue.compareTo(((__realAP) r).javaValue()) < 0);
  }

  @Override
  public Value __le(Value r) {
    return new __bool(javaValue.compareTo(((__realAP) r).javaValue()) <= 0);
  }

  @Override
  public Value __neg() {
    return new __realAP(javaValue.negate());
  }

  @Override
  public Value __add(Value r) {
    return new __realAP(javaValue.add(((__realAP) r).javaValue));
  }

  @Override
  public Value __sub(Value r) {
    return new __realAP(javaValue.subtract(((__realAP) r).javaValue));
  }

  @Override
  public Value __mul(Value r) {
    return new __realAP(javaValue.multiply(((__realAP) r).javaValue));
  }

  @Override
  public Value __div(Value r) {
    return new __realAP(javaValue.divide(((__realAP) r).javaValue));
  }

  @Override
  public Value __mod(Value r) {
    return new __realAP(javaValue.remainder(((__realAP) r).javaValue));
  }

  @Override
  public Value __exp(Value r) {
    return new __realAP(javaValue.pow(((__int32) r).javaValue()));
  }

  @Override
  public Value __cast__bool() {
    return (javaValue.compareTo(BigDecimal.ZERO) == 0) ? iTerms.valueBoolFalse : iTerms.valueBoolTrue;
  }

  @Override
  public Value __cast__intAP() {
    return new __intAP(javaValue.toBigInteger());
  }

  @Override
  public Value __cast__int32() {
    return new __int32(javaValue.intValue(), 0);
  }

  @Override
  public Value __cast__realAP() {
    return new __realAP(javaValue);
  }

  @Override
  public Value __cast__real64() {
    return new __real64(javaValue.doubleValue());
  }
}