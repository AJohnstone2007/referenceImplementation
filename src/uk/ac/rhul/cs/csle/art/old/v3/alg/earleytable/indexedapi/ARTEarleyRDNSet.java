package uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.indexedapi;

import java.util.HashSet;

public class ARTEarleyRDNSet {

  // This is a wrapper class so that we can have an array of sets

  HashSet<ARTEarleyRDNSetElement> set = new HashSet<>();

  @Override
  public String toString() {
    return set.toString();
  }

  void add(ARTEarleyRDNSetElement configuration) {
    set.add(configuration);
  }

  public HashSet<ARTEarleyRDNSetElement> getSet() {
    return set;
  }

  public boolean contains(ARTEarleyRDNSetElement tmp) {
    return set.contains(tmp);
  }
}
