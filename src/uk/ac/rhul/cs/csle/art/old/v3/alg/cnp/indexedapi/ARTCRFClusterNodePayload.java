package uk.ac.rhul.cs.csle.art.old.v3.alg.cnp.indexedapi;

import java.util.HashSet;
import java.util.Set;

public class ARTCRFClusterNodePayload {

  private final Set<Integer> popSetPartition = new HashSet<>();
  private final Set<ARTCRFLeafNode> leafSet = new HashSet<>();

  public Set<Integer> getPopSetPartition() {
    return popSetPartition;
  }

  public Set<ARTCRFLeafNode> getleafSet() {
    return leafSet;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((popSetPartition == null) ? 0 : popSetPartition.hashCode());
    result = prime * result + ((leafSet == null) ? 0 : leafSet.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTCRFClusterNodePayload)) return false;
    ARTCRFClusterNodePayload other = (ARTCRFClusterNodePayload) obj;
    if (popSetPartition == null) {
      if (other.popSetPartition != null) return false;
    } else if (!popSetPartition.equals(other.popSetPartition)) return false;
    if (leafSet == null) {
      if (other.leafSet != null) return false;
    } else if (!leafSet.equals(other.leafSet)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "( " + popSetPartition + ", " + leafSet + " )";
  }

}
