package uk.ac.rhul.cs.csle.art.cfg.referenceFamily.gll;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.Reference;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.ReferenceParser;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.GKind;
import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.grammar.Grammar;

public abstract class ParserHashPool extends ReferenceParser {
  // Tabular copies of grammar information installed by setGrammar()
  protected int endOfStringNodeNi;
  protected int startNonterminalNodeNi;
  protected int kindOf[];
  protected int altOf[][];
  protected int targetOf[];
  protected int elementOf[];
  protected final int T = 1; // This is a bit weird - these constants must be compile time, and GKind.T.ordinal() is runtime, so we hardcode and check bellow
  protected final int EPS = 5;
  protected final int N = 6;
  protected final int END = 8;

  @Override
  protected void setGrammar(Grammar grammar) {
    this.grammar = grammar;
    endOfStringNodeNi = grammar.endOfStringNode.num;
    startNonterminalNodeNi = grammar.rules.get(grammar.startNonterminal).num;
    kindOf = grammar.makeKindsArray();
    altOf = grammar.makeAltsArray();
    targetOf = grammar.makeCallTargetsArray();
    elementOf = grammar.makeElementOfArray();
    // Defensive programming - make sure we've not messed up the enumeration value
    if (T != GKind.T.ordinal()) Reference.fatal("Enumeration mismatch for T - check ParserHashPool.java for consistency with Kind enumeration");
    if (EPS != GKind.EPS.ordinal()) Reference.fatal("Enumeration mismatch for EPS - check ParserHashPool.java for consistency with Kind enumeration");
    if (N != GKind.N.ordinal()) Reference.fatal("Enumeration mismatch for N - check ParserHashPool.java for consistency with Kind enumeration");
    if (END != GKind.END.ordinal()) Reference.fatal("Enumeration mismatch for END - check ParserHashPool.java for consistency with Kind enumeration");

    // Debug: print precomputed tables
    // System.out.println(grammar);
    // for (int i = 0; i < kindOf.length; i++)
    // System.out.print(i + ":" + kindOf[i] + " ");
    // System.out.println();
    // for (int i = 0; i < altOf.length; i++) {
    // System.out.print(i + ":");
    // if (altOf[i] == null)
    // System.out.print("null");
    // else
    // for (int j = 0; j < altOf[i].length; j++)
    // System.out.print(altOf[i][j] + " ");
    // System.out.println();
    // }
    // for (int i = 0; i < targetOf.length; i++)
    // System.out.println(i + ":" + targetOf[i] + " ");
    // for (int i = 0; i < elementOf.length; i++)
    // System.out.println(i + ":" + elementOf[i] + " ");
  }

  /** Constant field offsets. Offset zero links to next in hash chain ********/
  protected final int gssNode_gn = 1; // Key
  protected final int gssNode_i = 2; // Key
  protected final int gssNode_edgeList = 3;
  protected final int gssNode_popList = 4;
  protected final int gssNode_SIZE = 5; // Key size 2

  protected final int gssEdge_src = 1; // Key
  protected final int gssEdge_dst = 2; // Key
  protected final int gssEdge_dn = 3; // Key
  protected final int gssEdge_edgeList = 4;
  protected final int gssEdge_SIZE = 5; // Key size 3

  protected final int popElement_i = 1; // Key
  protected final int popElement_sn = 2; // Key
  protected final int popElement_dn = 3; // Key
  protected final int popElement_popList = 4;
  protected final int popElement_SIZE = 5; // Key size 3

  protected final int sppfNode_gn = 1; // Key
  protected final int sppfNode_leftExt = 2; // Key
  protected final int sppfNode_rightExt = 3; // Key
  protected final int sppfNode_packNodeList = 4;
  protected final int sppfNode_SIZE = 5; // Key size 3

  protected final int sppfPackNode_parent = 1; // Key
  protected final int sppfPackNode_gn = 2; // Key
  protected final int sppfPackNode_pivot = 3; // Key
  protected final int sppfPackNode_leftChild = 4;
  protected final int sppfPackNode_rightChild = 5;
  protected final int sppfPackNode_packNodeList = 6;
  protected final int sppfPackNode_SIZE = 7; // Key size 3

  protected final int descriptor_gn = 1; // Key
  protected final int descriptor_i = 2; // Key
  protected final int descriptor_sn = 3; // Key
  protected final int descriptor_dn = 4; // Key
  protected final int descriptor_queue = 5; // zero once processed, otherwise next in the queue of awaited descriptors
  protected final int descriptor_SIZE = 6; // Key size 4

