package uk.ac.rhul.cs.csle.art.old.v3.value;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ARTValueArray extends ARTValueCollection {
  ArrayList<ARTValue> payload = new ArrayList<ARTValue>(); // use array lists to support flexible arrays

  @Override
  public ArrayList<ARTValue> getPayload() {
    return payload;
  }

  @Override
  public String toString() {
    return "ARTValueArray [l=" + payload + "]";
  }

  @Override
  public String toLatexString(Map<String, String> map) {
    String ret = "ARTValueArray [l=";
    for (ARTValue v : payload)
      ret += v.toLatexString(map) + " ";
    return ret += "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((payload == null) ? 0 : payload.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTValueArray)) return false;
    ARTValueArray other = (ARTValueArray) obj;
    if (payload == null) {
      if (other.payload != null) return false;
    } else if (!payload.equals(other.payload)) return false;
    return true;
  }

  @Override
  public Iterator<ARTValue> iterator() {
    return payload.iterator();
  }

}
