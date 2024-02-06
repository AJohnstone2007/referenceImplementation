package uk.ac.rhul.cs.csle.art.old.term;

import java.math.BigDecimal;
import java.math.BigInteger;

public class __intAP extends Value {
  private final BigInteger javaValue;

  @Override
  public BigInteger javaValue() {
    return javaValue;
  }

  public __intAP(int termIndex) {
    javaValue = null;
  }

  public __intAP(BigInteger value) {
    javaValue = value;
  }

  @Override
  public int hashCode() {
    return javaValue.hashCode();
  }

  @Override
  public Value __gt(Value r) {
    return new __bool(javaValue.compareTo(((__intAP) r).javaValue()) > 0);
  }

  @Override
  public Value __ge(Value r) {
    return new __bool(javaValue.compareTo(((__intAP) r).javaValue()) >= 0);
  }

  @Override
  public Value __lt(Value r) {
    return new __bool(javaValue.compareTo(((__intAP) r).javaValue()) < 0);
  }

  @Override
  public Value __le(Value r) {
    return new __bool(javaValue.compareTo(((__intAP) r).javaValue()) <= 0);
  }

  @Override
  public Value __not() {
    return new __intAP(javaValue.not());
  }

  @Override
  public Value __and(Value r) {
    return new __intAP(javaValue.and(((__intAP) r).javaValue));
  }

  @Override
  public Value __or(Value r) {
    return new __intAP(javaValue.or(((__intAP) r).javaValue));
  }

  @Override
  public Value __xor(Value r) {
    return new __intAP(javaValue.xor(((__intAP) r).javaValue));
  }

  @Override
  public Value __cnd(Value r) {
    return new __intAP(javaValue.not().or(((__intAP) r).javaValue));
  }

  @Override
  public Value __lsh(Value r) {
    return new __intAP(javaValue.shiftLeft(((__int32) r).javaValue()));
  }

  @Override
  public Value __ash(Value r) {
    return new __intAP(javaValue.shiftRight(((__int32) r).javaValue()));
  }

  @Override
  public Value __neg() {
    return new __intAP(javaValue.negate());
  }

  @Override
  public Value __add(Value r) {
    return new __intAP(javaValue.add(((__intAP) r).javaValue));
  }

  @Override
  public Value __sub(Value r) {
    return new __intAP(javaValue.subtract(((__intAP) r).javaValue));
  }

  @Override
  public Value __mul(Value r) {
    return new __intAP(javaValue.multiply(((__intAP) r).javaValue));
  }

  @Override
  public Value __div(Value r) {
    return new __intAP(javaValue.divide(((__intAP) r).javaValue));
  }

  @Override
  public Value __mod(Value r) {
    return new __intAP(javaValue.mod(((__intAP) r).javaValue));
  }

  @Override
  public Value __exp(Value r) {
    return new __intAP(javaValue.pow(((__int32) r).javaValue()));
  }

  @Override
  public Value __cast__bool() {
    return (javaValue.compareTo(BigInteger.ZERO) == 0) ? iTerms.valueBoolFalse : iTerms.valueBoolTrue;
  }

  @Override
  public Value __cast__intAP() {
    return new __intAP(javaValue);
  }

  @Override
  public Value __cast__int32() {
    return new __int32(javaValue.intValue(), 0);
  }

  @Override
  public Value __cast__realAP() {
    return new __realAP(new BigDecimal(javaValue));
  }

  @Override
  public Value __cast__real64() {
    return new __real64(javaValue.doubleValue());
  }
}