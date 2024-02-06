package uk.ac.rhul.cs.csle.art.old.util.graph;

import java.io.PrintWriter;

public class ARTTreeVertex extends ARTAbstractVertex {
  protected ARTTreeVertex child = null;
  protected ARTTreeVertex sibling = null;

  public ARTTreeVertex(Object key, Object payload, ARTTreeVertex child, ARTTreeVertex sibling) {
    super(key, payload);
    this.child = child;
    this.sibling = sibling;
  }

  public ARTTreeVertex(Object key, Object payload) {
    super(key, payload);
  }

  public ARTTreeVertex addChild(ARTTreeVertex newChild) {
    System.err.printf("Adding tree vertex " + newChild + " to vertex" + this + "\n");
    if (child == null)
      child = newChild;
    else {
      ARTTreeVertex tmp = child;
      while (tmp.sibling != null)
        tmp = tmp.sibling;

      tmp.sibling = newChild;
    }
    return newChild;
  }

  public void print() {
    System.out.printf(this + "(");
    // Now visit the children
    for (ARTTreeVertex target = child; target != null; target = target.sibling) {
      target.print();
    }
    System.out.printf(")");
  }

  public ARTTreeVertex getChild() {
    return child;
  }

  public void setChild(ARTTreeVertex child) {
    this.child = child;
  }

  public ARTTreeVertex getSibling() {
    return sibling;
  }

  public void setSibling(ARTTreeVertex sibling) {
    this.sibling = sibling;
  }

  @Override
  public void printDot(ARTAbstractGraph graph, PrintWriter printWriter) {
    printWriter.print("\n!!!\"" + key + "\"  [label=\"" + (key.toString()) + " " + (payload == null ? "null" : payload.toString()) + "\"]");
    // Now visit the children
    for (ARTTreeVertex target = child; target != null; target = target.sibling) {
      printWriter.print("\n\"" + key + "\"->\"" + target.getKey() + "\"");
      target.printDot(graph, printWriter);
    }
  }

  @Override
  public String toString() {
    return (key.toString()) + (payload == null ? "null" : payload.toString());
  }
}
