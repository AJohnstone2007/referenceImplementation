package uk.ac.rhul.cs.csle.art.old.v3.alg.earley.linkedapi;

import java.util.HashSet;
import java.util.Set;

public abstract class ARTEarleySPPFNode {

  protected int leftExtent;
  protected int rightExtent;
  public Set<ARTEarleySPPFFamily> families;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + leftExtent;
    result = prime * result + rightExtent;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTEarleySPPFNode)) return false;
    ARTEarleySPPFNode other = (ARTEarleySPPFNode) obj;
    if (leftExtent != other.leftExtent) return false;
    if (rightExtent != other.rightExtent) return false;
    return true;
  }

  public void addFamily(ARTEarleySPPFNode right) {
    if (families == null) families = new HashSet<>();

    families.add(new ARTEarleySPPFFamily(right));
  }

  public void addFamily(ARTEarleySPPFNode left, ARTEarleySPPFNode right) {
    if (families == null) families = new HashSet<>();

    families.add(new ARTEarleySPPFFamily(left, right));
  }
}