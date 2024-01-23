package uk.ac.rhul.cs.csle.art.v3.value;

import java.util.Map;

public class ARTValueUndefined extends ARTValue {

  @Override
  public String toString() {
    return "undefined";
  }

  @Override
  public String toLatexString(Map<String, String> map) {
    return mapString("undefined", map);
  }

  @Override
  public int hashCode() {
    return 31;
  }

  @Override
  public boolean equals(Object obj) {
    return false; // Undefineds are never equal to anything
  }

  @Override
  public Object getPayload() {
    return null;
  }

}
