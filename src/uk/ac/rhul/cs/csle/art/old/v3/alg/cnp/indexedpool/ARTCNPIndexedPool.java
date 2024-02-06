package uk.ac.rhul.cs.csle.art.old.v3.alg.cnp.indexedpool;

import java.time.ZonedDateTime;

import uk.ac.rhul.cs.csle.art.old.util.pool.ARTPool;
import uk.ac.rhul.cs.csle.art.old.util.slotarray.ARTSlotArray;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTTextHandlerConsole;
import uk.ac.rhul.cs.csle.art.old.v3.alg.ARTParserBase;
import uk.ac.rhul.cs.csle.art.old.v3.lex.ARTLexerV3;

public class ARTCNPIndexedPool extends ARTParserBase {

  protected int m;// Length of the input
  protected int cU;
  protected int cI;
  protected int artSlot;
  protected int descriptorFinds;
  protected int bsrFinds; // Count accesses to upsilon, NOT including scan of upsilon for start candiates in acceptance testing
  protected int crfKeyFinds;
  protected int crfLeafFinds;

  protected int[] input;

  // Note to self: we have implemented a forest of trees, rather than the CRF, in the sense that a CRF leaf node can appear in the leafSet of more than one

  protected ARTPool pool;
  protected int startSymbol;
  protected int epsilonSlot;
  // protected String[] symbolStrings;

  // Structure of a bsr set element: 4_0
  protected final int bsrElementSlotOffset = 0;
  protected final int bsrElementIOffset = 1;
  protected final int bsrElementKOffset = 2;
  protected final int bsrElementJOffset = 3;

  protected String bsrElementToString(int base) {
    if (base == 0) return "null";
    return "<" + pool.poolGet(base) + ", " + pool.poolGet(base + bsrElementIOffset) + ", " + pool.poolGet(base + bsrElementKOffset) + ", "
        + pool.poolGet(base + bsrElementJOffset) + ">";
  }

  protected int upsilon; // A set of bsrElements

  // Structure of a descriptor: 3_0
  protected final int descriptorSlotOffset = 0;
  protected final int descriptorKOffset = 1;
  protected final int descriptorIOffset = 2;

  protected int U; // A set of descriptors

  // structure of a descriptor list element: the base address of a descriptor, and the base address of the next list element
  protected final int descriptorListElementDescriptorOffset = 0;
  protected final int descriptorListElementNextOffset = 1;

  protected int R; // A list of references to descriptors

  // structure of a CRF leaf node
  protected final int CRFleafSlotOffset = 0;
  protected final int CRFleafHOffset = 1;

  // Structure of a CRF node which is a map from (nonterminal X h) -> (popSetPartition x leafSet) which is a 2_2
  // These nodes encode both the Call-Return Forest and the P set
  protected final int CRFNodeNonterminalOffset = 0;
  protected final int CRFNodeHOffset = 1;
  protected final int CRFNodePopSetPartitionOffset = 2;
  protected final int CRFNodeLeafSetOffset = 3;

  protected int CRF; // The Call-Return Forest

  // Hash table sizes
  protected final int largePrime = 99999989;
  protected final int upsilonBucketCount = 500000;
  protected final int UBucketCount = 500000;
  protected final int CRFBucketCount = 500000;
  protected final int CRFPopSetPartitionBucketCount = 100;
  protected final int CRFleafSetBucketCount = 100;

  public ARTCNPIndexedPool(ARTSlotArray artSlotArray) {
    super(artSlotArray);
    epsilonSlot = artSlotArray.epsilon;
    startSymbol = artSlotArray.startSymbol;
  }

  // This costructor is implictly called by generated parsers which have all of the grammar information embedded within them
  public ARTCNPIndexedPool() {
    this(null);
  }

  // ntAdd() needs a sequence of production first slots for each nonterminal
  // Pass in firstSlots[N] as well as N
  protected void ntAdd(int N, int i) {
    if (artTrace > 0) artTraceText.println("ntAdd(" + artSlotArray.symbolJavaStrings[N] + ", " + i + ")");
    for (int p = 0; artSlotArray.slotIndex[N][p] != 0; p++) {
      int firstSlot = artSlotArray.slotIndex[N][p] + 1; // Add first slot in production, not the production label itself
      if (testSelect(input[i], N, firstSlot)) dscAdd(firstSlot, i, i);
    }
  }

