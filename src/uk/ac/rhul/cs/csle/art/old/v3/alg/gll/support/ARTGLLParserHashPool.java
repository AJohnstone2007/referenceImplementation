/**
 *
 *
 * In this version we suppress type information and treat all of our objects as
 * indices into an integer array. The goal is to achieve high performance, or at
 * least as high as Java might allow.
 *
 * All values are represented as type int.
 *
 * (c) Adrian Johnstone 2013
 */
package uk.ac.rhul.cs.csle.art.old.v3.alg.gll.support;

import uk.ac.rhul.cs.csle.art.old.util.histogram.ARTHistogram;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTTextLevel;
import uk.ac.rhul.cs.csle.art.old.v3.lex.ARTLexerV3;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTGrammar;

public abstract class ARTGLLParserHashPool extends ARTGLLParserBase {

  /**
   * Constant field offsets for each kind
   */

  // Offset zero is link to next in hash chain
  protected static final int artsppfNode_label = 1; // Negative if visited
  protected static final int artsppfNode_leftExtent = 2;
  protected static final int artsppfNode_rightExtent = 3;
  protected static final int artsppfNode_packNodeList = 4; // Negative if selected
  protected static final int artsppfNode_SIZE = 5;

  // Offset zero is link to next in hash chain
  protected static final int artsppfPackedNode_parent = 1;
  protected static final int artsppfPackedNode_label = 2; // Negative if visited
  protected static final int artsppfPackedNode_pivot = 3;
  protected static final int artsppfPackedNode_leftChildLabel = 4; // Negative if suppressed
  protected static final int artsppfPackedNode_rightChildLabel = 5; // Negative if selected
  protected static final int artsppfPackedNode_packNodeList = 6;
  protected static final int artsppfPackedNode_SIZE = 7;

  // Offset zero is link to next in hash chain
  protected static final int artgssNode_label = 1;
  protected static final int artgssNode_level = 2;
  protected static final int artgssNode_edgeList = 3;
  protected static final int artgssNode_pop_clusterElementList = 4;
  protected static final int artgssNode_SIZE = 5;

  // Offset zero is link to next in hash chain
  protected static final int artgssEdge_sppfNode = 1;
  protected static final int artgssEdge_source = 2;
  protected static final int artgssEdge_destination = 3;
  protected static final int artgssEdge_edgeList = 4;
  protected static final int artgssEdge_SIZE = 5;
  // stackTop, currentInputPairReference, currentsppfNode
  // Offset zero is link to next in hash chain
  protected static final int artpopElement_gssNode_nonterminal = 1;
  protected static final int artpopElement_currentTokenIndex_MGLLInputPair = 2; // Clustering - is this acually used anywhere?
  protected static final int artpopElement_sppfNode = 3;
  protected static final int artpopElement_popElementList = 4;
  protected static final int artpopElement_SIZE = 5;

  // Offset zero is link to next in hash chain
  protected static final int artdescriptor_label = 1;
  protected static final int artdescriptor_gssNode = 2;
  protected static final int artdescriptor_inputIndex_inputTriple = 3;
  protected static final int artdescriptor_sppfNode = 4;
  protected static final int artdescriptor_descriptorList = 5;
  protected static final int artdescriptor_SIZE = 6;

  // Offset zero is link to next in hash chain
  protected static final int arttestRepeatElement_label = 1;
  protected static final int arttestRepeatElement_gssNode = 2;
  protected static final int arttestRepeatElement_inputIndex = 3;
  protected static final int arttestRepeatElement_sppfNode = 4;
  protected static final int arttestRepeatElement_SIZE = 5;

  // Offset zero is link to next in hash chain
  protected static final int artclusterElement_label = 1; // A nonterminal
  protected static final int artclusterElement_index = 2; // An offset into the string, aka level
  protected static final int artclusterElement_nodeList = 3; // The list of nodes in this cluster
  protected static final int artclusterElement_inEdgeList = 4; // The list of nodes and edge labels that connect to nodes in this cluster
  protected static final int artclusterElement_popList = 5; // The list of SPPF labels on pops that have been done involving this cluster
  protected static final int artclusterElement_SIZE = 6;

  // Offset zero does not need to be reserved since there is no hash table for cluster inedges - the gssEdge table manages uniqueness
  protected static final int artclusterInEdge_source = 0;
  protected static final int artclusterInEdge_sppfNode = 1;
  protected static final int artclusterInEdge_inEdgeList = 2; // The list of nodes and edge labels that connect to nodes in this cluster
  protected static final int artclusterInEdge_SIZE = 3;

  // Offset zero does not need to be reserved since there is no hash table for cluster pops - the popElement table manages uniqueness
  protected static final int artclusterPopElement_sppfNode = 0;
  protected static final int artclusterPopElement_popList = 1;
  protected static final int artclusterPopElement_SIZE = 2;

  public ARTGLLParserHashPool(ARTGrammar artGrammar, ARTLexerV3 artLexerBase) {
    super(artGrammar, artLexerBase);
  }

  /**
   * Getters, setters and iterators
   */
  @Override
  public int artSPPFNodeLabel(int element) {
    return Math.abs(artpoolGet(element + artsppfNode_label));
  } // Negative if visited

  @Override
  public int artSPPFNodeLeftExtent(int element) {
    return artpoolGet(element + artsppfNode_leftExtent);
  }

  @Override
  public int artSPPFNodeRightExtent(int element) {
    return artpoolGet(element + artsppfNode_rightExtent);
  }

  @Override
  public int artSPPFNodePackedNodeList(int element) {
    return Math.abs(artpoolGet(element + artsppfNode_packNodeList));
  } // Negative if selected

  /* Symbol nodes may be visited or selected */
  @Override
  public boolean artSPPFNodeVisited(int element) {
    return artpoolGet(element + artsppfNode_label) < 0;
  }

  @Override
  public void artSPPFNodeResetVisited(int element) {
    artpoolSet(element + artsppfNode_label, Math.abs(artpoolGet(element + artsppfNode_label)));
  }

  @Override
  public void artSPPFNodeSetVisited(int element) {
    artpoolSet(element + artsppfNode_label, -Math.abs(artpoolGet(element + artsppfNode_label)));
  }

  @Override
  public boolean artSPPFNodeSelected(int element) {
    return artpoolGet(element + artsppfNode_packNodeList) <= 0;
  }

  @Override
  public void artSPPFNodeResetSelected(int element) {
    artpoolSet(element + artsppfNode_packNodeList, Math.abs(artpoolGet(element + artsppfNode_packNodeList)));
  }

  @Override
  public void artSPPFNodeSetSelected(int element) {
    artpoolSet(element + artsppfNode_packNodeList, -Math.abs(artpoolGet(element + artsppfNode_packNodeList)));
  }

  /*
   * Iterate over all SPPF nodes independent of linking
   */
  private int artsppfIteratorBucket;
  private int artsppfIteratorElement;

  @Override
  public int artSPPFNodeFirst() {
    artsppfIteratorBucket = -1;
    artsppfIteratorElement = 0;
    return artSPPFNodeNext();
  }

  @Override
  public int artSPPFNodeNext() {
    if (artsppfIteratorElement != 0) artsppfIteratorElement = artpoolGet(artsppfIteratorElement); // Go to next element in chain

    if (artsppfIteratorElement == 0) // Increment past the empty buckets
      do {
        if (++artsppfIteratorBucket >= artsppfNodeBucketCount) return 0;

      } while ((artsppfIteratorElement = artsppfNodeBuckets[artsppfIteratorBucket]) == 0);

    // if (TRACE) arttext.printf(TextLevel.TRACE, "sppfNodeNext returns %d%n", sppfIteratorElement);

    return artsppfIteratorElement;
  }

  @Override
  protected int artFindRightmostTerminalSPPFNode() {
    int rightmostLeftExtent = -1;
    int rightmostSPPFNode = -1;

    for (int element = artSPPFNodeFirst(); element != 0; element = artSPPFNodeNext())
      if (artSPPFNodeLeftExtent(element) >= rightmostLeftExtent && (artKindOfs[artSPPFNodeLabel(element)] == ARTK_BUILTIN_TERMINAL
          || artKindOfs[artSPPFNodeLabel(element)] == ARTK_CHARACTER_TERMINAL || artKindOfs[artSPPFNodeLabel(element)] == ARTK_CASE_SENSITIVE_TERMINAL
          || artKindOfs[artSPPFNodeLabel(element)] == ARTK_CASE_INSENSITIVE_TERMINAL)) {
        rightmostLeftExtent = artSPPFNodeLeftExtent(element);
        rightmostSPPFNode = element;
      }

    return rightmostSPPFNode;
  }

  @Override
  public int artSPPFNodeArity(int element) {
    int arity = 0;
    for (int tmp = artSPPFNodePackedNodeList(element); tmp != 0; tmp = artSPPFPackedNodePackedNodeList(tmp))
      arity++;
    return arity;
  };

  @Override
  public int artSPPFPackedNodeParent(int element) {
    return artpoolGet(element + artsppfPackedNode_parent);
  }

  @Override
  public int artSPPFPackedNodeLabel(int element) {
    return Math.abs(artpoolGet(element + artsppfPackedNode_label));
  } // Negative if visited

  @Override
  public int artSPPFPackedNodePivot(int element) {
    return artpoolGet(element + artsppfPackedNode_pivot);
  }

  @Override
  public int artSPPFPackedNodeLeftChildLabel(int element) {
    return Math.abs(artpoolGet(element + artsppfPackedNode_leftChildLabel));
  } // Negative if suppressed

  @Override
  public int artSPPFPackedNodeRightChildLabel(int element) {
    return Math.abs(artpoolGet(element + artsppfPackedNode_rightChildLabel));
  } // Negative if selected

  @Override
  public int artSPPFPackedNodePackedNodeList(int element) {
    return artpoolGet(element + artsppfPackedNode_packNodeList);
  }

  /* Packed nodes may be suppressed or selected */
  @Override
  public boolean artSPPFPackedNodeSuppressed(int element) {
    return artpoolGet(element + artsppfPackedNode_leftChildLabel) < 0;
  }

  @Override
  public void artSPPFPackedNodeResetSuppressed(int element) {
    artpoolSet(element + artsppfPackedNode_leftChildLabel, Math.abs(artpoolGet(element + artsppfPackedNode_leftChildLabel)));
  }

  @Override
  public void artSPPFPackedNodeSetSuppressed(int element) {
    artpoolSet(element + artsppfPackedNode_leftChildLabel, -Math.abs(artpoolGet(element + artsppfPackedNode_leftChildLabel)));
  }

  @Override
  public boolean artSPPFPackedNodeSelected(int element) {
    return artpoolGet(element + artsppfPackedNode_rightChildLabel) < 0;
  }

  @Override
  public void artSPPFPackedNodeResetSelected(int element) {
    artpoolSet(element + artsppfPackedNode_rightChildLabel, Math.abs(artpoolGet(element + artsppfPackedNode_rightChildLabel)));
  }

  @Override
  public void artSPPFPackedNodeSetSelected(int element) {
    artpoolSet(element + artsppfPackedNode_rightChildLabel, -Math.abs(artpoolGet(element + artsppfPackedNode_rightChildLabel)));
  }

  @Override
  public int artGSSNodeLabel(int element) {
    return Math.abs(artpoolGet(element + artgssNode_label));
  }

  @Override
  public int artGSSNodeLevel(int element) {
    return artpoolGet(element + artgssNode_level);
  }

  @Override
  public int artGSSNodeEdgeList(int element) {
    return artpoolGet(element + artgssNode_edgeList);
  }

  public int artgssNodePopElementList(int element) {
    return artpoolGet(element + artgssNode_pop_clusterElementList);
  }

  @Override
  public int artGSSEdgeSPPFNode(int element) {
    return artpoolGet(element + artgssEdge_sppfNode);
  }

  @Override
  public int artGSSEdgeSource(int element) {
    return artpoolGet(element + artgssEdge_source);
  }

  @Override
  public int artGSSEdgeDestination(int element) {
    return artpoolGet(element + artgssEdge_destination);
  }

  @Override
  public int artGSSEdgeEdgeList(int element) {
    return artpoolGet(element + artgssEdge_edgeList);
  }

  /**
   * Iterate over all GSS nodes independent of linking
   */
  private int artgssNodeIteratorBucket;
  private int artgssNodeIteratorElement;

  @Override
  public int artGSSNodeFirst() {
    artgssNodeIteratorBucket = artgssNodeIteratorElement = 0;
    return artGSSNodeNext();
  }

  @Override
  public int artGSSNodeNext() {
    if (artgssNodeIteratorElement != 0) artgssNodeIteratorElement = artpoolGet(artgssNodeIteratorElement);
    if (artgssNodeIteratorElement == 0) for (artgssNodeIteratorBucket++; artgssNodeIteratorBucket < artgssNodeBucketCount; artgssNodeIteratorBucket++)
      if (artgssNodeBuckets[artgssNodeIteratorBucket] != 0) {
        artgssNodeIteratorElement = artgssNodeBuckets[artgssNodeIteratorBucket];
        break;
      }

    return artgssNodeIteratorElement;
  }

  /* Iterate over all GSS edges independent of linking */
  private int artgssEdgeIteratorBucket;
  private int artgssEdgeIteratorElement;

  @Override
  public int artGSSEdgeFirst() {
    artgssEdgeIteratorBucket = artgssEdgeIteratorElement = 0;
    return artGSSEdgeNext();
  }

  @Override
  public int artGSSEdgeNext() {
    if (artgssEdgeIteratorElement != 0) artgssEdgeIteratorElement = artpoolGet(artgssEdgeIteratorElement);
    if (artgssEdgeIteratorElement == 0) for (artgssEdgeIteratorBucket++; artgssEdgeIteratorBucket < artgssEdgeBucketCount; artgssEdgeIteratorBucket++)
      if (artgssEdgeBuckets[artgssEdgeIteratorBucket] != 0) {
        artgssEdgeIteratorElement = artgssEdgeBuckets[artgssEdgeIteratorBucket];
        break;
      }

    return artgssEdgeIteratorElement;
  }

