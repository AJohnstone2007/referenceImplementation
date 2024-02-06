package uk.ac.rhul.cs.csle.art.old.term;

import java.util.HashMap;

import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;

public class __mapChain extends Value {

  private final __mapChain parent;
  private HashMap<Value, Value> javaValue;

  public __mapChain getPayload2() {
    return parent;
  }

  @Override
  public HashMap<Value, Value> javaValue() {
    return javaValue;
  }

  public __mapChain() {
    javaValue = new HashMap<>();
    this.parent = null;
  }

  public __mapChain(__mapChain parent) {
    javaValue = new HashMap<>();
    this.parent = parent;
  }

  public __mapChain(HashMap<Value, Value> value) {
    value = new HashMap<Value, Value>();
    parent = null;
  }

  public __mapChain(HashMap<Value, Value> value, __mapChain parent) {
    value = new HashMap<Value, Value>();
    this.parent = parent;
  }

  public __mapChain(int termIndex) {
    parent = null; // More work needed here - define the external and internal syntax of __mapChain
    javaValue = new HashMap<>();
    // Now walk the children of termIndex, adding them to the list
    int[] children = iTerms.getTermChildren(termIndex);
    if (iTerms.getTermSymbolIndex(children[0]) != iTerms.__mapChainStringIndex && iTerms.getTermSymbolIndex(children[0]) != iTerms.__emptyStringIndex)
      throw new ARTUncheckedException("First argument of __mapChain muct be of type __mapChain or __empty: " + toString());

    for (int i = 1; i < children.length; i++) {
      if (!iTerms.getTermSymbolString(children[i]).equals("__binding"))
        throw new ARTUncheckedException("Argument of __map must be a __binding " + iTerms.toString(termIndex));
      int[] grandChildren = iTerms.getTermChildren(children[i]);
      if (grandChildren.length != 2) throw new ARTUncheckedException("Type __binding must have arity " + i + ": " + iTerms.toString(termIndex));
      javaValue.put(iTerms.valueFromTerm(grandChildren[0]), iTerms.valueFromTerm(grandChildren[1]));
    }
  }

  private void toStringRec(StringBuilder sb, boolean formatted) {
    if (parent == null)
      sb.append("__empty");
    else
      sb.append(parent.toString());
    for (Value v : javaValue.keySet()) {
      sb.append(",");
      if (formatted) sb.append("\n");
      if (formatted)
        sb.append(v + "->" + javaValue.get(v));
      else
        sb.append("__binding(" + v + ", " + javaValue.get(v) + ")");
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("__mapChain(");
    toStringRec(sb, false);
    sb.append(")");
    return sb.toString();
  }

  @Override
  public Value __size() {
    return new __int32(javaValue.size() + (parent != null ? ((__int32) parent.__size()).javaValue() : 0), 0);
  }

  @Override
  public Value __put(Value r, Value rr) {
    javaValue.put(r, rr);
    return this;
  }

  @Override
  public Value __get(Value r) {

    if (javaValue.containsKey(r))
      return javaValue.get(r);
    else if (parent != null) return parent.__get(r);

    return iTerms.valueEmpty;
  }

  @Override
  public Value __contains(Value r) {
    if (javaValue.containsKey(r))
      return iTerms.valueBoolTrue;
    else if (parent != null) return parent.__contains(r);

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