package uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.linkedapi;

import java.util.HashSet;

// This is a wrapper class so that we can have an array of sets

public class ARTEarleyConfigurationSet {
  HashSet<ARTEarleyConfiguration> set = new HashSet<>();

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("{");
    builder.append(set);
    builder.append("}");
    return builder.toString();
  }

  void add(ARTEarleyConfiguration configuration) {
    set.add(configuration);
  }

  public HashSet<ARTEarleyConfiguration> getSet() {
    return set;
  }

  public boolean contains(ARTEarleyConfiguration tmp) {
    return set.contains(tmp);
  }
}
