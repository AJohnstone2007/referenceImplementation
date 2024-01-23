package uk.ac.rhul.cs.csle.art.util.sppflinked;

import java.util.Set;

import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstance;

public abstract class ARTSPPFNode {

  abstract public void addPivot(int k);

  abstract public boolean isSuppressed();

  abstract public void setSuppressed(boolean suppressed);

  abstract public boolean isSelected();

  abstract public void setSelected(boolean selected);

  abstract public boolean isVisited();

  abstract public void setVisited(boolean visited);

  abstract public ARTGrammarInstance getInstance();

  abstract public int getLeftExtent();

  abstract public int getRightExtent();

  abstract public Set<Integer> getPivots();

}