  /**
   * An expandable pool of integers which supports sequential allocation of small blocks. There is no facility to free memory once it has been allocated.
   *
   * We use a 2-D array of integers organised as an array of pointers to 1-D arrays of integers called poolBlocks. Initially, only one poolBlock is allocated.
   *
   * Further poolBlocks are allocated as needed. If the required number of poolBlocks exceeds the capacity of pool, it is resized by 150%
   */
  protected int[][] artpool;
  private static final int artpoolBlockInitialCount = 1024;

  // poolAddressOffset must be a power of 2
  protected static final int artpoolAddressOffset = 26;
  private static final int artpoolBlockSize = 1 << artpoolAddressOffset;
  protected static final int artpoolAddressMask = artpoolBlockSize - 1;

  private int artpoolBlockCount; // Total number of available pool blocks
  private int artpoolBlockTop; // Current allocation point: block number
  private int artpoolOffsetTop; // Current allocation point: offset

  public int artGetFirstUnusedElement() {
    return (artpoolBlockTop * artpoolBlockSize) + artpoolOffsetTop + 1;
  }

  // The allocation function: check to see if the current poolBlock has enough space; if not make a new one.
  private void artallocate(int size) {
    if (artpoolOffsetTop + size > artpoolBlockSize) { // need new poolBlock

      artpoolBlockTop++;

      if (artTrace > 0) artText.printf(ARTTextLevel.TRACE, "Allocating new pool block %d%n", artpoolBlockTop);

      if (artpoolBlockTop >= artpoolBlockCount) { // resize pointer array
        if (artTrace > 0) artText.printf(ARTTextLevel.TRACE, "Resizing pool%n");

        artpoolBlockCount += artpoolBlockCount / 2;
        int[][] newPool = new int[artpoolBlockCount][];

        for (int i = 0; i < artpoolBlockTop; i++)
          // Copy old pointers
          newPool[i] = artpool[i];

        artpool = newPool;
      }

      // if (pool[poolBlockTop] == null)
      artpool[artpoolBlockTop] = new int[artpoolBlockSize];
      // else
      // for (int i = 0; i < pool[poolBlockTop].length; i++)
      // pool[poolBlockTop][i] = 0;

      artpoolOffsetTop = 0;
    }
    artpoolOffsetTop += size; // Perform the actual allocation
  }

  private void artallocateAndLoad(int allocationSize, int a, int b) {
    if (allocationSize != 0) {
      artallocate(allocationSize);
      artfindOffset = artpoolOffsetTop - allocationSize;
      artfindBlockIndex = artpoolBlockTop;
      artfindBlock = artpool[artfindBlockIndex];
      artfindIndex = artfindBlockIndex << artpoolAddressOffset | artfindOffset;
      artfindLoadOffset = artfindOffset;

      artfindBlock[artfindLoadOffset++] = a;
      artfindBlock[artfindLoadOffset] = b;

      if (artTrace > 0) artText.printf(ARTTextLevel.TRACE, "***,%d,%d,%s%n", artfindIndex, allocationSize, "allocateAndLoad");
    }
  }

  private void artallocateAndLoad(int allocationSize, int a) {
    if (allocationSize != 0) {
      artallocate(allocationSize);
      artfindOffset = artpoolOffsetTop - allocationSize;
      artfindBlockIndex = artpoolBlockTop;
      artfindBlock = artpool[artfindBlockIndex];
      artfindIndex = artfindBlockIndex << artpoolAddressOffset | artfindOffset;
      artfindLoadOffset = artfindOffset;

      artfindBlock[artfindLoadOffset++] = a;

      if (artTrace > 0) artText.printf(ARTTextLevel.TRACE, "***,%d,%d,%s%n", artfindIndex, allocationSize, "allocateAndLoad");
    }
  }

  private int artpoolGet(int index) {
    return artpool[index >> artpoolAddressOffset][index & artpoolAddressMask];
  }

  private void artpoolSet(int index, int value) {
    artpool[index >> artpoolAddressOffset][index & artpoolAddressMask] = value;
  }

  /**
   * Hash tables: array xyBuckets contains the pool index of the first element in each hash list; each element in the hash list must have the pool index of its
   * successor in its first element.
   *
   * Future extension: rehash function when load factor exceeds threshold
   */
  // DescriptorMax data from ansi_c and gtb_src; primes set for load factor 2. This is the experimental setup for the SLE23 paper
  protected int sizeKludge = 1; // Leave this at one for normal operations. Can be used to zoom up the bucket count, but probably loses coprime property
  protected final int artdescriptorMax = 6_578_603;
  protected final int artdescriptorPrime = 13_157_231;
  protected final int artdescriptorBucketInitialCount = artdescriptorPrime * sizeKludge;
  protected int artdescriptorBucketCount;
  protected int artdescriptorBuckets[];

  protected final int artgssNodeMax = 946_975;
  protected final int artgssNodePrime = 1_893_967;
  protected final int artgssNodeBucketInitialCount = artgssNodePrime * sizeKludge;
  protected int artgssNodeBucketCount;
  protected int artgssNodeBuckets[];

  protected final int artgssEdgeMax = 2_989_166;
  protected final int artgssEdgePrime = 5_978_341;
  protected final int artgssEdgeBucketInitialCount = artgssEdgePrime * sizeKludge;
  protected int artgssEdgeBucketCount;
  protected int artgssEdgeBuckets[];

  protected final int artpopElementMax = 776_934;
  protected final int artpopElementPrime = 1_553_869;
  protected final int artpopElementBucketInitialCount = artpopElementPrime * sizeKludge;
  protected int artpopElementBucketCount;
  protected int artpopElementBuckets[];

  protected final int artsppfNodeMax = 881_128;
  protected final int artsppfNodePrime = 1_762_259;
  protected final int artsppfNodeBucketInitialCount = artsppfNodePrime * sizeKludge;
  protected int artsppfNodeBucketCount;
  protected int artsppfNodeBuckets[];

  protected final int artsppfPackedNodeMax = 829_463;
  protected final int artsppfPackedNodePrime = 1_658_927;
  protected final int artsppfPackedNodeBucketInitialCount = artsppfPackedNodePrime * sizeKludge;
  protected int artsppfPackedNodeBucketCount;
  protected int artsppfPackedNodeBuckets[];

  protected final int arttestRepeatElementMax = 829_463;
  protected final int arttestRepeatElementPrime = 1_658_927;
  protected final int arttestRepeatElementBucketInitialCount = arttestRepeatElementPrime * sizeKludge;
  protected int arttestRepeatElementBucketCount;
  protected int arttestRepeatElementBuckets[];

  protected final int artclusterElementMax = 829_463;
  protected final int artclusterElementPrime = 1_658_927;
  protected final int artclusterElementBucketInitialCount = artclusterElementPrime * sizeKludge;
  protected int artclusterElementBucketCount;
  protected int artclusterElementBuckets[];

  private final int arthashPrime = 1013; // A large prime

  private int arthashResult;

  // Knuth style multiplier hash functions for 64-bits
  void arthash(int hashBucketCount, int a, int b) {
    arthashResult = (a + (b * arthashPrime));
    arthashResult %= hashBucketCount;
    if (arthashResult < 0) arthashResult = -arthashResult;
  }

  void arthash(int hashBucketCount, int a, int b, int c) {
    arthashResult = ((a + (b * arthashPrime)) + (c * arthashPrime));
    arthashResult %= hashBucketCount;
    if (arthashResult < 0) arthashResult = -arthashResult;
  }

  void arthash(int hashBucketCount, int a, int b, int c, int d) {
    arthashResult = ((a + (b * arthashPrime)) + (c * arthashPrime) + (d * arthashPrime));
    arthashResult %= hashBucketCount;
    if (arthashResult < 0) arthashResult = -arthashResult;
  }

  /*
   * find functions: pool information is left in these members
   */
  private int artfindIndex; // Combined index
  private int artfindBlockIndex; // top part of index
  private int artfindOffset; // bottom part of index
  private int artfindBlock[]; // reference to block containing this index
  private int artfindLoadOffset; // offset to first unused field

  private boolean artfind(int[] hashBuckets, int hashBucketCount, int allocationSize, int a, int b) {
    arthash(hashBucketCount, a, b);

    artfindIndex = hashBuckets[arthashResult];
    do {
      artfindBlockIndex = artfindIndex >> artpoolAddressOffset;
      artfindOffset = artfindIndex & artpoolAddressMask;
      artfindBlock = artpool[artfindBlockIndex];

      if (a == artfindBlock[artfindOffset + 1] && b == artfindBlock[artfindOffset + 2]) {
        if (artTrace > 0) artfindDiagnostic(hashBuckets, hashBucketCount, allocationSize, a, b, true);
        return true;
      }
      artHashCollisions++;
      artfindIndex = artfindBlock[artfindOffset]; // Step to next
    } while (artfindIndex != 0);

    artHashCollisions--; // If we got to here, then we fell off the end of the chain which will be one more seek than we really want to record
    if (artTrace > 0) artfindDiagnostic(hashBuckets, hashBucketCount, allocationSize, a, b, false);

    if (allocationSize != 0) {
      artallocate(allocationSize);
      artfindOffset = artpoolOffsetTop - allocationSize;
      artfindBlockIndex = artpoolBlockTop;
      artfindBlock = artpool[artfindBlockIndex];
      artfindIndex = artfindBlockIndex << artpoolAddressOffset | artfindOffset;
      artfindLoadOffset = artfindOffset;

      artfindBlock[artfindOffset] = hashBuckets[arthashResult];
      hashBuckets[arthashResult] = artfindIndex;

      artfindBlock[++artfindLoadOffset] = a;
      artfindBlock[++artfindLoadOffset] = b;
    }
    return false;
  }

  private boolean artfind(int[] hashBuckets, int hashBucketCount, int allocationSize, int a, int b, int c) {
    arthash(hashBucketCount, a, b, c);

    artfindIndex = hashBuckets[arthashResult];
    do {
      artfindBlockIndex = artfindIndex >> artpoolAddressOffset;
      artfindOffset = artfindIndex & artpoolAddressMask;
      artfindBlock = artpool[artfindBlockIndex];

      if (a == artfindBlock[artfindOffset + 1] && b == artfindBlock[artfindOffset + 2] && c == artfindBlock[artfindOffset + 3]) {
        if (artTrace > 0) artfindDiagnostic(hashBuckets, hashBucketCount, allocationSize, a, b, true);
        return true;
      }
      artHashCollisions++;
      artfindIndex = artfindBlock[artfindOffset]; // Step to next
    } while (artfindIndex != 0);

    artHashCollisions--; // If we got to here, then we fell off the end of the chain which will be one more seek than we really want to record
    if (artTrace > 0) artfindDiagnostic(hashBuckets, hashBucketCount, allocationSize, a, b, false);

    if (allocationSize != 0) {
      artallocate(allocationSize);
      artfindOffset = artpoolOffsetTop - allocationSize;
      artfindBlockIndex = artpoolBlockTop;
      artfindBlock = artpool[artfindBlockIndex];
      artfindIndex = artfindBlockIndex << artpoolAddressOffset | artfindOffset;
      artfindLoadOffset = artfindOffset;

      artfindBlock[artfindOffset] = hashBuckets[arthashResult];
      hashBuckets[arthashResult] = artfindIndex;

      artfindBlock[++artfindLoadOffset] = a;
      artfindBlock[++artfindLoadOffset] = b;
      artfindBlock[++artfindLoadOffset] = c;
    }
    return false;
  }

  /*
   * This is a version of find(...,a,b,c) that ignores the sign bits for a,b,c when testing for equality
   *
   * It is used by lookupSPPF to ensure that we correctly locate records even when we have been messing with their sign bits
   *
   * WLOG we could use Math.abs in all find functions, but that would be slower, so we use this special version only when needed
   */
  private boolean artfindWithFlags(int[] hashBuckets, int hashBucketCount, int allocationSize, int a, int b, int c) {
    arthash(hashBucketCount, a, b, c);

    artfindIndex = hashBuckets[arthashResult];
    do {
      artfindBlockIndex = artfindIndex >> artpoolAddressOffset;
      artfindOffset = artfindIndex & artpoolAddressMask;
      artfindBlock = artpool[artfindBlockIndex];

      if (Math.abs(a) == Math.abs(artfindBlock[artfindOffset + 1]) && Math.abs(b) == Math.abs(artfindBlock[artfindOffset + 2])
          && Math.abs(c) == Math.abs(artfindBlock[artfindOffset + 3])) {
        if (artTrace > 0) artfindDiagnostic(hashBuckets, hashBucketCount, allocationSize, a, b, true);
        return true;
      }
      artHashCollisions++;
      artfindIndex = artfindBlock[artfindOffset]; // Step to next
    } while (artfindIndex != 0);

    artHashCollisions--; // If we got to here, then we fell off the end of the chain which will be one more seek than we really want to record
    if (artTrace > 0) artfindDiagnostic(hashBuckets, hashBucketCount, allocationSize, a, b, false);

    if (allocationSize != 0) {
      artallocate(allocationSize);
      artfindOffset = artpoolOffsetTop - allocationSize;
      artfindBlockIndex = artpoolBlockTop;
      artfindBlock = artpool[artfindBlockIndex];
      artfindIndex = artfindBlockIndex << artpoolAddressOffset | artfindOffset;
      artfindLoadOffset = artfindOffset;

      artfindBlock[artfindOffset] = hashBuckets[arthashResult];
      hashBuckets[arthashResult] = artfindIndex;

      artfindBlock[++artfindLoadOffset] = Math.abs(a);
      artfindBlock[++artfindLoadOffset] = Math.abs(b);
      artfindBlock[++artfindLoadOffset] = Math.abs(c);
    }
    return false;
  }

