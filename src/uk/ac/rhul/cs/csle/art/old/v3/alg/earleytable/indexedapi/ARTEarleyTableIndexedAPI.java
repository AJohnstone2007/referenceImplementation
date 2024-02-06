package uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.indexedapi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.util.bsr.ARTBSRIndexed;
import uk.ac.rhul.cs.csle.art.old.v3.alg.ARTParserBase;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.support.ARTChiBSR;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.support.ARTChiBSRSet;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.support.ARTEarleyTableDataIndexed;
import uk.ac.rhul.cs.csle.art.old.v3.lex.ARTLexerV3;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.mode.ARTModeGrammarKind;

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

public class ARTEarleyTableIndexedAPI extends ARTParserBase {
  private int rSetRemovals;
  private ARTEarleyTableDataIndexed earleyTableIndexed = null;
  private final int epsilon;
  private final int eos;

  ARTChiBSRSet p;
  Set<ARTBSRIndexed> simpleBSRSet;
  // private long startTime;
  // private long parseTime;
  private int inputLength = 0;

  public ARTEarleyTableIndexedAPI(ARTGrammar artParserGrammar) {
    super(artParserGrammar);
    artGrammar.getARTManager().printMemory(this.getClass().getSimpleName() + " start of constructor");
    epsilon = artParserGrammar.getEpsilon().getElementNumber();
    eos = artParserGrammar.getEoS().getElementNumber();

    if (artParserGrammar.getGrammarKind() != ARTModeGrammarKind.BNF) return;

    earleyTableIndexed = new ARTEarleyTableDataIndexed(artParserGrammar);

    // ARTText.writeFile("", "EarleyNFA.txt", earleyTableIndexed.toString());

    p = new ARTChiBSRSet(earleyTableIndexed);
  }

  private final ArrayList<Integer> extents = new ArrayList<>();

  ARTEarleyConfigurationQueue R[]; // We aren't allowed arrays of generics in Java, so we make a variant just for parse configurations
  ARTEarleyConfigurationSet E[];
  ARTEarleyRDNSet rdn[];

  public void artParse(String stringInput, String inputFilename, boolean useRDNSet) {
    artIsInLanguage = false;

    int upsilonCardinality = 0;
    if (artNotBNF()) {
      if (artTrace > 0) artTraceText.println(this.getClass() + " called on EBNF grammar aborting");
      return;
    }

    // This has been inlined. Needs to be tidied up by calling relevant routine in ARTLexer
    int[] ret;
    ArrayList<ARTGrammarElementTerminal> input1 = new ARTLexerV3(earleyTableIndexed.getGrammar()).lexicaliseToArrayListOfTerminals(stringInput, 1);
    if (input1 == null)
      ret = null;
    else {
      ret = new int[input1.size()];
      for (int j1 = 0; j1 < ret.length; j1++)
        ret[j1] = input1.get(j1).getElementNumber();
    }

    int[] input = ret;
    // End of inlining

    if (input == null) {
      System.out.println("Reject lexical");
      if (artTrace > 0) artTraceText.println("Reject lexical");
    } else {

      inputLength = input.length - 2; // input[0] is not used and input[n+1] is $

      // System.out.println("Earley table driven parser running on " + inputLength + " tokens: " + stringInput);
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
      R[0].add(new ARTEarleyConfiguration(0, 0));
      E[0].add(new ARTEarleyConfiguration(0, 0));

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
          int G = c.getNfaVertex();
          int k = c.getInputIndex();
          if (artTrace > 0) artTraceText.println("At index position " + j + ", removed from R configuration " + c);
          // if ( k != j)
          if (k != j) {
            // for(X \in NFA_2(G; a_{j+1}) {
            int successorElement = input[j + 1]; // successorElement = a_{j+1}
            if (successorElement == eos) successorElement = epsilon;
            // int[] tmpRed = earleyTableIndexed.redSetCache[earleyTableIndexed.redMap[G][successorElement]];
            int[] tmpRed = earleyTableIndexed.rLHS[G];
            if (tmpRed != null) {
              for (int xi = 0; xi < tmpRed.length; xi++) {
                int x = tmpRed[xi];
                ARTEarleyRDNSetElement rdnSetElement = new ARTEarleyRDNSetElement(x, k);
                if (useRDNSet) {
                  if (rdn[j].contains(rdnSetElement)) {
                    if (artTrace > 0) artTraceText.println("At index position " + j + ", found " + rdnSetElement + " in rdn, so skipping");
                    continue;
                  }
                  rdn[j].add(rdnSetElement);
                } // for ((H, i) \in E_k)
                for (ARTEarleyConfiguration e : E[k].getSet()) {
                  int H = e.getNfaVertex();
                  int i = e.getInputIndex();
                  // ADD(H, X, i, k, j)
                  add(H, x, i, k, j, input[j + 1]);
                }
              }
            }
          }
          // ADD(G, \epsilon, j, j, j)
          add(G, epsilon, j, j, j, input[j + 1]);

          // if (j < n) ADD(G, a_{j+1}, k, j, j + 1)
          if (j < inputLength) {
            add(G, input[j + 1], k, j, j + 1, input[j + 2]);
          }
        }
      }

