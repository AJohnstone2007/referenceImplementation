package uk.ac.rhul.cs.csle.art.old.util.bitset;

import java.util.BitSet;

public class ARTBitSet extends BitSet {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ARTBitSet(int i) {
    super(i);
  }

  public ARTBitSet() {
    super();
  }

  public void setAll(int... elements) {
    for (int e : elements)
      set(e);
  }

}
