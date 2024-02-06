package uk.ac.rhul.cs.csle.art.old.v3.alg.cnp.indexedapi;

public class ARTCRFLeafNode {
  @Override
  public String toString() {
    return "(" + slot + ", " + h + ")";
  }

  private final int slot;
  private final int h;

  public ARTCRFLeafNode(int slot, int h) {
    this.slot = slot;
    this.h = h;
  }

  public int getSlot() {
    return slot;
  }

  public int getH() {
    return h;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + h;
    result = prime * result + slot;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTCRFLeafNode)) return false;
    ARTCRFLeafNode other = (ARTCRFLeafNode) obj;
    if (h != other.h) return false;
    if (slot != other.slot) return false;
    return true;
  }

}
