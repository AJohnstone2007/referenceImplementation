package uk.ac.rhul.cs.csle.art.old.v3.alg.lcnp.linkedapi;

import java.util.HashSet;
import java.util.Set;

public class ARTCRFClusterNodePayload {
  @Override
  public String toString() {
    return "(" + popSetPartition + ", " + leafSet + ")";
  }

  private final Set<Integer> popSetPartition = new HashSet<>();
  private final Set<ARTCRFLeafNode> leafSet = new HashSet<>();

  public Set<Integer> getPopSetPartition() {
    return popSetPartition;
  }

  public Set<ARTCRFLeafNode> getleafSet() {
    return leafSet;
  }
}
