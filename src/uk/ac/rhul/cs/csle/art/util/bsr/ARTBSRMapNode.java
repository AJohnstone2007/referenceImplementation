package uk.ac.rhul.cs.csle.art.util.bsr;

import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstance;

public class ARTBSRMapNode {

  private final ARTGrammarInstance instance;
  private final int leftExtent;
  private final int rightExtent;
  private final Set<Integer> pivots = new HashSet<>();

  private boolean suppressed = false;
  private boolean selected = false;
  private boolean visited = false;

  public ARTBSRMapNode(ARTGrammarInstance instance, int leftExtent, int rightExtent) {
    this.instance = instance;
    this.leftExtent = leftExtent;
    this.rightExtent = rightExtent;
  }

  public void addPivot(int k) {
    pivots.add(k);
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

  public boolean isSuppressed() {
    return suppressed;
  }

  public void setSuppressed(boolean suppressed) {
    this.suppressed = suppressed;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public boolean isVisited() {
    return visited;
  }

  public void setVisited(boolean visited) {
    this.visited = visited;
  }

  public ARTGrammarInstance getInstance() {
    return instance;
  }

  public int getLeftExtent() {
    return leftExtent;
  }

  public int getRightExtent() {
    return rightExtent;
  }

  public Set<Integer> getPivots() {
    return pivots;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTBSRMapNode)) return false;
    ARTBSRMapNode other = (ARTBSRMapNode) obj;
    if (instance == null) {
      if (other.instance != null) return false;
    } else if (!instance.equals(other.instance)) return false;
    if (leftExtent != other.leftExtent) return false;
    if (rightExtent != other.rightExtent) return false;
    return true;
  }

  @Override
  public String toString() {
    return "<" + instance.toGrammarString(".") + ", " + leftExtent + ", " + pivots + ", " + rightExtent + ">";
  }

}