  protected void ntAddTemplate(int N, int i, int[] firstSlots) {
    if (artTrace > 0) artTraceText.println("ntAdd(" + artSlotArray.symbolJavaStrings[N] + ", " + i + ")");
    for (int f = 0; firstSlots[f] != 0; f++)
      if (testSelect(input[i], N, f)) dscAdd(f, i, i);
  }

  // testSelect() needs first(alpha) and follow(X)
  // pass in first(alpha) instead of alpha and follow(X) instead of X
  protected boolean testSelect(int b, int X, int alpha) {
    if (artTrace > 0) artTraceText.println(
        "testSelect(" + artSlotArray.symbolJavaStrings[b] + ", " + artSlotArray.symbolJavaStrings[X] + ", " + artSlotArray.symbolJavaStrings[alpha] + ")");
    return artSlotArray.mergedSets[artSlotArray.slotFirstSetAddresses[alpha]][b]
        || (artSlotArray.mergedSets[artSlotArray.slotFirstSetAddresses[alpha]][epsilonSlot]
            && artSlotArray.mergedSets[artSlotArray.nonterminalFollowSetAddresses[X]][b]);
    // return true;
  }

  // protected boolean testSelectTemplate(int b, int X, int alpha, boolean[] firstAlpha, boolean[] followX) {
  // if (artTrace > 0) artTraceText.println("testSelectTemplate(" + artSlotArray.symbolJavaStrings[b] + ", " + artSlotArray.symbolJavaStrings[X] + ", "
  // + artSlotArray.symbolJavaStrings[alpha] + ")");
  // return firstAlpha[b] || firstAlpha[epsilonSlot] && followX[b];
  // }

  // dscAdd() is independent of slotArray
  // no changes required
  protected void dscAdd(int slot, int k, int i) {
    if (artTrace > 0) artTraceText.println("dscAdd(" + artSlotArray.symbolJavaStrings[slot] + ", " + k + ", " + i + ")");

    descriptorFinds++;
    int added = pool.mapFind_3_0(U, slot, k, i);
    if (!pool.found) {
      int newListElement = pool.poolAllocate(2);
      pool.poolPut(newListElement + descriptorListElementDescriptorOffset, added);
      pool.poolPut(newListElement + descriptorListElementNextOffset, R);
      R = newListElement;
    }
  }

  // rtn() is independent of slotArray
  // no changes required
  protected void rtn(int X, int k, int j) {
    if (artTrace > 0) artTraceText.println("rtn(" + artSlotArray.symbolJavaStrings[X] + ", " + k + ", " + j + ")");

    int CRFNode = pool.mapFind_2_2(CRF, X, k); // This must already exist
    crfKeyFinds++;

    int popSetPartition = pool.poolGet(CRFNode + CRFNodePopSetPartitionOffset);
    int leafSet = pool.poolGet(CRFNode + CRFNodeLeafSetOffset);

    pool.mapFind_1_0(popSetPartition, j);
    if (!pool.found) {
      for (int v = pool.mapIteratorFirst1(leafSet); v != 0; v = pool.mapIteratorNext1()) {
        int slot = pool.poolGet(v + CRFleafSlotOffset);
        int H = pool.poolGet(v + CRFleafHOffset);

        dscAdd(slot, H, j);
        bsrAdd(slot, H, k, j);
      }
    }
  }