      artParseCompleteTime = artReadClock();
      artGrammar.getARTManager().printMemory(this.getClass().getSimpleName() + " end of parse");

      if (artTrace > 0) artTraceText.println("Acceptance testing against start symbol "
          + earleyTableIndexed.slotArray.symbolJavaStrings[earleyTableIndexed.slotArray.startSymbol] + " with right extent " + input.length);
      artIsInLanguage = false;

      for (ARTChiBSR pp : p.getSet()) {
        upsilonCardinality++;
        if (pp.getI() == 0 && pp.getJ() == inputLength) for (int ppc : earleyTableIndexed.chiSetCache[pp.getChiSetIndex()]) {
          // System.out.println("Acceptance testing: pp = " + pp + " ppc = " + ppc);
          artIsInLanguage |= earleyTableIndexed.acceptingProductions[ppc];
        }
      }
      System.out.println("EarleyTableIndexedAPI " + (artIsInLanguage ? "accept" : "reject") + " in " + artParseCompleteTime * 1E-6 + "ms");
      System.out.println("Total removals from R =  " + rSetRemovals);
      System.out.println("Final raw P with Chi set based BSRs: |PChi| = " + upsilonCardinality);
      if (artTrace > 0) {
        artTraceText.println("\n" + (artIsInLanguage ? "Accept" : "Reject") + " in " + artParseCompleteTime * 1E-6 + "ms");
        artTraceText.println("\nFinal raw P with Chi set based BSRs: |PChi| = " + p.getSet().size() + "\n" + p);

        simpleBSRSet = makeBSRSet();
        artTraceText.println("\nFinal raw P with simple BSRs: |Psimple| = " + simpleBSRSet.size() + "\n" + simpleBSRSet);
      }
    }

    if (artTrace > 0) artTraceText.close();
  }

  private void add(int G, int x, int i, int k, int j, int t) {
    if (artTrace > 0) artTraceText.println("ADD(G" + G + ", " + earleyTableIndexed.slotArray.symbolJavaStrings[x] + ", " + i + ", " + k + ", " + j + ")");
    int[] tempChiSet;
    int H = earleyTableIndexed.outEdgeMap[G][x];
    if (H != -1 && earleyTableIndexed.select[H][t]) {

      tempChiSet = earleyTableIndexed.chiSetCache[earleyTableIndexed.epnMap[G][x]];
      if (tempChiSet != null && !(tempChiSet.length == 0)) {
        if (artTrace > 0) artTraceText.println("epn triggers add");
        p.add(new ARTChiBSR(earleyTableIndexed.epnMap[G][x], i, k, j));
      }

      tempChiSet = earleyTableIndexed.chiSetCache[earleyTableIndexed.eeMap[G][x]];
      if (tempChiSet != null && !(tempChiSet.length == 0)) {
        if (artTrace > 0) artTraceText.println("ee triggers add");
        p.add(new ARTChiBSR(earleyTableIndexed.eeMap[G][x], i, j, j));
      }

      if (artTrace > 0) artTraceText.println("H = " + (H == 0 ? "null" : ("G" + H)));
      if (H != -1) {
        ARTEarleyConfiguration tmpConfiguration = new ARTEarleyConfiguration(H, i);
        if (artTrace > 0) artTraceText.println("Checking " + tmpConfiguration + "against " + E[j]);
        if (!E[j].contains(tmpConfiguration)) {
          if (artTrace > 0) artTraceText.println("Adding to E/R[" + j + "] " + tmpConfiguration);
          E[j].add(tmpConfiguration);
          R[j].add(tmpConfiguration);
        }
      }
    } else {
      if (x == epsilon && earleyTableIndexed.select[G][t]) {
        tempChiSet = earleyTableIndexed.chiSetCache[earleyTableIndexed.eeMap[G][x]];
        if (tempChiSet != null && !(tempChiSet.length == 0)) {
          if (artTrace > 0) artTraceText.println("ee triggers add");
          p.add(new ARTChiBSR(earleyTableIndexed.eeMap[G][x], i, j, j));
        }
      }
    }
    if (artTrace > 0) artTraceText.println("After add(), p is " + pToString(p) + ", E[j] is " + E[j] + ", R[j] is " + R[j]);
  }

  private String pToString(ARTChiBSRSet p) {
    String ret = "";
    for (ARTChiBSR x : p.getSet())
      ret += x + "";
    return ret;
  }

  public Set<ARTBSRIndexed> makeBSRSet() {
    Set<ARTBSRIndexed> ret = new HashSet<>();
    // Some gyrations required here to ensure subtyping is correctly maintained: we don't want a more generale ARTBSR constructor because that would be too
    // broad
    for (ARTChiBSR pp : p.getSet())
      for (int ppi : earleyTableIndexed.chiSetCache[pp.getChiSetIndex()])
        ret.add(new ARTBSRIndexed(ppi, pp.getI(), pp.getJ(), pp.getK()));

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
    // TODO Auto-generated method stub

  }
}
