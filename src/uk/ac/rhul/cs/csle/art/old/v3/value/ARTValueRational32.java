package uk.ac.rhul.cs.csle.art.old.v3.value;

public class ARTValueRational32 extends ARTValueRational {

  ARTValueInteger32 numerator, denominator;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((denominator == null) ? 0 : denominator.hashCode());
    result = prime * result + ((numerator == null) ? 0 : numerator.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTValueRational32)) return false;
    ARTValueRational32 other = (ARTValueRational32) obj;
    if (denominator == null) {
      if (other.denominator != null) return false;
    } else if (!denominator.equals(other.denominator)) return false;
    if (numerator == null) {
      if (other.numerator != null) return false;
    } else if (!numerator.equals(other.numerator)) return false;
    return true;
  }

  @Override
  public Object getPayload() {
    return null;
  }

}
