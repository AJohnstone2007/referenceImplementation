package uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.gll;

public class GLLHP extends ParserHashPool {
  @Override
  protected String subStatistics() {
    int descriptorCount = cardinality(descriptorBuckets), gssNodeCount = cardinality(gssNodeBuckets), gssEdgeCount = cardinality(gssEdgeBuckets),
        popElementCount = cardinality(popElementBuckets), sppfNodeCount = cardinality(sppfNodeBuckets), sppfPackNodeCount = cardinality(sppfPackNodeBuckets);

    long exactSize = 4 * descriptorCount * descriptor_SIZE + gssNodeCount * gssNode_SIZE + gssEdgeCount * gssEdge_SIZE + popElementCount * popElement_SIZE
        + sppfNodeCount * sppfNode_SIZE + sppfPackNodeCount * sppfPackNode_SIZE;

    long tableSize = 4 * descriptorBuckets.length + gssNodeBuckets.length + gssEdgeBuckets.length + popElementBuckets.length + sppfNodeBuckets.length
        + sppfPackNodeBuckets.length;

    long poolSize = 4 * getFirstUnusedElement();

    return descriptorCount + "," + gssNodeCount + "," + gssEdgeCount + "," + popElementCount + "," + sppfNodeCount + "," + sppfPackNodeCount + ","
        + sppfEdgeCount() + "," + sppfAmbiguityCount() + "," + (endMemory - startMemory) + "," + ((double) (endMemory - startMemory) / input.length) + ","
        + exactSize + "," + (double) exactSize / input.length + "," + tableSize + "," + (double) tableSize / input.length + "," + poolSize + ","
        + (double) poolSize / input.length + "," + totalOccupancy();
  }

  /* Stack handling **********************************************************/
  private void call(int nonterminalNi) {
    find(gssNodeBuckets, gssNodeBucketCount, gssNode_SIZE, gni + 1, i);
    int parentGSSNode = findIndex;

    find(gssEdgeBuckets, gssEdgeBucketCount, gssEdge_SIZE, parentGSSNode, sni, dni);
    int gssEdge = findIndex;

    if (findMadeNew) {
      poolSet(gssEdge + gssEdge_edgeList, poolGet(parentGSSNode + gssNode_edgeList));
      poolSet(parentGSSNode + gssNode_edgeList, gssEdge);
      for (int contingent = poolGet(parentGSSNode + gssNode_popList); contingent != 0; contingent = poolGet(contingent + popElement_popList)) {
        int rightChild = poolGet(contingent + popElement_dn);
        enqueueDescriptor(gni + 1, poolGet(rightChild + sppfNode_rightExt), sni, derivationUpdate(gni + 1, dni, rightChild));
      }
      enqueueDescriptorsFor(targetOf[nonterminalNi], i, parentGSSNode, 0);
    }
  }

  private void ret() {
    if (poolGet(sni + gssNode_gn) == endOfStringNodeNi) {
      if (grammar.acceptingNodeNumbers.contains(gni)) accepted |= (i == input.length - 1); // Make gni to boolean array for acceptance testing
      return;
    }
    find(popElementBuckets, popElementBucketCount, popElement_SIZE, i, sni, dni);
    if (findMadeNew) {
      poolSet(findIndex + popElement_popList, poolGet(sni + gssNode_popList));
      poolSet(sni + gssNode_popList, findIndex);
    }

    for (int e = poolGet(sni + gssNode_edgeList); e != 0; e = poolGet(e + gssEdge_edgeList))
      enqueueDescriptor(poolGet(sni + gssNode_gn), i, poolGet(e + gssEdge_dst), derivationUpdate(poolGet(sni + gssNode_gn), poolGet(e + gssEdge_dn), dni));
  }

  /* Derivation handling *****************************************************/
  private int derivationFindNode(int dni, int leftExt, int rightExt) {
    find(sppfNodeBuckets, sppfNodeBucketCount, sppfNode_SIZE, dni, leftExt, rightExt);
    return findIndex;
  }

