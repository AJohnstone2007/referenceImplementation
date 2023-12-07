package uk.ac.rhul.cs.csle.art.term;

import java.util.HashMap;

public class __map extends Value {
  private final HashMap<Value, Value> javaValue;

  @Override
  public HashMap<Value, Value> javaValue() {
    return javaValue;
  }

  public __map() {
    this.javaValue = new HashMap<>();
  }

  public __map(HashMap<Value, Value> value) {
    this.javaValue = value;
  }

  public __map(int termIndex) {
    javaValue = new HashMap<>();
    // Now walk the children of termIndex, adding them to the list
    int[] children = iTerms.getTermChildren(termIndex);
    for (int i = 0; i < children.length; i++) {
      if (!iTerms.getTermSymbolString(children[i]).equals("__binding"))
        throw new ValueException("Argument of __map must be a __binding " + iTerms.toString(termIndex));
      int[] grandChildren = iTerms.getTermChildren(children[i]);
      if (grandChildren.length != 2) throw new ValueException("Type __binding must have arity " + i + ": " + iTerms.toString(termIndex));
      javaValue.put(iTerms.valueFromTerm(grandChildren[0]), iTerms.valueFromTerm(grandChildren[1]));
    }
  }

  // Collection type with mutable payload: each instance must be unique
  @Override
  public int hashCode() {
    return System.identityHashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("__map");

    if (!javaValue.isEmpty()) {
      sb.append("(");

      boolean notFirst = false;
      for (Value v : javaValue.keySet()) {
        if (notFirst)
          sb.append(",");
        else
          notFirst = true;
        sb.append("__binding(" + v + ", " + javaValue.get(v) + ")");
      }
      sb.append(")");
    }
    return sb.toString();
  }

  @Override
  public Value __size() {
    return new __int32(javaValue.size(), 0);
  }

  @Override
  public Value __put(Value r, Value rr) {
    javaValue.put(r, rr);
    return this;
  }

  @Override
  public Value __get(Value r) {
    if (javaValue.containsKey(r)) return javaValue.get(r);

    return iTerms.valueEmpty;
  }

  @Override
  public Value __contains(Value r) {
    if (javaValue.containsKey(r)) return iTerms.valueBoolTrue;

    return iTerms.valueBoolFalse;
  }

  @Override
  public Value __remove(Value r) {
    javaValue.remove(r);
    return this;
  }

  @Override
  public Value __cast__map() {
    return new __map(new HashMap<Value, Value>(javaValue));
  }

  @Override
  public Value __cast__mapChain() {
    return new __mapChain(new HashMap<Value, Value>(javaValue));
  }
}