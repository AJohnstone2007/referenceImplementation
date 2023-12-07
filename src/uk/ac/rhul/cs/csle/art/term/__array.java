package uk.ac.rhul.cs.csle.art.term;

public class __array /* extends Value */ {
  // private final ArrayList<Value> javaValue;
  //
  // @Override
  // public ArrayList<Value> javaValue() {
  // return javaValue;
  // }
  //
  // public __array() {
  // this.javaValue = new ArrayList<Value>();
  // }
  //
  // public __array(ArrayList<Value> value) {
  // this.javaValue = new ArrayList<Value>(value);
  // }
  //
  // public __array(int termIndex) {
  // // System.out.println("__array constructor called with termIndex " + termIndex + ": " + iTerms.toString(termIndex));
  // int[] children = iTerms.getTermChildren(termIndex);
  // this.javaValue = new Value[(int) iTerms.valueFromTerm(children[0]).javaValue()];
  //
  // // Now walk the children of termIndex, adding them to the array
  // for (int i = 1; i < children.length; i++)
  // javaValue[i - 1] = iTerms.valueFromTerm(children[i]);
  //
  // // Now walk the remaining new children, setting them to __empty
  // for (int i = children.length - 1; i < javaValue.length; i++)
  // javaValue[i] = iTerms.valueEmpty;
  // }
  //
  // // Collection type with mutable payload: each instance must be unique
  // @Override
  // public int hashCode() {
  // return System.identityHashCode(this);
  // }
  //
  // @Override
  // public boolean equals(Object obj) {
  // return this == obj;
  // }
  //
  // @Override
  // public String toString() {
  // StringBuilder sb = new StringBuilder("__array(__int32(" + javaValue.length + ")");
  // for (Value v : javaValue)
  // sb.append(", " + v);
  // sb.append(")");
  // return sb.toString();
  // }
  //
  // @Override
  // public Value __size() {
  // return new __int32(javaValue.length, 0);
  // }
  //
  // @Override
  // public Value __put(Value r, Value rr) {
  // __int32 ri = (__int32) r;
  // if (ri.javaValue() >= javaValue.length)
  // throw new ARTUncheckedException("__array access out of bounds: __put at index " + ri + " with bounds 0.." + javaValue.length);
  // javaValue[((__int32) r).javaValue()] = rr;
  // return this;
  // }
  //
  // @Override
  // public Value __get(Value r) {
  // return javaValue[((__int32) r).javaValue()];
  // }
  //
  // private Value slice(int lo, int hi) {
  // if (hi < lo) throw new ARTUncheckedException("__slice operation on __array type has hi bound lower than lo bound");
  // Value[] retValue = new Value[hi - lo];
  //
  // for (int i = lo; i < hi; i++)
  // retValue[i - lo] = javaValue[i];
  //
  // return new __array(retValue);
  // }
  //
  // @Override
  // public Value __slice(Value r, Value rr) {
  // return slice(((__int32) r).javaValue(), ((__int32) rr).javaValue());
  // }
  //
  // @Override
  // public Value __cast__array() {
  // return new __array(javaValue);
  // }
  //
  // @Override
  // public Value __cast__list() {
  // return new __list((LinkedList<Value>) Arrays.asList(javaValue));
  // }
  //
  // @Override
  // public Value __cast__map() {
  // Value ret = new __map();
  // for (Value v : javaValue)
  // ret.__put(v, iTerms.valueEmpty);
  // return ret;
  // }
  //
  // @Override
  // public Value __cast__mapChain() {
  // Value ret = new __mapChain();
  // for (Value v : javaValue)
  // ret.__put(v, iTerms.valueEmpty);
  // return ret;
  // }
}