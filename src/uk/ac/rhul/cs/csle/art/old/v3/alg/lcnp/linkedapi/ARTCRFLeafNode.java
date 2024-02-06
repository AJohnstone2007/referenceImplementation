package uk.ac.rhul.cs.csle.art.old.v3.alg.lcnp.linkedapi;

import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceSlot;

public class ARTCRFLeafNode {
  @Override
  public String toString() {
    return "(" + slot + ", " + h + ")";
  }

  private final ARTGrammarInstanceSlot slot;
  private final int h;

  public ARTGrammarInstanceSlot getSlot() {
    return slot;
  }

  public int getH() {
    return h;
  }

  public ARTCRFLeafNode(ARTGrammarInstanceSlot slot, int h) {
    this.slot = slot;
    this.h = h;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + h;
    result = prime * result + ((slot == null) ? 0 : slot.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTCRFLeafNode)) return false;
    ARTCRFLeafNode other = (ARTCRFLeafNode) obj;
    if (h != other.h) return false;
    if (slot == null) {
      if (other.slot != null) return false;
    } else if (!slot.equals(other.slot)) return false;
    return true;
  }
}