  /**
   * An expandable pool of integers which supports sequential allocation of small blocks. There is no facility to free memory once it has been allocated.
   *
   * We use a 2-D array of integers organised as an array of pointers to 1-D arrays of integers called poolBlocks. Initially, only one poolBlock is allocated.
   *
   * Further poolBlocks are allocated as needed. If the required number of poolBlocks exceeds the capacity of pool, it is resized by 150%
   */
  protected int[][] pool;
  private final int poolBlockInitialCount = 1024;
  private final int poolAddressOffset = 26; // poolAddressOffset must be a power of 2
  private final int poolBlockSize = 1 << poolAddressOffset;
  private final int poolAddressMask = poolBlockSize - 1;

  private int poolBlockCount; // Total number of available pool blocks
  private int poolBlockTop; // Current allocation point: block number
  private int poolOffsetTop; // Current allocation point: offset

  protected int getFirstUnusedElement() {
    return (poolBlockTop * poolBlockSize) + poolOffsetTop + 1;
  }

  // The allocation function: check to see if the current poolBlock has enough space; if not make a new one.
  protected void allocate(int size) {
    if (poolOffsetTop + size > poolBlockSize) { // need new poolBlock
      poolBlockTop++;

      if (poolBlockTop >= poolBlockCount) { // resize pointer array
        poolBlockCount += poolBlockCount / 2;
        int[][] newPool = new int[poolBlockCount][];

        for (int i = 0; i < poolBlockTop; i++)
          newPool[i] = pool[i]; // Copy old pointers

        pool = newPool;
      }
      pool[poolBlockTop] = new int[poolBlockSize];
      poolOffsetTop = 0;
    }
    poolOffsetTop += size; // Perform the actual allocation
  }

  protected int poolGet(int index) {
    return pool[index >> poolAddressOffset][index & poolAddressMask];
  }

  protected void poolSet(int index, int value) {
    pool[index >> poolAddressOffset][index & poolAddressMask] = value;
  }

  /**
   * Hash tables: array <name>Buckets contains the pool index of the first element in each hash list; each element in the hash list must have the pool index of
   * its successor in its first element.
   *
   * Future extension: rehash function when load factor exceeds threshold
   */

  private final int loadFactor = 2;

  // DescriptorMax data from ansi_c and gtb_src; primes set for load factor 2. This is the experimental setup for the SLE23 paper
  protected int sizeKludge = 1; // Leave this at one for normal operations. Can be used to zoom up the bucket count, but probably loses coprime property
  protected final int descriptorMax = 6_578_603;
  protected final int descriptorPrime = 13_157_231;
  protected final int descriptorBucketInitialCount = descriptorPrime * sizeKludge;
  protected int descriptorBucketCount;
  protected int descriptorBuckets[];

  protected final int gssNodeMax = 946_975;
  protected final int gssNodePrime = 1_893_967;
  protected final int gssNodeBucketInitialCount = gssNodePrime * sizeKludge;
  protected int gssNodeBucketCount;
  protected int gssNodeBuckets[];

  protected final int gssEdgeMax = 2_989_166;
  protected final int gssEdgePrime = 5_978_341;
  protected final int gssEdgeBucketInitialCount = gssEdgePrime * sizeKludge;
  protected int gssEdgeBucketCount;
  protected int gssEdgeBuckets[];

  protected final int PopElementMax = 776_934;
  protected final int popElementPrime = 1_553_869;
  protected final int popElementBucketInitialCount = popElementPrime * sizeKludge;
  protected int popElementBucketCount;
  protected int popElementBuckets[];

  protected final int sppfNodeMax = 881_128;
  protected final int sppfNodePrime = 1_762_259;
  protected final int sppfNodeBucketInitialCount = sppfNodePrime * sizeKludge;
  protected int sppfNodeBucketCount;
  protected int sppfNodeBuckets[];

  protected final int sppfPackNodeMax = 829_463;
  protected final int sppfPackNodePrime = 1_658_927;
  protected final int sppfPackNodeBucketInitialCount = sppfPackNodePrime * sizeKludge;
  protected int sppfPackNodeBucketCount;
  protected int sppfPackNodeBuckets[];

  private int[] clean(int[] buckets, int bucketCount) {
    if (buckets == null) return new int[bucketCount];

    for (int i = 0; i < buckets.length; i++)
      buckets[i] = 0;

    return buckets;
  }

