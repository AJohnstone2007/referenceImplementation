package uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.linkedapi;

import java.util.ArrayList;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.util.bsr.ARTBSR;
import uk.ac.rhul.cs.csle.art.old.util.bsr.ARTBSRSet;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.old.v3.alg.ARTParserBase;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.support.ARTChiBSR;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.support.ARTChiBSRSet;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.support.ARTChiSet;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.support.ARTEarleyNFAVertex;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.support.ARTEarleyTableDataLinked;
import uk.ac.rhul.cs.csle.art.old.v3.lex.ARTLexerV3;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementEoS;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementEpsilon;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstance;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceCat;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceSlot;

/*
 * An implementation of the basic EarleyNFA traversing parser
 *
 *  In the paper, the parser is described as operating on a table indexed by state number G:integer and grammar symbol X:symbol
 *
 *  The cell indexed by (G, x) contains a triple and a set
 *     The triple is (target state G_x:integer, set EPN_G_x, set EE_G_x)
 *     The set is G^red_x is a the follow set of the Y::= \alpa . productions in the label of G
 *
 *  In the NFA, this corresponds to going to the state G and then accessing the maps for element x
 *
 */

public class ARTEarleyTableLinkedAPI extends ARTParserBase {
  private ARTEarleyTableDataLinked nfa = null;
  private final ARTGrammarElementEpsilon epsilon;
  private final ARTGrammarElementEoS eos;
  private int qSetRemovals, rSetRemovals;

  ARTChiBSRSet p;
  ARTBSRSet simpleBSRSet;
  // private long startTime;
  // private long parseTime;
  private int inputLength = 0;

  public ARTEarleyTableLinkedAPI(ARTGrammar artParserGrammar) {
    super(artParserGrammar);
    artGrammar.getARTManager().printMemory(this.getClass().getSimpleName() + " start of constructor");
    epsilon = artParserGrammar.getEpsilon();
    eos = artParserGrammar.getEoS();

    if (!artNotBNF()) {
      nfa = new ARTEarleyTableDataLinked(artParserGrammar);

      ARTText.writeFile("", "EarleyNFA.txt", nfa.toString());
      // ARTText.writeFile("", "EarleyTable.c", nfa.toANSIC());

      // ARTTextHandlerString artStringHandler = new ARTTextHandlerString();
      // ARTText artStringText = new ARTText(artStringHandler);
      // nfa.generateDot(artStringText);
      // ARTText.writeFile("", "EarleyNFA.dot", artStringHandler.getText());

      p = new ARTChiBSRSet(nfa);
    }
  }

  private final ArrayList<Integer> extents = new ArrayList<>();

  ARTEarleyConfigurationQueue R[]; // We aren't allowed arrays of generics in Java, so we make a variant just for parse configurations
  ARTEarleyConfigurationSet E[];
  ARTEarleyRDNSet rdn[];

