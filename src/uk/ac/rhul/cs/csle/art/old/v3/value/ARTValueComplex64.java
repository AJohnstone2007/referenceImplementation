package uk.ac.rhul.cs.csle.art.old.v3.value;

public class ARTValueComplex64 extends ARTValueComplex {
  ARTValueReal64 real, imaginary;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((imaginary == null) ? 0 : imaginary.hashCode());
    result = prime * result + ((real == null) ? 0 : real.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTValueComplex64)) return false;
    ARTValueComplex64 other = (ARTValueComplex64) obj;
    if (imaginary == null) {
      if (other.imaginary != null) return false;
    } else if (!imaginary.equals(other.imaginary)) return false;
    if (real == null) {
      if (other.real != null) return false;
    } else if (!real.equals(other.real)) return false;
    return true;
  }

  @Override
  public Object getPayload() {
    return null;
  }

}
