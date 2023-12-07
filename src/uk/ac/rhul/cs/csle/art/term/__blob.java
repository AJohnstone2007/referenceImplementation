package uk.ac.rhul.cs.csle.art.term;

public class __blob extends Value {
  private static int nextFreeBlobNumber = 1;
  private final Object javaValue;
  private final int blobNumber = nextFreeBlobNumber++;

  @Override
  public Object javaValue() {
    return javaValue;
  }

  public __blob(Object value) {
    javaValue = value;
  }

  public __blob(int termIndex) {
    javaValue = termIndex;
  }

  @Override
  public String toString() {
    return "__blob(" + blobNumber + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + blobNumber;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    __blob other = (__blob) obj;
    if (blobNumber != other.blobNumber) return false;
    return true;
  }
}