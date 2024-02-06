package uk.ac.rhul.cs.csle.art.old.term;

import java.io.InputStream;

import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;

public class __input extends Value {
  private final InputStream javaValue;

  public __input(InputStream stream) {
    this.javaValue = stream;
  }

  @Override
  public Object javaValue() {
    return javaValue;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((javaValue == null) ? 0 : javaValue.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    __input other = (__input) obj;
    if (javaValue == null) {
      if (other.javaValue != null) return false;
    } else if (!javaValue.equals(other.javaValue)) return false;
    return true;
  }

  @Override
  public String toString() {
    return ("__input()");
  }

  @Override
  public Value __get(Value r) {
    throw new ARTUncheckedException("Value type __input operation __get(_,_) not yet implemented");
  }

}
