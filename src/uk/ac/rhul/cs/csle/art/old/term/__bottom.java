package uk.ac.rhul.cs.csle.art.old.term;

public class __bottom extends Value {
  // Special type with no payload: use class based equality
  @Override
  public int hashCode() {
    return this.getClass().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof __bottom;
  }

  @Override
  public String toString() {
    return "__bottom";
  }
}