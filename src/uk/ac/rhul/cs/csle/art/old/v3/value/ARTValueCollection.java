package uk.ac.rhul.cs.csle.art.old.v3.value;

import java.util.Map;

public abstract class ARTValueCollection extends ARTValue implements Iterable<ARTValue> {
  @Override
  public String toString() {
    return getPayload().toString();
  }

  @Override
  public String toLatexString(Map<String, String> map) {
    return getPayload().toString(); // kludge
    // String ret = "[ ";
    // for (ARTValue v : payload)
    // ret += v.toLatexString(map) + " ";
    // return ret + "]";
  }

}
