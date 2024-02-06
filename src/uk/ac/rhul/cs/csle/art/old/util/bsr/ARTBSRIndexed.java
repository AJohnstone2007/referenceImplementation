package uk.ac.rhul.cs.csle.art.old.util.bsr;

public class ARTBSRIndexed {
  final private int instance, i, j, k;

  public ARTBSRIndexed(int instance, int i, int j, int k) {
    this.instance = instance;
    this.i = i;
    this.j = j;
    this.k = k;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + i;
    result = prime * result + instance;
    result = prime * result + j;
    result = prime * result + k;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTBSRIndexed)) return false;
    ARTBSRIndexed other = (ARTBSRIndexed) obj;
    if (i != other.i) return false;
    if (instance != other.instance) return false;
    if (j != other.j) return false;
    if (k != other.k) return false;
    return true;
  }

  public int getInstance() {
    return instance;
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