  private boolean artfind(int[] hashBuckets, int hashBucketCount, int allocationSize, int a, int b, int c, int d) {
    arthash(hashBucketCount, a, b, c, d);

    artfindIndex = hashBuckets[arthashResult];
    do {
      artfindBlockIndex = artfindIndex >> artpoolAddressOffset;
      artfindOffset = artfindIndex & artpoolAddressMask;
      artfindBlock = artpool[artfindBlockIndex];

      if (a == artfindBlock[artfindOffset + 1] && b == artfindBlock[artfindOffset + 2] && c == artfindBlock[artfindOffset + 3]
          && d == artfindBlock[artfindOffset + 4]) {
        if (artTrace > 0) artfindDiagnostic(hashBuckets, hashBucketCount, allocationSize, a, b, true);
        return true;
      }
      artHashCollisions++;
      artfindIndex = artfindBlock[artfindOffset]; // Step to next
    } while (artfindIndex != 0);

    artHashCollisions--; // If we got to here, then we fell off the end of the chain which will be one more seek than we really want to record
    if (artTrace > 0) artfindDiagnostic(hashBuckets, hashBucketCount, allocationSize, a, b, false);

    if (allocationSize != 0) {
      artallocate(allocationSize);
      artfindOffset = artpoolOffsetTop - allocationSize;
      artfindBlockIndex = artpoolBlockTop;
      artfindBlock = artpool[artfindBlockIndex];
      artfindIndex = artfindBlockIndex << artpoolAddressOffset | artfindOffset;
      artfindLoadOffset = artfindOffset;

      artfindBlock[artfindOffset] = hashBuckets[arthashResult];
      hashBuckets[arthashResult] = artfindIndex;

      artfindBlock[++artfindLoadOffset] = a;
      artfindBlock[++artfindLoadOffset] = b;
      artfindBlock[++artfindLoadOffset] = c;
      artfindBlock[++artfindLoadOffset] = d;
    }
    return false;
  }

  /**
   * Diagnostic output functions
   */
  void arthashTablePrintSPPFNode(int el) {
    if (el == 0)
      artText.printf(ARTTextLevel.TRACE, "nullElement");
    else {
      artText.printf(ARTTextLevel.TRACE, "(%d, %d, %d)", Math.abs(artpoolGet(el + 1)), artpoolGet(el + 2), artpoolGet(el + 3));
    }
  }

  void arthashTablePrintSPPFPackedNode(int el) {
    if (el == 0)
      artText.printf(ARTTextLevel.TRACE, "nullElement");
    else
      artText.printf(ARTTextLevel.TRACE, "(%s, %d)", getArtLabelInternalStrings()[artpoolGet(el + 2)], artpoolGet(el + 3));
  }

  void arthashTablePrintGSSNode(int el) {
    if (el == 0)
      artText.printf(ARTTextLevel.TRACE, "nullElement");
    else
      artText.printf(ARTTextLevel.TRACE, "(%s, %d)", getArtLabelInternalStrings()[Math.abs(artpoolGet(el + 1))], artpoolGet(el + 2));
  }

  void arthashTablePrintGSSEdge(int el) {
    if (el == 0)
      artText.printf(ARTTextLevel.TRACE, "nullElement");
    else {
      artText.printf(ARTTextLevel.TRACE, "(");
      arthashTablePrintSPPFNode(artpoolGet(el + 1));
      artText.printf(ARTTextLevel.TRACE, ", ");
      arthashTablePrintGSSNode(artpoolGet(el + 2));
      artText.printf(ARTTextLevel.TRACE, "-->");
      arthashTablePrintGSSNode(artpoolGet(el + 3));
      artText.printf(ARTTextLevel.TRACE, ")");
    }
  }

  // Offset zero is link to next in hash chain
  void arthashTablePrintPopElement(int el) {
    if (el == 0)
      artText.printf(ARTTextLevel.TRACE, "nullElement");
    else {
      artText.printf(ARTTextLevel.TRACE, "(");
      artText.printf(ARTTextLevel.TRACE, "%d", artpoolGet(el + 1)); // Just print address, since this could be a nonterminal or a GSS node depending on mode
      artText.printf(ARTTextLevel.TRACE, ", %d", artpoolGet(el + 2)); // Zero, or current token index
      artText.printf(ARTTextLevel.TRACE, ", %d, ", artpoolGet(el + 3)); // Zero, or current token index
      arthashTablePrintSPPFNode(artpoolGet(el + 4));
      artText.printf(ARTTextLevel.TRACE, ")");
    }
  }

  void arthashTablePrintPopElementMGLL(int el) {
    if (el == 0)
      artText.printf(ARTTextLevel.TRACE, "nullElement");
    else {
      artText.printf(ARTTextLevel.TRACE, "(");
      artText.printf(ARTTextLevel.TRACE, "%d", artpoolGet(el + 1)); // Just print address, since this could be a nonterminal or a GSS node depending on mode
      artText.printf(ARTTextLevel.TRACE, ", %d", artpoolGet(el + 2)); // Zero, or current token index
      artText.printf(ARTTextLevel.TRACE, ", %d, ", artpoolGet(el + 3)); // Zero, or current token index
      arthashTablePrintSPPFNode(artpoolGet(el + 4));
      artText.printf(ARTTextLevel.TRACE, ")");
    }
  }

  void arthashTablePrintDescriptor(int el) {
    if (el == 0)
      artText.printf(ARTTextLevel.TRACE, "nullElement");
    else {
      artText.printf(ARTTextLevel.TRACE, "(");
      artText.printf(ARTTextLevel.TRACE, "%s, ", getArtLabelInternalStrings()[Math.abs(artpoolGet(el + 1))]);
      arthashTablePrintGSSNode(artpoolGet(el + 2));
      artText.printf(ARTTextLevel.TRACE, ", %d, ", artpoolGet(el + 3));
      arthashTablePrintSPPFNode(artpoolGet(el + 4));
      artText.printf(ARTTextLevel.TRACE, ")");
    }
  }

  void arthashTablePrintDescriptorMGLL(int el) {
    if (el == 0)
      artText.printf(ARTTextLevel.TRACE, "nullElement");
    else {
      artText.printf(ARTTextLevel.TRACE, "(");
      artText.printf(ARTTextLevel.TRACE, "%s, ", getArtLabelInternalStrings()[Math.abs(artpoolGet(el + 1))]);
      arthashTablePrintGSSNode(artpoolGet(el + 2));
      artText.printf(ARTTextLevel.TRACE, ", %d:(%s, %d), ", artpoolGet(el + 3), artLabelStrings[artInputPairBuffer[artpoolGet(el + 3)]],
          artInputPairBuffer[artpoolGet(el + 3) + 1]);
      arthashTablePrintSPPFNode(artpoolGet(el + 4));
      artText.printf(ARTTextLevel.TRACE, ")");
    }
  }

  void arthashTablePrintTestRepeatElement(int el) {
    if (el == 0)
      artText.printf(ARTTextLevel.TRACE, "nullElement");
    else {
      artText.printf(ARTTextLevel.TRACE, "(");
      artText.printf(ARTTextLevel.TRACE, "%s, ", getArtLabelInternalStrings()[Math.abs(artpoolGet(el + 1))]);
      arthashTablePrintGSSNode(artpoolGet(el + 2));
      artText.printf(ARTTextLevel.TRACE, ", %d, ", artpoolGet(el + 3));
      arthashTablePrintSPPFNode(artpoolGet(el + 4));
      artText.printf(ARTTextLevel.TRACE, ")");
    }
  }

  /*
   * protected static final int clusterElement_label = 1; // A nonterminal protected static final int clusterElement_index = 2; // An offset into the string,
   * aka level protected static final int clusterElement_nodeList = 3; // The list of nodes in this cluster protected static final int clusterElement_inEdgeList
   * = 4; // The list of nodes and edge labels that connect to nodes in this cluster protected static final int clusterElement_clusterPopList = 5; // The list
   * of SPPF labels on pops that have been done involving this cluster protected static final int clusterElement_SIZE = 6;
   */
  void arthashTablePrintClusterElement(int el) {
    int cardinality = 0;

    for (int i = artpoolGet(el + artclusterElement_nodeList); i != 0; i = artpoolGet(i + artgssNode_pop_clusterElementList))
      cardinality++;

    // arttext.printf(TextLevel.TRACE, "%d:(", el);
    artText.printf(ARTTextLevel.TRACE, "(");
    // Skip hash table link
    artText.printf(ARTTextLevel.TRACE, "%s", artLabelStrings[artpoolGet(el + artclusterElement_label)]);
    artText.printf(ARTTextLevel.TRACE, ", %d", artpoolGet(el + artclusterElement_index));
    artText.printf(ARTTextLevel.TRACE, ")\",%d,\"", cardinality);
    for (int i = artpoolGet(el + artclusterElement_nodeList); i != 0; i = artpoolGet(i + artgssNode_pop_clusterElementList))
      artText.printf(ARTTextLevel.TRACE, "->%d", i);
    // arttext.printf(TextLevel.TRACE, ", %d", poolGet(el + clusterElement_inEdgeList));
    // arttext.printf(TextLevel.TRACE, ", %d", poolGet(el + clusterElement_popList));

  }

  void arthashTablePrintClusterInEdge(int el) {
    // arttext.printf(TextLevel.TRACE, "%d:(", el);
    artText.printf(ARTTextLevel.TRACE, "(");
    arthashTablePrintGSSNode(artpoolGet(el + artclusterInEdge_source));
    artText.printf(ARTTextLevel.TRACE, ", ");
    arthashTablePrintSPPFNode(artpoolGet(el + artclusterInEdge_sppfNode));
    artText.printf(ARTTextLevel.TRACE, ")");
  }

  void arthashTablePrintClusterPopElement(int el) {
    // Skip hash table link
    artText.printf(ARTTextLevel.TRACE, "(");
    arthashTablePrintSPPFNode(artpoolGet(el));
    artText.printf(ARTTextLevel.TRACE, ", %d", artpoolGet(el + 1));
    artText.printf(ARTTextLevel.TRACE, ")");
  }

  void arthashTablePrintElementRaw(int[] hashBuckets, int el) {
    artText.printf(ARTTextLevel.TRACE, "[");
    for (int i = 0; i < arthashTableElementSize(hashBuckets); i++)
      artText.printf(ARTTextLevel.TRACE, "%s%s", i == 0 ? "" : ",", artpoolGet(el + i));
    artText.printf(ARTTextLevel.TRACE, "]");
  }

  void arthashTablePrintElementFormatted(int[] hashBuckets, int el) {
    if (hashBuckets == artsppfNodeBuckets) arthashTablePrintSPPFNode(el);
    if (hashBuckets == artsppfPackedNodeBuckets) arthashTablePrintSPPFPackedNode(el);
    if (hashBuckets == artgssNodeBuckets) arthashTablePrintGSSNode(el);
    if (hashBuckets == artgssEdgeBuckets) arthashTablePrintGSSEdge(el);
    if (hashBuckets == artpopElementBuckets) arthashTablePrintPopElement(el);
    if (hashBuckets == artdescriptorBuckets) arthashTablePrintDescriptor(el);
    if (hashBuckets == arttestRepeatElementBuckets) arthashTablePrintTestRepeatElement(el);
    if (hashBuckets == artclusterElementBuckets) arthashTablePrintClusterElement(el);
  }

  String arthashTableName(int[] hashBuckets) {
    if (hashBuckets == artsppfNodeBuckets) return "sppfNode";
    if (hashBuckets == artsppfPackedNodeBuckets) return "sppfPackedNode";
    if (hashBuckets == artgssNodeBuckets) return "gssNode";
    if (hashBuckets == artgssEdgeBuckets) return "gssEdge";
    if (hashBuckets == artpopElementBuckets) return "popElement";
    if (hashBuckets == artdescriptorBuckets) return "descriptor";
    if (hashBuckets == arttestRepeatElementBuckets) return "testRepeatElement";
    if (hashBuckets == artclusterElementBuckets) return "clusterElement";

    return "???";
  }

  int arthashTableBucketCount(int[] hashBuckets) {
    if (hashBuckets == artsppfNodeBuckets) return artsppfNodeBucketCount;
    if (hashBuckets == artsppfPackedNodeBuckets) return artsppfPackedNodeBucketCount;
    if (hashBuckets == artgssNodeBuckets) return artgssNodeBucketCount;
    if (hashBuckets == artgssEdgeBuckets) return artgssEdgeBucketCount;
    if (hashBuckets == artpopElementBuckets) return artpopElementBucketCount;
    if (hashBuckets == artdescriptorBuckets) return artdescriptorBucketCount;
    if (hashBuckets == arttestRepeatElementBuckets) return arttestRepeatElementBucketCount;
    if (hashBuckets == artclusterElementBuckets) return artclusterElementBucketCount;

    return 0;
  }

  int arthashTableElementSize(int[] hashBuckets) {
    if (hashBuckets == artsppfNodeBuckets) return artsppfNode_SIZE;
    if (hashBuckets == artsppfPackedNodeBuckets) return artsppfPackedNode_SIZE;
    if (hashBuckets == artgssNodeBuckets) return artgssNode_SIZE;
    if (hashBuckets == artgssEdgeBuckets) return artgssEdge_SIZE;
    if (hashBuckets == artpopElementBuckets) return artpopElement_SIZE;
    if (hashBuckets == artdescriptorBuckets) return artdescriptor_SIZE;
    if (hashBuckets == arttestRepeatElementBuckets) return arttestRepeatElement_SIZE;
    if (hashBuckets == artclusterElementBuckets) return artclusterElement_SIZE;

    return 0;
  }

  void arthashTablePrint(int[] hashBuckets) {
    // arttext.printf(TextLevel.TRACE, "** Hash table print for %s%n", hashTableName(hashBuckets));
    for (int i = 0; i < arthashTableBucketCount(hashBuckets); i++) {
      if (hashBuckets[i] != 0) {
        // arttext.printf(TextLevel.TRACE, "Bucket %d:%n", i);
        for (int poolElement = hashBuckets[i]; poolElement != 0; poolElement = artpoolGet(poolElement)) {
          artText.printf(ARTTextLevel.TRACE, "***,%d,%d,%s,\"", poolElement, arthashTableElementSize(hashBuckets), arthashTableName(hashBuckets));
          // hashTablePrintElementRaw(hashBuckets, poolElement);
          // arttext.printf(TextLevel.TRACE, "\",\"");
          arthashTablePrintElementFormatted(hashBuckets, poolElement);
          artText.printf(ARTTextLevel.TRACE, "\"%n");
        }
      }
    }
  }

