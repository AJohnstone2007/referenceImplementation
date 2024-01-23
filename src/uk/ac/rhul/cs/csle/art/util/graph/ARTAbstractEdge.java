package uk.ac.rhul.cs.csle.art.util.graph;

import java.io.PrintWriter;

public class ARTAbstractEdge {

  protected ARTGraphVertex src;
  protected ARTGraphVertex dst;

  public ARTAbstractEdge(ARTGraphVertex parent, ARTGraphVertex child) {
    this.src = parent;
    this.dst = child;
  }

  public void remove() {
    src.removeOutEdge(this);
    dst.removeInEdge(this);
  }

  public ARTGraphVertex getSrc() {
    return src;
  }

  public ARTGraphVertex getDst() {
    return dst;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dst == null) ? 0 : dst.hashCode());
    result = prime * result + ((src == null) ? 0 : src.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ARTAbstractEdge other = (ARTAbstractEdge) obj;
    if (dst == null) {
      if (other.dst != null) return false;
    } else if (!dst.equals(other.dst)) return false;
    if (src == null) {
      if (other.src != null) return false;
    } else if (!src.equals(other.src)) return false;
    return true;
  }

  public void printDot(ARTAbstractGraph graph, PrintWriter printWriter) {
    printWriter.print("\n\"" + src.getKey() + "\"->\"" + dst.getKey() + "\";");
  }

}