  protected void initialisehashPool() {
    poolBlockCount = poolBlockInitialCount;
    if (pool == null) {
      pool = new int[poolBlockCount][];
      pool[0] = new int[poolBlockSize];
    } else
      for (int i = 0; i < poolBlockSize; i++)
        pool[0][i] = 0;

    poolBlockTop = 0;
    poolOffsetTop = 2; // Block 0, offsets 0 and 1 reserved for 'not found' and illegal values
    pool[0][1] = -1; // Defensive programming: make the first label element illegal to catch address zero errors

    descriptorBucketCount = descriptorBucketInitialCount;
    descriptorBuckets = clean(descriptorBuckets, descriptorBucketCount);

    gssNodeBucketCount = gssNodeBucketInitialCount;
    gssNodeBuckets = clean(gssNodeBuckets, gssNodeBucketCount);

    gssEdgeBucketCount = gssEdgeBucketInitialCount;
    gssEdgeBuckets = clean(gssEdgeBuckets, gssEdgeBucketCount);

    popElementBucketCount = popElementBucketInitialCount;
    popElementBuckets = clean(popElementBuckets, popElementBucketCount);

    sppfNodeBucketCount = sppfNodeBucketInitialCount;
    sppfNodeBuckets = clean(sppfNodeBuckets, sppfNodeBucketCount);

    sppfPackNodeBucketCount = sppfPackNodeBucketInitialCount;
    sppfPackNodeBuckets = clean(sppfPackNodeBuckets, sppfPackNodeBucketCount);

  }

  /* Low level hashpool functions *********************************************/

  private final int hashPrime = 1013; // Another large prime
  private int hashResult;

  // Knuth style multiplier hash functions for 32-bits
  private void hash(int hashBucketCount, int a, int b) {
    hashResult = (a + (b * hashPrime));
    hashResult %= hashBucketCount;
    if (hashResult < 0) hashResult = -hashResult;
  }

  private void hash(int hashBucketCount, int a, int b, int c) {
    hashResult = ((a + (b * hashPrime)) + (c * hashPrime));
    hashResult %= hashBucketCount;
    if (hashResult < 0) hashResult = -hashResult;
  }

  private void hash(int hashBucketCount, int a, int b, int c, int d) {
    hashResult = ((a + (b * hashPrime)) + (c * hashPrime) + (d * hashPrime));
    hashResult %= hashBucketCount;
    if (hashResult < 0) hashResult = -hashResult;
  }

  /** Low level find functions: lookup an element and create if not present ***/

  /* The result of a find is left in these members */
  protected boolean findMadeNew;
  protected int findIndex; // Combined index
  private int findBlockIndex; // top p of index
  private int findOffset; // bottom p of index
  private int findBlock[]; // reference to block containing this index
  private int findLoadOffset; // offset to first unused field

  /* Lookup key <a,b> on hashBuckets. If not found, allocate allocationSize and load <a,b> to offsets (1,2) */
  protected void find(int[] hashBuckets, int hashBucketCount, int allocationSize, int a, int b) {
    hash(hashBucketCount, a, b);

    findIndex = hashBuckets[hashResult];
    do {
      findBlockIndex = findIndex >> poolAddressOffset;
      findOffset = findIndex & poolAddressMask;
      findBlock = pool[findBlockIndex];

      if (a == findBlock[findOffset + 1] && b == findBlock[findOffset + 2]) {
        findMadeNew = false;
        return;
      }

      findIndex = findBlock[findOffset]; // Step to next
    } while (findIndex != 0);

    if (allocationSize != 0) { // Note: allocation size turns find into lookup...
      allocate(allocationSize);
      findOffset = poolOffsetTop - allocationSize;
      findBlockIndex = poolBlockTop;
      findBlock = pool[findBlockIndex];
      findIndex = findBlockIndex << poolAddressOffset | findOffset;
      findLoadOffset = findOffset;

      findBlock[findOffset] = hashBuckets[hashResult];
      hashBuckets[hashResult] = findIndex;

      findBlock[++findLoadOffset] = a;
      findBlock[++findLoadOffset] = b;
    }
    findMadeNew = true;
    return;
  }