  protected void call(int L, int i, int j) {
    if (artTrace > 0) artTraceText.println("call(" + artSlotArray.symbolJavaStrings[L] + ", " + i + ", " + j + ")");

    int X = artSlotArray.slotRightSymbols[L - 1];
    // int u = pool.mapFind2_2(CRF, L, i);
    crfLeafFinds++;

    // This is normalisation of the leaf element, it keeps the leaf cardinality to its real value. We shalln't use this in HashPool
    // BUT MAYBE WE SHOULD!
    // if (CRFleaves.containsKey(u))
    // u = CRFleaves.get(u);
    // else
    // CRFleaves.put(u, u);

    int crfNode = pool.mapFind_2_2(CRF, X, j); // Add (X,j) to CRF
    crfKeyFinds++;
    if (!pool.found) {
      pool.poolPut(crfNode + CRFNodeLeafSetOffset, pool.mapMake(CRFleafSetBucketCount)); // Allocate the leaf set for the new CRF node (X,j)
      pool.poolPut(crfNode + CRFNodePopSetPartitionOffset, pool.mapMake(CRFPopSetPartitionBucketCount)); // Allocate the pop set partition for the
                                                                                                         // new CRF node (X,j)
      pool.mapFind_2_2(pool.poolGet(crfNode + CRFNodeLeafSetOffset), L, i); // Add (L,i) to the leaf set
      ntAdd(X, j);
    } else {
      int leafSet = pool.poolGet(crfNode + CRFNodeLeafSetOffset);
      pool.mapFind_2_2(leafSet, L, i); // Add (L,i) to the leaf set
      if (!pool.found) { // If the leaf set for (X, j) does not contain (L, i)
        int popSetPartition = pool.poolGet(crfNode + CRFNodePopSetPartitionOffset);
        for (int hh = pool.mapIteratorFirst1(popSetPartition); hh != 0; hh = pool.mapIteratorNext1()) {
          int h = pool.poolGet(hh);
          if (artTrace > 0) artTraceText.println("Contingent PP actions for cluster node " + crfNode + " index = " + h);
          dscAdd(L, i, h);
          bsrAdd(L, i, j, h);
        }
      }
    }
  }

  // call() requires X which is computed from slotRightSymbol(L-1)
  protected void callTemplate(int L, int i, int j, int X /* call with artSlotArray.slotRightSymbols[L - 1] */ ) {
    if (artTrace > 0) artTraceText.println("call(" + artSlotArray.symbolJavaStrings[L] + ", " + i + ", " + j + ")");

    // int u = pool.mapFind2_2(CRF, L, i);
    crfLeafFinds++;

    // This is normalisation of the leaf element, it keeps the leaf cardinality to its real value. We shalln't use this in HashPool
    // BUT MAYBE WE SHOULD!
    // if (CRFleaves.containsKey(u))
    // u = CRFleaves.get(u);
    // else
    // CRFleaves.put(u, u);

    int crfNode = pool.mapFind_2_2(CRF, X, j); // Add (X,j) to CRF
    crfKeyFinds++;
    if (!pool.found) {
      pool.poolPut(crfNode + CRFNodeLeafSetOffset, pool.mapMake(CRFleafSetBucketCount)); // Allocate the leaf set for the new CRF node (X,j)
      pool.poolPut(crfNode + CRFNodePopSetPartitionOffset, pool.mapMake(CRFPopSetPartitionBucketCount)); // Allocate the pop set partition for the
                                                                                                         // new CRF node (X,j)
      pool.mapFind_2_2(pool.poolGet(crfNode + CRFNodeLeafSetOffset), L, i); // Add (L,i) to the leaf set
      ntAdd(X, j);
    } else {
      int leafSet = pool.poolGet(crfNode + CRFNodeLeafSetOffset);
      pool.mapFind_2_2(leafSet, L, i); // Add (L,i) to the leaf set
      if (!pool.found) { // If the leaf set for (X, j) does not contain (L, i)
        int popSetPartition = pool.poolGet(crfNode + CRFNodePopSetPartitionOffset);
        for (int hh = pool.mapIteratorFirst1(popSetPartition); hh != 0; hh = pool.mapIteratorNext1()) {
          int h = pool.poolGet(hh);
          if (artTrace > 0) artTraceText.println("Contingent PP actions for cluster node " + crfNode + " index = " + h);
          dscAdd(L, i, h);
          bsrAdd(L, i, j, h);
        }
      }
    }
  }

  // bsrAdd() needs isEpsilonSlotOrZeroSlot(slot)
  // Make two versions, one for isEpsilonSlotOrZeroSlot true and one for false
  protected void bsrAddTemplateisEpsilonSlotOrZeroSlotTrue(int slot, int i, int k, int j) {
    if (artTrace > 0)
      artTraceText.print("bsrAddTemplateisEpsilonSlotOrZeroSlotTrue(" + artSlotArray.symbolJavaStrings[slot] + ", " + i + ", " + k + ", " + j + ")");
    int added = pool.mapFind_4_0(upsilon, artSlotArray.slotGetProductionL(slot), i, k, j);
    bsrFinds++;
    if (artTrace > 0) artTraceText.println(" added " + bsrElementToString(added));
  }

