package uk.ac.rhul.cs.csle.art.old.term;

import java.io.PrintStream;

import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;

public class __output extends Value {
  private final PrintStream javaValue;

  public __output(PrintStream stream) {
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
    __output other = (__output) obj;
    if (javaValue == null) {
      if (other.javaValue != null) return false;
    } else if (!javaValue.equals(other.javaValue)) return false;
    return true;
  }

  @Override
  public String toString() {
    return ("__output()");
  }

  @Override
  public Value __put(Value r) {
    if (!(javaValue instanceof PrintStream)) throw new ARTUncheckedException("Value type __channel invalid operation __put(_,_) - wrong stream kind ");
    javaValue.print(r);
    return this;
  }
}