  public void arthashTablePrintAll() {
    arthashTablePrint(artsppfNodeBuckets);
    arthashTablePrint(artsppfPackedNodeBuckets);
    arthashTablePrint(artgssNodeBuckets);
    arthashTablePrint(artgssEdgeBuckets);
    arthashTablePrint(artpopElementBuckets);
    arthashTablePrint(artdescriptorBuckets);
    arthashTablePrint(arttestRepeatElementBuckets);
    arthashTablePrint(artclusterElementBuckets);
  }

  private void artfindDiagnostic(int[] hashBuckets, int hashBucketCount, int allocationSize, int a, int b, boolean found) {
    // arttext.printf(TextLevel.TRACE, "findDiagnostic(%s[%d, %d] %d, %d) - %sfound%n", hashTableName(hashBuckets), hashBucketCount, allocationSize, a, b,
    // found ?
    // ""
    // : "not ");
  }

  /**
   * The standard GLL support functions
   */
  @Override
  protected int artLookupSPPF(int label, int leftExtent, int rightExtent) {
    // Note - we use findWithFlags to allow successful lookup whilst flags are in use

    // arttext.printf(TextLevel.TRACE, "lookupSPPF(label = %d, leftExtent = %d, rightExtent = %d%n", label, leftExtent, rightExtent);

    boolean found = artfindWithFlags(artsppfNodeBuckets, artsppfNodeBucketCount, 0, label, leftExtent, rightExtent);
    artSPPFNodeFinds++;
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "lookupSPPF");
      arthashTablePrintSPPFNode(artfindIndex);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", found ? "" : "not ", artfindIndex);
    }
    return artfindIndex;
  }

  @Override
  protected int artFindSPPFInitial(int label, int leftExtent, int rightExtent) {
    boolean found = artfind(artsppfNodeBuckets, artsppfNodeBucketCount, artsppfNode_SIZE, label, leftExtent, rightExtent);
    artSPPFNodeFinds++;
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "findSPPFInitial ");
      arthashTablePrintSPPFNode(artfindIndex);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", found ? "" : "not ", artfindIndex);
    }
    return artfindIndex;
  }

  @Override
  protected int artFindSPPFEpsilon(int currentTokenIndex) {
    boolean found = artfind(artsppfNodeBuckets, artsppfNodeBucketCount, artsppfNode_SIZE, ARTL_EPSILON, currentTokenIndex, currentTokenIndex);
    artSPPFNodeFinds++;
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "findSPPFEpsilon ");
      arthashTablePrintSPPFNode(artfindIndex);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", found ? "" : "not ", artfindIndex);
    }
    return artfindIndex;
  }

  @Override
  protected int artFindSPPFTerminal(int label, int currentTokenIndex) {
    boolean found = artfind(artsppfNodeBuckets, artsppfNodeBucketCount, artsppfNode_SIZE, label, currentTokenIndex, currentTokenIndex + 1);
    artSPPFNodeFinds++;
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "findSPPFTerminal ");
      arthashTablePrintSPPFNode(artfindIndex);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", found ? "" : "not ", artfindIndex);
    }
    return artfindIndex;
  }

  @Override
  protected int artFindSPPFTerminal(int label, int currentTokenIndex, int tokenRightExtent) {
    boolean found = artfind(artsppfNodeBuckets, artsppfNodeBucketCount, artsppfNode_SIZE, label, currentTokenIndex, tokenRightExtent);
    artSPPFNodeFinds++;
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "findSPPFTerminalX ");
      arthashTablePrintSPPFNode(artfindIndex);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", found ? "" : "not ", artfindIndex);
    }
    return artfindIndex;
  }

  /*********************************************************************************/
  @Override
  protected int artFindSPPF(int label, int leftChild, int rightChild) {
    // findSPPFNode(L, w, z) // This is the inlining of getNode and getNodeRest in the EBNF paper
    // L is label, w is leftChild, z is rightChild
    // if FiR(L) return z;
    // j := if w = dummynode then z.leftExtent else w.leftExtent fi
    // if EoR(p(L)) t:= lhs(L) else t:=p(L) fi
    // y := findSPPF(t, j, z.rightIndex)
    // if (FiPC(L)) && w != dummyNode && z.leftExtent == j then L' := L: else L' := L fi
    // if (y does not have a child labelled (L', k) then
    // create child x of y labelled (L' k)
    // if w != dummyNode then create left child of x labelled w fi
    // create right child of x labelled z
    // return y

    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "findSPPF (%s, ", getArtLabelInternalStrings()[label]);
      arthashTablePrintSPPFNode(leftChild);
      artText.printf(ARTTextLevel.TRACE, ", ");
      arthashTablePrintSPPFNode(rightChild);
      artText.printf(ARTTextLevel.TRACE, ")%n");
    }

    // if FiR(L) return z;
    if (artfiRL[label]) {
      if (artTrace > 0) artText.printf(ARTTextLevel.TRACE, "Returning rightChild%n");
      return rightChild;
    }

    // j := if w = dummynode then z.leftExtent else w.leftExtent fi
    int leftExtent = (leftChild == artDummySPPFNode ? artpoolGet(rightChild + artsppfNode_leftExtent) : artpoolGet(leftChild + artsppfNode_leftExtent));

    int newSPPFNodeLabel; // t in the theory

    // if EoR(p(L)) t:= lhs(L) else t:=p(L) fi
    if (arteoRL[artpL[label]])
      newSPPFNodeLabel = artlhsL[label];
    else
      newSPPFNodeLabel = artpL[label];

    // y := findSPPF(t, j, z.rightIndex)
    boolean found = artfind(artsppfNodeBuckets, artsppfNodeBucketCount, artsppfNode_SIZE, newSPPFNodeLabel, leftExtent,
        artpoolGet(rightChild + artsppfNode_rightExtent));
    artSPPFNodeFinds++;
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "findSPPF ");
      arthashTablePrintSPPFNode(artfindIndex);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", found ? "" : "not ", artfindIndex);
    }

    int newSPPFNode = artfindIndex; // newSPPFNode is y in the theory

    // Add a pack node if necessary

    // if (FiPC(L)) && w != dummyNode && z.leftExtent == j then L' := L: else L' := L fi
    // if (y does not have a child labelled (L', k) then

    int labelPrime;

    if (artfiPCL[label] && (leftChild != artDummySPPFNode) && (leftExtent == artpoolGet(rightChild + artsppfNode_leftExtent)))
      labelPrime = artcolonL[label];
    else
      labelPrime = label;

    boolean packNodeFound = artfind(artsppfPackedNodeBuckets, artsppfPackedNodeBucketCount, artsppfPackedNode_SIZE, newSPPFNode, labelPrime,
        artpoolGet(rightChild + artsppfNode_leftExtent));
    artSPPFPackedNodeFinds++;

    // if w != dummyNode then create left child of x labelled w fi
    // create right child of x labelled z0
    if (!packNodeFound) {
      // Fill in fields in this newly created pack node and link to parent
      // 1: left child label
      artpool[artfindBlockIndex][++artfindLoadOffset] = artpoolGet(leftChild + artsppfNode_label);
      // 2: right child label
      artpool[artfindBlockIndex][++artfindLoadOffset] = artpoolGet(rightChild + artsppfNode_label);
      // 3: right sibling in pack node list taken from parent
      int parentPackedNodeListIndex = newSPPFNode + artsppfNode_packNodeList;
      artpool[artfindBlockIndex][++artfindLoadOffset] = artpoolGet(parentPackedNodeListIndex);
      // and finally point parent packNodeList at the new packNode
      artpoolSet(parentPackedNodeListIndex, artfindIndex);
    }

    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "findPackedNode ");
      arthashTablePrintSPPFPackedNode(artfindIndex);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", found ? "" : "not ", artfindIndex);
    }

    // return y
    return newSPPFNode;
  }

  /*********************************************************************************/

  @Override
  protected int artFindSPPFClosure(int parentLabel, int childLabel, int currentTokenIndex) {

    boolean found = artfind(artsppfNodeBuckets, artsppfNodeBucketCount, artsppfNode_SIZE, parentLabel, currentTokenIndex, currentTokenIndex);
    artSPPFNodeFinds++;

    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "findSPPFClosure ");
      arthashTablePrintSPPFNode(artfindIndex);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", found ? "" : "not ", artfindIndex);
    }

    int newSPPFNode = artfindIndex;

    // if there is no pack node labelled (L, i), the make one and add an
    // epsilon node as a child
    if (!artfind(artsppfPackedNodeBuckets, artsppfPackedNodeBucketCount, artsppfPackedNode_SIZE, newSPPFNode, childLabel, currentTokenIndex)) {
      // Fill in fields in this newly created pack node and link to parent
      // 1: left child label
      artpool[artfindBlockIndex][++artfindLoadOffset] = ARTL_DUMMY;
      // 2: right child label
      artpool[artfindBlockIndex][++artfindLoadOffset] = ARTL_EPSILON;
      // 3: right sibling in pack node list taken from parent
      int parentPackedNodeListIndex = newSPPFNode + artsppfNode_packNodeList;
      artpool[artfindBlockIndex][++artfindLoadOffset] = artpoolGet(parentPackedNodeListIndex);
      // and finally point parent packNodeList at the new packNode
      artpoolSet(parentPackedNodeListIndex, artfindIndex);

      artFindSPPFEpsilon(currentTokenIndex); // Find counted in find Epsilon
    }
    artSPPFNodeFinds++;
    return newSPPFNode;
  }

  @Override
  protected int artFindSPPFBaseNode(int parentLabel, int childLabel, int currentTokenIndex) {

    // y = findSPPF(L, j, i)
    boolean found = artfind(artsppfNodeBuckets, artsppfNodeBucketCount, artsppfNode_SIZE, parentLabel, currentTokenIndex, currentTokenIndex);
    artSPPFNodeFinds++;

    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "findSPPFBaseNode ");
      arthashTablePrintSPPFNode(artfindIndex);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", found ? "" : "not ", artfindIndex);
    }

    int newSPPFNode = artfindIndex; // newSPPFNode is y

    // if there is no pack node labelled (M:, i), the make one and add
    // w (derivationNode) as left child (#, i, i) as rightchild
    if (!artfind(artsppfPackedNodeBuckets, artsppfPackedNodeBucketCount, artsppfPackedNode_SIZE, newSPPFNode, childLabel, currentTokenIndex)) {
      // Fill in fields in this newly created pack node and link to parent
      // 1: left child label
      artpool[artfindBlockIndex][++artfindLoadOffset] = artSPPFNodeLabel(artDummySPPFNode);
      // 2: right child label
      artpool[artfindBlockIndex][++artfindLoadOffset] = ARTL_EPSILON;
      // 3: right sibling in pack node list taken from parent
      int parentPackedNodeListIndex = newSPPFNode + artsppfNode_packNodeList;
      artpool[artfindBlockIndex][++artfindLoadOffset] = artpoolGet(parentPackedNodeListIndex);
      // and finally point parent packNodeList at the new packNode
      artpoolSet(parentPackedNodeListIndex, artfindIndex);

      artFindSPPFEpsilon(currentTokenIndex); // Find counted in find Epsilon
    }
    artSPPFNodeFinds++;
    return newSPPFNode;
  }

  @Override
  protected int artFindGSS(int stackTopLabel, int stackTop, int currentToken, int currentsppfNode) {
    /* Trace output - introduce ourselves */
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "findGSS (%s, ", getArtLabelInternalStrings()[stackTopLabel]);
      arthashTablePrintGSSNode(stackTop);
      artText.printf(ARTTextLevel.TRACE, ", %d, ", currentToken);
      arthashTablePrintSPPFNode(artCurrentSPPFNode);
      artText.printf(ARTTextLevel.TRACE, ")");
    }

    // v := lookupGSS(L, i) // Returns NULL if (L, i) is not in the GSS,
    // otherwise returns the GSS node labelled (L, i)
    // if v = NULL then v := addtoGSS(L, i) fi
    //
    // if there is not an edge from v to u labelled w then
    // create an edge from v to u labelled w
    // for each (v, z) in P do add(L, u, z.rightExtent, getNodeP(L, w, z))
    // od
    // fi
    // return v

    /*
     * 1A. We are pushing the current return context onto the stack. A return context is a label to return to, and the current input index at th epoint of the
     * call.
     *
     * In a GSS, there are multiple stacks, and one of them might already contain the contex.
     *
     * The find() function looks to see if an element exists, and if not it makes one. Either way, the findIndex variable contains the address of the elemeny
     */
    boolean nodeFound = artfind(artgssNodeBuckets, artgssNodeBucketCount, artgssNode_SIZE, stackTopLabel, currentToken);
    artGSSNodeFinds++;

    /* Trace output - report result of find() */
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", nodeFound ? "" : "not ", artfindIndex);
    }

    int newGSSNode = artfindIndex;

    /*
     * 1B. Except for the root node (which is a special case) a pushed context needs to be connected to its predecessor on the stack We don't just immediately
     * make a new edge, because this connection may itself already exist, so we use find again on the edges table to avoid replicating edges
     */
    if (stackTop != 0) {
      boolean edgeFound = artfind(artgssEdgeBuckets, artgssEdgeBucketCount, artgssEdge_SIZE, artCurrentSPPFNode, newGSSNode, stackTop);
      artGSSEdgeFinds++;

      if (!edgeFound) {
        artpoolSet(artfindIndex + artgssEdge_edgeList, artpoolGet(newGSSNode + artgssNode_edgeList));
        artpoolSet(newGSSNode + artgssNode_edgeList, artfindIndex);
      }

      /* Trace output - report result of find() */
      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "findGSSEdge ");
        arthashTablePrintGSSEdge(artfindIndex);
        artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", edgeFound ? "" : "not ", artfindIndex);
      }

      /*
       * 2A. Contingent pop processing
       *
       * Here's the hard bit. For pushed elements that were already in the GSS, their associated production instances may already have been processed, in which
       * a pop will already have been executed on the node. These pops are remembered for each node in a linked list hanging off of each gss node and also in a
       * hash table which allows global checks to be done efficiently.
       *
       * If we have added a new edge to an old node, then we must check the pop set and restart the contexts for any pops that have already been processed using
       * this node
       *
       * Here's a thought: delay pops as long as possible to keep the lists short. Why not separate R into two parts: R1 holds descriptors for code fragments
       * that do not include a pop() call at the end, and R2 holds the descriptors whose code does finish with a pop(). Decsriptor fetch then becomes while R2
       * non empty { extract R2; while R1 non empty { extract R1 } }
       */

      /*
       * There's some rework of the control flow here compared to the version in the paper. Contingent pops can only be needed if we added a new edge to an old
       * GSS node. That means, if the node is new, no contingent processing (because the contingent list must certainly be empty. Also, if the node AND the edge
       * are old then the pops will have been done for that edge and there's no more to do at this stage.
       *
       * So only need to do contingent processing if the node was found and the edge was not.
       */
      /* Trace output - introduce pop processing */
      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "Start of contingent pop processing for GSS node ");
        arthashTablePrintGSSNode(newGSSNode);
        artText.printf(ARTTextLevel.TRACE, "%n");
      }

      if (nodeFound && !edgeFound) {

        /*
         * 2B walk down the linked list creating derivationNodes and descriptors which restart the popped context
         */
        for (int poppedElement = artpoolGet(newGSSNode + artgssNode_pop_clusterElementList); poppedElement != 0; poppedElement = artpoolGet(
            poppedElement + artpopElement_popElementList)) {
          int derivationNode = artFindSPPF(stackTopLabel, currentsppfNode, artpoolGet(poppedElement + artpopElement_sppfNode));
          artFindDescriptor(stackTopLabel, stackTop, artpoolGet(artpoolGet(poppedElement + artpopElement_sppfNode) + artsppfNode_rightExtent), derivationNode);
          artContingentPops++;
          // AJ Opt - derivationNode IS poolGet(poppedElement + popElement_sppfNode!
        }

      }
      /* Trace output - wrap up pop processing */
      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "End of contingent pop processing for GSS node ");
        arthashTablePrintGSSNode(newGSSNode);
        artText.printf(ARTTextLevel.TRACE, "%n");
      }
    }

    return newGSSNode;
  }

  protected int artFindGSSRecogniser(int stackTopLabel, int stackTop, int currentTokenIndex) {
    /* Trace output - introduce ourselves */
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "findGSS (%s, ", getArtLabelInternalStrings()[stackTopLabel]);
      arthashTablePrintGSSNode(stackTop);
      artText.printf(ARTTextLevel.TRACE, ", %d, ", currentTokenIndex);
      arthashTablePrintSPPFNode(artCurrentSPPFNode);
      artText.printf(ARTTextLevel.TRACE, ")");
    }

    // v := lookupGSS(L, i) // Returns NULL if (L, i) is not in the GSS,
    // otherwise returns the GSS node labelled (L, i)
    // if v = NULL then v := addtoGSS(L, i) fi
    //
    // if there is not an edge from v to u labelled w then
    // create an edge from v to u labelled w
    // for each (v, z) in P do add(L, u, z.rightExtent, getNodeP(L, w, z))
    // od
    // fi
    // return v

    /*
     * 1A. We are pushing the current return context onto the stack. A return context is a label to return to, and the current input index at th epoint of the
     * call.
     *
     * In a GSS, there are multiple stacks, and one of them might already contain the contex.
     *
     * The find() function looks to see if an element exists, and if not it makes one. Either way, the findIndex variable contains the address of the elemeny
     */
    boolean nodeFound = artfind(artgssNodeBuckets, artgssNodeBucketCount, artgssNode_SIZE, stackTopLabel, currentTokenIndex);
    artGSSNodeFinds++;

    /* Trace output - report result of find() */
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", nodeFound ? "" : "not ", artfindIndex);
    }

    int newGSSNode = artfindIndex;

    /*
     * 1B. Except for the root node (which is a special case) a pushed context needs to be connected to its predecessor on the stack We don't just immediately
     * make a new edge, because this connection may itself already exist, so we use find again on the edges table to avoid replicating edges
     */
    if (stackTop != 0) {
      boolean edgeFound = artfind(artgssEdgeBuckets, artgssEdgeBucketCount, artgssEdge_SIZE, artCurrentSPPFNode, newGSSNode, stackTop);
      artGSSEdgeFinds++;

      if (!edgeFound) {
        artpoolSet(artfindIndex + artgssEdge_edgeList, artpoolGet(newGSSNode + artgssNode_edgeList));
        artpoolSet(newGSSNode + artgssNode_edgeList, artfindIndex);
      }

      /* Trace output - report result of find() */
      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "findGSSEdge ");
        arthashTablePrintGSSEdge(artfindIndex);
        artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", edgeFound ? "" : "not ", artfindIndex);
      }

      /*
       * 2A. Contingent pop processing
       *
       * Here's the hard bit. For pushed elements that were already in the GSS, their associated production instances may already have been processed, in which
       * a pop will already have been executed on the node. These pops are remembered for each node in a linked list hanging off of each gss node and also in a
       * hash table which allows global checks to be done efficiently.
       *
       * If we have added a new edge to an old node, then we must check the pop set and restart the contexts for any pops that have already been processed using
       * this node
       *
       * Here's a thought: delay pops as long as possible to keep the lists short. Why not separate R into two parts: R1 holds descriptors for code fragments
       * that do not include a pop() call at the end, and R2 holds the descriptors whose code does finish with a pop(). Decsriptor fetch then becomes while R2
       * non empty { extract R2; while R1 non empty { extract R1 } }
       */

      /*
       * There's some rework of the control flow here compared to the version in the paper. Contingent pops can only be needed if we added a new edge to an old
       * GSS node. That means, if the node is new, no contingent processing (because the contingent list must certainly be empty. Also, if the node AND the edge
       * are old then the pops will have been done for that edge and there's no more to do at this stage.
       *
       * So only need to do contingent processing if the node was found and the edge was not.
       */
      /* Trace output - introduce pop processing */
      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "Start of contingent pop processing for GSS node ");
        arthashTablePrintGSSNode(newGSSNode);
        artText.printf(ARTTextLevel.TRACE, "%n");
      }

      if (nodeFound && !edgeFound) {

        /*
         * 2B walk down the linked list creating derivationNodes and descriptors which restart the popped context
         */
        for (int poppedElement = artpoolGet(newGSSNode + artgssNode_pop_clusterElementList); poppedElement != 0; poppedElement = artpoolGet(
            poppedElement + artpopElement_popElementList)) {
          int derivationNode = 0;
          // Note artpopElement_sppfNode field is used for left extent in the recogniser
          artFindDescriptor(stackTopLabel, stackTop, artpoolGet(poppedElement + artpopElement_currentTokenIndex_MGLLInputPair), derivationNode);
          artContingentPops++;
        }

      }
      /* Trace output - wrap up pop processing */
      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "End of contingent pop processing for GSS node ");
        arthashTablePrintGSSNode(newGSSNode);
        artText.printf(ARTTextLevel.TRACE, "%n");
      }
    }

    return newGSSNode;
  }

  @Override
  // Note currentTokenIndexUnused is always currentSPPFNode.rightExtent - 11 April 2017 change
  protected void artPop(int stackTop, int currentTokenIndexUnused, int currentsppfNode) {
    // pop(u, i, z)
    // if (not u = u_0) then
    // insert(u,z) into P
    //
    // for each edge (u, w, v)
    // y := lookupSPPF(u.label, w, z)
    // findDescriptor(u.label, v, i, y)

    if (stackTop == artRootGSSNode) {
      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "pop (");
        arthashTablePrintGSSNode(stackTop);
        artText.printf(ARTTextLevel.TRACE, ", %d, ", currentTokenIndexUnused);
        arthashTablePrintSPPFNode(artCurrentSPPFNode);
        artText.printf(ARTTextLevel.TRACE, ") - pop of root node - returning%n");
      }
      return;
    }

    boolean found = artfind(artpopElementBuckets, artpopElementBucketCount, artpopElement_SIZE, stackTop, currentTokenIndexUnused, currentsppfNode);
    artPopElementFinds++;

    int popElement;
    if (artTrace > 0) {
      popElement = artfindIndex;

      artText.printf(ARTTextLevel.TRACE, "pop ");
      arthashTablePrintPopElement(popElement);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", found ? "" : "not ", artfindIndex);
    }

    if (!found) {
      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "Adding popElement to GSSNode\n");
      }
      artpoolSet(artfindIndex + artpopElement_popElementList, artpoolGet(stackTop + artgssNode_pop_clusterElementList));
      artpoolSet(stackTop + artgssNode_pop_clusterElementList, artfindIndex);
    }
    /*
     * April 2017 EXPERIMENT - make rest of function contingent in having added popElement - comment this if statement out to restore classical behaviour
     *
     */
    if (found) {
      // System.err.println("Pop short circuited");
      return;
    }

    for (int outEdge = artpoolGet(stackTop + artgssNode_edgeList); outEdge != 0; outEdge = artpoolGet(outEdge + artgssEdge_edgeList)) {
      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "Popping GSSEdge %d ", outEdge);
        arthashTablePrintGSSEdge(outEdge);
        artText.printf(ARTTextLevel.TRACE, "%n");
      }

      artPrimaryPops++;

      int derivationNode = artFindSPPF(artpoolGet(stackTop + artgssNode_label), artpoolGet(outEdge + artgssEdge_sppfNode), currentsppfNode);
      artFindDescriptor(artpoolGet(stackTop + artgssNode_label), artpoolGet(outEdge + artgssEdge_destination), currentTokenIndexUnused, derivationNode);
    }
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "Exiting pop ");
      // arthashTablePrintPopElement(popElement);
      artText.printf(ARTTextLevel.TRACE, "%n");
    }
  }

  // In the recogniser, the pop set is ove rpairs of (stackTop, i)
  protected void artPopRecogniser(int stackTop, int currentTokenIndex) {
    // pop(u, i, z)
    // if (not u = u_0) then
    // insert(u,z) into P
    //
    // for each edge (u, w, v)
    // y := lookupSPPF(u.label, w, z)
    // findDescriptor(u.label, v, i, y)

    if (stackTop == artRootGSSNode) {
      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "pop (");
        arthashTablePrintGSSNode(stackTop);
        artText.printf(ARTTextLevel.TRACE, ", %d, ", currentTokenIndex);
        arthashTablePrintSPPFNode(artCurrentSPPFNode);
        artText.printf(ARTTextLevel.TRACE, ") - pop of root node - returning%n");
      }
      return;
    }

    boolean found = artfind(artpopElementBuckets, artpopElementBucketCount, artpopElement_SIZE, stackTop, currentTokenIndex, 0);
    artPopElementFinds++;

    int popElement;
    if (artTrace > 0) {
      popElement = artfindIndex;

      artText.printf(ARTTextLevel.TRACE, "pop ");
      arthashTablePrintPopElement(popElement);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", found ? "" : "not ", artfindIndex);
    }

    if (!found) {
      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "Adding popElement to GSSNode\n");
      }
      artpoolSet(artfindIndex + artpopElement_popElementList, artpoolGet(stackTop + artgssNode_pop_clusterElementList));
      artpoolSet(stackTop + artgssNode_pop_clusterElementList, artfindIndex);
    }
    /*
     * April 2017 EXPERIMENT - make rest of function contingent in having added popElement - comment this if statement out to restore classical behaviour
     *
     */
    if (found) {
      // System.err.println("Pop short circuited");
      return;
    }

    for (int outEdge = artpoolGet(stackTop + artgssNode_edgeList); outEdge != 0; outEdge = artpoolGet(outEdge + artgssEdge_edgeList)) {
      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "Popping GSSEdge %d ", outEdge);
        arthashTablePrintGSSEdge(outEdge);
        artText.printf(ARTTextLevel.TRACE, "%n");
      }

      artPrimaryPops++;

      int derivationNode = 0;
      artFindDescriptor(artpoolGet(stackTop + artgssNode_label), artpoolGet(outEdge + artgssEdge_destination), currentTokenIndex, derivationNode);
    }
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "Exiting pop ");
      // arthashTablePrintPopElement(popElement);
      artText.printf(ARTTextLevel.TRACE, "%n");
    }
  }

  @Override
  protected int artFindGSSMGLL(int stackTopLabel, int stackTop, int currentInputPairReference, int currentsppfNode) {
    /* Trace output - introduce ourselves */
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "findGSSMGLL (%s, ", getArtLabelInternalStrings()[stackTopLabel]);
      arthashTablePrintGSSNode(stackTop);
      artText.printf(ARTTextLevel.TRACE, ", %d, ", currentInputPairReference);
      arthashTablePrintSPPFNode(artCurrentSPPFNode);
      artText.printf(ARTTextLevel.TRACE, ")");
    }

    // v := lookupGSS(L, i) // Returns NULL if (L, i) is not in the GSS,
    // otherwise returns the GSS node labelled (L, i)
    // if v = NULL then v := addtoGSS(L, i) fi
    //
    // if there is not an edge from v to u labelled w then
    // create an edge from v to u labelled w
    // CLASSICAL: for each (v, z) in P do add(L, u, z.rightExtent, getNodeP(L, w, z)) od
    // MGLL: for each (v, a, z) in P do add(L, u, (a, z.rightExtent), getNode(L, w, z)) od
    // fi
    // return v

    /*
     * 1A. We are pushing the current return context onto the stack. A return context is a label to return to, and the current input index at th epoint of the
     * call.
     *
     * In a GSS, there are multiple stacks, and one of them might already contain the conarttext.
     *
     * The find() function looks to see if an element exists, and if not it makes one. Either way, the findIndex variable contains the address of the element
     */
    boolean nodeFound = artfind(artgssNodeBuckets, artgssNodeBucketCount, artgssNode_SIZE, stackTopLabel, currentInputPairReference);
    artGSSNodeFinds++;

    /* Trace output - report result of find() */
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", nodeFound ? "" : "not ", artfindIndex);
    }

    int newGSSNode = artfindIndex;

    /*
     * 1B. Except for the root node (which is a special case) a pushed context needs to be connected to its predecessor on the stack We don't just immediately
     * make a new edge, because this connection may itself already exist, so we use find again on the edges table to avoid replicating edges
     */
    if (stackTop != 0) {
      boolean edgeFound = artfind(artgssEdgeBuckets, artgssEdgeBucketCount, artgssEdge_SIZE, artCurrentSPPFNode, newGSSNode, stackTop);
      artGSSEdgeFinds++;

      if (!edgeFound) {
        artpoolSet(artfindIndex + artgssEdge_edgeList, artpoolGet(newGSSNode + artgssNode_edgeList));
        artpoolSet(newGSSNode + artgssNode_edgeList, artfindIndex);
      }

      /* Trace output - report result of find() */
      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "findGSSEdge ");
        arthashTablePrintGSSEdge(artfindIndex);
        artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", edgeFound ? "" : "not ", artfindIndex);
      }

      /*
       * 2A. Contingent pop processing
       *
       * Here's the hard bit. For pushed elements that were already in the GSS, their associated production instances may already have been processed, in which
       * a pop will already have been executed on the node. These pops are remembered for each node in a linked list hanging off of each gss node and also in a
       * hash table which allows global checks to be done efficiently.
       *
       * If we have added a new edge to an old node, then we must check the pop set and restart the contexts for any pops that have already been processed using
       * this node
       */

      /*
       * There's some rework if the control flow here compared to the version in the paper. Contingent pops can only be needed if we added a new edge to an old
       * GSS node. That means, if the node is new, no contingent processing (because the contingent list must certainly be empty. Also, if the node AND the edge
       * are old then the pops will have been done for that edge and there's no more to do at this stage.
       *
       * So only need to do contingent processing if the node was found and the edge was not.
       */
      /* Trace output - introduce pop processing */
      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "Start of contingent MGLL pop processing for GSS node ");
        arthashTablePrintGSSNode(newGSSNode);
        artText.printf(ARTTextLevel.TRACE, "%n");
      }

      if (nodeFound && !edgeFound) {

        /*
         * 2B walk down the linked list creating derivationNodes and descriptors which restart the popped context
         *
         * CLASSICAL: for each (v, z) in P do add(L, u, z.rightExtent, getNodeP(L, w, z)) od
         *
         * MGLL: for each (v, e, z) in P do add(L, u, e, getNode(L, w, z)) od
         */
        for (int poppedElement = artpoolGet(newGSSNode + artgssNode_pop_clusterElementList); poppedElement != 0; poppedElement = artpoolGet(
            poppedElement + artpopElement_popElementList)) {
          int derivationNode = artFindSPPF(stackTopLabel, currentsppfNode, artpoolGet(poppedElement + artpopElement_sppfNode));
          artFindDescriptor(stackTopLabel, stackTop, artpoolGet(poppedElement + artpopElement_currentTokenIndex_MGLLInputPair), derivationNode);
          artContingentPops++;
          // AJ Opt - derivationNode IS poolGet(poppedElement + popElement_sppfNode!
        }

      }
      /* Trace output - wrap up pop processing */
      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "End of contingent MGLL pop processing for GSS node ");
        arthashTablePrintGSSNode(newGSSNode);
        artText.printf(ARTTextLevel.TRACE, "%n");
      }
    }

    return newGSSNode;
  }

  @Override
  protected void artPopMGLL(int stackTop, int currentInputPairReference, int currentsppfNode) {
    // pop(u, e, z)
    // if (not u = u_0) then
    // let (L, k) be the label of u
    // insert(u, sym(e), z) into P
    //
    // for each edge (u, w, v)
    // y := lookupSPPF(L, w, z)
    // findDescriptor(, v, e, y)

    // Input pair being put into P should have a from currentInputPairReference and j from currentsppfNode - do we need a search

    if (stackTop == artRootGSSNode) {
      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "popMGLL (");
        arthashTablePrintGSSNode(stackTop);
        artText.printf(ARTTextLevel.TRACE, ", %d, ", currentInputPairReference);
        arthashTablePrintSPPFNode(artCurrentSPPFNode);
        artText.printf(ARTTextLevel.TRACE, ") - popMGLL of root node - returning%n");
      }
      return;
    }

    // Load reference to pair - note this is a refactpring of the code wrt the paper version at Dec 2014
    boolean found = artfind(artpopElementBuckets, artpopElementBucketCount, artpopElement_SIZE, stackTop, currentInputPairReference, currentsppfNode);
    artPopElementFinds++;

    int popElement;
    if (artTrace > 0) {
      popElement = artfindIndex;

      artText.printf(ARTTextLevel.TRACE, "popMGLL ");
      arthashTablePrintPopElementMGLL(popElement);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", found ? "" : "not ", artfindIndex);
    }

    if (!found) {
      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "Adding MGLL popElement to GSSNode\n");
      }
      artpoolSet(artfindIndex + artpopElement_popElementList, artpoolGet(stackTop + artgssNode_pop_clusterElementList));
      artpoolSet(stackTop + artgssNode_pop_clusterElementList, artfindIndex);
    }

    for (int outEdge = artpoolGet(stackTop + artgssNode_edgeList); outEdge != 0; outEdge = artpoolGet(outEdge + artgssEdge_edgeList)) {
      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "MGLL Popping GSSEdge %d ", outEdge);
        arthashTablePrintGSSEdge(outEdge);
        artText.printf(ARTTextLevel.TRACE, "%n");
      }

      artPrimaryPops++;

      int derivationNode = artFindSPPF(artpoolGet(stackTop + artgssNode_label), artpoolGet(outEdge + artgssEdge_sppfNode), currentsppfNode);
      artFindDescriptor(artpoolGet(stackTop + artgssNode_label), artpoolGet(outEdge + artgssEdge_destination), currentInputPairReference, derivationNode);
    }
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "Exiting pop MGLL ");
      // arthashTablePrintPopElement(popElement);
      artText.printf(ARTTextLevel.TRACE, "%n");
    }
  }

  @Override
  protected int artFindGSSClustered(int clusterNonTerminal, int clusterIndex, int currentToken, int currentsppfNode) {
    // (iii-4imp) findGSSNodeReduced4(L, k, i, w) (* Adrian's cluster implementation *)
    // (* Find (lookup, and if necessary create) the clusters for the nonterminal to be called and for the LHS nonterminal *)
    // let slot(L) = !B ::= _ !A . _
    // c = findCluster(A, i)
    // d = findCluster(B, k)
    //
    // (* Make the GSS node v, add it to the nodelist of c, and add to v any inedges that have already been created for c *)
    // if lookupGSSNode(L, i) = null
    // v = createGSSNode(L, i)
    // add v to the node list of c
    // (* This loop creates actual GSS edges; it can be omitted without breaking the algorithm because the cluster inedge captures the information *)
    // for all inedges to c (!f, !x, _)
    // createGSSedge(f, x, v)
    //
    // (* Add the specific edge for this push; and add this to the LHS cluster inedge list *)
    // let !u' be the first element on the nodelist of d
    // if lookupGSSEdge(v, w, u') = null
    // add (v, w) to the inedge list of d
    // (* This loop creates actual GSS edges; it can be omitted without breaking the algorithm because the cluster inedge captures the information *)
    // for all u in node list of d
    // createGSSedge(v, w, u)
    //
    // (* Look for contingent pops, and create associated descriptors *)
    // for all !z on pop list of c
    // findDescriptor(L, k, z.rightExtent, lookupSPPfNode(L, w, z))

    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "findGSSClustered(%s, %d, %d, ", getArtLabelInternalStrings()[clusterNonTerminal], clusterIndex, currentToken);
      arthashTablePrintSPPFNode(artCurrentSPPFNode);
      artText.printf(ARTTextLevel.TRACE, ")%n");
    }

    // (* Find (lookup, and if necessary create) the clusters for the nonterminal to be called and for the LHS nonterminal *)
    // let slot(L) = !B ::= _ !A . _
    // c = findCluster(A, i)
    // d = findCluster(B, k)
    // Let L be for the form B ::= \tau A . \mu; identify labels A and B
    int B = artlhsL[clusterNonTerminal];
    int A = artSlotInstanceOfs[clusterNonTerminal];

    boolean clusterAFound = artfind(artclusterElementBuckets, artclusterElementBucketCount, artclusterElement_SIZE, A, currentToken);
    artClusterElementFinds++;

    int clusterAElement = artfindIndex;
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "findClusterElement for ");
      arthashTablePrintClusterElement(clusterAElement);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", clusterAFound ? "" : "not ", clusterAElement);
    }

    boolean clusterBFound = artfind(artclusterElementBuckets, artclusterElementBucketCount, artclusterElement_SIZE, B, clusterIndex);
    artClusterElementFinds++;
    int clusterBElement = artfindIndex;

    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "findClusterElement for lhs");
      arthashTablePrintClusterElement(clusterBElement);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", clusterBFound ? "" : "not ", clusterBElement);
    }

    // (* Make the GSS node v, add it to the nodelist of c, and add to v any inedges that have already been created for c *)
    // if lookupGSSNode(L, i) = null
    // v = createGSSNode(L, i)
    // add v to the node list of c
    // (* This loop creates actual GSS edges; it can be omitted without breaking the algorithm because the cluster inedge captures the information *)
    // for all inedges to c (!f, !x, _)
    // createGSSedge(f, x, v)
    //
    boolean gssNodeFound = artfind(artgssNodeBuckets, artgssNodeBucketCount, artgssNode_SIZE, clusterNonTerminal, currentToken);
    artGSSNodeFinds++;
    int newGSSNode = artfindIndex;

    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "findGSSNode ");
      arthashTablePrintGSSNode(newGSSNode);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", gssNodeFound ? "" : "not ", newGSSNode);
    }

    // If the GSS node was new, link it into its cluster
    int oldStackTop = artpoolGet(clusterAElement + artclusterElement_nodeList);

    if (!gssNodeFound) {
      artpoolSet(newGSSNode + artpopElement_popElementList, oldStackTop);
      artpoolSet(clusterAElement + artclusterElement_nodeList, newGSSNode);

      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "Start of cluster A inEdge iteration%n");
      }

      for (int inEdge = artpoolGet(clusterAElement + artclusterElement_inEdgeList); inEdge != 0; inEdge = artpoolGet(inEdge + artclusterInEdge_inEdgeList)) {

        if (artTrace > 0) {
          artText.printf(ARTTextLevel.TRACE, "%nIterating over inedge list for cluster ");
          arthashTablePrintClusterElement(clusterAElement);
          artText.printf(ARTTextLevel.TRACE, " to inedge ");
          arthashTablePrintClusterInEdge(inEdge);
          artText.printf(ARTTextLevel.TRACE, "%n");

        }

        int inEdgeSourceNode = artpoolGet(inEdge + artclusterInEdge_source);

        boolean edgeFound = artfind(artgssEdgeBuckets, artgssEdgeBucketCount, artgssEdge_SIZE, artpoolGet(inEdge + artclusterInEdge_sppfNode), inEdgeSourceNode,
            newGSSNode);
        artGSSEdgeFinds++;
        int newEdge = artfindIndex;

        /* Trace output - report result of find() */
        if (artTrace > 0) {
          artText.printf(ARTTextLevel.TRACE, "Iterating over existing cluster inedges: findGSSEdge ");
          arthashTablePrintGSSEdge(newEdge);
          artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", edgeFound ? "" : "not ", newEdge);
        }

        if (!edgeFound) {
          artpoolSet(newEdge + artgssEdge_edgeList, artpoolGet(inEdgeSourceNode + artgssNode_edgeList));
          artpoolSet(inEdgeSourceNode + artgssNode_edgeList, newEdge);
        }
      }

      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "End of cluster A inEdge iteration%n");
      }

    }
    // (* Add the specific edge for this push; and add this to the LHS cluster inedge list *)
    // let !u' be the first element on the nodelist of d
    // if lookupGSSEdge(v, w, u') = null
    // add (v, w) to the inedge list of d
    // (* This loop creates actual GSS edges; it can be omitted without breaking the algorithm because the cluster inedge captures the information *)
    // for all u in node list of d
    // createGSSedge(v, w, u)

    int firstClusterNode = artpoolGet(clusterBElement + artclusterElement_nodeList);

    boolean specificEdgeFound = artfind(artgssEdgeBuckets, artgssEdgeBucketCount, artgssEdge_SIZE, artCurrentSPPFNode, newGSSNode, firstClusterNode);
    artGSSEdgeFinds++;
    int newSpecificEdge = artfindIndex;

    /* Trace output - report result of find() */
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "Checking specific edge for first cluster node: findGSSEdge ");
      arthashTablePrintGSSEdge(newSpecificEdge);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", specificEdgeFound ? "" : "not ", newSpecificEdge);
    }

    if (!specificEdgeFound) {
      // Link the newly created edge into the edge list of the new gssnode
      artpoolSet(newSpecificEdge + artgssEdge_edgeList, artpoolGet(newGSSNode + artgssNode_edgeList));
      artpoolSet(newGSSNode + artgssNode_edgeList, newSpecificEdge);

      // Add (v, w) to inedge list of d
      artallocateAndLoad(artclusterInEdge_SIZE, newGSSNode, artCurrentSPPFNode);
      int inEdgeElement = artfindIndex;

      artpoolSet(inEdgeElement + artclusterInEdge_inEdgeList, artpoolGet(clusterBElement + artclusterElement_inEdgeList));
      artpoolSet(clusterBElement + artclusterElement_inEdgeList, inEdgeElement);

      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "Start of cluster B node iteration%n");
      }
      // Add physical edges to GSS for the rest of the nodes in cluster B
      for (int clusterNode = artpoolGet(firstClusterNode + artgssNode_pop_clusterElementList); clusterNode != 0; clusterNode = artpoolGet(
          clusterNode + artgssNode_pop_clusterElementList)) {

        boolean clusterEdgeFound = artfind(artgssEdgeBuckets, artgssEdgeBucketCount, artgssEdge_SIZE, artCurrentSPPFNode, newGSSNode, clusterNode);
        artGSSEdgeFinds++;
        int newClusterEdge = artfindIndex;

        /* Trace output - report result of find() */
        if (artTrace > 0) {
          artText.printf(ARTTextLevel.TRACE, "Adding cluster edge: findGSSEdge ");
          arthashTablePrintGSSEdge(newClusterEdge);
          artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", clusterEdgeFound ? "" : "not ", newClusterEdge);
        }

        // Link the newly found edge into the edge list of the cluster node
        if (!clusterEdgeFound) {
          artpoolSet(newClusterEdge + artgssEdge_edgeList, artpoolGet(newGSSNode + artgssNode_edgeList));
          artpoolSet(newGSSNode + artgssNode_edgeList, newClusterEdge);
        }

      }
      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "End of cluster B node iteration%n");
      }
    }
    // (* Look for contingent pops, and create associated descriptors *)
    // for all !z on pop list of c
    // findDescriptor(L, k, z.rightExtent, lookupSPPfNode(L, w, z))
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "Start of contingent pop processing%n");
    }
    for (int clusterPopElement = artpoolGet(clusterAElement + artclusterElement_popList); clusterPopElement != 0; clusterPopElement = artpoolGet(
        clusterPopElement + artclusterPopElement_popList)) {

      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "Contingent pop iterated to clusterPopElement %d ", clusterPopElement);
        arthashTablePrintClusterPopElement(clusterPopElement);
        artText.printf(ARTTextLevel.TRACE, "%n");
      }

      int popSPPFNode = artpoolGet(clusterPopElement + artclusterPopElement_sppfNode);

      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "Contingent pop using SPPF node %d ", popSPPFNode);
        arthashTablePrintSPPFNode(popSPPFNode);
        artText.printf(ARTTextLevel.TRACE, "%n");
      }

      int derivationNode = artFindSPPF(clusterNonTerminal, currentsppfNode, popSPPFNode);
      artFindDescriptor(clusterNonTerminal, clusterIndex, artpoolGet(popSPPFNode + artsppfNode_rightExtent), derivationNode);
      artContingentPops++;
    }
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "End of contingent pop processing%n");
    }

    return currentToken; // A bit kludgy - reduced descriptors do not require a return and C_u = C_I could be set in the template
  };

  @Override
  protected int artFindGSSClusteredInitial(int stackTopLabel, int stackTop, int currentToken, int currentsppfNode) {

    // (* Find (lookup, and if necessary create) the clusters for the nonterminal to be called and for the LHS nonterminal *)
    // let slot(L) = !B ::= _ !A . _
    // c = findCluster(A, i)
    //
    // (* Make the GSS node v, add it to the nodelist of c, and add to v any inedges that have already been created for c *)
    // if lookupGSSNode(L, i) = null
    // v = createGSSNode(L, i)
    // add v to the node list of d

    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "findGSSClusteredInitial(%s, %d, %d, ", getArtLabelInternalStrings()[stackTopLabel], stackTop, currentToken);
      arthashTablePrintSPPFNode(artCurrentSPPFNode);
      artText.printf(ARTTextLevel.TRACE, ")%n");
    }

    int A = artStartSymbolLabel;

    boolean clusterAFound = artfind(artclusterElementBuckets, artclusterElementBucketCount, artclusterElement_SIZE, A, currentToken);
    artClusterElementFinds++;
    int clusterAElement = artfindIndex;
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "findClusterElement ");
      arthashTablePrintClusterElement(clusterAElement);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", clusterAFound ? "" : "not ", clusterAElement);
    }

    boolean gssNodeFound = artfind(artgssNodeBuckets, artgssNodeBucketCount, artgssNode_SIZE, stackTopLabel, currentToken);
    artClusterElementFinds++;
    int newGSSNode = artfindIndex;

    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "findGSSNode (%s, %d)", getArtLabelInternalStrings()[stackTopLabel], currentToken);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", gssNodeFound ? "" : "not ", newGSSNode);
    }

    // If the GSS node was new, link it into its cluster
    int oldStackTop = artpoolGet(clusterAElement + artclusterElement_nodeList);

    if (!gssNodeFound) {
      artpoolSet(newGSSNode + artpopElement_popElementList, oldStackTop);
      artpoolSet(clusterAElement + artclusterElement_nodeList, newGSSNode);
    }

    return currentToken; // A bit kludgy - reduced descriptors do not require a return and C_u = C_I could be set in the template
  };

  @Override
  protected void artPopClustered(int nonterminal, int k, int currentTokenIndex, int currentsppfNode) {
    // if (A, k, z) \not\in P {
    // insert (A, k, z) into P
    // c = findCluster(A, k)
    // for each GSS node u = ( (!L = _ ::= _ A . _), k) {
    // for each edge (u, !w, !v) {
    // findDescriptor_add(L, v.level, i, getSPPFNode(L, w, z))
    // add z to poplist of c

    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "popClustered(%s, %d, %d, ", getArtLabelInternalStrings()[nonterminal], k, currentTokenIndex);
      arthashTablePrintSPPFNode(artCurrentSPPFNode);
      artText.printf(ARTTextLevel.TRACE, ")%n");
    }

    boolean popFound = artfind(artpopElementBuckets, artpopElementBucketCount, artpopElement_SIZE, nonterminal, k, 0, currentsppfNode);
    artPopElementFinds++;
    int popIndex = artfindIndex;

    /* Trace output - report result of find() */
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "find popElement ");
      arthashTablePrintPopElement(popIndex);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", popFound ? "" : "not ", popIndex);
    }

    if (popFound) {
      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "Immediate return from popClustered%n");
      }
      return;
    }

    // Find our cluster c
    boolean clusterFound = artfind(artclusterElementBuckets, artclusterElementBucketCount, artclusterElement_SIZE, nonterminal, k);
    artClusterElementFinds++;
    int clusterElement = artfindIndex;
    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "findClusterElement for pop ");
      arthashTablePrintClusterElement(clusterElement);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d %n", clusterFound ? "" : "not ", clusterElement);
    }

    // Iterate over all GSS nodes in this cluster
    for (int clusterNode = artpoolGet(clusterElement + artclusterElement_nodeList); clusterNode != 0; clusterNode = artpoolGet(
        clusterNode + artgssNode_pop_clusterElementList)) {
      // Iterate over all of the edges in this GSS node
      if (artTrace > 0) {
        artText.printf(ARTTextLevel.TRACE, "popClustered iterating cluster node list to GSS node %d ", clusterNode);
        arthashTablePrintGSSNode(clusterNode);
        artText.printf(ARTTextLevel.TRACE, "%n");
      }
      for (int outEdge = artpoolGet(clusterNode + artgssNode_edgeList); outEdge != 0; outEdge = artpoolGet(outEdge + artgssEdge_edgeList)) {
        if (artTrace > 0) {
          artText.printf(ARTTextLevel.TRACE, "Popping GSSEdge %d ", outEdge);
          arthashTablePrintGSSEdge(outEdge);
          artText.printf(ARTTextLevel.TRACE, "%n");
        }

        artPrimaryPops++;

        int derivationNode = artFindSPPF(artpoolGet(clusterNode + artgssNode_label), artpoolGet(outEdge + artgssEdge_sppfNode), currentsppfNode);
        int outEdgeDestination = artpoolGet(outEdge + artgssEdge_destination);
        int clusterLevel = artpoolGet(outEdgeDestination + artgssNode_level);
        artFindDescriptor(artpoolGet(clusterNode + artclusterElement_label), clusterLevel, currentTokenIndex, derivationNode);
      }

    }

    // add pop element to cluster popList
    // Allocate pop element and head insert on cluster poplist
    artallocateAndLoad(artclusterPopElement_SIZE, artCurrentSPPFNode);
    int popElement = artfindIndex;

    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "popClustered allocated clusterPopElement %d ", popElement);
      arthashTablePrintClusterPopElement(popElement);
      artText.printf(ARTTextLevel.TRACE, ")%n");
    }

    artpoolSet(popElement + artclusterPopElement_popList, artpoolGet(clusterElement + artclusterElement_popList));
    artpoolSet(clusterElement + artclusterElement_popList, popElement);

    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "Exiting popClustered(%s, %d, %d)%n", getArtLabelInternalStrings()[nonterminal], k, currentTokenIndex);
    }
  }

  @Override
  protected void artFindDescriptor(int restartLabel, int gssNode, int currentTokenIndex, int sppfNode) {
    // findDescriptor_add(L, u, i, w)
    // if (L, u, i, w) %not\in U then
    // insert(L, u, i, w) into both U and R
    boolean found = artfind(artdescriptorBuckets, artdescriptorBucketCount, artdescriptor_SIZE, restartLabel, gssNode, currentTokenIndex, sppfNode);
    artDescriptorFinds++;

    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "findDescriptor ");
      arthashTablePrintDescriptor(artfindIndex);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", found ? "" : "not ", artfindIndex);
    }

    if (!found) {
      boolean processingPoppingDescriptor = artPopD[restartLabel];

      if (artFIFODescriptors) { // Insert at tail new code
        if (processingPoppingDescriptor) {
          if (artPoppingDescriptorsToBeProcessed == 0) { // Special case of empty list - no sentinel
            artpool[artfindBlockIndex][++artfindLoadOffset] = artPoppingDescriptorsToBeProcessed;
            artPoppingDescriptorsToBeProcessed = artfindIndex;
            artPoppingDescriptorsToBeProcessedTail = artfindIndex;
          } else {
            artpoolSet(artPoppingDescriptorsToBeProcessedTail + artdescriptor_descriptorList, artfindIndex);
            artpool[artfindBlockIndex][++artfindLoadOffset] = 0;
            artPoppingDescriptorsToBeProcessedTail = artfindIndex;
          }
        } else {
          if (artNonpoppingDescriptorsToBeProcessed == 0) { // Special case of empty list - no sentinel
            artpool[artfindBlockIndex][++artfindLoadOffset] = artNonpoppingDescriptorsToBeProcessed;
            artNonpoppingDescriptorsToBeProcessed = artfindIndex;
            artNonpoppingDescriptorsToBeProcessedTail = artfindIndex;
          } else {
            artpoolSet(artNonpoppingDescriptorsToBeProcessedTail + artdescriptor_descriptorList, artfindIndex);
            artpool[artfindBlockIndex][++artfindLoadOffset] = 0;
            artNonpoppingDescriptorsToBeProcessedTail = artfindIndex;
          }
        }
      } else { // Insert at head - old code
        if (processingPoppingDescriptor) {
          artpool[artfindBlockIndex][++artfindLoadOffset] = artPoppingDescriptorsToBeProcessed;
          artPoppingDescriptorsToBeProcessed = artfindIndex;
        } else {
          artpool[artfindBlockIndex][++artfindLoadOffset] = artNonpoppingDescriptorsToBeProcessed;
          artNonpoppingDescriptorsToBeProcessed = artfindIndex;
        }
      }

    }
  }

  @Override
  protected boolean artTestRepeat(int regexpLabel, int stackTop, int currentTokenIndex, int derivationNode) {
    // boolean testRepeat(T, u, i, w)
    // if (T, u, i, w) \in TR return true else {
    // insert (T, u, i, w) into TR
    // return false
    // }
    boolean found = artfind(arttestRepeatElementBuckets, arttestRepeatElementBucketCount, arttestRepeatElement_SIZE, regexpLabel, stackTop, currentTokenIndex,
        derivationNode);
    artTestRepeatElementFinds++;

    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "testRepeat ");
      arthashTablePrintTestRepeatElement(artfindIndex);
      artText.printf(ARTTextLevel.TRACE, " - %sfound %d%n", found ? "" : "not ", artfindIndex);
    }

    return found;
  }

  @Override
  protected void artCheckAcceptance() {
    if (artTrace > 0) artText.printf(ARTTextLevel.TRACE, "checkAcceptance with rightmost index %d%n", artLexer.artInputLength - artWhitespaceEOSPrefixLength);
    artIsInLanguage = artfind(artsppfNodeBuckets, artsppfNodeBucketCount, 0, artStartSymbolLabel, 0, artLexer.artInputLength - artWhitespaceEOSPrefixLength);

    artSPPFNodeFinds++;
    if (artIsInLanguage)
      artRootSPPFNode = artfindIndex;
    else {
      artRootSPPFNode = 0;
      int rightmostSPPFNode = artFindRightmostTerminalSPPFNode();
      artReportParseError(rightmostSPPFNode);
    }
  }

  @Override
  protected boolean artNoDescriptors() {
    // if (artParseTimeLimit != 0 && artReadClock() > artParseTimeLimit) {
    // isTimedOut = true;
    // System.out.println("Parse time exceeded " + artParseTimeLimit + "ms - aborting");
    // return true;
    // }
    return artNonpoppingDescriptorsToBeProcessed == 0 && artPoppingDescriptorsToBeProcessed == 0;
  }

  @Override
  protected void artUnloadDescriptor() {
    // Nonpopping descriptors have priority over popping descriptors
    boolean processingPoppingDescriptor = artNonpoppingDescriptorsToBeProcessed == 0;

    if (processingPoppingDescriptor) {
      artCurrentDescriptor = artPoppingDescriptorsToBeProcessed;
      artPoppingDescriptors++;
    } else {
      artCurrentDescriptor = artNonpoppingDescriptorsToBeProcessed;
      artNonpoppingDescriptors++;
    }

    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "%n******%n%nProcessing GLL descriptor ");
      arthashTablePrintDescriptor(artCurrentDescriptor);
      artText.printf(ARTTextLevel.TRACE, "%n");
    }

    int[] descriptorPoolBlock = artpool[artCurrentDescriptor >> artpoolAddressOffset];
    int descriptorOffset = artCurrentDescriptor & artpoolAddressMask;

    artCurrentRestartLabel = descriptorPoolBlock[descriptorOffset + artdescriptor_label];
    artCurrentInputPairIndex = descriptorPoolBlock[descriptorOffset + artdescriptor_inputIndex_inputTriple]; // Classical GLL stores the index in the
    // descriptor
    // and we
    // need to turn it into a pair
    artCurrentInputPairReference = artInputFirstPairAtLeftExtent[artCurrentInputPairIndex];
    artCurrentGSSNode = descriptorPoolBlock[descriptorOffset + artdescriptor_gssNode];
    artCurrentSPPFNode = descriptorPoolBlock[descriptorOffset + artdescriptor_sppfNode];

    if (processingPoppingDescriptor)
      artPoppingDescriptorsToBeProcessed = descriptorPoolBlock[descriptorOffset + artdescriptor_descriptorList];
    else
      artNonpoppingDescriptorsToBeProcessed = descriptorPoolBlock[descriptorOffset + artdescriptor_descriptorList];
  }

  // Next, a copy of unloadDescriptor() which loads up currentToken etc
  @Override
  protected void artUnloadDescriptorMGLL() {
    // Nonpopping descriptors have priority over popping descriptors
    boolean processingPoppingDescriptor = artNonpoppingDescriptorsToBeProcessed == 0;

    if (processingPoppingDescriptor) {
      artCurrentDescriptor = artPoppingDescriptorsToBeProcessed;
      artPoppingDescriptors++;
    } else {
      artCurrentDescriptor = artNonpoppingDescriptorsToBeProcessed;
      artNonpoppingDescriptors++;
    }

    if (artTrace > 0) {
      artText.printf(ARTTextLevel.TRACE, "%n******%n%nProcessing MGLL descriptor ");
      arthashTablePrintDescriptorMGLL(artCurrentDescriptor);
      artText.printf(ARTTextLevel.TRACE, "%n");
    }

    int[] descriptorPoolBlock = artpool[artCurrentDescriptor >> artpoolAddressOffset];
    int descriptorOffset = artCurrentDescriptor & artpoolAddressMask;

    artCurrentRestartLabel = descriptorPoolBlock[descriptorOffset + artdescriptor_label];
    artCurrentInputPairReference = descriptorPoolBlock[descriptorOffset + artdescriptor_inputIndex_inputTriple];
    artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];

    artCurrentGSSNode = descriptorPoolBlock[descriptorOffset + artdescriptor_gssNode];
    artCurrentSPPFNode = descriptorPoolBlock[descriptorOffset + artdescriptor_sppfNode];

    if (processingPoppingDescriptor)
      artPoppingDescriptorsToBeProcessed = descriptorPoolBlock[descriptorOffset + artdescriptor_descriptorList];
    else
      artNonpoppingDescriptorsToBeProcessed = descriptorPoolBlock[descriptorOffset + artdescriptor_descriptorList];
  }

  @Override
  protected void artLoadDescriptorInitialMGLL() {
    for (int i = 0; artInputSuccessorBuffer[i] != -1; i++) {
      artFindDescriptor(artStartSymbolLabel, artRootGSSNode, artInputSuccessorBuffer[i], artDummySPPFNode);
    }
  }

  private long artcomputeHistogram(ARTHistogram histogram, int buckets[], int bucketCount) {
    long cardinality = 0;
    for (int bucket = 0; bucket < bucketCount; bucket++) {
      long chainLength = 0;
      for (int element = buckets[bucket]; element != 0; element = artpoolGet(element)) {
        chainLength++;
        cardinality += 1;
      }

      histogram.update(chainLength);
      artOverallHistogram.update(chainLength); // Make summary of whole
    }
    return cardinality;
  }

  private void artcomputeSPPFStatistics() {
    for (int bucket = 0; bucket < artsppfNodeBucketCount; bucket++) {
      for (int element = artsppfNodeBuckets[bucket]; element != 0; element = artpoolGet(element)) {
        int sppfNodeLabel = artpoolGet(element + artsppfNode_label);

        switch (artKindOfs[sppfNodeLabel]) {
        case ARTK_ILLEGAL:
          artSPPFOtherNodes++;
          break;
        case ARTK_EOS:
          artSPPFOtherNodes++;
          break;
        case ARTK_EPSILON:
          artSPPFEpsilonNodes++;
          break;
        case ARTK_BUILTIN_TERMINAL:
          artSPPFTerminalNodes++;
          break;
        case ARTK_CHARACTER_TERMINAL:
          artSPPFTerminalNodes++;
          break;
        case ARTK_CASE_SENSITIVE_TERMINAL:
          artSPPFTerminalNodes++;
          break;
        case ARTK_CASE_INSENSITIVE_TERMINAL:
          artSPPFTerminalNodes++;
          break;
        case ARTK_NONTERMINAL:
          artSPPFNonterminalNodes++;
          break;
        case ARTK_INTERMEDIATE:
          artSPPFIntermediateNodes++;
          break;
        case ARTK_END_OF_RULE:
          artSPPFOtherNodes++;
          break;
        case ARTK_DO_FIRST:
          artSPPFOtherNodes++;
          break;
        case ARTK_OPTIONAL:
          artSPPFOtherNodes++;
          break;
        case ARTK_POSITIVE_CLOSURE:
          artSPPFOtherNodes++;
          break;
        case ARTK_KLEENE_CLOSURE:
          artSPPFOtherNodes++;
          break;
        }

        int sppfPackedNodeFirst = Math.abs(artpoolGet(element + artsppfNode_packNodeList));

        if (sppfPackedNodeFirst != 0 && artpoolGet(sppfPackedNodeFirst + artsppfPackedNode_packNodeList) != 0) artSPPFAmbiguityNodes++;
      }
    }
  }

  @Override
  public void artComputeParseCounts() {
    artOverallHistogram = new ARTHistogram();

    artSPPFNodeHistogram = new ARTHistogram();
    artSPPFNodeCardinality = artcomputeHistogram(artSPPFNodeHistogram, artsppfNodeBuckets, artsppfNodeBucketCount);

    artSPPFPackedNodeHistogram = new ARTHistogram();
    artSPPFPackedNodeCardinality = artcomputeHistogram(artSPPFPackedNodeHistogram, artsppfPackedNodeBuckets, artsppfPackedNodeBucketCount);

    artGSSNodeHistogram = new ARTHistogram();
    artGSSNodeCardinality = artcomputeHistogram(artGSSNodeHistogram, artgssNodeBuckets, artgssNodeBucketCount);

    artGSSEdgeHistogram = new ARTHistogram();
    artGSSEdgeCardinality = artcomputeHistogram(artGSSEdgeHistogram, artgssEdgeBuckets, artgssEdgeBucketCount);

    artPopElementHistogram = new ARTHistogram();
    artPopElementCardinality = artcomputeHistogram(artPopElementHistogram, artpopElementBuckets, artpopElementBucketCount);

    artDescriptorHistogram = new ARTHistogram();
    artDescriptorCardinality = artcomputeHistogram(artDescriptorHistogram, artdescriptorBuckets, artdescriptorBucketCount);

    artTestRepeatElementHistogram = new ARTHistogram();
    artTestRepeatElementCardinality = artcomputeHistogram(artTestRepeatElementHistogram, arttestRepeatElementBuckets, arttestRepeatElementBucketCount);

    artClusterElementHistogram = new ARTHistogram();
    artClusterElementCardinality = artcomputeHistogram(artClusterElementHistogram, artclusterElementBuckets, artclusterElementBucketCount);

    artPackedFamilyArityHistogram = new ARTHistogram();
    artcomputeSPPFStatistics();
  };

  protected int[] artclean(int[] buckets, int bucketCount) {
    if (buckets == null) return new int[bucketCount];

    for (int i = 0; i < buckets.length; i++)
      buckets[i] = 0;

    return buckets;
  }

  @Override
  protected void artInitialise() {
    artParseStartMemory = artMemoryUsed();
    artRestartClock();

    artpoolBlockCount = artpoolBlockInitialCount;
    if (artpool == null) {
      artpool = new int[artpoolBlockCount][];
      artpool[0] = new int[artpoolBlockSize];
    } else
      for (int i = 0; i < artpoolBlockSize; i++)
        artpool[0][i] = 0;

    artpoolBlockTop = 0;
    artpoolOffsetTop = 2; // Block 0, offsets 0 and 1 reserved for 'not found' and illegal values
    artpool[0][1] = -1; // Defensive programming: make the first label element illegal to catch address zero errors

    artsppfNodeBucketCount = artsppfNodeBucketInitialCount;
    artsppfNodeBuckets = artclean(artsppfNodeBuckets, artsppfNodeBucketInitialCount);

    artsppfPackedNodeBucketCount = artsppfPackedNodeBucketInitialCount;
    artsppfPackedNodeBuckets = artclean(artsppfPackedNodeBuckets, artsppfPackedNodeBucketInitialCount);

    artgssNodeBucketCount = artgssNodeBucketInitialCount;
    artgssNodeBuckets = artclean(artgssNodeBuckets, artgssNodeBucketInitialCount);

    artgssEdgeBucketCount = artgssEdgeBucketInitialCount;
    artgssEdgeBuckets = artclean(artgssEdgeBuckets, artgssEdgeBucketInitialCount);

    artpopElementBucketCount = artpopElementBucketInitialCount;
    artpopElementBuckets = artclean(artpopElementBuckets, artpopElementBucketInitialCount);

    artdescriptorBucketCount = artdescriptorBucketInitialCount;
    artdescriptorBuckets = artclean(artdescriptorBuckets, artdescriptorBucketInitialCount);

    arttestRepeatElementBucketCount = arttestRepeatElementBucketInitialCount;
    arttestRepeatElementBuckets = artclean(arttestRepeatElementBuckets, arttestRepeatElementBucketInitialCount);

    artclusterElementBucketCount = artclusterElementBucketInitialCount;
    artclusterElementBuckets = artclean(artclusterElementBuckets, artclusterElementBucketInitialCount);

    artSPPFNodeCardinality = 0;
    artSPPFNodeFinds = 0;
    artSPPFPackedNodeCardinality = 0;
    artSPPFPackedNodeFinds = 0;
    artGSSNodeCardinality = 0;
    artGSSNodeFinds = 0;
    artGSSEdgeCardinality = 0;
    artGSSEdgeFinds = 0;
    artPopElementCardinality = 0;
    artPopElementFinds = 0;
    artDescriptorCardinality = 0;
    artDescriptorFinds = 0;
    artTestRepeatElementCardinality = 0;
    artTestRepeatElementFinds = 0;
    artClusterElementCardinality = 0;
    artClusterElementFinds = 0;
    artSPPFNodeHistogram = null;
    artSPPFPackedNodeHistogram = null;
    artGSSNodeHistogram = null;
    artGSSEdgeHistogram = null;
    artPopElementHistogram = null;
    artDescriptorHistogram = null;
    artTestRepeatElementHistogram = null;
    artClusterElementHistogram = null;
    artOverallHistogram = null;
    artSPPFEpsilonNodes = 0;
    artSPPFTerminalNodes = 0;
    artSPPFNonterminalNodes = 0;
    artSPPFIntermediateNodes = 0;
    artSPPFOtherNodes = 0;
    artSPPFAmbiguityNodes = 0;
    artPoppingDescriptors = 0;
    artNonpoppingDescriptors = 0;
    artPrimaryPops = 0;
    artContingentPops = 0;
    artHashCollisions = 0;
    artHashtableResizes = 0;
  }

  public String artLogStatsV5() {
    long descriptorCount = artDescriptorHistogram.weightedSumBuckets(),

        gssNodeCount = artGSSNodeHistogram.weightedSumBuckets(), gssEdgeCount = artGSSEdgeHistogram.weightedSumBuckets(),
        popElementCount = artPopElementHistogram.weightedSumBuckets(), sppfNodeCount = artSPPFNodeHistogram.weightedSumBuckets(),
        sppfPackNodeCount = artSPPFPackedNodeHistogram.weightedSumBuckets();

    long exactSize = 4 * descriptorCount * artdescriptor_SIZE + gssNodeCount * artgssNode_SIZE + gssEdgeCount * artgssEdge_SIZE
        + popElementCount * artpopElement_SIZE + sppfNodeCount * artsppfNode_SIZE + sppfPackNodeCount * artsppfPackedNode_SIZE;

    long tableSize = 4 * artdescriptorBuckets.length + artgssNodeBuckets.length + artgssEdgeBuckets.length + artpopElementBuckets.length
        + artsppfNodeBuckets.length + artsppfPackedNodeBuckets.length;

    long poolSize = 4 * artGetFirstUnusedElement();

    long tokens = artInputPairBuffer.length / 2;

    long memoryDelta = artParseEndMemory - artParseStartMemory;

    return descriptorCount + "," + gssNodeCount + "," + gssEdgeCount + "," + popElementCount + "," + sppfNodeCount + "," + sppfPackNodeCount + "," + ","
        + artSPPFAmbiguityNodes + "," + memoryDelta + "," + ((double) memoryDelta / tokens) + "," + exactSize + "," + (double) exactSize / tokens + ","
        + tableSize + "," + (double) tableSize / tokens + "," + poolSize + "," + (double) poolSize / tokens + "," + artOverallHistogram.bucketValue(0) + ","
        + artOverallHistogram.bucketValue(1) + "," + artOverallHistogram.bucketValue(2) + "," + artOverallHistogram.bucketValue(3) + ","
        + artOverallHistogram.sumBucketsFrom(4);
  }

}