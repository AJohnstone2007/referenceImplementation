package uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.support;

import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstance;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceCat;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceSlot;

// This class is used to support minilamist SPPF's which have been built from BSR sets
//For packed nodes, leftExtent and rightExtent will be -1
//For intermediate nodes, the pivot will be -2
//For symbol nodes, the pivot will be -1
// For epsilon nodes, the pivot will be -1 and the instance will be null

public class ARTBSRSPPFLabel {
  ARTGrammarInstance instance;
  int leftExtent, pivot, rightExtent;

  public ARTBSRSPPFLabel(ARTGrammarInstance instance, int leftExtent, int pivot, int righExtent) {
    this.instance = instance;
    this.leftExtent = leftExtent;
    this.pivot = pivot;
    this.rightExtent = righExtent;
  }

  public ARTBSRSPPFLabel(ARTGrammarInstance instance, int leftExtent, int righExtent) {
    this.instance = instance;
    this.leftExtent = leftExtent;
    this.pivot = -1;
    this.rightExtent = righExtent;
  }

  public ARTBSRSPPFLabel(ARTGrammarInstance instance, int pivot) {
    this.instance = instance;
    this.leftExtent = -1;
    this.pivot = pivot;
    this.rightExtent = -1;
  }

  @Override
  public String toString() {

    if (instance instanceof ARTGrammarInstanceCat) for (ARTGrammarInstance c = instance.getChild(); c != null; c = c.getSibling())
      // Run along to last slot under a cat so as to ensure the dot is printed
      if (c.getSibling() == null) return c.toGrammarString(".") + "  " + (leftExtent == -1 ? pivot : leftExtent + "," + rightExtent);

    if (instance instanceof ARTGrammarInstanceSlot)
      return "[" + instance.toGrammarPrefixString() + " ] " + (leftExtent == -1 ? pivot : leftExtent + "," + rightExtent);

    return instance.toSymbolString() + "  " + (leftExtent == -1 ? pivot : leftExtent + "," + rightExtent);
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
    if (!(obj instanceof ARTBSRSPPFLabel)) return false;
    ARTBSRSPPFLabel other = (ARTBSRSPPFLabel) obj;
    if (instance == null) {
      if (other.instance != null) return false;
    } else if (!instance.equals(other.instance)) return false;
    if (leftExtent != other.leftExtent) return false;
    if (pivot != other.pivot) return false;
    if (rightExtent != other.rightExtent) return false;
    return true;
  }

}