  protected void bsrAddTemplateisEpsilonSlotOrZeroSlotFalse(int slot, int i, int k, int j) {
    if (artTrace > 0) artTraceText.print("bsrAdd(" + artSlotArray.symbolJavaStrings[slot] + ", " + i + ", " + k + ", " + j + ")");
    int added = 0;
    if (artSlotArray.prefixLengths[slot] > 1) {
      added = pool.mapFind_4_0(upsilon, artSlotArray.prefixSlotMap[slot], i, k, j);
      bsrFinds++;
    }
    if (artTrace > 0) artTraceText.println(" added " + bsrElementToString(added));
  }

  protected void bsrAdd(int slot, int i, int k, int j) {
    if (artTrace > 0) artTraceText.print("bsrAdd(" + artSlotArray.symbolJavaStrings[slot] + ", " + i + ", " + k + ", " + j + ")");
    int added = 0;
    if (artSlotArray.isEpsilonSlotOrZeroSlot(slot)) {
      added = pool.mapFind_4_0(upsilon, artSlotArray.slotGetProductionL(slot), i, k, j);
      bsrFinds++;
    } else if (artSlotArray.prefixLengths[slot] > 1) {
      added = pool.mapFind_4_0(upsilon, artSlotArray.prefixSlotMap[slot], i, k, j);
      bsrFinds++;
    }
    if (artTrace > 0) artTraceText.println(" added " + bsrElementToString(added));
  }

  /**
   * These are dynamic interpreter support functions: do not statisice
   *
   **/
  protected void processEoC(int slot) {
    if (artSlotArray.mergedSets[artSlotArray.nonterminalFollowSetAddresses[artSlotArray.slotLHSSymbols[slot]]][input[cI]])
      rtn(artSlotArray.slotLHSSymbols[slot], cU, cI);
  }

  protected void processCat(int slot) {
    if (artTrace > 0) artTraceText.println("Start of cat with cI = " + cI + " on slot " + artSlotArray.symbolJavaStrings[slot]);
    while (true) {
      if (artTrace > 0) artTraceText.print("In cat with cI = " + cI + " processing " + artSlotArray.symbolJavaStrings[slot] + " as ");
      int leftSlot = slot - 1;
      if (artSlotArray.isTerminalSlot(leftSlot)) {
        if (artTrace > 0) artTraceText.println("terminal");
        bsrAdd(slot, cU, cI, cI + 1);
        cI = cI + 1;
      }

      else if (artSlotArray.isNonterminalSlot(leftSlot)) {
        if (artTrace > 0) artTraceText.println("nonterminal");
        call(slot, cU, cI);
        break;
      }

      else {
        System.out.println("Unexpected slot in CNPTraverser");
        break;
      }

      if (artSlotArray.isZeroSlot(slot)) {
        if (artTrace > 0) artTraceText.println("Processing EoC (end of catenation)");
        processEoC(slot);
        break;
      }

      if (!testSelect(input[cI], artSlotArray.slotLHSSymbols[slot], slot)) {
        if (artTrace > 0) artTraceText.println("In cat, lookahead fails: terminating catenation");
        break;
      }

      slot++;
    }
  }

