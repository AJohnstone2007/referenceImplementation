package uk.ac.rhul.cs.csle.art.old.v3.alg.earley.linkedapi;

import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElement;

public class ARTEarleySPPFNodeSymbol extends ARTEarleySPPFNode {
  ARTGrammarElement label;

  @Override
  public String toString() {
    return "(" + label + ", " + leftExtent + ", " + rightExtent + ")";
  }

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
    if (!(obj instanceof ARTEarleySPPFNodeSymbol))
      return false;
    ARTEarleySPPFNodeSymbol other = (ARTEarleySPPFNodeSymbol) obj;
    if (label == null) {
      if (other.label != null)
        return false;
    } else if (!label.equals(other.label))
      return false;
    return true;
  }

  public ARTEarleySPPFNodeSymbol(ARTGrammarElement d, int leftExtent, int rightExtent) {
    this.label = d;
    this.leftExtent = leftExtent;
    this.rightExtent = rightExtent;
  }
}
