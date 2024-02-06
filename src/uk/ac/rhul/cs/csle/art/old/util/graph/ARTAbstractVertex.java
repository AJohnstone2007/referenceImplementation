package uk.ac.rhul.cs.csle.art.old.util.graph;

import java.io.PrintWriter;

public abstract class ARTAbstractVertex {
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((key == null) ? 0 : key.hashCode());
    result = prime * result + ((payload == null) ? 0 : payload.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ARTAbstractVertex other = (ARTAbstractVertex) obj;
    if (key == null) {
      if (other.key != null) return false;
    } else if (!key.equals(other.key)) return false;
    if (payload == null) {
      if (other.payload != null) return false;
    } else if (!payload.equals(other.payload)) return false;
    return true;
  }

  protected Object key;
  protected Object payload; // Key must be unique; payload need not be unique, is mutable and can be null

  public ARTAbstractVertex(Object key, Object payload) {
    super();
    this.key = key;
    this.payload = payload;
  }

  public Object getKey() {
    return key;
  }

  public Object getPayload() {
    return payload;
  }

  public void setPayload(Object payload) {
    this.payload = payload;
  }

  public void printDot(ARTAbstractGraph graph, PrintWriter printWriter) {
    printWriter.print("\n\"" + key + "\"  [label=\"" + key + (payload != null ? (": " + payload) : "") + "\"]");
  }

  @Override
  public String toString() {
    return key.toString() + ":" + payload;
  }
}
