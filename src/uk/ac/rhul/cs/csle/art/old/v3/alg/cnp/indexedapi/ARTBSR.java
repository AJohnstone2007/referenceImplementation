package uk.ac.rhul.cs.csle.art.old.v3.alg.cnp.indexedapi;

public class ARTBSR {
  private int slot;
  final private int i, j, k;

  public ARTBSR(int instance, int i, int j, int k) {
    this.slot = instance;
    this.i = i;
    this.j = j;
    this.k = k;
  }

  public int getInstance() {
    return slot;
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

  public ARTBSR setSlotAndReturnThis(int c) {
    slot = c;
    return this;
  }

  @Override
  public String toString() {
    return "<" + slot + ", " + i + ", " + k + ", " + j + ">";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + i;
    result = prime * result + j;
    result = prime * result + k;
    result = prime * result + slot;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTBSR)) return false;
    ARTBSR other = (ARTBSR) obj;
    if (i != other.i) return false;
    if (j != other.j) return false;
    if (k != other.k) return false;
    if (slot != other.slot) return false;
    return true;
  }
}
