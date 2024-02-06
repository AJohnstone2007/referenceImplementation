package uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.linkedapi;

import java.util.LinkedList;

// This is a wrapper class so that we can have an array of queues

public class ARTEarleyConfigurationQueue {
  @Override
  public String toString() {
    return list.toString();
  }

  LinkedList<ARTEarleyConfiguration> list = new LinkedList<>();

  void add(ARTEarleyConfiguration configuration) {
    list.addLast(configuration);
  }

  ARTEarleyConfiguration remove() {
    return list.remove();
  }

  public boolean isEmpty() {
    return list.isEmpty();
  }

  public LinkedList<ARTEarleyConfiguration> getList() {
    return list;
  }
}