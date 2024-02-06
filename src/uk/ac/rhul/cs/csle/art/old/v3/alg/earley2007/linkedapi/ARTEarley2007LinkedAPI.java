package uk.ac.rhul.cs.csle.art.old.v3.alg.earley2007.linkedapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.v3.alg.ARTParserBase;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earley.linkedapi.ARTEarleyItem;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earley.linkedapi.ARTEarleySPPFFamily;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earley.linkedapi.ARTEarleySPPFNode;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earley.linkedapi.ARTEarleySPPFNodeIntermediate;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earley.linkedapi.ARTEarleySPPFNodeSymbol;
import uk.ac.rhul.cs.csle.art.old.v3.alg.gll.support.ARTGLLAttributeBlock;
import uk.ac.rhul.cs.csle.art.old.v3.lex.ARTLexerV3;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementEpsilon;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstance;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceCat;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceEpsilon;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceLHS;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceSlot;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceTerminal;

public class ARTEarley2007LinkedAPI extends ARTParserBase {
  /*
   * This is a straightforward implementation of the Earley+SPPF algorithm from pp 10-11 of
   *
   * 'Recognition Is Not Parsing – SPPF-Style Parsing From Cubic Recognisers'
   *
   * Science of Computer Programming Volume 75, Issues 1–2, 1 January 2010, Pages 55-70
   *
   * One modification: the creation of terminals and Q set processing is now guarded by a test that i!=n
   *
   */
  private int inputLength;
  private ARTEarleySPPFNode epsilonSPPFNode;
  private int qSetRemovals, rSetRemovals;

  // Set declarations: initialisation in parse()
  private ArrayList<Set<ARTEarleyItem>> eSets;
  private Set<ARTEarleyItem> qSet, qPrimeSet, rSet;
  private Map<ARTEarleySPPFNode, ARTEarleySPPFNode> vMap; // Use a map to overcome Java's lack of a get() method for
  // class Set<>
  private Map<ARTGrammarElementNonterminal, ARTEarleySPPFNode> hMap;
  private Set<ARTEarleySPPFNode> sppf; // Remember all SPPF nodes for rendering - not needed for core algorithm since
  // vMap has that for each level
  private Set<ARTGrammarInstanceSlot> acceptingSlotsSet;

  public ARTEarley2007LinkedAPI(ARTGrammar grammar) {
    super(grammar);
    artGrammar.getARTManager().printMemory(this.getClass().getSimpleName() + " start of constructor");
  }

  private boolean inSigmaN(ARTGrammarInstance instance) {
    if (instance instanceof ARTGrammarInstanceCat) instance = instance.getChild();

    if (!(instance instanceof ARTGrammarInstanceSlot)) System.out.println("Internal error: call to inSigmaN with instance that is neither a cat nor a slot");

    ARTGrammarInstance sibling = instance.getSibling();

    return sibling == null || sibling instanceof ARTGrammarInstanceNonterminal || sibling instanceof ARTGrammarInstanceEpsilon;
  }

  // These are 'support functions' which bury the OO API accesses inside function
  // calls so as to ease conversion to ANSI-C
  private void setAdd(Set<ARTEarleyItem> set, ARTEarleyItem element) {
    if (artTrace > 0) artTraceText.println("Adding to Earley set " + element);
    set.add(element);
  }

  private ARTEarleyItem setRemove(Set<ARTEarleyItem> set) {
    ARTEarleyItem element;
    Iterator<ARTEarleyItem> iterator = set.iterator();
    element = iterator.next();
    set.remove(element);
    return element;
  }

