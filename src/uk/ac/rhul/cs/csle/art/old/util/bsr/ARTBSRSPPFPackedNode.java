package uk.ac.rhul.cs.csle.art.old.util.bsr;

import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstance;

public class ARTBSRSPPFPackedNode {
  ARTGrammarInstance instance;
  int leftExtent;
  int pivot;
  int rightExtent;

  @Override
  public String toString() {
    return "(" + instance.toGrammarString(".") + ", " + leftExtent + ", " + pivot + ", " + rightExtent + ")";
  }

  public ARTBSRSPPFPackedNode(ARTGrammarInstance instance, int leftExtent, int pivot, int rightExtent) {
    this.instance = instance;
    this.leftExtent = leftExtent;
    this.pivot = pivot;
    this.rightExtent = rightExtent;
  }

  public ARTGrammarInstance getInstance() {
    return instance;
  }

  public int getLeftExtent() {
    return leftExtent;
  }

  public int getPivot() {
    return pivot;
  }

  public int getRightExtent() {
    return rightExtent;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((instance == null) ? 0 : instance.hashCode());
    result = prime * result + leftExtent;
    result = prime * result + pivot;
    result = prime * result + rightExtent;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTBSRSPPFPackedNode)) return false;
    ARTBSRSPPFPackedNode other = (ARTBSRSPPFPackedNode) obj;
    if (instance == null) {
      if (other.instance != null) return false;
    } else if (!instance.equals(other.instance)) return false;
    if (leftExtent != other.leftExtent) return false;
    if (pivot != other.pivot) return false;
    if (rightExtent != other.rightExtent) return false;
    return true;
  }

}
