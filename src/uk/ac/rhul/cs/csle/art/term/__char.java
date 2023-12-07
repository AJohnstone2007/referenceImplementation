package uk.ac.rhul.cs.csle.art.term;

public class __char extends Value {
  private final char javaValue;

  @Override
  public Character javaValue() {
    return javaValue;
  }

  public __char(char value) {
    this.javaValue = value;
  }

  public __char(int termIndex) {
    int[] children = iTerms.getTermChildren(termIndex);
    String child = iTerms.getTermSymbolString(children[0]);
    if (child.charAt(0) != '`') throw new ValueException("__char argument must have leading `");
    javaValue = child.charAt(1);
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
    __char other = (__char) obj;
    if (javaValue != other.javaValue) return false;
    return true;
  }

  @Override
  public String toString() {
    // todo: find routine
    // return toLiteralString(Character.toString(javaValue));
    return null;
  }

  @Override
  public Value __compare(Value r) {
    if (javaValue > ((__char) r).javaValue) return iTerms.valueInt32One;
    if (javaValue < ((__char) r).javaValue) return iTerms.valueInt32MinusOne;
    return iTerms.valueInt32Zero;
  }

  @Override
  public Value __gt(Value r) {
    if (javaValue > ((__char) r).javaValue) return iTerms.valueBoolTrue;
    return iTerms.valueBoolFalse;
  }

  @Override
  public Value __ge(Value r) {
    if (javaValue >= ((__char) r).javaValue) return iTerms.valueBoolTrue;
    return iTerms.valueBoolFalse;
  }

  @Override
  public Value __lt(Value r) {
    if (javaValue < ((__char) r).javaValue) return iTerms.valueBoolTrue;
    return iTerms.valueBoolFalse;
  }

  @Override
  public Value __le(Value r) {
    if (javaValue <= ((__char) r).javaValue) return iTerms.valueBoolTrue;
    return iTerms.valueBoolFalse;
  }
}