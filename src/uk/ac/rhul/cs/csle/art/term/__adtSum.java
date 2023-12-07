package uk.ac.rhul.cs.csle.art.term;

public class __adtSum extends Value {
  private final int javaValue;

  @Override
  public Integer javaValue() {
    return javaValue;
  }

  public __adtSum(int termIndex) {
    javaValue = termIndex;
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
    __adtSum other = (__adtSum) obj;
    if (javaValue != other.javaValue) return false;
    return true;
  }
}