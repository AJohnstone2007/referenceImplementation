package uk.ac.rhul.cs.csle.art.old.v3.lex;

import java.util.HashMap;
import java.util.Map;

public class ARTTWEPairSet {
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ARTTWEPairSet [map=");
    builder.append(map);
    builder.append("]");
    return builder.toString();
  }

  public Map<ARTTWEPair, ARTTWEPair> map = new HashMap<>();
}