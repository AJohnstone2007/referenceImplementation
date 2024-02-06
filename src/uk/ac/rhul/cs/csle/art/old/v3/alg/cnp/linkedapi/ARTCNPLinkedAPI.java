package uk.ac.rhul.cs.csle.art.old.v3.alg.cnp.linkedapi;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.util.bsr.ARTBSR;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTTextHandlerConsole;
import uk.ac.rhul.cs.csle.art.old.v3.alg.ARTParserBase;
import uk.ac.rhul.cs.csle.art.old.v3.lex.ARTLexerV3;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceCat;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceEpsilon;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceSlot;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceTerminal;

public class ARTCNPLinkedAPI extends ARTParserBase {

  protected ArrayList<ARTGrammarElementTerminal> input;
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

  public ARTCNPLinkedAPI(ARTGrammar artGrammar) {
    super(artGrammar);
  }

  protected Map<ARTGrammarInstanceSlot, Integer> artSlotToIntMap = new HashMap<>();

  protected void ntAdd(ARTGrammarElementNonterminal N, int i) {
    if (artTrace > 0) artTraceText.println("ntAdd(" + N + ", " + i + ")");
    for (ARTGrammarInstanceCat catNode : N.getProductions()) {
      ARTGrammarInstanceSlot firstSlot = (ARTGrammarInstanceSlot) catNode.getChild();
      if (testSelect(input.get(i), N, firstSlot)) dscAdd(firstSlot, i, i);
    }
  }

  protected boolean testSelect(ARTGrammarElementTerminal b, ARTGrammarElementNonterminal X, ARTGrammarInstanceSlot alpha) {
    if (artTrace > 0) artTraceText.println(
        "testSelect(" + b + ", " + X + ", " + alpha.toGrammarString(".") + ") with first(alpha) = " + alpha.getFirst() + " and follow(X) = " + X.getFollow());
    return alpha.getFirst().contains(b) || (alpha.getFirst().contains(artGrammar.getEpsilon()) && X.getFollow().contains(b));
  }

  protected void dscAdd(ARTGrammarInstanceSlot slot, int k, int i) {
    if (artTrace > 0) artTraceText.println("dscAdd(" + slot.toGrammarString(".") + ", " + k + ", " + i + ")");
    ARTCNPDescriptor descriptor = new ARTCNPDescriptor(slot, k, i);

    descriptorFinds++;
    if (!U.contains(descriptor)) {
      U.add(descriptor);
      R.add(descriptor);
    }
  }

