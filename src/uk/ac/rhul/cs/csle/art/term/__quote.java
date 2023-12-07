package uk.ac.rhul.cs.csle.art.term;

public class __quote extends Value {
  private final int value;

  @Override
  public Integer javaValue() {
    return value;
  }

  public __quote(int termIndex) {
    this.value = termIndex;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + value;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    __quote other = (__quote) obj;
    if (value != other.value) return false;
    return true;
  }

  @Override
  // Note this very special case - the literal form of a quote is its body!
  public String toString() {
    return iTerms.toString(value);
  }
}