  private void printSets() {
    artTraceText.println("Earley sets");
    int totalCardinality = 0;
    for (int i = 0; i < eSets.size(); i++) {
      totalCardinality += eSets.get(i).size();
      for (ARTEarleyItem s : eSets.get(i))
        artTraceText.println(i + ": " + s);
    }
    artTraceText.println("Q = " + qSet + "\nQ' = " + qPrimeSet + "\nR = " + rSet);
    artTraceText.println("Sum over Earley set cardinalities = " + totalCardinality);
    System.out.println("Sum over Earley set cardinalities = " + totalCardinality);
    artTraceText.println("Total removals from R =  " + rSetRemovals + ", total removals from Q = " + qSetRemovals);
    System.out.println("Total removals from R =  " + rSetRemovals + ", total removals from Q = " + qSetRemovals);
    int sppfSymbolIntermdiateNodeCount = sppf.size(), sppfPackedNodeCount = 0, sppfEdgeCount = 0;
    for (ARTEarleySPPFNode s : sppf) {
      if (s.families == null) continue;
      sppfEdgeCount += s.families.size();
      sppfPackedNodeCount += s.families.size();
      for (ARTEarleySPPFFamily f : s.families) {
        if (f.left != null) sppfEdgeCount++;
        if (f.right != null) sppfEdgeCount++;
      }
    }
    artTraceText.println("SPPF symbol/intermediate node count = " + sppfSymbolIntermdiateNodeCount + ", SPPF packed node count = " + sppfPackedNodeCount
        + ", SPF edge count = " + sppfEdgeCount);
    System.out.println("SPPF symbol/intermediate node count = " + sppfSymbolIntermdiateNodeCount + ", SPPF packed node count = " + sppfPackedNodeCount
        + ", SPF edge count = " + sppfEdgeCount);
  }

