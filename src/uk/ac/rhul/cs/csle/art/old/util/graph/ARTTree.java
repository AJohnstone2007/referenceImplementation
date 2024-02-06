package uk.ac.rhul.cs.csle.art.old.util.graph;

import java.io.PrintWriter;

public class ARTTree extends ARTAbstractGraph {

  protected ARTTreeVertex root = null;

  public ARTTree(Object label) {
    super(label);
  }

  public ARTTreeVertex getRoot() {
    return root;
  }

  public void setRoot(ARTTreeVertex root) {
    this.root = root;
  }

  @Override
  protected void printDotBody(ARTAbstractGraph graph, PrintWriter printWriter) {
    if (root != null) root.printDot(graph, printWriter);
  }
}
