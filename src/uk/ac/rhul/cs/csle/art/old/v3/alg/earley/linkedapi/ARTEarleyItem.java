package uk.ac.rhul.cs.csle.art.old.v3.alg.earley.linkedapi;

import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceSlot;

public class ARTEarleyItem {
  public ARTGrammarInstanceSlot slot;
  public int i;
  public ARTEarleySPPFNode sppfNode;

  @Override
  public String toString() {
    return "(" + slot + ", " + i + ", " + sppfNode + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + i;
    result = prime * result + ((slot == null) ? 0 : slot.hashCode());
    result = prime * result + ((sppfNode == null) ? 0 : sppfNode.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTEarleyItem)) return false;
    ARTEarleyItem other = (ARTEarleyItem) obj;
    if (i != other.i) return false;
    if (slot == null) {
      if (other.slot != null) return false;
    } else if (!slot.equals(other.slot)) return false;
    if (sppfNode == null) {
      if (other.sppfNode != null) return false;
    } else if (!sppfNode.equals(other.sppfNode)) return false;
    return true;
  }

  public ARTEarleyItem(ARTGrammarInstanceSlot slot, int i, ARTEarleySPPFNode y) {
    this.slot = slot;
    this.i = i;
    this.sppfNode = y;
  }

}