  @Override
  public void artParse(String stringInput, ARTGLLAttributeBlock startAttributes) {
    // ARTTRACE = true;
    artIsInLanguage = false;

    if (artNotBNF()) {
      if (artTrace > 0) artTraceText.println(this.getClass().getSimpleName() + " called on EBNF grammar aborting");
      return;
    }

    qSetRemovals = rSetRemovals = 0;
    epsilonSPPFNode = new ARTEarleySPPFNodeSymbol(new ARTGrammarElementEpsilon(), 0, 0);

    acceptingSlotsSet = new HashSet<>();
    for (ARTGrammarInstance p : artGrammar.getDefaultStartNonterminal().getProductions()) {
      // !! update p to be the accepting slot!
      p = p.getChild();
      if (!(p.getSibling() instanceof ARTGrammarInstanceEpsilon)) while (p.getSibling() != null)
        p = p.getSibling();
      acceptingSlotsSet.add((ARTGrammarInstanceSlot) p);
    }

    ArrayList<ARTGrammarElementTerminal> input = new ARTLexerV3(artGrammar).lexicaliseToArrayListOfTerminals(stringInput, 1);

    if (input == null) {
      System.out.println(this.getClass().getSimpleName() + " reject lexical");
      if (artTrace > 0) {
        artTraceText.println(this.getClass().getSimpleName() + " reject lexical");
        artTraceText.close();
      }
      return;
    }

    inputLength = input.size() - 2; // input[0] is not used and input[n+1] is $

    if (artTrace > 0) artTraceText.println(this.getClass().getSimpleName() + " running on " + inputLength + " tokens");

    // E0 , . . . , En , R, Q′ , V = ∅
    eSets = new ArrayList<>();
    for (int i = 0; i < inputLength + 1; i++)
      eSets.add(new HashSet<>());
    rSet = new HashSet<>(); // redundant?
    qPrimeSet = new HashSet<>();
    vMap = new HashMap<>();
    hMap = new HashMap<>();
    sppf = new HashSet<>();

    artGrammar.getARTManager().printMemory(this.getClass().getSimpleName() + " start of parse");
    artRestartClock();

    // for all (S ::= α) ∈ P { if α ∈ ΣN add (S ::= ·α, 0, null) to E0
    // if α = a1 α′ add (S ::= ·α, 0, null) to Q′ }
    for (ARTGrammarInstance p : artGrammar.getDefaultStartNonterminal().getProductions()) {
      {
        if (inSigmaN(p)) setAdd(eSets.get(0), new ARTEarleyItem((ARTGrammarInstanceSlot) p.getChild(), 0, null));
        if (p.getChild().getSibling().getPayload() != null && p.getChild().getSibling().getPayload().equals(input.get(1)))
          setAdd(qPrimeSet, new ARTEarleyItem((ARTGrammarInstanceSlot) p.getChild(), 0, null));
      }
    }
    // for 0 ≤ i ≤ n {
    for (int i = 0; i <= inputLength; i++) {
      // System.out.println("At index " + i);
      // printSets();
      // H = ∅, R = Ei , Q = Q′
      hMap.clear();
      rSet = new HashSet<>(eSets.get(i));
      qSet = new HashSet<>(qPrimeSet);

      // Q′ = ∅
      qPrimeSet = new HashSet<>();

      // while R ! = ∅ {
      while (!rSet.isEmpty()) {
        if (artTrace > 0) artTraceText.println("At top of main loop, rSet is " + rSet);

        // remove an element, Λ say, from R
        ARTEarleyItem lambda = setRemove(rSet);
        if (artTrace > 0) artTraceText.println("Processing " + lambda);
        rSetRemovals++;

        ARTGrammarInstance c = lambda.slot.getSibling();
        int h = lambda.i;
        ARTEarleySPPFNode w = lambda.sppfNode;

        // if Λ = (B ::= α · Cβ, h, w) {
        if (c instanceof ARTGrammarInstanceNonterminal) {
          // for all (C ::= δ) ∈ P {
          for (ARTGrammarInstanceCat p : ((ARTGrammarElementNonterminal) c.getPayload()).getProductions()) {
            ARTGrammarInstanceSlot deltaSlot = (ARTGrammarInstanceSlot) p.getChild();
            // if δ ∈ ΣN and (C ::= ·δ, i, null) ! ∈ Ei {
            ARTEarleyItem eSetCandidate1 = new ARTEarleyItem(deltaSlot, i, null);
            if (inSigmaN(deltaSlot) && !eSets.get(i).contains(eSetCandidate1)) {
              // add (C ::= ·δ, i, null) to Ei and R }
              setAdd(eSets.get(i), eSetCandidate1);
              setAdd(rSet, eSetCandidate1);
            }
            // if δ = ai+1 δ' { add (C ::= ·δ, i, null) to Q } }
            ARTGrammarInstance deltaFirst = deltaSlot.getSibling();
            if (deltaFirst instanceof ARTGrammarInstanceTerminal && ((ARTGrammarElementTerminal) deltaFirst.getPayload()).equals(input.get(i + 1))) {
              setAdd(qSet, eSetCandidate1);
            }
          }
          // if ((C, v) ∈ H) {
          ARTEarleySPPFNode v;
          if ((v = hMap.get(c.getPayload())) != null) {
            // let y = MAKE_NODE(B ::= αC · β, h, i, w, v, V)
            ARTGrammarInstanceSlot betaSlot = (ARTGrammarInstanceSlot) lambda.slot.getSibling().getSibling();
            ARTEarleySPPFNode y = makeNode(betaSlot, h, i, w, v, vMap);
            ARTEarleyItem eSetCandidate2 = new ARTEarleyItem(betaSlot, h, y);
            // if β ∈ ΣN and (B ::= αC · β, h, y) ! ∈ Ei {
            if (inSigmaN(betaSlot) && !eSets.get(i).contains(eSetCandidate2)) {
              // add (B ::= αC · β, h, y) to Ei and R }
              setAdd(eSets.get(i), eSetCandidate2);
              setAdd(rSet, eSetCandidate2);
            }
            // if β = ai+1 β ′ { add (B ::= αC · β, h, y) to Q }
            ARTGrammarInstance betaFirst = betaSlot.getSibling();
            if (betaFirst instanceof ARTGrammarInstanceTerminal && ((ARTGrammarElementTerminal) betaFirst.getPayload()).equals(input.get(i + 1))) {
              setAdd(qSet, eSetCandidate2);
            }
          }
        }

        // if Λ = (D ::= α·, h, w) {
        ARTGrammarElementNonterminal D = (ARTGrammarElementNonterminal) lambda.slot.getLhsL().getPayload();
        if (lambda.slot.getSibling() instanceof ARTGrammarInstanceEpsilon || lambda.slot.getSibling() == null) {
          if (artTrace > 0) artTraceText.println("Processing end slot item " + lambda);
          // if w = null {
          if (w == null) {
            // if there is no node v in V labelled (D, i, i) create one
            ARTEarleySPPFNodeSymbol v = new ARTEarleySPPFNodeSymbol(D, i, i);
            if (artTrace > 0) artTraceText.println("Created end slot SPPF node " + v);
            sppf.add(v);
            // set w = v
            w = v;
            // if w does not have family (epsilon) add one }
            w.addFamily(epsilonSPPFNode);
          }
          // if h = i { add (D, w) to H }
          if (h == i) hMap.put(D, w);

          // for all (A ::= τ · Dδ, k, z) in Eh {
          for (ARTEarleyItem e : new LinkedList<>(eSets.get(h))) {
            // major inefficiency here: rework as a set of hashmaps from D to items
            if (e.slot.getSibling() != null && e.slot.getSibling().getPayload() != null && e.slot.getSibling().getPayload().equals(D)) {
              // let y = MAKE_NODE(A ::= τ D · δ, k, i, z, w, V)
              // System.out.println("Found matching e " + e);
              int k = e.i;
              ARTEarleySPPFNode z = e.sppfNode;
              ARTGrammarInstanceSlot deltaPreSlot = (ARTGrammarInstanceSlot) e.slot.getSibling().getSibling();
              ARTEarleySPPFNode y = makeNode(deltaPreSlot, k, i, z, w, vMap);
              // if δ ∈ ΣN and (A ::= τ D · δ, k, y) ! ∈ Ei {
              ARTEarleyItem candidateItem = new ARTEarleyItem(deltaPreSlot, k, y);
              if (inSigmaN(deltaPreSlot) && !eSets.get(i).contains(candidateItem)) {
                // add (A ::= τ D · δ, k, y) to Ei and R }
                setAdd(eSets.get(i), candidateItem);
                setAdd(rSet, candidateItem);
              }
              // if δ = ai+1 δ ′ { add (A ::= τ D · δ, k, y) to Q } }
              if (deltaPreSlot.getSibling() != null && deltaPreSlot.getSibling().getPayload() != null
                  && deltaPreSlot.getSibling().getPayload().equals(input.get(i + 1))) {
                setAdd(qSet, candidateItem);
              }
            }
          }
        }
      }
      // V=∅
      vMap.clear();
      // create an SPPF node v labelled (ai+1 , i, i + 1)
      if (i != inputLength) {
        ARTEarleySPPFNodeSymbol v = new ARTEarleySPPFNodeSymbol(input.get(i + 1), i, i + 1);
        sppf.add(v);

        // while Q ! = ∅ {
        while (!qSet.isEmpty()) {
          // remove an element, Λ = (B ::= α · ai+1 β, h, w) say, from Q
          ARTEarleyItem lambda = setRemove(qSet);
          qSetRemovals++;
          ARTGrammarInstance postAlphaSlot = lambda.slot;
          int h = lambda.i;
          ARTEarleySPPFNode w = lambda.sppfNode;
          // let y = MAKE_NODE(B ::= α ai+1 · β, h, i + 1, w, v, V)
          ARTGrammarInstanceSlot preBetaSlot = (ARTGrammarInstanceSlot) postAlphaSlot.getSibling().getSibling();
          ARTEarleySPPFNode y = makeNode(preBetaSlot, h, i + 1, w, v, vMap);
          // if β ∈ ΣN { add (B ::= α ai+1 · β, h, y) to Ei+1 }
          ARTEarleyItem candidateItem = new ARTEarleyItem(preBetaSlot, h, y);
          if (inSigmaN(preBetaSlot)) {
            setAdd(eSets.get(i + 1), candidateItem);
          }
          // if β = ai+2 β′ { add (B ::= α ai+1 · β, h, y) to Q′ }
          if (preBetaSlot.getSibling() != null && preBetaSlot.getSibling().getPayload() != null
              && preBetaSlot.getSibling().getPayload().equals(input.get(i + 2))) {
            setAdd(qPrimeSet, candidateItem);
          }
        }
      }
    }
    artParseCompleteTime = artReadClock();
    artGrammar.getARTManager().printMemory(this.getClass().getSimpleName() + " start of parse");

    // if (S ::= τ ·, 0, w) ∈ En return w

    // Scan eSets.get(inputLength) to look for accepting slots and some w, then
    // return w
    if (artTrace > 0)
      artTraceText.println("input length is " + inputLength + "\naccepting slots are " + acceptingSlotsSet + "\nfinal Earley set is " + eSets.get(inputLength));

    for (ARTEarleyItem e : eSets.get(inputLength)) {
      if (e.i == 0 && acceptingSlotsSet.contains(e.slot)) {
        artIsInLanguage = true;
        System.out.println(this.getClass().getSimpleName() + (artIsInLanguage ? " accept" : " reject") + " in " + artParseCompleteTime * 1E-6 + "ms");
        if (artTrace > 0) {
          artTraceText.println(this.getClass().getSimpleName() + " accept");
          printSets();
          artTraceText.close();
        }
        return /* w */ /* e.sppfNode */;
      }
    }

    artIsInLanguage = false;
    System.out.println(this.getClass().getSimpleName() + (artIsInLanguage ? " accept" : " reject") + " in " + artParseCompleteTime * 1E-6 + "ms");
    if (artTrace > 0) {
      artTraceText.println(this.getClass().getSimpleName() + " reject");
      printSets();
      artTraceText.close();
    }
    return;

  }

