package uk.ac.rhul.cs.csle.art.v3.value;

/* A special value, intended to model the null pointer value in languages which use pointer */

public class ARTValueNull extends ARTValue {

  @Override
  public int hashCode() {
    return 31;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTValueNull)) return false;
    return true;
  }

  @Override
  public Object getPayload() {
    return null;
  }
}
