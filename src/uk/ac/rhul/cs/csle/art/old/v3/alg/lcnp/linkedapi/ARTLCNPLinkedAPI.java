package uk.ac.rhul.cs.csle.art.old.v3.alg.lcnp.linkedapi;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;
import uk.ac.rhul.cs.csle.art.old.util.bsr.ARTBSR;
import uk.ac.rhul.cs.csle.art.old.util.bsr.ARTBSRSet;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTTextHandlerConsole;
import uk.ac.rhul.cs.csle.art.old.v3.alg.ARTParserBase;
import uk.ac.rhul.cs.csle.art.old.v3.lex.ARTLexerV3;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceCat;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceEpsilon;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceSlot;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceTerminal;

public class ARTLCNPLinkedAPI extends ARTParserBase {

  protected int m;// Length of the input
  protected Set<ARTBSR> upsilon;
  protected Set<ARTCNPDescriptor> U;
  protected List<ARTCNPDescriptor> R;
  protected Map<ARTCRFClusterNodeKey, ARTCRFClusterNodePayload> CRF; // A digraph - really a forest of unit trees toor -> leaf1, leaf2, ...
  protected Map<ARTCRFLeafNode, ARTCRFLeafNode> CRFleaves;
  protected int cU;
  protected int cI;
  protected ARTGrammarInstanceSlot artSlot;
  protected int descriptorFinds;
  protected int bsrFinds; // Count accesses to upsilon, NOT including scam of upsilon for start candiates in acceptance testing
  protected int crfKeyFinds;
  protected int crfLeafFinds;
  private ARTLexerV3 lexer;
  private final boolean artTrace;

  public ARTLCNPLinkedAPI(ARTGrammar artGrammar) {
    super(artGrammar);
    artTrace = artGrammar.artDirectives.b("trace");
  }

  /* Start of lexical interface **********************************************/

  protected Map<ARTGrammarInstanceSlot, Integer> artSlotToIntMap = new HashMap<>();
  private int[][][] tweSet;

  boolean lex(int i, Set<ARTGrammarElement> T) {
    if (artTrace) artTraceText.print("lex(" + i + ", " + T + ") returns ");

    for (ARTGrammarElement e : T)
      if (tweSet[i][e.getElementNumber()] != null) {
        if (artTrace) artTraceText.println("true");
        return true;
      }
    if (artTrace) artTraceText.println("false");
    return false;
  }

  int[] lex(int i, ARTGrammarElement t) {
    if (artTrace) {
      artTraceText.print("lex(" + i + ", " + t + ") returns {");
      if (tweSet[i][t.getElementNumber()] != null) for (int e : tweSet[i][t.getElementNumber()])
        artTraceText.print(" " + e);
      artTraceText.println(" }");
    }
    return tweSet[i][t.getElementNumber()];
  }

  Set<ARTGrammarElement> predict(ARTGrammarInstanceSlot beta, ARTGrammarElementNonterminal X) {
    if (artTrace) artTraceText.println("predict(" + X + ", " + beta.toGrammarString(".") + ") with first(beta) = " + beta.getFirst() + " and follow(X) = "
        + X.getFollow() + " returns " + beta.getGuard());
    return beta.getGuard();
  }

  Set<Integer> lexLKH(int i, ARTGrammarElementTerminal t, ARTGrammarInstanceSlot beta, ARTGrammarElementNonterminal X) { // Note I is assummed global - it's
    if (artTrace) artTraceText.println("lexLKH(" + i + ", " + t + ", " + beta + ", " + X + ") entered");
    Set<Integer> J = new HashSet<>();
    int[] lexed = lex(i, t);
    if (lexed != null) for (int k : lexed)
      if (valid(i + k, predict(beta, X))) J.add(i + k);

    if (artTrace) artTraceText.println("lexLKH(" + i + ", " + t + ", " + beta + ", " + X + ") returns " + J);
    return J;
  }

  boolean valid(int i, Set<ARTGrammarElement> T) {
    if (artTrace) artTraceText.println("valid(" + i + ", " + T + ")");

    return lex(i, T);
  }

  /* End of lexical interface **********************************************/

  protected void ntAdd(ARTGrammarElementNonterminal N, int j) {
    if (artTrace) artTraceText.println("ntAdd(" + N + ", " + j + ")");
    for (ARTGrammarInstanceCat catNode : N.getProductions()) {
      ARTGrammarInstanceSlot firstSlot = (ARTGrammarInstanceSlot) catNode.getChild();
      if (valid(j, predict(firstSlot, N))) dscAdd(firstSlot, j, j);
    }
  }

  protected void dscAdd(ARTGrammarInstanceSlot slot, int i, int k) {
    if (artTrace) artTraceText.println("dscAdd(" + slot.toGrammarString(".") + ", " + i + ", " + k + ")");
    ARTCNPDescriptor descriptor = new ARTCNPDescriptor(slot, i, k);

    descriptorFinds++;
    if (!U.contains(descriptor)) {
      U.add(descriptor);
      R.add(descriptor);
    }
  }

