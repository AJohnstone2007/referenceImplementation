package uk.ac.rhul.cs.csle.art.old.term;

import java.math.BigDecimal;
import java.math.BigInteger;

public class __int32 extends Value {

  public final int javaValue;

  @Override
  public Integer javaValue() {
    return javaValue;
  }

  public __int32(int value, int dummyIgnored) {
    this.javaValue = value;
  }

  public __int32(Integer value) {
    this.javaValue = value;
  }

  public __int32(int termIndex) {
    String child = iTerms.getTermSymbolString(iTerms.getTermChildren(termIndex)[0]);
    javaValue = Integer.parseInt(child);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + javaValue;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    __int32 other = (__int32) obj;
    if (javaValue != other.javaValue) return false;
    return true;
  }

  @Override
  public Value __gt(Value r) {
    return new __bool(javaValue > ((__int32) r).javaValue);
  }

  @Override
  public Value __ge(Value r) {
    return new __bool(javaValue >= ((__int32) r).javaValue);
  }

  @Override
  public Value __lt(Value r) {
    return new __bool(javaValue < ((__int32) r).javaValue);
  }

  @Override
  public Value __le(Value r) {
    return new __bool(javaValue <= ((__int32) r).javaValue);
  }

  @Override
  public Value __not() {
    return new __int32(~javaValue, 0);
  }

  @Override
  public Value __and(Value r) {
    return new __int32(javaValue & ((__int32) r).javaValue, 0);
  }

  @Override
  public Value __or(Value r) {
    return new __int32(javaValue | ((__int32) r).javaValue, 0);
  }

  @Override
  public Value __xor(Value r) {
    return new __int32(javaValue ^ ((__int32) r).javaValue, 0);
  }

  @Override
  public Value __cnd(Value r) {
    return new __int32(~javaValue | ((__int32) r).javaValue, 0);
  }

  @Override
  public Value __lsh(Value r) {
    return new __int32(javaValue << ((__int32) r).javaValue, 0);
  }

  @Override
  public Value __rsh(Value r) {
    return new __int32(javaValue >>> ((__int32) r).javaValue, 0);
  }

  @Override
  public Value __ash(Value r) {
    return new __int32(javaValue >> ((__int32) r).javaValue, 0);
  }

  @Override
  public Value __rol(Value r) {
    int lsb = javaValue < 0 ? 1 : 0;
    return new __int32((javaValue << ((__int32) r).javaValue) | lsb, 0);
  }

  @Override
  public Value __ror(Value r) {
    int msb = javaValue << 31;
    return new __int32((javaValue >> ((__int32) r).javaValue) | msb, 0);
  }

  @Override
  public Value __neg() {
    return new __int32(-javaValue, 0);
  }

  @Override
  public Value __add(Value r) {
    return new __int32(javaValue + ((__int32) r).javaValue, 0);
  }

  @Override
  public Value __sub(Value r) {
    return new __int32(javaValue - ((__int32) r).javaValue, 0);
  }

  @Override
  public Value __mul(Value r) {
    return new __int32(javaValue * ((__int32) r).javaValue, 0);
  }

  @Override
  public Value __div(Value r) {
    return new __int32(javaValue / ((__int32) r).javaValue, 0);
  }

  @Override
  public Value __mod(Value r) {
    return new __int32(javaValue % ((__int32) r).javaValue, 0);
  }

  @Override
  public Value __exp(Value r) {
    int ret = javaValue, ri = ((__int32) r).javaValue;
    if (ri < 0) return iTerms.valueInt32Zero;
    if (ri == 0) return iTerms.valueInt32One;
    while (ri-- > 1)
      ret *= javaValue;
    return new __int32(ret, 0);
  }

  @Override
  public Value __cast__bool() {
    return (javaValue == 0) ? iTerms.valueBoolFalse : iTerms.valueBoolTrue;
  }

  @Override
  public Value __cast__char() {
    return new __char((char) javaValue);
  }

  @Override
  public Value __cast__intAP() {
    return new __intAP(BigInteger.valueOf(javaValue));
  }

  @Override
  public Value __cast__int32() {
    return new __int32(javaValue, 0);
  }

  @Override
  public Value __cast__realAP() {
    return new __realAP(new BigDecimal(javaValue));
  }

  @Override
  public Value __cast__real64() {
    return new __real64((double) javaValue);
  }
}