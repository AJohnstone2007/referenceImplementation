package uk.ac.rhul.cs.csle.art.old.util.graph;

import java.io.PrintWriter;

public class ARTGraphEdge extends ARTAbstractEdge {
  Object payload;

  public ARTGraphEdge(ARTGraphVertex parent, ARTGraphVertex child, Object payload) {
    super(parent, child);
    this.src = parent;
    this.dst = child;
    this.payload = payload;

    parent.addOutEdge(this);
    child.addInEdge(this);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ARTGraphEdge [payload=");
    builder.append(payload);
    builder.append(", parent=");
    builder.append(src.getKey());
    builder.append(", child=");
    builder.append(dst.getKey());
    builder.append("]");
    return builder.toString();
  }

  public Object getPayload() {
    return payload;
  }

  public void setPayload(Object payload) {
    this.payload = payload;
  }

  @Override
  public void printDot(ARTAbstractGraph graph, PrintWriter printWriter) {
    printWriter.print("\n\"" + src + "\"->\"" + dst + "\"" + (payload == null ? "" : "[label=\"" + payload + "\"]") + ";");
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((payload == null) ? 0 : payload.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    ARTGraphEdge other = (ARTGraphEdge) obj;
    if (payload == null) {
      if (other.payload != null) return false;
    } else if (!payload.equals(other.payload)) return false;
    return true;
  }
}
