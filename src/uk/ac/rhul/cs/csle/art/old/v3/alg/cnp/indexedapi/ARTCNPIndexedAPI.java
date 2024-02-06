package uk.ac.rhul.cs.csle.art.old.v3.alg.cnp.indexedapi;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.util.slotarray.ARTSlotArray;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTTextHandlerConsole;
import uk.ac.rhul.cs.csle.art.old.v3.alg.ARTParserBase;
import uk.ac.rhul.cs.csle.art.old.v3.lex.ARTLexerV3;

public class ARTCNPIndexedAPI extends ARTParserBase {

  private final int startSymbol;
  private final int epsilonSlot;
  protected int m;// Length of the input
  protected Set<ARTBSR> upsilon;
  protected Set<ARTCNPDescriptor> U;
  protected List<ARTCNPDescriptor> R;
  protected Map<ARTCRFClusterNodeKey, ARTCRFClusterNodePayload> CRF; // A digraph - really a forest of unit trees toor -> leaf1, leaf2, ...
  protected Map<ARTCRFLeafNode, ARTCRFLeafNode> CRFleaves; // This map exists so as to make a set whose members can be taken - not needed in pool since we can
                                                           // just use a set
  protected int cU;
  protected int cI;
  protected int artSlot;
  protected int descriptorFinds;
  protected int bsrFinds; // Count accesses to upsilon, NOT including scam of upsilon for start candiates in acceptance testing
  protected int crfKeyFinds;
  protected int crfLeafFinds;

  protected int[] input;

  public ARTCNPIndexedAPI(ARTSlotArray artSlotArray) {
    super(artSlotArray);
    epsilonSlot = artSlotArray.epsilon;
    startSymbol = artSlotArray.startSymbol;
  }

  protected void ntAdd(int N, int i) {
    if (artTrace > 0) artTraceText.println("ntAdd(" + artSlotArray.symbolJavaStrings[N] + ", " + i + ")");
    for (int p = 0; artSlotArray.slotIndex[N][p] != 0; p++) {
      int firstSlot = artSlotArray.slotIndex[N][p] + 1; // Add first slot in production, not the production label itself
      if (testSelect(input[i], N, firstSlot)) dscAdd(firstSlot, i, i);
    }
  }

  protected boolean testSelect(int b, int X, int alpha) {
    if (artTrace > 0) artTraceText.println(
        "testSelect(" + artSlotArray.symbolJavaStrings[b] + ", " + artSlotArray.symbolJavaStrings[X] + ", " + artSlotArray.symbolJavaStrings[alpha] + ")");
    return artSlotArray.mergedSets[artSlotArray.slotFirstSetAddresses[alpha]][b]
        || (artSlotArray.mergedSets[artSlotArray.slotFirstSetAddresses[alpha]][epsilonSlot]
            && artSlotArray.mergedSets[artSlotArray.nonterminalFollowSetAddresses[X]][b]);
  }

  protected void dscAdd(int slot, int k, int i) {
    if (artTrace > 0) artTraceText.println("dscAdd(" + artSlotArray.symbolJavaStrings[slot] + ", " + k + ", " + i + ")");
    ARTCNPDescriptor descriptor = new ARTCNPDescriptor(slot, k, i);

    descriptorFinds++;
    if (!U.contains(descriptor)) {
      U.add(descriptor);
      R.add(descriptor);
    }
  }

  protected void rtn(int X, int k, int j) {
    if (artTrace > 0) artTraceText.println("rtn(" + artSlotArray.symbolJavaStrings[X] + ", " + k + ", " + j + ")");
    ARTCRFClusterNodeKey crfNode = new ARTCRFClusterNodeKey(X, k);
    ARTCRFClusterNodePayload crfNodePayload = CRF.get(crfNode);
    crfKeyFinds++;

    Set<Integer> kSet = crfNodePayload.getPopSetPartition();
    if (!kSet.contains(j)) {
      kSet.add(j);
      for (ARTCRFLeafNode v : crfNodePayload.getleafSet()) {
        dscAdd(v.getSlot(), v.getH(), j);
        bsrAdd(v.getSlot(), v.getH(), k, j);
      }
    }
  }

  protected void call(int L, int i, int j) {
    if (artTrace > 0) artTraceText.println("call(" + artSlotArray.symbolJavaStrings[L] + ", " + i + ", " + j + ")");

    int X = artSlotArray.slotRightSymbols[L - 1];
    ARTCRFLeafNode u = new ARTCRFLeafNode(L, i);
    crfLeafFinds++;
    if (CRFleaves.containsKey(u))
      u = CRFleaves.get(u);
    else
      CRFleaves.put(u, u);
    ARTCRFClusterNodeKey v = new ARTCRFClusterNodeKey(X, j);
    crfKeyFinds++;
    if (!CRF.containsKey(v)) {
      CRF.put(v, new ARTCRFClusterNodePayload());
      CRF.get(v).getleafSet().add(u);
      ntAdd(X, j);
    } else {
      ARTCRFClusterNodePayload crfNodePayload = CRF.get(v);
      Set<ARTCRFLeafNode> children = crfNodePayload.getleafSet();
      if (!children.contains(u)) {
        children.add(u);
        for (Integer h : crfNodePayload.getPopSetPartition()) {
          if (artTrace > 0) artTraceText.println("Contingent PP actions for cluster node " + v + " index = " + h);
          dscAdd(L, i, h);
          bsrAdd(L, i, j, h);
        }
      }
    }
  }

