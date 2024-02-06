package uk.ac.rhul.cs.csle.art.old.v3.value;

import java.util.Iterator;
import java.util.LinkedList;

public class ARTValueList extends ARTValueCollection {
  LinkedList<ARTValue> payload = new LinkedList<ARTValue>();

  @Override
  public LinkedList<ARTValue> getPayload() {
    return payload;
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
    if (!(obj instanceof ARTValueList)) return false;
    ARTValueList other = (ARTValueList) obj;
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
  public ARTValue insert(ARTValue v) {
    payload.addFirst(v);
    return this;
  }

  @Override
  public ARTValue delete() {
    if (!payload.isEmpty()) return new ARTValueNull();
    payload.removeFirst();
    return this;
  }

  @Override
  public ARTValue value() {
    if (payload.isEmpty()) return new ARTValueNull();

    return payload.getFirst();
  }

}