  protected void rtn(ARTGrammarElementNonterminal X, int k, int j) {
    if (artTrace > 0) artTraceText.println("rtn(" + X + ", " + k + ", " + j + ")");
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

  protected void call(ARTGrammarInstanceSlot L, int i, int j) {
    if (artTrace > 0) artTraceText.println("call(" + L.toGrammarString(".") + ", " + i + ", " + j + ")");

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
          if (artTrace > 0) artTraceText.println("Contingent PP actions for cluster node " + v + " index = " + h);
          dscAdd(L, i, h);
          bsrAdd(L, i, j, h);
        }
      }
    }
  }

  protected void bsrAdd(ARTGrammarInstanceSlot slot, int i, int k, int j) {
    if (artTrace > 0) artTraceText.print("bsrAdd(" + slot.toGrammarString(".") + ", " + i + ", " + k + ", " + j + ")");
    ARTBSR added = null;
    if (slot.getSibling() == null || slot.getSibling() instanceof ARTGrammarInstanceEpsilon) {
      upsilon.add(added = new ARTBSR(slot.getProductionL(), i, k, j)); // Not mapped since this is a cat representing a production, not a slot representing a
                                                                       // prefix
      bsrFinds++;
    } else if (slot.getPrefixLength() > 1) {
      upsilon.add(added = new ARTBSR(artGrammar.getPrefixSlotMap().get(slot), i, k, j)); // Mapped to canonical slot
      // upsilon.add(added = new ARTBSR(slot, i, k, j)); // Mapped to canonical slot
      bsrFinds++;
    }
    if (artTrace > 0) artTraceText.println(" added " + added);
  }

  void processEoC(ARTGrammarInstanceSlot slot) {
    if (slot.getLhsL().getFollow().contains(input.get(cI))) rtn((ARTGrammarElementNonterminal) slot.getLhsL().getPayload(), cU, cI);
  }

  void processCat(ARTGrammarInstanceSlot slot) {
    if (artTrace > 0) artTraceText.println("Start of cat with cI = " + cI + " on slot " + slot.toGrammarString("."));
    while (true) {
      if (artTrace > 0) artTraceText.print("In cat with cI = " + cI + " processing " + slot.toGrammarString(".") + " as ");

      if (slot.getLeftSibling() instanceof ARTGrammarInstanceTerminal) {
        if (artTrace > 0) artTraceText.println("terminal");
        bsrAdd(slot, cU, cI, cI + 1);
        cI = cI + 1;
      }

      else if (slot.getLeftSibling() instanceof ARTGrammarInstanceNonterminal) {
        if (artTrace > 0) artTraceText.println("nonterminal");
        call(slot, cU, cI);
        break;
      }

      else {
        System.out.println("Internal error: unexpected slot in CNPTraverser");
        break;
      }

      if (slot.getSibling() == null) {
        if (artTrace > 0) artTraceText.println("Processing EoC (end of catenation)");
        processEoC(slot);
        break;
      }

      if (!testSelect(input.get(cI), (ARTGrammarElementNonterminal) slot.getLhsL().getPayload(), slot)) {
        if (artTrace > 0) artTraceText.println("In cat, lookahead fails: terminating catenation");
        break;
      }

      slot = (ARTGrammarInstanceSlot) slot.getSibling().getSibling();
    }
  }

  @Override
  public void artParse(String stringInput) {
    if (artTrace > 0) {
      // traceText = new ARTText(new ARTTextHandlerFile("CNPTrace.txt"));
      artTraceText = new ARTText(new ARTTextHandlerConsole());
      artTraceText.println("CNP trace " + ZonedDateTime.now());
    }

    artIsInLanguage = false;

    if (artNotBNF()) {
      if (artTrace > 0) artTraceText.println(this.getClass() + " called on EBNF grammar aborting");
      artInadmissable = true;
      return;
    }

    input = new ARTLexerV3(artGrammar).lexicaliseToArrayListOfTerminals(stringInput, 0);

    if (input == null) {
      System.out.println("Lexical reject");

      if (artTrace > 0) artTraceText.println("Lexical reject");
      return;
    } else
      // System.out.println("Lexed: " + input)
      ;

    artRestartClock();
    m = input == null ? 0 : input.size() - 1;
    if (artTrace > 0) {
      artTraceText.println("Parsing " + m + " tokens");
      artTraceText.println(input.toString());
    }
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
      if (R.isEmpty()) {
        artParseCompleteTime = artReadClock();

        artIsInLanguage = false;

        if (artTrace > 0)
          artTraceText.println("Acceptance testing against start symbol " + artGrammar.getDefaultStartNonterminal() + " with right extent " + m);

        for (ARTGrammarInstanceCat c : artGrammar.getDefaultStartNonterminal().getProductions())
          for (ARTBSR u : upsilon) {
            artIsInLanguage |= u.getInstance() == c && u.getI() == 0 && u.getJ() == m;
            if (artTrace > 0)
              artTraceText.println("Test " + c + ": " + c.toGrammarString(".") + " against " + u.getInstance() + ":" + u + " yields " + artIsInLanguage);
          }

        if (artTrace > 0) {
          artTraceText.println("Final descriptor set (U): " + U);
          artTraceText.println("Final BSR set (upsilon): " + upsilon);
          artTraceText.println("Final CRF: " + CRF);
          artTraceText.println((artIsInLanguage ? "Accept" : "Reject") + " in " + artParseCompleteTime * 1E-6 + "ms");
          artTraceText.close();
        }

        System.out.println("CNPLinkedAPI " + (artIsInLanguage ? "accept" : "reject") + " in " + artParseCompleteTime * 1E-6 + "ms");

        if (artTrace > 0) artTraceText.close();
        return;
      } else {
        // Unload a descriptor
        ARTCNPDescriptor descriptor = R.remove(0);
        if (artTrace > 0) artTraceText.println("\nProcessing descriptor " + descriptor);
        artSlot = descriptor.getSlot();
        cU = descriptor.getK();
        cI = descriptor.getI();

        artCNPParseBody();
      }
    }
  }

  protected void artCNPParseBody() {
    // Epsilon
    if (artSlot.getSibling() instanceof ARTGrammarInstanceEpsilon) {
      if (artTrace > 0) artTraceText.println("cI = " + cI + " processing " + artSlot.toGrammarString(".") + " as epsilon");
      upsilon.add(new ARTBSR(artSlot.getProductionL(), cI, cI, cI)); // doesn't need to be mapped because it is a cat representing a production, not a slot
      // representing a prefix
      bsrFinds++;
      if (artSlot.getLhsL().getFollow().contains(input.get(cI))) rtn((ARTGrammarElementNonterminal) artSlot.getLhsL().getPayload(), cU, cI);
    } else
    // Initial slot => cat
    if (artSlot.getLeftSibling() == null) {// We are the first slot in a production
      if (artTrace > 0) artTraceText.println("cI = " + cI + " processing " + artSlot.toGrammarString(".") + " as initial slot");
      processCat((ARTGrammarInstanceSlot) artSlot.getSibling().getSibling());
    } else {
      if (artTrace > 0) artTraceText.println("cI = " + cI + " processing " + artSlot.toGrammarString(".") + " as return slot");
      if (testSelect(input.get(cI), (ARTGrammarElementNonterminal) artSlot.getLhsL().getPayload(), artSlot)) {
        if (artSlot.getSibling() == null) {
          if (artTrace > 0) artTraceText.println("Return slot is at end of rule: processEoC(" + artSlot + ")");
          processEoC(artSlot);
        } else {
          if (artTrace > 0) artTraceText.println("Return slot is in rule: calling processCat(" + artSlot.getSibling().getSibling() + ")");
          processCat((ARTGrammarInstanceSlot) artSlot.getSibling().getSibling());
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