  public void artParse(String stringInput, String inputFilename, boolean useRDNSet) {

    if (artNotBNF()) {
      if (artTrace > 0) artTraceText.println(this.getClass() + " called on EBNF grammar aborting");
      return;
    }

    artIsInLanguage = false;

    ArrayList<ARTGrammarElementTerminal> input = new ARTLexerV3(nfa.getGrammar()).lexicaliseToArrayListOfTerminals(stringInput, 1);

    if (input == null) {
      System.out.println("Reject lexical");
      if (artTrace > 0) artTraceText.println("Reject lexical");
    } else {

      inputLength = input.size() - 2; // input[0] is not used and input[n+1] is $

      // System.out.println("Earley table driven parser running on " + inputLength + " tokens");
      if (artTrace > 0) {
        artTraceText.println("Parsing " + inputLength + " tokens");
        artTraceText.println(input.toString());
      }

      // Declare arrays of sets representing R and E (curly E in document) and rdn
      R = new ARTEarleyConfigurationQueue[inputLength + 2];
      E = new ARTEarleyConfigurationSet[inputLength + 2];
      rdn = new ARTEarleyRDNSet[inputLength + 2];

      // Create empty sets at each index position
      // E_j = R_j = \emptyset: include j = 0 because we need to make the set before adding (G_0, 0)
      for (int j = 0; j <= inputLength; j++) {
        R[j] = new ARTEarleyConfigurationQueue(); // R_j is populated with an empty set
        E[j] = new ARTEarleyConfigurationSet(); // E_j is populated with an empty set
        rdn[j] = new ARTEarleyRDNSet(); // rdn[j] is populated with an empty set
      }

      // E_0 = R_0 = { (G_0,0) }
      R[0].add(new ARTEarleyConfiguration(nfa.getState(0), 0));
      E[0].add(new ARTEarleyConfiguration(nfa.getState(0), 0));

      artGrammar.getARTManager().printMemory(this.getClass().getSimpleName() + " start of parse");
      artRestartClock();

      // for (0 \le j \= n)
      for (int j = 0; j <= inputLength; j++) {
        if (artTrace > 0) artTraceText.println("Level " + j);
        // while ( R_j \ne \emptyset)
        while (!R[j].isEmpty()) {
          // Remove an item (G, k) from R_j
          ARTEarleyConfiguration c = R[j].remove();
          rSetRemovals++;
          ARTEarleyNFAVertex G = c.getNfaVertex();
          int q = c.getInputIndex();
          if (artTrace > 0) artTraceText.println("At index position " + j + ", removed from R configuration " + c);
          // if ( q != j)
          if (q != j) {
            // for(X \in NFA_2(G; a_{j+1}) {
            ARTGrammarElement successorElement = input.get(j + 1); // successorElement = a_{j+1}
            if (successorElement == eos) successorElement = epsilon;

            Set<ARTGrammarElementNonterminal> tmpRed = G.getrLHS();
            if (tmpRed != null) {
              for (ARTGrammarElement X : tmpRed) {
                ARTEarleyRDNSetElement rdnSetElement = new ARTEarleyRDNSetElement((ARTGrammarElementNonterminal) X, q);
                if (useRDNSet) {
                  if (rdn[j].contains(rdnSetElement)) {
                    if (artTrace > 0) artTraceText.println("At index position " + j + ", found " + rdnSetElement + " in rdn, so skipping");
                    continue;
                  }
                  rdn[j].add(rdnSetElement);
                } // for ((H, i) \in E_q)
                for (ARTEarleyConfiguration e : E[q].getSet()) {
                  ARTEarleyNFAVertex K = e.getNfaVertex();
                  int i = e.getInputIndex();
                  // ADD(K, X, i, q, j, a_{j+1})
                  add(K, X, i, q, j, input.get(j + 1));
                }
              }
            }
          }
          // ADD(G, \epsilon, j, j, j, a_{j+1})
          add(G, epsilon, j, j, j, input.get(j + 1));

          // if (j < n) ADD(G, a_{j+1}, k, j, j + 1, a_{j+2})
          if (j < inputLength) {
            add(G, input.get(j + 1), q, j, j + 1, input.get(j + 2));
          }
        }
      }

      artParseCompleteTime = artReadClock();
      artGrammar.getARTManager().printMemory(this.getClass().getSimpleName() + " end of parse");

      // System.out.println("Input length = " + inputLength);
      for (ARTChiBSR pp : p.getSet())
        for (ARTGrammarInstance ppc : nfa.getChiSetCache().get(pp.getChiSetIndex()).getSet()) {
          // System.out.println("Acceptance testing: pp = " + pp + " ppc = " + ppc + "ap = " + nfa.getAcceptingProductions());
          if (pp.getI() == 0 && pp.getJ() == inputLength && nfa.getAcceptingProductions().contains(ppc)) artIsInLanguage = true;
        }

      simpleBSRSet = makeBSRSet();
      System.out.println("EarleyTableLinkedAPI " + (artIsInLanguage ? "accept" : "reject") + " in " + artParseCompleteTime * 1E-6 + "ms");
      System.out.println("Total removals from R = " + rSetRemovals);
      System.out.println("Final raw P with Chi set based BSRs: |PChi| = " + p.getSet().size());
      System.out.println("Final raw P with simple BSRs: |Psimple| = " + simpleBSRSet.getBSRSet().size());
      if (artTrace > 0) {
        artTraceText.println("\n" + (artIsInLanguage ? "Accept" : "Reject") + " in " + artParseCompleteTime * 1E-6 + "ms");
        artTraceText.println("Total removals from R = " + rSetRemovals);
        artTraceText.println("Final raw P with Chi set based BSRs: |PChi| = " + p.getSet().size());
        artTraceText.println("Final raw P with simple BSRs: |Psimple| = " + simpleBSRSet.getBSRSet().size());
        int totalCardinality = 0;
        artTraceText.println("Earley table sets\n");
        for (int i = 0; i < E.length; i++) {
          if (E[i] == null || E[i].set == null) continue;
          totalCardinality += E[i].set.size();
          artTraceText.println(i + ": " + E[i].getSet());
        }
        artTraceText.println("Total cardinality of Earley Table sets: " + totalCardinality + "\n");

        artTraceText.println("\nFinal raw P with Chi set based BSRs: |PChi| = " + p.getSet().size() + "\n" + p);

        artTraceText.println("\nFinal raw P with simple BSRs: |Psimple| = " + simpleBSRSet.getBSRSet().size() + "\n" + simpleBSRSet);
      }
    }

    if (artTrace > 0) artTraceText.close();

  }

