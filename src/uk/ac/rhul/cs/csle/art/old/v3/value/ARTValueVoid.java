package uk.ac.rhul.cs.csle.art.old.v3.value;

import java.util.Map;

public class ARTValueVoid extends ARTValue {
  @Override
  public String toString() {
    return "void";
  }

  @Override
  public String toLatexString(Map<String, String> map) {
    return mapString("void", map);
  }

  @Override
  public int hashCode() {
    return 31;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof ARTValueVoid; // pseudo-singleton
  }

  @Override
  public Object getPayload() {
    return null;
  }

}
