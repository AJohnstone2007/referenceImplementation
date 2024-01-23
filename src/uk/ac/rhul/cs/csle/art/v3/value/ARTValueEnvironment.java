package uk.ac.rhul.cs.csle.art.v3.value;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import uk.ac.rhul.cs.csle.art.core.ARTUncheckedException;

public class ARTValueEnvironment extends ARTValueCollection {
  private final Map<ARTValue, ARTValue> payload;
  private ARTValueEnvironment parent;

  public ARTValueEnvironment getParent() {
    return parent;
  }

  public void setParent(ARTValueEnvironment parent) {
    this.parent = parent;
  }

  @Override
  public Map<ARTValue, ARTValue> getPayload() {
    return payload;
  }

  public ARTValueEnvironment(ARTValueEnvironment parent) {
    payload = new LinkedHashMap<>();
    this.parent = parent;
  }

  public ARTValueEnvironment(ARTValueEnvironment map, ARTValueEnvironment parent) {
    payload = new LinkedHashMap<>(map.getPayload());
    this.parent = parent;
  }

  public ARTValueEnvironment() {
    this(null);
  }

  @Override
  public String toString() {
    String ret = "{ ";
    for (ARTValue v : payload.keySet())
      ret += v.toString() + "->" + payload.get(v).toString() + " ";
    return ret + "}";
  }

  @Override
  public String toLatexString(Map<String, String> map) {
    String ret = "\\{ ";
    for (ARTValue v : payload.keySet())
      ret += v.toLatexString(map) + "->" + payload.get(v).toLatexString(map) + " ";
    return ret + "\\}";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((payload == null) ? 0 : payload.hashCode());
    result = prime * result + ((parent == null) ? 0 : parent.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTValueEnvironment)) return false;
    ARTValueEnvironment other = (ARTValueEnvironment) obj;
    if (payload == null) {
      if (other.payload != null) return false;
    } else if (!payload.equals(other.payload)) return false;
    if (parent == null) {
      if (other.parent != null) return false;
    } else if (!parent.equals(other.parent)) return false;
    return true;
  }

  @Override
  public Iterator<ARTValue> iterator() {
    // Recursively gather key set from this and parents; then return iterator on it
    return null;
  }

  @Override
  public ARTValue cardinality() {
    if (parent == null)
      return new ARTValueInteger32(payload.size());
    else
      return parent.cardinality().add(new ARTValueInteger32(payload.size()));
  }

  @Override
  public ARTValue contains(ARTValue key) {
    if (payload.containsKey(key))
      return new ARTValueBoolean(true);
    else if (parent != null) return parent.contains(key);

    return new ARTValueBoolean(false);
  }

  @Override
  public ARTValue insertKey(ARTValue key, ARTValue value) {
    if (payload.containsKey(key)) throw new ARTUncheckedException("ARTValueEnvironment insertion of key that is already in the local map" + key);
    payload.put(key, value);
    return this;
  }

  @Override
  public ARTValue deleteKey(ARTValue key) {
    if (!payload.containsKey(key)) throw new ARTUncheckedException("ARTValueEnvironment deletion of key that is not in the local map" + key);
    payload.remove(key);
    return this;
  }

  @Override
  public ARTValue update(ARTValue key, ARTValue value) {
    if (!payload.containsKey(key)) throw new ARTUncheckedException(
        "ARTValueEnvironment update of key that is not in the local map: " + key + "\nAvailable parameters are: " + payload.keySet());
    payload.put(key, value);
    return this;
  }

  @Override
  public ARTValue updateOrdered(ARTValueCollection value) {
    Iterator<ARTValue> mapIterator = payload.keySet().iterator();
    Iterator<ARTValue> loadIterator = value.iterator();

    while (loadIterator.hasNext() && mapIterator.hasNext())
      payload.put(mapIterator.next(), loadIterator.next());

    if (loadIterator.hasNext()) throw new ARTUncheckedException("update ordered has more update elements than collection elements");

    return this;
  }

  @Override
  public ARTValue valueKey(ARTValue key) {
    if (payload.containsKey(key))
      return payload.get(key);
    else if (parent != null) return parent.valueKey(key);
    throw new ARTUncheckedException("ARTValueEnvironment lookup of unknown key: " + key);
  }
}
