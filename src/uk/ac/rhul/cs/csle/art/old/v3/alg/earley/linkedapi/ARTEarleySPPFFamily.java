package uk.ac.rhul.cs.csle.art.old.v3.alg.earley.linkedapi;

public class ARTEarleySPPFFamily {
  public final ARTEarleySPPFNode left;
  public final ARTEarleySPPFNode right;

  public ARTEarleySPPFFamily(ARTEarleySPPFNode left, ARTEarleySPPFNode right) {
    this.left = left;
    this.right = right;
  }

  public ARTEarleySPPFFamily(ARTEarleySPPFNode right) {
    this.left = null;
    this.right = right;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((left == null) ? 0 : left.hashCode());
    result = prime * result + ((right == null) ? 0 : right.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTEarleySPPFFamily)) return false;
    ARTEarleySPPFFamily other = (ARTEarleySPPFFamily) obj;
    if (left == null) {
      if (other.left != null) return false;
    } else if (!left.equals(other.left)) return false;
    if (right == null) {
      if (other.right != null) return false;
    } else if (!right.equals(other.right)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "(" + left + ", " + right + ")";
  }

}