  private void add(ARTEarleyNFAVertex G, ARTGrammarElement x, int i, int q, int j, ARTGrammarElementTerminal t) {
    if (artTrace > 0) artTraceText.println("ADD(G" + G.getNumber() + ", " + x + ", " + i + ", " + q + ", " + j + ")");

    ARTEarleyNFAVertex H = G.getOutEdgeMap().get(x);
    ARTChiSet tmpChiSet;

    if (H != null && H.getSelect().contains(t)) {

      tmpChiSet = nfa.getChiSetCache().get(G.getEpnMap().get(x));
      if (tmpChiSet != null && !tmpChiSet.isEmpty()) {
        if (artTrace > 0) artTraceText.println("epn triggers add");
        p.add(new ARTChiBSR(G.getEpnMap().get(x), i, q, j));
      }

      tmpChiSet = nfa.getChiSetCache().get(G.getEeMap().get(x));
      if (tmpChiSet != null && !tmpChiSet.isEmpty()) {
        if (artTrace > 0) artTraceText.println("ee triggers add");
        p.add(new ARTChiBSR(G.getEeMap().get(x), i, j, j));
      }

      ARTEarleyConfiguration tmpConfiguration = new ARTEarleyConfiguration(H, i);
      if (artTrace > 0) artTraceText.println("Checking " + tmpConfiguration + "against " + E[j]);
      if (!E[j].contains(tmpConfiguration)) {
        if (artTrace > 0) artTraceText.println("Adding to E/R[" + j + "] " + tmpConfiguration);
        E[j].add(tmpConfiguration);
        R[j].add(tmpConfiguration);
      }
    } else {
      if (x == artGrammar.getEpsilon() && G.getSelect().contains(t)) {
        tmpChiSet = nfa.getChiSetCache().get(G.getEeMap().get(x));
        if (tmpChiSet != null && !tmpChiSet.isEmpty()) {
          if (artTrace > 0) artTraceText.println("x is epsilon triggers add");
          p.add(new ARTChiBSR(G.getEeMap().get(x), i, j, j));
        }
      }
    }

    if (artTrace > 0) artTraceText.println("After add(), p is " + p + ", E[j] is " + E[j] + ", R[j] is " + R[j]);
  }

  public ARTBSRSet makeBSRSet() {
    ARTBSRSet ret = new ARTBSRSet(nfa.getGrammar());
    // Some gyrations required here to ensure subtyping is correctly maintained: we don't want a more generale ARTBSR constructor because that would be too
    // broad
    for (ARTChiBSR pp : p.getSet())
      for (ARTGrammarInstance ppi : nfa.getChiSetCache().get(pp.getChiSetIndex()).getSet())
        if (ppi instanceof ARTGrammarInstanceCat)
          ret.add(new ARTBSR((ARTGrammarInstanceCat) ppi, pp.getI(), pp.getJ(), pp.getK()));
        else
          ret.add(new ARTBSR((ARTGrammarInstanceSlot) ppi, pp.getI(), pp.getJ(), pp.getK()));

    return ret;
  }

  @Override
  public void artParse(String inputString) {
  }

  @Override
  public void artWriteRDT(String filename) {
  }

  @Override
  public void artPrintRDT() {
  }
}
