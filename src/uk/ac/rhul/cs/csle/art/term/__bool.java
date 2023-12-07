package uk.ac.rhul.cs.csle.art.term;

public class __bool extends Value {
  private final boolean javaValue;

  @Override
  public Boolean javaValue() {
    return javaValue;
  }

  public __bool(Boolean b) {
    javaValue = b;
  }

  public __bool(int termIndex) {
    int[] children = iTerms.getTermChildren(termIndex);
    String child = iTerms.getTermSymbolString(children[0]);
    if (child.equals("True"))
      javaValue = true;
    else
      javaValue = false;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (javaValue ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    __bool other = (__bool) obj;
    if (javaValue != other.javaValue) return false;
    return true;
  }

  @Override
  public String toString() {
    return javaValue ? "__bool(True)" : "__bool(False)";
  }

  @Override
  public Value __not() {
    return new __bool(!javaValue);
  }

  @Override
  public Value __or(Value r) {
    return new __bool(javaValue | ((__bool) r).javaValue);
  }

  @Override
  public Value __and(Value r) {
    return new __bool(javaValue & ((__bool) r).javaValue);
  }

  @Override
  public Value __xor(Value r) {
    return new __bool(javaValue ^ ((__bool) r).javaValue);
  }

  @Override
  public Value __cnd(Value r) {
    return new __bool(!javaValue | ((__bool) r).javaValue);
  }
}