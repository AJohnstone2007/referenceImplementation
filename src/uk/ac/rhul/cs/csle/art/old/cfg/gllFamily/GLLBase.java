package uk.ac.rhul.cs.csle.art.old.cfg.gllFamily;

import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;

/*
 *
 * Superclass for all GLL family algorithms
 *
 * This is an abstract class, but contains many concrete methods so as to relieve sub classes of the implemntation demand
 *
 * We take the union over all of the templates and support functions for our algorithms, and create an error-reporting method here
 *
 * For each algorithm/data structure combination, we make a derived concrete class that overrides the superclass as appropriate
 *
 * This is essentially the same implementation strategy as we use in the value package
 *
 */
public class GLLBase {

//@formatter:off
  int sppfLookup(int label, int leftExtent, int rightExtent)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int sppfFind(int label, int leftChild, int rightChild)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int sppfFindClosure(int parentLabel, int childLabel, int currentTokenIndex)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int sppfFindBaseNode(int parentLabel, int childLabel, int currentTokenIndex)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int sppfFindInitial(int label, int leftExtent, int rightExtent)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int sppfFindTerminal(int label, int currentTokenIndex)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int sppfFindTerminal(int label, int currentTokenIndex, int tokenRightExtent)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int sppfFindEpsilon(int currentTokenIndex)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int sppfFindRightmostTerminalNode()  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }

  int sppfNodeLabel(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int sppfNodeLeftExtent(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int sppfNodeRightExtent(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int sppfNodePackedNodeList(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }

  int sppfNodeArity(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int sppfNodeFirst()  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int sppfNodeNext()  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }

  boolean sppfNodeVisited(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  void sppfNodeResetVisited(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  void sppfNodeSetVisited(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }

  int sppfPackedNodeParent(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int sppfPackedNodeLabel(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int sppfPackedNodePivot(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int sppfPackedNodePackedNodeList(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int sppfPackedNodeLeftChildLabel(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int sppfPackedNodeRightChildLabel(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }

  boolean sppfPackedNodeSuppressed(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  void sppfPackedNodeResetSuppressed(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  void sppfPackedNodeSetSuppressed(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }

  boolean sppfPackedNodeSelected(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  void sppfPackedNodeResetSelected(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  void sppfPackedNodeSetSelected(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }

  int gssFind(int stackTopLabel, int stackTop, int currentToken, int currentsppfNode)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int gssNodeLabel(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int gssNodeLevel(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int gssNodeEdgeList(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }

  int gssEdgeSource(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int gssEdgeDestination(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int gssEdgeSPPFNode(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  int gssEdgeEdgeList(int element)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }

  void descriptorFind(int restartLabel, int gssNode, int currentTokenIndex, int sppfNode)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  void descriptorUnload()  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  boolean descriptorAvailable()  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }

  boolean testRepeat(int regexpLabel, int stackTop, int currentTokenIndex, int derivationNode)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  void pop(int stackTop, int currentTokenIndex, int currentsppfNode)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  void pop(int nonTerminal, int k, int currentTokenIndex, int currentsppfNode)  { throw new ARTUncheckedException("Unimplemented GLLBase method"); }
  //@formatter:on
  void checkAcceptance() {
    throw new ARTUncheckedException("Unimplemented GLLBase method");
  }

  void parse(int grammarTerm) {

  }
}