  protected void bsrAdd(int slot, int i, int k, int j) {
    if (artTrace > 0) artTraceText.print("bsrAdd(" + artSlotArray.symbolJavaStrings[slot] + ", " + i + ", " + k + ", " + j + ")");
    ARTBSR added = null;
    if (artSlotArray.isEpsilonSlotOrZeroSlot(slot)) {
      upsilon.add(added = new ARTBSR(artSlotArray.slotGetProductionL(slot), i, j, k));
      bsrFinds++;
    } else if (artSlotArray.prefixLengths[slot] > 1) {
      upsilon.add(added = new ARTBSR(artSlotArray.prefixSlotMap[slot], i, j, k)); // Mapped to canonical slot
      bsrFinds++;
    }
    if (artTrace > 0) artTraceText.println(" added " + added);
  }

  void processEoC(int slot) {
    if (artSlotArray.mergedSets[artSlotArray.nonterminalFollowSetAddresses[artSlotArray.slotLHSSymbols[slot]]][input[cI]])
      rtn(artSlotArray.slotLHSSymbols[slot], cU, cI);
  }

  void processCat(int slot) {
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
        System.out.println("Internal error: unexpected slot " + leftSlot + " in CNPTraverser");
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

  @Override
  public void artParse(String stringInput) {
    if (artTrace > 0) {
      // traceText = new ARTText(new ARTTextHandlerFile("CNPTrace.txt"));
      artTraceText = new ARTText(new ARTTextHandlerConsole());
      artTraceText.println("CNPIndexedAPI trace " + ZonedDateTime.now());
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

    artRestartClock();
    m = input == null ? 0 : input.length - 1;
    if (artTrace > 0) {
      artTraceText.println("Parsing " + m + " tokens");
      for (int i = 0; i < input.length; i++)
        artTraceText.print(artSlotArray.symbolJavaStrings[input[i]] + " ");
      artTraceText.println();
    }
    descriptorFinds = bsrFinds = crfKeyFinds = crfLeafFinds = 0;
    upsilon = new HashSet<>();
    U = new HashSet<>();
    R = new LinkedList<>();
    CRF = new HashMap<>();
    CRFleaves = new HashMap<>();
    CRF.put(new ARTCRFClusterNodeKey(startSymbol, 0), new ARTCRFClusterNodePayload());
    crfKeyFinds++;
    ntAdd(startSymbol, 0);

    while (true) {
      if (R.isEmpty()) {
        artParseCompleteTime = artReadClock();

        artIsInLanguage = false;

        if (artTrace > 0) artTraceText
            .println("Acceptance testing against start symbol " + artSlotArray.symbolJavaStrings[artSlotArray.startSymbol] + " with right extent " + m);

        for (int i = 0; artSlotArray.slotIndex[artSlotArray.startSymbol][i] != 0; i++) {
          int c = artSlotArray.slotIndex[artSlotArray.startSymbol][i];
          for (ARTBSR u : upsilon) {
            artIsInLanguage |= u.getInstance() == c && u.getI() == 0 && u.getJ() == m;
            if (artTrace > 0) artTraceText
                .println("Test " + c + ": " + artSlotArray.symbolJavaStrings[c] + " against " + u.getInstance() + ":" + u + " yields " + artIsInLanguage);
          }
        }

        if (artTrace > 0) {
          artTraceText.println("Final descriptor set (U): " + U);
          artTraceText.println("Final BSR set (upsilon): " + upsilon);
          artTraceText.println("Final CRF: " + CRF);
          artTraceText.println((artIsInLanguage ? "Accept" : "Reject") + " in " + artParseCompleteTime * 1E-6 + "ms");
          artTraceText.close();
        }

        System.out.println("CNPInterpretIndexedAPI " + (artIsInLanguage ? "accept" : "reject") + " in " + artParseCompleteTime * 1E-6 + "ms");

        if (artTrace > 0) artTraceText.close();
        return;
      } else {
        // Unload a descriptor
        ARTCNPDescriptor descriptor = R.remove(0);
        if (artTrace > 0) artTraceText.println("\nProcessing descriptor " + descriptor.toString(artSlotArray.symbolJavaStrings));
        artSlot = descriptor.getSlot();
        cU = descriptor.getK();
        cI = descriptor.getI();

        artCNPParseBody(); // interpret or template if overridden
      }
    }
  }

  // This method is overwritten by the generated parsers
  protected void artCNPParseBody() {
    // Epsilon
    if (artSlotArray.isEpsilonSlot(artSlot)) {
      if (artTrace > 0) artTraceText.println("cI = " + cI + " processing " + artSlotArray.symbolJavaStrings[artSlot] + " as epsilon");
      ARTBSR added;
      upsilon.add(added = new ARTBSR(artSlotArray.slotGetProductionL(artSlot), cI, cI, cI)); // doesn't need to be mapped because it is a cat
      // representing a production, not a slot
      // representing a prefix
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
