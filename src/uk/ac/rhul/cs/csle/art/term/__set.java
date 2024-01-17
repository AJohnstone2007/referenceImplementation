package uk.ac.rhul.cs.csle.art.term;

import java.util.HashSet;
import java.util.Iterator;

public class __set extends Value {
  private final HashSet<Value> javaValue;

  @Override
  public HashSet<Value> javaValue() {
    return javaValue;
  }

  public __set() {
    this.javaValue = new HashSet<>();
  }

  public __set(HashSet<Value> value) {
    this.javaValue = value;
  }

  public __set(int termIndex) {
    javaValue = new HashSet<>();
    // Now walk the children of termIndex, adding them to the list
    int[] children = iTerms.getTermChildren(termIndex);
    for (int i = 0; i < children.length; i++) {
      javaValue.add(iTerms.valueFromTerm(children[i]));
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
  public Value __put(Value r) {
    javaValue.add(r);
    return this;
  }

  @Override
  public Value __extract() {
    Iterator<Value> iterator = javaValue.iterator();

    return iterator.hasNext() ? iterator.next() : iTerms.valueEmpty;
  }

  @Override
  public Value __contains(Value r) {
    if (javaValue.contains(r)) return iTerms.valueBoolTrue;

    return iTerms.valueBoolFalse;
  }

  @Override
  public Value __remove(Value r) {
    javaValue.remove(r);
    return this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Value __union(Value r) {
    if (!(r instanceof __set)) throw new ValueException("arguments of __union must both be of type __set");
    javaValue.addAll((HashSet<Value>) r.javaValue());
    return this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Value __intersection(Value r) {
    if (!(r instanceof __set)) throw new ValueException("arguments of __intersection must both be of type __set");
    javaValue.retainAll((HashSet<Value>) r.javaValue());
    return this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Value __difference(Value r) {
    if (!(r instanceof __set)) throw new ValueException("arguments of __difference must both be of type __set");
    javaValue.removeAll((HashSet<Value>) r.javaValue());
    return this;
  }
}