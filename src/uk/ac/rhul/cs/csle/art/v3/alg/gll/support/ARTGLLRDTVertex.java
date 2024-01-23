package uk.ac.rhul.cs.csle.art.v3.alg.gll.support;

import java.io.PrintWriter;

import uk.ac.rhul.cs.csle.art.util.graph.ARTAbstractGraph;
import uk.ac.rhul.cs.csle.art.util.graph.ARTTreeVertex;
import uk.ac.rhul.cs.csle.art.util.text.ARTText;

public class ARTGLLRDTVertex extends ARTTreeVertex {

  public ARTGLLRDTVertex(Integer key, ARTGLLRDTPayload payload) {
    super(key, payload);
  }

  public ARTGLLRDTVertex addChild(Integer key, ARTGLLRDTPayload payload) {
    // System.err.printf("Adding tree node (" + key + " ," + label + ") to node (" + this.key + ", " + this.label + ")\n");
    if (child == null)
      return (ARTGLLRDTVertex) (child = new ARTGLLRDTVertex(key, payload));
    else {
      ARTGLLRDTVertex tmp = (ARTGLLRDTVertex) child;
      while (tmp.sibling != null)
        tmp = (ARTGLLRDTVertex) tmp.sibling;

      return (ARTGLLRDTVertex) (tmp.sibling = new ARTGLLRDTVertex(key, payload));
    }
  }

  private String suppressPrefix(String s) {
    if (s.startsWith("ART."))
      return s.substring(4);
    else
      return s;
  }

  @Override
  public String toString() {
    return key.toString() + " " + (payload);
  }

  public String toString(ARTAbstractGraph tree) {
    return key.toString() + " " + ((ARTGLLRDTPayload) payload).toString(tree);
  }

  @Override
  public void printDot(ARTAbstractGraph tree, PrintWriter printWriter) {
    printWriter.print("\n\"" + key + "\"  [label=\"" + ARTText.toLiteralString(key.toString()) + ": " // Uncomment for node number
        + ARTText.toLiteralString(payload == null ? "null" : suppressPrefix(((ARTGLLRDTPayload) payload).toString(tree))) + "\"]");
    // Now visit the children
    for (ARTGLLRDTVertex target = (ARTGLLRDTVertex) child; target != null; target = (ARTGLLRDTVertex) target.sibling) {
      printWriter.print("\n\"" + key + "\"->\"" + target.getKey() + "\"");
      target.printDot(tree, printWriter);
    }
  }

  @Override
  public ARTGLLRDTPayload getPayload() {
    return (ARTGLLRDTPayload) payload;
  }

  @Override
  public ARTGLLRDTVertex getChild() {
    return (ARTGLLRDTVertex) child;
  }

  @Override
  public ARTGLLRDTVertex getSibling() {
    return (ARTGLLRDTVertex) sibling;
  }

  public ARTGLLRDTVertex getRightmostChild() {
    if (child == null) return null;
    for (ARTGLLRDTVertex tmp = (ARTGLLRDTVertex) child;; tmp = (ARTGLLRDTVertex) child.getSibling())
      if (tmp.getSibling() == null) return tmp;
  }
}
