package uk.ac.rhul.cs.csle.art.old.v3.alg.gll.support;

import uk.ac.rhul.cs.csle.art.old.util.graph.ARTAbstractGraph;

public class ARTGLLRDTPayload {
  public int leftExtent, rightExtent;
  public int label;
  public final ARTGLLAttributeBlock attributes;

  public ARTGLLRDTPayload(ARTGLLRDT tree, int leftExtent, int rightExtent, int label) {
    this(tree, leftExtent, rightExtent, label, null);
  }

  public ARTGLLRDTPayload(ARTGLLRDT tree, int leftExtent, int rightExtent, int label, ARTGLLAttributeBlock attributes) {
    super();
    this.leftExtent = leftExtent;
    this.rightExtent = rightExtent;
    this.label = label;
    this.attributes = attributes;
  }

  public int getLeftExtent() {
    return leftExtent;
  }

  public int getRightExtent() {
    return rightExtent;
  }

  public int getLabel() {
    return label;
  }

  public ARTGLLAttributeBlock getAttributes() {
    return attributes;
  }

  @Override
  public String toString() {
    return "(" + label + ":" + leftExtent + "," + rightExtent + ")";
  }

  public String toStringNoAttributes(ARTAbstractGraph graph) {
    ARTGLLRDT tree = (ARTGLLRDT) graph;
    String ret = ""; // "(" + leftExtent + ":" + rightExtent + " " + label + ") ";
    if (tree.artGetLabelKind(label) == ARTGLLParserBase.ARTK_EPSILON)
      ret += "#";
    else if (tree.artGetLabelKind(label) == ARTGLLParserBase.ARTK_BUILTIN_TERMINAL)
      ret += (/* "&" + tree.artGetLabelString(label) + " " + */ tree.artLexeme(leftExtent, rightExtent).trim());
    else if (tree.artGetLabelKind(label) == ARTGLLParserBase.ARTK_CHARACTER_TERMINAL)
      ret += ("`" + tree.artGetLabelString(label));
    else if (tree.artGetLabelKind(label) == ARTGLLParserBase.ARTK_CASE_SENSITIVE_TERMINAL)
      ret += ("'" + tree.artGetLabelString(label) + "'");
    else if (tree.artGetLabelKind(label) == ARTGLLParserBase.ARTK_CASE_INSENSITIVE_TERMINAL)
      ret += ("\"" + tree.artGetLabelString(label) + "\"");
    else if (tree.artGetLabelKind(label) == ARTGLLParserBase.ARTK_NONTERMINAL)
      ret += (tree.artGetLabelString(label));
    else
      ret += "unknown RDT payload kind";

    return ret;
  }

  public String toString(ARTAbstractGraph graph) {
    String ret = this.toStringNoAttributes(graph);

    if (attributes != null) ret += " <" + attributes.toString() + ">";

    return ret;
  }
}
