package uk.ac.rhul.cs.csle.art.old.util.sppflinked;

import java.util.HashMap;

/*
 * This class provides support for SPPF's in various forms...
 */

import java.util.Map;

import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstance;

public class ARTSPPFMapped extends ARTSPPF {
  private final Map<ARTSPPFMappedNode, ARTSPPFMappedNode> map = new HashMap<>();
  private final ARTGrammar grammar;

  public ARTSPPFMapped(ARTGrammar grammar) {
    super();
    this.grammar = grammar;
  }

  @Override
  void resetSelected() {
    for (ARTSPPFMappedNode s : map.keySet())
      map.get(s).setSelected(false);
  }

  @Override
  void resetSuppressed() {
    for (ARTSPPFMappedNode s : map.keySet())
      map.get(s).setSuppressed(false);
  }

  @Override
  void resetVisited() {
    for (ARTSPPFMappedNode s : map.keySet())
      map.get(s).setVisited(false);
  }

  @Override
  ARTSPPFMappedNode find(ARTGrammarInstance instance, int leftExtent, int pivot, int rightExtent) {
    ARTSPPFMappedNode key = new ARTSPPFMappedNode(instance, leftExtent, rightExtent), ret;

    if ((ret = map.get(key)) == null) map.put(key, ret = key);
    ret.addPivot(pivot);
    return ret;
  }

  @Override
  ARTSPPFMappedNode find(ARTGrammarInstance instance, int leftExtent, int rightExtent) {
    ARTSPPFMappedNode key = new ARTSPPFMappedNode(instance, leftExtent, rightExtent), ret;

    if ((ret = map.get(key)) == null) map.put(key, ret = key);
    return ret;
  }

}
