package uk.ac.rhul.cs.csle.art.util.sppflinked;

import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstance;

public abstract class ARTSPPF {

  abstract void resetSelected();

  abstract void resetSuppressed();

  abstract void resetVisited();

  abstract ARTSPPFNode find(ARTGrammarInstance instance, int leftExtent, int pivot, int rightExtent);

  abstract ARTSPPFNode find(ARTGrammarInstance instance, int leftExtent, int rightExtent);
}