  // MAKE_NODE(B ::= αx · β, j, i, w, v, V) {
  ARTEarleySPPFNode makeNode(ARTGrammarInstanceSlot preBetaSlot, int j, int i, ARTEarleySPPFNode w, ARTEarleySPPFNode v,
      Map<ARTEarleySPPFNode, ARTEarleySPPFNode> vSet2) {
    if (artTrace > 0) artTraceText.println("MAKE_NODE(" + preBetaSlot + ", " + i + ", " + j + ")");
    // if β = epsilon { let s = B } else { let s = (B ::= αx · β) }
    ARTGrammarInstance s;
    if (preBetaSlot.getSibling() == null || preBetaSlot.getSibling() instanceof ARTGrammarInstanceEpsilon)
      s = preBetaSlot.getLhsL();
    else
      s = preBetaSlot;

    // if α = epsilon and β != epsilon { let y = v }
    ARTEarleySPPFNode y;
    ARTGrammarInstanceSlot postAlphaSlot = (ARTGrammarInstanceSlot) preBetaSlot.getLeftSibling().getLeftSibling();
    if (postAlphaSlot.getLeftSibling() == null && preBetaSlot.getSibling() != null && !(preBetaSlot.getSibling() instanceof ARTGrammarInstanceEpsilon)) {
      y = v;
    }
    // else {
    else {
      ARTEarleySPPFNode candidateItem, candidateItemImage;
      if (s instanceof ARTGrammarInstanceLHS)
        candidateItem = new ARTEarleySPPFNodeSymbol(s.getPayload(), j, i);
      else
        candidateItem = new ARTEarleySPPFNodeIntermediate((ARTGrammarInstanceSlot) s, j, i);
      // if there is no node y in V labelled (s, j, i) create one and add it to V
      if ((candidateItemImage = vMap.get(candidateItem)) == null) {
        y = candidateItem;
        vMap.put(y, y);
        sppf.add(candidateItem);
      } else
        y = candidateItemImage;

      // if w = null and y does not have a family of children (v) add one
      if (w == null)
        y.addFamily(v);
      // if w != null and y does not have a family of children (w, v) add one }
      else
        y.addFamily(w, v);
    }

    // return y
    return y;
  }

  @Override
  public void artParse(String inputString) {
    System.out.println("Fatal error: no grammar supplied to " + this.getClass());
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