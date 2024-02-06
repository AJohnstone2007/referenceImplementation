package uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.support;

public class ARTChiBSR {
  private final int hashcode, chiSetIndex, i, k, j;

  @Override
  public String toString() {
    return "(" + chiSetIndex + ", " + i + ", " + k + ", " + j + ")";
  }

  @Override
  public int hashCode() { // ExtendedPackNodes never change, so the constructor computes a once and for ever hash code
    return hashcode;
  }

  public int computeHashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + chiSetIndex;
    result = prime * result + i;
    result = prime * result + j;
    result = prime * result + k;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTChiBSR)) return false;
    ARTChiBSR other = (ARTChiBSR) obj;
    if (hashcode != other.hashcode) return false; // Check hashcode first, because unless there is a collision this will subsum the next four tests
    if (chiSetIndex != other.chiSetIndex) return false;
    if (i != other.i) return false;
    if (j != other.j) return false;
    if (k != other.k) return false;
    return true;
  }

  public ARTChiBSR(int chiSetIndex, int i, int k, int j) {
    super();
    this.chiSetIndex = chiSetIndex;
    this.i = i;
    this.k = k;
    this.j = j;
    this.hashcode = computeHashCode();
  }

  public int getChiSetIndex() {
    return chiSetIndex;
  }

  public int getI() {
    return i;
  }

  public int getJ() {
    return j;
  }

  public int getK() {
    return k;
  }

}