  // public void artLog(String inputFilename) throws FileNotFoundException {
  // PrintStream logStream = new PrintStream("log.csv");
  //
  // int left = inputFilename.lastIndexOf('.') + 1;
  // int right = Math.min(inputFilename.length(), left + 3);
  //
  // String inputFiletype = inputFilename.substring(left, right);
  //
  // int pathSeparatorIndex = 0;
  // if (inputFilename.lastIndexOf('/') != -1)
  // pathSeparatorIndex = inputFilename.lastIndexOf('/') + 1;
  // else if (inputFilename.lastIndexOf('\\') != -1) pathSeparatorIndex = inputFilename.lastIndexOf('\\') + 1;
  // String shortInputFilename = inputFilename.substring(pathSeparatorIndex);
  //
  // String status = "good";
  // if (inputFiletype.equals("acc") || inputFiletype.equals("rej")) {
  // if ((inputFiletype.equals("acc") && !artIsInLanguage) || (inputFiletype.equals("rej") && artIsInLanguage)) status = "bad";
  // } else
  // status = "--";
  //
  // String msg = String.format("%s,%s,%s,%s,%s,%s,%s,%d,,, Parse: %f,,,", "CNPSlotArrayPool", "BNF", "start", shortInputFilename,
  // artIsInLanguage ? "accept" : "reject", status, new Date(), m, artParseCompleteTime / 1.0E9);
  // if (U != 0) {
  // msg += String.format(",descriptor,%d,%s", descriptorFinds, pool.mapCardinality(U));
  // msg += String.format(",,,,BSRSet,%d,%s", bsrFinds, pool.mapCardinality(upsilon));
  // msg += String.format(",CRF cluster node,%d,%s", crfKeyFinds, pool.mapCardinality(CRF));
  // msg += String.format(",CRF leaf node,%d,%s", crfLeafFinds, "0"); // construct the union over the CRF leaf node sets);
  // int CRFEdges = 0;
  // for (int c = pool.iteratorFirst1(CRF); c != 0; c = pool.iteratorNext1())
  // CRFEdges += pool.mapCardinality(pool.poolGet(c + CRFNodeLeafSetOffset));
  // msg += String.format(",CRF edges,%s", CRFEdges);
  // }
  // logStream.println(msg);
  // logStream.close();
  //
  // // Now render the SPPF
  // // upsilon.toDot("CNPBSRSPPF.dot", m);
  // }
  //
  @Override
  public void artParse(String stringInput) {
    if (artTrace > 0) {
      // traceText = new ARTText(new ARTTextHandlerFile("CNPTrace.txt"));
      artTraceText = new ARTText(new ARTTextHandlerConsole());
      artTraceText.println("CNPIndexedPool trace " + ZonedDateTime.now());
    }

    artIsInLanguage = false;

    if (artNotBNF()) {
      if (artTrace > 0) artTraceText.println(this.getClass() + " called on EBNF grammar aborting");
      artInadmissable = true;
      return;
    }

    input = ARTLexerV3.lexicaliseToArrayOfIntForCNP(stringInput, 0, artSlotArray.symbolJavaStrings, epsilonSlot);

    if (input == null) {
      if (artTrace > 0) artTraceText.println("Lexical reject");
      System.out.println("Lexical reject");
      return;
    } else
      // System.out.println("Lexed: " + input)
      ;

    pool = new ARTPool(20, 1024); // 1024 x 1Mlocation blocks: at 32-buit integers that is 4G of memory when fully allocated

    artRestartClock();
    m = input == null ? 0 : input.length - 1;
    if (artTrace > 0) {
      artTraceText.println("Parsing " + m + " tokens");
      for (int i = 0; i < input.length; i++)
        artTraceText.print(artSlotArray.symbolJavaStrings[input[i]] + " ");
      artTraceText.println();
    }
    descriptorFinds = bsrFinds = crfKeyFinds = crfLeafFinds = 0;
    upsilon = pool.mapMake(upsilonBucketCount);

    U = pool.mapMake(UBucketCount);
    R = 0; // Set the list base to 'null'
    CRF = pool.mapMake(CRFBucketCount);
    int crfNode = pool.mapFind_2_2(CRF, startSymbol, 0);
    pool.poolPut(crfNode + CRFNodeLeafSetOffset, pool.mapMake(CRFleafSetBucketCount)); // Allocate the leaf set for the new CRF node (X,j)
    pool.poolPut(crfNode + CRFNodePopSetPartitionOffset, pool.mapMake(CRFPopSetPartitionBucketCount)); // Allocate the pop set partition for the

    crfKeyFinds++;
    ntAdd(startSymbol, 0);

    while (true) {
      if (R == 0) {
        artParseCompleteTime = artReadClock();

        artIsInLanguage = false;

        if (artTrace > 0) artTraceText
            .println("Acceptance testing against start symbol " + artSlotArray.symbolJavaStrings[artSlotArray.startSymbol] + " with right extent " + m);

        for (int i = 0; artSlotArray.slotIndex[artSlotArray.startSymbol][i] != 0; i++) {
          int c = artSlotArray.slotIndex[artSlotArray.startSymbol][i];
          for (int u = pool.mapIteratorFirst1(upsilon); u != 0; u = pool.mapIteratorNext1()) {
            artIsInLanguage |= (pool.poolGet(u + bsrElementSlotOffset) == c && pool.poolGet(u + bsrElementIOffset) == 0
                && pool.poolGet(u + bsrElementJOffset) == m);
            if (artTrace > 0) artTraceText.println("Test " + c + ": " + artSlotArray.symbolJavaStrings[c] + " against " + pool.poolGet(u + bsrElementSlotOffset)
                + ":" + bsrElementToString(u) + " yields " + artIsInLanguage);
          }
        }

        if (artTrace > 0) {
          artTraceText.println("Final descriptor set (U): " + U);
          artTraceText.print("Final BSR set (upsilon): ");
          for (int i = pool.mapIteratorFirst1(upsilon); i != 0; i = pool.mapIteratorNext1())
            artTraceText.print(bsrElementToString(i));
          artTraceText.println();
          artTraceText.println("Final CRF: " + CRF);
          artTraceText.println((artIsInLanguage ? "Accept" : "Reject") + " in " + artParseCompleteTime * 1E-6 + "ms");
          artTraceText.close();
        }

        System.out.println("CNPIndexedPool " + (artIsInLanguage ? "accept" : "reject") + " in " + artParseCompleteTime * 1E-6 + "ms");

        if (artTrace > 0) artTraceText.close();
        return;
      } else {
        // Unload a descriptor
        int descriptor = pool.poolGet(R + descriptorListElementDescriptorOffset);
        R = pool.poolGet(R + descriptorListElementNextOffset);
        if (artTrace > 0) artTraceText.println("\nProcessing descriptor (" + artSlotArray.symbolJavaStrings[pool.poolGet(descriptor + descriptorSlotOffset)]
            + ", " + pool.poolGet(descriptor + descriptorIOffset) + ", " + pool.poolGet(descriptor + descriptorKOffset) + ")");
        artSlot = pool.poolGet(descriptor + descriptorSlotOffset);
        cU = pool.poolGet(descriptor + descriptorKOffset);
        cI = pool.poolGet(descriptor + descriptorIOffset);

        artCNPParseBody(); // interpret or template if overridden
      }
    }
  }

