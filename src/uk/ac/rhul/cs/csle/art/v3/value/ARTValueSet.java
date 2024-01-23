package uk.ac.rhul.cs.csle.art.v3.value;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ARTValueSet extends ARTValueCollection {
  private final Set<ARTValue> payload = new HashSet<ARTValue>();

  @Override
  public Set<ARTValue> getPayload() {
    return payload;
  }

  @Override
  public String toString() {
    String ret = "{ ";
    for (ARTValue v : payload)
      ret += v.toString() + " ";
    return ret + "}";
  }

  @Override
  public String toLatexString(Map<String, String> map) {
    String ret = mapString("\\{ ", map);
    for (ARTValue v : payload)
      ret += v.toLatexString(map) + " ";
    return ret + mapString("\\}", map);
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
    if (!(obj instanceof ARTValueSet)) return false;
    ARTValueSet other = (ARTValueSet) obj;
    if (payload == null) {
      if (other.payload != null) return false;
    } else if (!payload.equals(other.payload)) return false;
    return true;
  }

  @Override
  public Iterator<ARTValue> iterator() {
    return payload.iterator();
  }

  @Override
  public ARTValue cardinality() {
    return new ARTValueInteger32(payload.size());
  }

  @Override
  public ARTValue contains(ARTValue key) {
    return new ARTValueBoolean(payload.contains(key));
  }

  @Override
  public ARTValue insert(ARTValue val) {
    return this;
  }

  @Override
  public ARTValue deleteKey(ARTValue key) {
    payload.remove(key);
    return this;
  }

}
