package uk.ac.rhul.cs.csle.art.term;

import java.util.LinkedList;

public class __list extends Value {
  private final LinkedList<Value> javaValue;

  @Override
  public LinkedList<Value> javaValue() {
    return javaValue;
  }

  public __list() {
    this.javaValue = new LinkedList<>();
  }

  public __list(LinkedList<Value> value) {
    this.javaValue = value;
  }

  public __list(int termIndex) {
    javaValue = new LinkedList<>();
    // Now walk the children of termIndex, adding them to the list
    int[] children = iTerms.getTermChildren(termIndex);
    for (int i = 0; i < children.length; i++)
      javaValue.add(iTerms.valueFromTerm(children[i]));
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
    StringBuilder sb = new StringBuilder("__list");
    if (!javaValue.isEmpty()) {
      sb.append("(");

      boolean notFirst = false;
      for (Value v : javaValue) {
        if (notFirst)
          sb.append(",");
        else
          notFirst = true;
        sb.append(v);
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
  public Value __cat(Value r) {
    if (r instanceof __list)
      for (Value v : ((__list) r).javaValue())
        javaValue.addLast(v);
    else
      javaValue.addLast(r);
    return this;
  }

  @Override
  public Value __put(Value r) {
    javaValue.addLast(r);
    return this;
  }

  @Override
  public Value __get() {
    if (javaValue.isEmpty()) return iTerms.valueEmpty;
    return javaValue.get(0);
  }

  @Override
  public Value __slice() {
    if (javaValue.size() < 2) return iTerms.valueEmpty;

    __list ret = new __list(javaValue);
    ret.javaValue.remove();
    return ret;
  }

  // @Override
  // public Value __cast__array() {
  // Value ret = new __array();
  // for (Value v : javaValue)
  // ret.__put(v, iTerms.valueEmpty);
  // return ret;
  // }

  @Override
  public Value __cast__list() {
    return new __list(new LinkedList<Value>(javaValue));
  }

  @Override
  public Value __cast__map() {
    Value ret = new __map();
    for (Value v : javaValue)
      ret.__put(v, iTerms.valueEmpty);
    return ret;
  }

  @Override
  public Value __cast__mapChain() {
    Value ret = new __mapChain();
    for (Value v : javaValue)
      ret.__put(v, iTerms.valueEmpty);
    return ret;
  }
}