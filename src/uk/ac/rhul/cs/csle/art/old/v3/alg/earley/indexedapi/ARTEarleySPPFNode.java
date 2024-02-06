package uk.ac.rhul.cs.csle.art.old.v3.alg.earley.indexedapi;

import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.util.slotarray.ARTSlotArray;

public class ARTEarleySPPFNode {

  int label;
  int leftExtent;
  int rightExtent;
  Set<ARTEarleySPPFFamily> families;

  public ARTEarleySPPFNode(int label, int leftExtent, int rightExtent) {
    this.label = label;
    this.leftExtent = leftExtent;
    this.rightExtent = rightExtent;
  }

  public String toString(String[] symbolStrings) {
    return "(" + symbolStrings[label] + " " + leftExtent + "," + rightExtent + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + label;
    result = prime * result + leftExtent;
    result = prime * result + rightExtent;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof ARTEarleySPPFNode))
      return false;
    ARTEarleySPPFNode other = (ARTEarleySPPFNode) obj;
    if (label != other.label)
      return false;
    if (leftExtent != other.leftExtent)
      return false;
    if (rightExtent != other.rightExtent)
      return false;
    return true;
  }

  public void addFamily(ARTEarleySPPFNode right) {
    if (families == null)
      families = new HashSet<>();

    families.add(new ARTEarleySPPFFamily(right));
  }

  public void addFamily(ARTEarleySPPFNode left, ARTEarleySPPFNode right) {
    if (families == null)
      families = new HashSet<>();

    families.add(new ARTEarleySPPFFamily(left, right));
  }

  @Override
  public String toString() {
    return "(" + label + ", " + leftExtent + ", " + rightExtent + ")";
  }

  public String toString(ARTSlotArray slotArray) {
    return "(" + slotArray.symbolJavaStrings[label] + ", " + leftExtent + ", " + rightExtent + ")";
  }
}