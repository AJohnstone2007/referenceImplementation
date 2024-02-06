package uk.ac.rhul.cs.csle.art.old.term;

public class __adtProd extends Value {
  private final int javaValue;

  @Override
  public Integer javaValue() {
    return javaValue;
  }

  public __adtProd(int termIndex) {
    this.javaValue = termIndex;
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
    __adtProd other = (__adtProd) obj;
    if (javaValue != other.javaValue) return false;
    return true;
  }

}