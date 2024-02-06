package uk.ac.rhul.cs.csle.art.old.term;

import java.math.BigDecimal;
import java.math.BigInteger;

public class __real64 extends Value {
  private final double javaValue;

  @Override
  public Double javaValue() {
    return javaValue;
  }

  public __real64(int termIndex) {
    javaValue = Double.parseDouble(iTerms.getTermSymbolString(iTerms.getTermChildren(termIndex)[0]));
  }

  public __real64(double value) {
    this.javaValue = value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    long temp;
    temp = Double.doubleToLongBits(javaValue);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    __real64 other = (__real64) obj;
    if (Double.doubleToLongBits(javaValue) != Double.doubleToLongBits(other.javaValue)) return false;
    return true;
  }

  @Override
  public Value __gt(Value r) {
    return new __bool(javaValue > ((__real64) r).javaValue);
  }

  @Override
  public Value __ge(Value r) {
    return new __bool(javaValue >= ((__real64) r).javaValue);
  }

  @Override
  public Value __lt(Value r) {
    return new __bool(javaValue < ((__real64) r).javaValue);
  }

  @Override
  public Value __le(Value r) {
    return new __bool(javaValue <= ((__real64) r).javaValue);
  }

  @Override
  public Value __neg() {
    return new __real64(-javaValue);
  }

  @Override
  public Value __add(Value r) {
    return new __real64(javaValue + ((__real64) r).javaValue);
  }

  @Override
  public Value __sub(Value r) {
    return new __real64(javaValue - ((__real64) r).javaValue);
  }

  @Override
  public Value __mul(Value r) {
    return new __real64(javaValue * ((__real64) r).javaValue);
  }

  @Override
  public Value __div(Value r) {
    return new __real64(javaValue / ((__real64) r).javaValue);
  }

  @Override
  public Value __mod(Value r) {
    return new __real64(javaValue % ((__real64) r).javaValue);
  }

  @Override
  public Value __exp(Value r) {
    return new __real64(Math.pow(javaValue, ((__real64) r).javaValue));
  }

  @Override
  public Value __cast__bool() {
    return (javaValue == 0.0) ? iTerms.valueBoolFalse : iTerms.valueBoolTrue;
  }

  @Override
  public Value __cast__intAP() {
    return new __intAP(BigInteger.valueOf((int) javaValue));
  }

  @Override
  public Value __cast__int32() {
    return new __int32((int) javaValue, 0);
  }

  @Override
  public Value __cast__realAP() {
    return new __realAP(new BigDecimal(javaValue));
  }

  @Override
  public Value __cast__real64() {
    return new __real64(javaValue);
  }
}