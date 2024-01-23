package uk.ac.rhul.cs.csle.art.v3.manager.grammar;

import uk.ac.rhul.cs.csle.art.util.bitset.ARTBitSet;

public class ARTChooserSet {

  public final ARTBitSet[] higher;
  public final ARTBitSet[] longer;
  public final ARTBitSet[] shorter;

  public ARTChooserSet(int size) {
    super();
    higher = new ARTBitSet[size];
    longer = new ARTBitSet[size];
    shorter = new ARTBitSet[size];
  }

  public boolean empty() {
    for (int i = 0; i < higher.length; i++)
      if (higher[i] != null && higher[i].cardinality() != 0) return false;
    for (int i = 0; i < longer.length; i++)
      if (longer[i] != null && longer[i].cardinality() != 0) return false;
    for (int i = 0; i < shorter.length; i++)
      if (shorter[i] != null && shorter[i].cardinality() != 0) return false;
    return true;
  }

}
