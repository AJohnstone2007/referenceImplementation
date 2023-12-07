package uk.ac.rhul.cs.csle.art.term;

public class __string extends Value {
  private final String value;

  @Override
  public String javaValue() {
    return value;
  }

  public __string(String value) {
    this.value = value;
  }

  public __string(int termIndex) {
    String label = iTerms.getTermSymbolString(iTerms.getTermChildren(termIndex)[0]);
    value = label.substring(1, label.length() - 1);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    __string other = (__string) obj;
    if (value == null) {
      if (other.value != null) return false;
    } else if (!value.equals(other.value)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "" + value;
  }

  @Override
  public Value __compare(Value r) {
    int i = value.compareTo(((__string) r).javaValue());

    if (i > 0) return iTerms.valueInt32One;
    if (i < 0) return iTerms.valueInt32MinusOne;
    return iTerms.valueInt32Zero;
  }

  @Override
  public Value __size() {
    return new __int32(value.length(), 0);
  }

  @Override
  public Value __cat(Value r) {
    return new __string(value + ((__string) r).value);
  }

  @Override
  public Value __get(Value r) {
    return new __char(value.charAt(((__int32) r).javaValue()));
  }

  @Override
  public Value __slice(Value r, Value rr) {
    int lo = ((__int32) r).javaValue();
    int hi = ((__int32) rr).javaValue();

    return new __string(value.substring(lo, hi));
  }
}