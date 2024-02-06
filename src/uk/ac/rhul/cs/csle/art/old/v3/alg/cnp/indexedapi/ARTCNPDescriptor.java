package uk.ac.rhul.cs.csle.art.old.v3.alg.cnp.indexedapi;

// This descriptor contains both grammar slot and integer slot fields for use with the CNP interpreter (which uses grammar slots) or the CNP generated parsers (which use integer slots

public class ARTCNPDescriptor {
  private final int slot;
  private final int k;
  private final int i;

  public int getSlot() {
    return slot;
  }

  public int getI() {
    return i;
  }

  public int getK() {
    return k;
  }

  public ARTCNPDescriptor(int slot, int k, int i) {
    this.slot = slot;
    this.i = i;
    this.k = k;
  }

  @Override
  public String toString() {
    return "(" + slot + ", " + i + ", " + k + ")";
  }

  public String toString(String[] slotStrings) {
    return "(" + slotStrings[slot] + ", " + i + ", " + k + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + i;
    result = prime * result + k;
    result = prime * result + slot;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTCNPDescriptor)) return false;
    ARTCNPDescriptor other = (ARTCNPDescriptor) obj;
    if (i != other.i) return false;
    if (k != other.k) return false;
    if (slot != other.slot) return false;
    return true;
  }
}
