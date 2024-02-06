package uk.ac.rhul.cs.csle.art.old.v3.value;

import java.util.Map;

public class ARTValueUnmatchable extends ARTValue {

  @Override
  public String toString() {
    return "unmatchable";
  }

  @Override
  public String toLatexString(Map<String, String> map) {
    return mapString("unmatchable", map);
  }

  @Override
  public int hashCode() {
    return 31;
  }

  @Override
  public boolean equals(Object obj) {
    return false; // Unmatchables are never equal to anything: that's their whole point
  }

  @Override
  public Object getPayload() {
    return null;
  }
}