  protected void rtn(ARTGrammarElementNonterminal X, int k, int j) {
    if (artTrace) artTraceText.println("rtn(" + X + ", " + k + ", " + j + ")");
    ARTCRFClusterNodeKey crfNode = new ARTCRFClusterNodeKey(X, k);
    ARTCRFClusterNodePayload crfNodePayload = CRF.get(crfNode);
    crfKeyFinds++;

    Set<Integer> kSet = crfNodePayload.getPopSetPartition();
    if (!kSet.contains(j)) {
      kSet.add(j);
      for (ARTCRFLeafNode v : crfNodePayload.getleafSet()) {
        if (valid(j, v.getSlot().getGuard())) {
          dscAdd(v.getSlot(), v.getH(), j);
          bsrAdd(v.getSlot(), v.getH(), k, j);
        }
      }
    }
  }

  protected void call(ARTGrammarInstanceSlot L, int i, int j) {
    if (artTrace) artTraceText.println("call(" + L.toGrammarString(".") + ", " + i + ", " + j + ")");

    ARTGrammarElementNonterminal X = (ARTGrammarElementNonterminal) L.getLeftSibling().getPayload();
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
          if (artTrace) artTraceText.println("Contingent PP actions for cluster node " + v + " index = " + h);
          if (valid(h, L.getGuard())) {
            dscAdd(L, i, h);
            bsrAdd(L, i, j, h);
          }
        }
      }
    }
  }

  protected void bsrAdd(ARTGrammarInstanceSlot slot, int i, int k, int j) {
    if (artTrace) artTraceText.print("bsrAdd(" + slot.toGrammarString(".") + ", " + i + ", " + k + ", " + j + ")");
    ARTBSR added = null;
    if (slot.getSibling() == null || slot.getSibling() instanceof ARTGrammarInstanceEpsilon) {
      upsilon.add(added = new ARTBSR(slot.getProductionL(), i, k, j)); // Not mapped since this is a cat representing a production, not a slot representing a
                                                                       // prefix
      bsrFinds++;
    } else if (slot.getPrefixLength() > 1) {
      upsilon.add(added = new ARTBSR(artGrammar.getPrefixSlotMap().get(slot), i, k, j)); // Mapped to canonical slot
      bsrFinds++;
    }
    if (artTrace) artTraceText.println(" added " + added);
  }

  void processEoC(ARTGrammarInstanceSlot slot) {
    if (lex(cI, slot.getLhsL().getFollow())) rtn((ARTGrammarElementNonterminal) slot.getLhsL().getPayload(), cU, cI);
  }

  void processCat(ARTGrammarInstanceSlot slot) {
    if (artTrace) artTraceText.println("Start of cat with cI = " + cI + " on slot " + slot.toGrammarString("."));
    while (true) {
      if (artTrace) artTraceText.print("In cat with cI = " + cI + " processing " + slot.toGrammarString(".") + " as ");

      if (slot.getLeftSibling() instanceof ARTGrammarInstanceTerminal) {
        if (artTrace) artTraceText.println("terminal");
        Set<Integer> J = lexLKH(cI, (ARTGrammarElementTerminal) slot.getLeftSibling().getPayload(), slot,
            (ARTGrammarElementNonterminal) slot.getLhsL().getPayload());
        // if (J.size() != 1) {
        for (int j : J) {
          bsrAdd(slot, cU, cI, j);
          dscAdd(slot, cU, j);
        }
        return;
        // break; // goto L_0 in this world
        // }
        //
        // for (int j : J) { // This is a bit ugly; use iterator to iterate over the single element of J. Yuk.
        // bsrAdd(slot, cU, cI, j);
        //
        // cI = j;
        // }
      }

      else if (slot.getLeftSibling() instanceof ARTGrammarInstanceNonterminal) {
        if (artTrace) artTraceText.println("nonterminal");
        call(slot, cU, cI);
        break;
      }

      else
        throw new ARTUncheckedException("Unexpected slot in CNPTraverser");

      //// if (slot.getSibling() == null) {
      //// if (ARTTRACE) traceText.println("Processing EoC (end of catenation)");
      //// processEoC(slot);
      //// break;
      //// }
      //
      // slot = (ARTGrammarInstanceSlot) slot.getSibling().getSibling();
    }
  }

  @Override
  public void artParse(String stringInput) {

    if (artTrace) {
      // traceText = new ARTText(new ARTTextHandlerFile("CNPTrace.txt"));
      artTraceText = new ARTText(new ARTTextHandlerConsole());
      artTraceText.println("CNP trace " + ZonedDateTime.now());
    }

    artIsInLanguage = false;

    if (artNotBNF()) {
      if (artDirectives.b("artTrace")) System.out.println(this.getClass() + " called on EBNF grammar aborting");
      return;
    }

    lexer = new ARTLexerV3(artGrammar); // uncomfortable: change so as to instantiate lexer in constructor and just call lexer here
    lexer.artDirectives.set("tweWSSuffix", true);
    tweSet = lexer.lexicaliseToIndexedTWESet(stringInput);

    // Stitch up TWESet to suit this version
    for (int i = 0; i < tweSet.length; i++)
      if (tweSet[i] != null) for (int t = 0; t < tweSet[i].length; t++)
        if (tweSet[i][t] != null) for (int jj = 0; jj < tweSet[i][t].length; jj++)
          tweSet[i][t][jj] -= i;

    artRestartClock();
    m = tweSet.length - 2;
    if (artTrace) artTraceText.println("Parsing " + m + " tokens");

    descriptorFinds = bsrFinds = crfKeyFinds = crfLeafFinds = 0;
    upsilon = new HashSet<>();
    U = new HashSet<>();
    R = new LinkedList<>();
    CRF = new HashMap<>();
    CRFleaves = new HashMap<>();
    CRF.put(new ARTCRFClusterNodeKey(artGrammar.getDefaultStartNonterminal(), 0), new ARTCRFClusterNodePayload());
    crfKeyFinds++;
    ntAdd(artGrammar.getDefaultStartNonterminal(), 0);

    while (true) {
      if (R.isEmpty()) { // Acceptance testing
        artParseCompleteTime = artReadClock();

        artIsInLanguage = false;

        if (artTrace) artTraceText.println("Acceptance testing against start symbol " + artGrammar.getDefaultStartNonterminal() + " with right extent " + m);

        for (ARTGrammarInstanceCat c : artGrammar.getDefaultStartNonterminal().getProductions())
          for (ARTBSR u : upsilon) {
            artIsInLanguage |= u.getInstance() == c && u.getI() == 0 && u.getJ() == m;
            if (artTrace)
              artTraceText.println("Test " + c + ": " + c.toGrammarString(".") + " against " + u.getInstance() + ":" + u + " yields " + artIsInLanguage);
          }

        if (artTrace) {
          artTraceText.println("Final descriptor set (U): " + U);
          artTraceText.println("Final BSR set (upsilon): " + upsilon);
          artTraceText.println("Final CRF: " + CRF);
          artTraceText.println((artIsInLanguage ? "Accept" : "Reject") + " in " + artParseCompleteTime * 1E-6 + "ms");
          artTraceText.close();
        }

        System.out.println("Descriptor set (U) cardinality = " + U.size());
        System.out.println("BSR set (upsilon) cardinality = " + upsilon.size());
        System.out.println("LCNPInterpretLinkedAPI " + (artIsInLanguage ? "accept" : "reject") + " in " + artParseCompleteTime * 1E-6 + "ms");
        if (artTrace) artTraceText.close();
        if (lexer.artDirectives.b("twePrint")) processSLEdata();
        return;
      } else {
        // Unload a descriptor
        ARTCNPDescriptor descriptor = R.remove(0);
        if (artTrace) artTraceText.println("\nProcessing descriptor " + descriptor);
        artSlot = descriptor.getSlot();
        cU = descriptor.getI();
        cI = descriptor.getK();

        artCNPProcessDescriptor();
      }
    }
  }

  // Template changes below here
  protected void artCNPProcessDescriptor() {
    // Epsilon template
    if (artSlot.getSibling() instanceof ARTGrammarInstanceEpsilon) {
      if (artTrace) artTraceText.println("cI = " + cI + " processing " + artSlot.toGrammarString(".") + " as epsilon");
      upsilon.add(new ARTBSR(artSlot.getProductionL(), cI, cI, cI)); // doesn't need to be mapped because it is a cat representing a production, not a slot
      // representing a prefix
      bsrFinds++;
      if (lex(cI, artSlot.getLhsL().getFollow())) rtn((ARTGrammarElementNonterminal) artSlot.getLhsL().getPayload(), cU, cI);
    } else // production template
    // Initial slot => cat
    if (artSlot.getLeftSibling() == null) {// We are the first slot in a production
      if (artTrace) artTraceText.println("cI = " + cI + " processing " + artSlot.toGrammarString(".") + " as initial slot");
      processCat((ARTGrammarInstanceSlot) artSlot.getSibling().getSibling());
    } else {
      if (artTrace) artTraceText.println("cI = " + cI + " processing " + artSlot.toGrammarString(".") + " as return slot");
      if (artSlot.getSibling() == null) {
        if (artTrace) artTraceText.println("Return slot is at end of rule: processEoC(" + artSlot + ")");
        processEoC(artSlot);
      } else {
        if (artTrace) artTraceText.println("Return slot is in rule: calling processCat(" + artSlot.getSibling().getSibling() + ")");
        processCat((ARTGrammarInstanceSlot) artSlot.getSibling().getSibling());
      }
    }
  }

  private void processSLEdata() {
    System.out.println("\nDescriptor set (U) = " + U);
    System.out.println("BSR set (upsilon) = " + upsilon + "\n");
    ARTBSRSet bsrSet = new ARTBSRSet(artGrammar, upsilon);
    bsrSet.constructFullSPPF();
    bsrSet.printSPPF();
    bsrSet.sppfToDotFull("SPPFFull.dot");
    bsrSet.sppfTraverse(artGrammar, "SPPFReachable.dot", m);
    System.out.println();
  }

  @Override
  public void artWriteRDT(String filename) {
  }

  @Override
  public void artPrintRDT() {
  }
}