  /* Lookup key <a,b,c> on hashBuckets. If not found, allocate allocationSize and load <a,b> to offsets (1,2,3) */
  protected void find(int[] hashBuckets, int hashBucketCount, int allocationSize, int a, int b, int c) {
    hash(hashBucketCount, a, b, c);

    findIndex = hashBuckets[hashResult];
    do {
      findBlockIndex = findIndex >> poolAddressOffset;
      findOffset = findIndex & poolAddressMask;
      findBlock = pool[findBlockIndex];

      if (a == findBlock[findOffset + 1] && b == findBlock[findOffset + 2] && c == findBlock[findOffset + 3]) {
        findMadeNew = false;
        return;
      }
      findIndex = findBlock[findOffset]; // Step to next
    } while (findIndex != 0);

    if (allocationSize != 0) {
      allocate(allocationSize);
      findOffset = poolOffsetTop - allocationSize;
      findBlockIndex = poolBlockTop;
      findBlock = pool[findBlockIndex];
      findIndex = findBlockIndex << poolAddressOffset | findOffset;
      findLoadOffset = findOffset;

      findBlock[findOffset] = hashBuckets[hashResult];
      hashBuckets[hashResult] = findIndex;

      findBlock[++findLoadOffset] = a;
      findBlock[++findLoadOffset] = b;
      findBlock[++findLoadOffset] = c;
    }
    findMadeNew = true;
    return;
  }

  /* Lookup key <a,b,c,d> on hashBuckets. If not found, allocate allocationSize and load <a,b> to offsets (1,2,3,4) */
  protected void find(int[] hashBuckets, int hashBucketCount, int allocationSize, int a, int b, int c, int d) {
    hash(hashBucketCount, a, b, c, d);

    findIndex = hashBuckets[hashResult];
    do {
      findBlockIndex = findIndex >> poolAddressOffset;
      findOffset = findIndex & poolAddressMask;
      findBlock = pool[findBlockIndex];

      if (a == findBlock[findOffset + 1] && b == findBlock[findOffset + 2] && c == findBlock[findOffset + 3] && d == findBlock[findOffset + 4]) {
        findMadeNew = false;
        return;
      }

      findIndex = findBlock[findOffset]; // Step to next
    } while (findIndex != 0);

    if (allocationSize != 0) {
      allocate(allocationSize);
      findOffset = poolOffsetTop - allocationSize;
      findBlockIndex = poolBlockTop;
      findBlock = pool[findBlockIndex];
      findIndex = findBlockIndex << poolAddressOffset | findOffset;
      findLoadOffset = findOffset;

      findBlock[findOffset] = hashBuckets[hashResult];
      hashBuckets[hashResult] = findIndex;

      findBlock[++findLoadOffset] = a;
      findBlock[++findLoadOffset] = b;
      findBlock[++findLoadOffset] = c;
      findBlock[++findLoadOffset] = d;
    }
    findMadeNew = true;
    return;
  }

  int cardinality(int[] hashBuckets) {
    int count = 0;
    for (int bucket : hashBuckets)
      for (int chain = bucket; chain != 0; chain = poolGet(chain))
        count++;

    return count;
  }

  private int[] occupancies;

  void occupancyReset() {
    occupancies = new int[5];
  }

  String occupancy(int[] table) {
    occupancyReset();
    return accumulateOccupancy(table);
  }

  String accumulateOccupancy(int[] table) {
    int occupanciesMax = 5;
    for (int bucket : table) {
      int occupancy = 0;
      for (int chain = bucket; chain != 0; chain = poolGet(chain))
        occupancy++;
      if (occupancy > occupanciesMax - 1) occupancy = occupanciesMax - 1;
      occupancies[occupancy]++;
    }

    String res = "";
    for (int i = 0; i < occupanciesMax; i++)
      res += occupancies[i] + ",";
    return res;
  }

  String totalOccupancy() {
    occupancyReset();
    accumulateOccupancy(descriptorBuckets);
    accumulateOccupancy(gssNodeBuckets);
    accumulateOccupancy(gssEdgeBuckets);
    accumulateOccupancy(popElementBuckets);
    accumulateOccupancy(sppfNodeBuckets);
    return accumulateOccupancy(sppfPackNodeBuckets);
  }

  int sppfEdgeCount() {
    int count = 0;
    for (int bucket : sppfNodeBuckets)
      for (int chain = bucket; chain != 0; chain = poolGet(chain))
        for (int packNode = poolGet(chain + sppfNode_packNodeList); packNode != 0; packNode = poolGet(packNode + sppfPackNode_packNodeList)) {
          count++; // Inedge
          if (poolGet(packNode + sppfPackNode_leftChild) == 0) count++;
          if (poolGet(packNode + sppfPackNode_rightChild) == 0) count++;
        }
    return count;
  }

  int sppfAmbiguityCount() {
    int count = 0;
    for (int bucket : sppfNodeBuckets)
      for (int chain = bucket; chain != 0; chain = poolGet(chain))
        if (poolGet(poolGet(chain + sppfNode_packNodeList) + sppfPackNode_packNodeList) != 0) count++;
    return count;
  }
}