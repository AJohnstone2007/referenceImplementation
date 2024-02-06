package uk.ac.rhul.cs.csle.art.old.v3.alg.earley.linkedapi;

import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceSlot;

public class ARTEarleySPPFNodeIntermediate extends ARTEarleySPPFNode {
  ARTGrammarInstanceSlot label;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((label == null) ? 0 : label.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (!(obj instanceof ARTEarleySPPFNodeIntermediate))
      return false;
    ARTEarleySPPFNodeIntermediate other = (ARTEarleySPPFNodeIntermediate) obj;
    if (label == null) {
      if (other.label != null)
        return false;
    } else if (!label.equals(other.label))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "(" + label + ", " + leftExtent + ", " + rightExtent + ")";
  }

  public ARTEarleySPPFNodeIntermediate(ARTGrammarInstanceSlot d, int leftExtent, int rightExtent) {
    this.label = d;
    this.leftExtent = leftExtent;
    this.rightExtent = rightExtent;
  }
}