  private int derivationUpdate(int gni, int leftChild, int rightChild) {
    int symbolNode = derivationFindNode(kindOf[gni] == END ? gni + 1 : gni,
        leftChild == 0 ? poolGet(rightChild + sppfNode_leftExt) : poolGet(leftChild + sppfNode_leftExt), poolGet(rightChild + sppfNode_rightExt));
    find(sppfPackNodeBuckets, sppfPackNodeBucketCount, sppfPackNode_SIZE, gni,
        leftChild == 0 ? poolGet(rightChild + sppfNode_leftExt) : poolGet(leftChild + sppfNode_rightExt), leftChild, rightChild);
    if (findMadeNew) { // New packed node: add to this SPPFnode's packed node list
      poolSet(findIndex + sppfPackNode_packNodeList, poolGet(symbolNode + sppfNode_packNodeList));
      poolSet(symbolNode + sppfNode_packNodeList, findIndex);
    }
    return symbolNode;
  }

  private void d(int width) {
    dni = derivationUpdate(gni + 1, dni, derivationFindNode(gni, i, i + width));
  }

  /* Thread handling *********************************************************/
  int gni;
  int sni;
  int dni;
  int descriptorQueue = 0;

  private boolean dequeueDescriptor() { // load current descriptor
    if (descriptorQueue == 0) return false;
    gni = poolGet(descriptorQueue + descriptor_gn);
    i = poolGet(descriptorQueue + descriptor_i);
    sni = poolGet(descriptorQueue + descriptor_sn);
    dni = poolGet(descriptorQueue + descriptor_dn);

    descriptorQueue = poolGet(descriptorQueue + descriptor_queue);
    return true;
  }

  private void enqueueDescriptor(int gni, int i, int sni, int dni) {
    // System.out.println("enqueueDescriptor(" + gni + "," + i + "," + sni + "," + dni + ")");
    find(descriptorBuckets, descriptorBucketCount, descriptor_SIZE, gni, i, sni, dni);
    if (findMadeNew) {
      poolSet(findIndex + descriptor_queue, descriptorQueue);
      descriptorQueue = findIndex;
    }
  }

  private void enqueueDescriptorsFor(int ni, int i, int parentGSSNode, int sppfNode) {
    int[] productions = altOf[ni];
    int pi = 0;
    while (productions[pi] != 0) // enqueue the start nonterminl's productions
      enqueueDescriptor(productions[pi++] + 1, i, parentGSSNode, sppfNode);
  }

  /* Parser ******************************************************************/
  private void initialise() {
    initialisehashPool();
    find(gssNodeBuckets, gssNodeBucketCount, gssNode_SIZE, endOfStringNodeNi, 0);
    int gssRoot = findIndex;
    i = 0;
    sni = gssRoot;
    dni = 0;
    enqueueDescriptorsFor(startNonterminalNodeNi, i, sni, dni);
    startMemory = memoryUsed();
    startTime = readClock();
  }

  @Override
  public void parse() {
    gllHP();
    endTime = readClock();
    endMemory = memoryUsed();
    if (!accepted) {
      int rightmost = 0;
      // TODO Implement
      // Scan all SPPF modes for rightmost index
      // for (SPPFNode s : sppf.keySet())
      // rightmost = Math.max(rightmost, sppf.get(s).rightIndex);
      System.out.println("Reject: widest parse consumed " + rightmost + " tokens");
    }
  }

// @formatter:off
void gllHP() {
 initialise();
 nextDescriptor: while (dequeueDescriptor())
 while (true) {
  switch (kindOf[gni]) {
  case T: if (input[i] == elementOf[gni])
           {d(1); i++; gni++; break;}
           else continue nextDescriptor;
   case N: call(gni); continue nextDescriptor;
   case EPS: d(0); gni++; break;
   case END: ret(); continue nextDescriptor;
   }}}
}