  // This method is overwritten by the generated parsers
  protected void artCNPParseBody() {
    // Epsilon
    if (artSlotArray.isEpsilonSlot(artSlot)) {
      if (artTrace > 0) artTraceText.println("cI = " + cI + " processing " + artSlotArray.symbolJavaStrings[artSlot] + " as epsilon");
      pool.mapFind_4_0(upsilon, artSlotArray.slotGetProductionL(artSlot), cI, cI, cI);
      // doesn't need to be mapped because it is a production, not a prefix
      bsrFinds++;
      if (artSlotArray.mergedSets[artSlotArray.nonterminalFollowSetAddresses[artSlotArray.slotLHSSymbols[artSlot]]][input[cI]])
        rtn(artSlotArray.slotLHSSymbols[artSlot], cU, cI);
    } else
    // Initial slot => cat
    if (artSlotArray.isZeroSlot(artSlot - 1)) {// We are the first slot in a production
      if (artTrace > 0) artTraceText.println("cI = " + cI + " processing " + artSlotArray.symbolJavaStrings[artSlot] + " as initial slot");
      processCat(artSlot + 1);
    } else {
      if (artTrace > 0) artTraceText.println("cI = " + cI + " processing " + artSlotArray.symbolJavaStrings[artSlot] + " as return slot");
      if (testSelect(input[cI], artSlotArray.slotLHSSymbols[artSlot], artSlot)) {
        if (artSlotArray.isZeroSlot(artSlot)) {
          if (artTrace > 0) artTraceText.println("Return slot is at end of rule: processEoC(" + artSlot + ")");
          processEoC(artSlot);
        } else {
          if (artTrace > 0) artTraceText.println("Return slot is in rule: calling processCat(" + (artSlot + 1) + ")");
          processCat(artSlot + 1);
        }
      } else if (artTrace > 0) artTraceText.println("Lookahead fails at return slot");
    }
  }

  @Override
  public void artWriteRDT(String filename) {
    // TODO Auto-generated method stub

  }

  @Override
  public void artPrintRDT() {
    // TODO Auto-generated method stub

  }
}
