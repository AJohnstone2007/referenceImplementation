package uk.ac.rhul.cs.csle.art.old.v3.value;

public abstract class ARTValueSignal extends ARTValue {
  public abstract ARTValue v();

  @Override
  public String toString() {
    return v().toString();
  }

  @Override
  public Object getPayload() {
    return null;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    return false;
  }

}
