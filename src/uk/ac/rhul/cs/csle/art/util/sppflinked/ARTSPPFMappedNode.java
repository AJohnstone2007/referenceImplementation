package uk.ac.rhul.cs.csle.art.util.sppflinked;

import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstance;

public class ARTSPPFMappedNode extends ARTSPPFNode {

  private final ARTGrammarInstance instance;
  private final int leftExtent;
  private final int rightExtent;
  private final Set<Integer> pivots = new HashSet<>();

  private boolean suppressed = false;
  private boolean selected = false;
  private boolean visited = false;

  public ARTSPPFMappedNode(ARTGrammarInstance instance, int leftExtent, int rightExtent) {
    this.instance = instance;
    this.leftExtent = leftExtent;
    this.rightExtent = rightExtent;
  }

  @Override
  public void addPivot(int k) {
    pivots.add(k);
  }

  @Override
  public boolean isSuppressed() {
    return suppressed;
  }

  @Override
  public void setSuppressed(boolean suppressed) {
    this.suppressed = suppressed;
  }

  @Override
  public boolean isSelected() {
    return selected;
  }

  @Override
  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  @Override
  public boolean isVisited() {
    return visited;
  }

  @Override
  public void setVisited(boolean visited) {
    this.visited = visited;
  }

  @Override
  public ARTGrammarInstance getInstance() {
    return instance;
  }

  @Override
  public int getLeftExtent() {
    return leftExtent;
  }

  @Override
  public int getRightExtent() {
    return rightExtent;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((instance == null) ? 0 : instance.hashCode());
    result = prime * result + leftExtent;
    result = prime * result + rightExtent;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ARTSPPFMappedNode other = (ARTSPPFMappedNode) obj;
    if (instance == null) {
      if (other.instance != null) return false;
    } else if (!instance.equals(other.instance)) return false;
    if (leftExtent != other.leftExtent) return false;
    if (rightExtent != other.rightExtent) return false;
    return true;
  }

  public Set<Integer> getPivots() {
    return pivots;
  }

  @Override
  public String toString() {
    return "<" + instance.toGrammarString(".") + ", " + leftExtent + ", " + pivots + ", " + rightExtent + ">";
  }

}
