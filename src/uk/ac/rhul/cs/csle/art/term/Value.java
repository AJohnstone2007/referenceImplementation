package uk.ac.rhul.cs.csle.art.term;

public abstract class Value {
  public static ITerms iTerms = null;

  Value[] make__array(int... x) {
    Value[] ret = new Value[x.length + 1];

    ret[0] = new __int32(x.length, 0);
    for (int i = 0; i < x.length; i++)
      ret[i + 1] = new __int32(x[i], 0);

    return ret;
  }

  @Override
  public int hashCode() {
    return 11;
  }

  @Override
  public boolean equals(Object obj) {
    return true;
  }

  private void operationNotImplemented(String operation) {
    throw new ValueException("Value type " + this.getClass().getSimpleName() + " does not provide operation " + operation);
  }

  private void operationNotImplemented(String operation, Value r) {
    throw new ValueException("Value type " + this.getClass().getSimpleName() + " does not allow operation " + operation + " with argument " + r);
  }

  private void operationNotImplemented(String operation, Value r, Value rr) {
    throw new ValueException("Value type " + this.getClass().getSimpleName() + " does not allow operation " + operation + " with arguments " + r + ", " + rr);
  }

  public Object javaValue() {
    return this.getClass().getSimpleName();
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "(" + javaValue() + ")";
  }

  public String toValueString() {

    return javaValue() == null ? "null" : javaValue().toString();
  }

  char processEscape(char e) {
    switch (e) {
    case 'b':
      return '\n';
    case 'f':
      return '\f';
    case 'n':
      return '\n';
    case 'r':
      return '\r';
    case 't':
      return '\t';
    }
    return e;
  }

  public Value __eq(Value r) {
    if (equals(r))
      return iTerms.valueBoolTrue;
    else
      return iTerms.valueBoolFalse;
  }

  public Value __ne(Value r) {
    if (!equals(r))
      return iTerms.valueBoolTrue;
    else
      return iTerms.valueBoolFalse;
  }

  public Value __gt(Value r) {
    operationNotImplemented("__gt(_,_)", r);
    return null;
  }

  public Value __lt(Value r) {
    operationNotImplemented("__lt(_,_)", r);
    return null;
  }

  public Value __ge(Value r) {
    operationNotImplemented("__ge(_,_)", r);
    return null;
  }

  public Value __le(Value r) {
    operationNotImplemented("__le(_,_)", r);
    return null;
  }

  public Value __compare(Value r) {
    operationNotImplemented("__compare(_,_)", r);
    return null;
  }

  public Value __not() {
    operationNotImplemented("__not(_)");
    return null;
  }

  public Value __and(Value r) {
    operationNotImplemented("__and(_,_)", r);
    return null;
  }

  public Value __or(Value r) {
    operationNotImplemented("__or(_,_)", r);
    return null;
  }

  public Value __xor(Value r) {
    operationNotImplemented("__xor(_,_)", r);
    return null;
  }

  public Value __cnd(Value r) {
    operationNotImplemented("__cnd(_,_)", r);
    return null;
  }

  public Value __lsh(Value r) {
    operationNotImplemented("__lsh(_,_)", r);
    return null;
  }

  public Value __rsh(Value r) {
    operationNotImplemented("__rsh(_,_)", r);
    return null;
  }

  public Value __ash(Value r) {
    operationNotImplemented("__ash(_,_)", r);
    return null;
  }

  public Value __rol(Value r) {
    operationNotImplemented("__rol(_,_)", r);
    return null;
  }

  public Value __ror(Value r) {
    operationNotImplemented("__ror(_,_)", r);
    return null;
  }

  public Value __neg() {
    operationNotImplemented("__neg(_)");
    return null;
  }

  public Value __add(Value r) {
    operationNotImplemented("__add(_,_)", r);
    return null;
  }

  public Value __sub(Value r) {
    operationNotImplemented("__sub(_,_)", r);
    return null;
  }

  public Value __mul(Value r) {
    operationNotImplemented("__mul(_,_)", r);
    return null;
  }

  public Value __div(Value r) {
    operationNotImplemented("__div(_,_)", r);
    return null;
  }

  public Value __mod(Value r) {
    operationNotImplemented("__mod(_,_)", r);
    return null;
  }

  public Value __exp(Value r) {
    operationNotImplemented("__exp(_,_)", r);
    return null;
  }

  public Value __size() {
    operationNotImplemented("__size(_)");
    return null;
  }

  public Value __cat(Value r) {
    operationNotImplemented("__cat(_,_)", r);
    return null;
  }

  public Value __slice() {
    operationNotImplemented("__slice(_)");
    return null;
  }

  public Value __slice(Value r, Value rr) {
    operationNotImplemented("__slice(_,_,_)", r);
    return null;
  }

  public Value __get() {
    operationNotImplemented("__get(_)");
    return null;
  }

  public Value __get(Value r) {
    operationNotImplemented("__get(_,_)", r);
    return null;
  }

  public Value __put(Value r) {
    operationNotImplemented("__put(_) - check arity", r);
    return null;
  }

  public Value __put(Value r, Value rr) {
    operationNotImplemented("__put(_,_)", r);
    return null;
  }

  public Value __put(Value r, Value rr, boolean lock) {
    operationNotImplemented("__put(_,_,_)", r);
    return null;
  }

  public Value __contains(Value r) {
    operationNotImplemented("__contains(_,_)", r);
    return null;
  }

  public Value __remove(Value r) {
    operationNotImplemented("__remove()", r);
    return null;
  }

  public Value __extract() {
    operationNotImplemented("__extract()");
    return null;
  }

  public Value __union(Value r) {
    operationNotImplemented("__union(_,_)", r);
    return null;
  }

  public Value __intersection(Value r) {
    operationNotImplemented("__intersection(_,_)", r);
    return null;
  }

  public Value __difference(Value r) {
    operationNotImplemented("__difference(_,_)", r);
    return null;
  }

  public Value __cast__bool() {
    operationNotImplemented("__cast__bool(_)");
    return null;
  }

  public Value __cast__char() {
    operationNotImplemented("__cast__char(_)");
    return null;
  }

  public Value __cast__intAP() {
    operationNotImplemented("__cast__intAP(_)");
    return null;
  }

  public Value __cast__int32() {
    operationNotImplemented("__cast__int32(_)");
    return null;
  }

  public Value __cast__realAP() {
    operationNotImplemented("__cast__realAP(_)");
    return null;
  }

  public Value __cast__real64() {
    operationNotImplemented("__cast__real64(_)");
    return null;
  }

  public Value __cast__string() {
    return new __string(toString());
  }

  public Value __cast__array() {
    operationNotImplemented("__cast__array(_)");
    return null;
  }

  public Value __cast__list() {
    operationNotImplemented("__cast__list(_)");
    return null;
  }

  public Value __cast__flexArray() {
    operationNotImplemented("__cast__flexArray(_)");
    return null;
  }

  public Value __cast__set() {
    operationNotImplemented("__cast__set(_)");
    return null;
  }

  public Value __cast__map() {
    operationNotImplemented("__cast__map(_)");
    return null;
  }

  public Value __cast__mapChain() {
    operationNotImplemented("__cast__mapChain(_)");
    return null;
  }

  public final Value __plugin(Value... args) {
    return iTerms.plugin.plugin(args);
  }

}