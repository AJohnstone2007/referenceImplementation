package uk.ac.rhul.cs.csle.art.old.v3.value;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;

public class ARTValueMapOrdered extends ARTValueCollection {
  private final Map<ARTValue, ARTValue> payload = new LinkedHashMap<>();;

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
    if (!(obj instanceof ARTValueMapOrdered)) return false;
    ARTValueMapOrdered other = (ARTValueMapOrdered) obj;
    if (payload == null) {
      if (other.payload != null) return false;
    } else if (!payload.equals(other.payload)) return false;
    return true;
  }

  @Override
  public Iterator<ARTValue> iterator() {
    return payload.keySet().iterator();
  }

  @Override
  public ARTValue cardinality() {
    return new ARTValueInteger32(payload.size());
  }

  @Override
  public ARTValue contains(ARTValue key) {
    return new ARTValueBoolean(payload.containsKey(key));
  }

  @Override
  public ARTValue insertKey(ARTValue key, ARTValue value) {
    if (payload.containsKey(key)) throw new ARTUncheckedException("ARTValueMapOrdered insertion of already existing key " + key);
    payload.put(key, value);
    return this;
  }

  @Override
  public ARTValue deleteKey(ARTValue key) {
    if (!payload.containsKey(key)) throw new ARTUncheckedException("ARTValueMapOrdered deletion of key that is not in the map" + key);
    payload.remove(key);
    return this;
  }

  @Override
  public ARTValue update(ARTValue key, ARTValue value) {
    // if (!payload.containsKey(key)) throw new ARTUncheckedException("ARTValueMap update of key that is not in the map - " + key);
    payload.put(key, value);
    return this;
  }

  @Override
  public ARTValue valueKey(ARTValue key) {
    if (payload.containsKey(key))
      return payload.get(key);
    else
      throw new ARTUncheckedException("ARTValueMapOrdered value requested for nonexistent key " + key);
  }

  @Override
  public Object getPayload() {
    return payload;
  }
}
