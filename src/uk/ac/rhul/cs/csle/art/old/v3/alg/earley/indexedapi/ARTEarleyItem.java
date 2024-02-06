package uk.ac.rhul.cs.csle.art.old.v3.alg.earley.indexedapi;

import uk.ac.rhul.cs.csle.art.old.util.slotarray.ARTSlotArray;

public class ARTEarleyItem {
  int slot;
  int i;
  ARTEarleySPPFNode sppfNode;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + i;
    result = prime * result + slot;
    result = prime * result + ((sppfNode == null) ? 0 : sppfNode.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof ARTEarleyItem))
      return false;
    ARTEarleyItem other = (ARTEarleyItem) obj;
    if (i != other.i)
      return false;
    if (slot != other.slot)
      return false;
    if (sppfNode == null) {
      if (other.sppfNode != null)
        return false;
    } else if (!sppfNode.equals(other.sppfNode))
      return false;
    return true;
  }

  public ARTEarleyItem(int slot, int i, ARTEarleySPPFNode y) {
    this.slot = slot;
    this.i = i;
    this.sppfNode = y;
  }

  @Override
  public String toString() {
    return "(" + slot + ", " + i + ", " + sppfNode + ")";
  }

  public String toString(ARTSlotArray slotArray) {
    return "(" + slotArray.symbolJavaStrings[slot] + ", " + i + ", "
        + (sppfNode == null ? "null" : sppfNode.toString(slotArray)) + ")";
  }

}
