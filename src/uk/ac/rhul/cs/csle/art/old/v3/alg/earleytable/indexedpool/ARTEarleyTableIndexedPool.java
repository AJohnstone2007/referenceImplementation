package uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.indexedpool;

import java.util.ArrayList;

import uk.ac.rhul.cs.csle.art.old.util.pool.ARTPool;
import uk.ac.rhul.cs.csle.art.old.v3.alg.ARTParserBase;
import uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.support.ARTEarleyTableDataIndexed;
import uk.ac.rhul.cs.csle.art.old.v3.lex.ARTLexerV3;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.mode.ARTModeGrammarKind;

public class ARTEarleyTableIndexedPool extends ARTParserBase {
  private int rSetRemovals;

  // Pool - we use a single pool which is reinitialised for each call to parse()
  private ARTPool pool;
  private final int earleyItemPerLevelBucketCount = 809;
  private final int upsilonBucketCount = 2000011;

  // End of language customisation

  // An Earley configuration is nfaVertex x inputIndex, that is a 2:0
  private final int earleyConfigurationNFAVertexOffset = 0;
  private final int earleyConfigurationInputIndexOffset = 1;

  // An Earley Table Chi Set element is ChiSetIndex x i X j X k, that is a 4:0
  private final int chiSetElementIndex = 0;
  private final int chiSetElementI = 1;
  private final int chiSetElementK = 2;
  private final int chiSetElementJ = 3;

  private ARTEarleyTableDataIndexed earleyTableIndexed = null;
  private final int epsilon;
  private final int eos;

  int upsilon;
  private int inputLength = 0;

  public ARTEarleyTableIndexedPool(ARTGrammar artGrammar) {
    super(artGrammar);
    System.out.println("Entered cnstructor: super() executed");
    artGrammar.getARTManager().printMemory(this.getClass().getSimpleName() + " start of constructor");
    epsilon = artGrammar.getEpsilon().getElementNumber();
    eos = artGrammar.getEoS().getElementNumber();

    if (artGrammar.getGrammarKind() != ARTModeGrammarKind.BNF) return;

    earleyTableIndexed = new ARTEarleyTableDataIndexed(artGrammar);
  }

  private int[] R;
  private int[] E;
  private int[] EList;
  private int[] rdn;

  public void artParse(String stringInput, String inputFilename, boolean useRDNSet) {
    artIsInLanguage = false;

    if (artNotBNF()) {
      if (artTrace > 0) artTraceText.println(this.getClass() + " called on EBNF grammar aborting");
      artInadmissable = true;
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

    // End of inlining

    int[] input = ret;

    if (input == null)
      System.out.println("Reject lexical");
    else {
      inputLength = input.length - 2; // input[0] is not used and input[n+1] is $

      pool = new ARTPool(21, 2048); // 2048 x 2Mlocation blocks: at 32-bit integers that 8G of memory when fully

      // Declare arrays of sets representing R and E (curly E in document) and rdn
      R = new int[inputLength + 1];
      E = new int[inputLength + 1];
      EList = new int[inputLength + 1];
      rdn = new int[inputLength + 1];
      for (int i = 0; i < inputLength + 1; i++) {
        R[i] = pool.listMake();
        E[i] = pool.mapMake(earleyItemPerLevelBucketCount);
        EList[i] = pool.listMake();
        rdn[i] = pool.mapMake(earleyItemPerLevelBucketCount);
      }
      upsilon = pool.mapMake(upsilonBucketCount);

      // E_0 = R_0 = { (G_0,0) }
      pool.listAdd_2(R[0], 0, 0);
      pool.mapFind_2_0(E[0], 0, 0);
      pool.listAdd_2(EList[0], 0, 0);

      earleyTableIndexed.getGrammar().getARTManager().printMemory(this.getClass().getSimpleName() + " start of parse");
      artRestartClock();

      // for (0 \le j \= n)
      for (int j = 0; j <= inputLength; j++) {
        // while ( R_j \ne \emptyset)
        // System.out.println(j);
        while (pool.poolGet(R[j]) != 0) {
          // Remove an item (G, k) from R_j
          int c = pool.listRemove(R[j]);
          rSetRemovals++;
          int G = pool.poolGet(c + earleyConfigurationNFAVertexOffset);
          int k = pool.poolGet(c + earleyConfigurationInputIndexOffset);
          // System.out.printf("At index position %d, removed from R configuration G%d, %d\n", j, G, k);
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
                // ARTEarleyRDNSetElement rdnSetElement = new ARTEarleyRDNSetElement(x, k);
                if (useRDNSet) {
                  if (pool.mapLookup_2(rdn[j], x, k) != 0) {
                    continue;
                  }
                  pool.mapFind_2_0(rdn[j], x, k);
                } // for ((H, i) \in E_k)

                // int offset = pool.poolGet(e + earleyItemIndexOffset);
                // int slot = pool.poolGet(e + earleyItemSlotOffset);

                // for (int e = pool.mapIteratorFirst1(E[k]); e != 0; e = pool.mapIteratorNext1()) {
                for (int e = pool.poolGet(EList[k]); e != 0; e = pool.poolGet(e)) {
                  // for (ARTEarleyConfiguration e : E[k].getSet()) {
                  int H = pool.poolGet(e + 1 + earleyConfigurationNFAVertexOffset);
                  int i = pool.poolGet(e + 1 + earleyConfigurationInputIndexOffset);
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
      earleyTableIndexed.getGrammar().getARTManager().printMemory(this.getClass().getSimpleName() + " end of parse");

      artIsInLanguage = false;

      int upsilonCardinality = 0;

      for (int pp = pool.mapIteratorFirst1(upsilon); pp != 0; pp = pool.mapIteratorNext1()) {
        upsilonCardinality++;
        if (pool.poolGet(pp + chiSetElementI) == 0 && pool.poolGet(pp + chiSetElementJ) == inputLength)
          for (int ppc : earleyTableIndexed.chiSetCache[pool.poolGet(pp + chiSetElementIndex)]) {
            // System.out.println("Acceptance testing: pp = " + pp + " ppc = " + ppc);
            artIsInLanguage |= earleyTableIndexed.acceptingProductions[ppc];
          }
      }
      System.out.println("EarleyTableIndexedPool " + (artIsInLanguage ? "accept" : "reject") + " in " + artParseCompleteTime * 1E-6 + "ms");
      System.out.println("Total removals from R =  " + rSetRemovals);
      System.out.println("Final raw P with Chi set based BSRs: |PChi| = " + upsilonCardinality);

      System.out.println("Statistics for E[] " + pool.mapArrayStatistics(E));
      System.out.println("Statistics for Upsilon" + pool.mapStatistics(upsilon));
    }
    System.out.println("Exiting parse function");
  }

  private void add(int G, int x, int i, int k, int j, int t) {
    int[] tmpChiSet;
    int H = earleyTableIndexed.outEdgeMap[G][x];
    if (H != -1 && earleyTableIndexed.select[H][t]) {

      tmpChiSet = earleyTableIndexed.chiSetCache[earleyTableIndexed.epnMap[G][x]];
      if (tmpChiSet != null && !(tmpChiSet.length == 0)) {
        pool.mapFind_4_0(upsilon, earleyTableIndexed.epnMap[G][x], i, k, j);
        // System.out.printf("Adding to Upsilon via epn (%d, %d, %d, %d)\n", earleyTableIndexed.epnMap[h][x], i, k, j);
        // upsilon.add(new ARTChiBSR(earleyTableIndexed.epnMap[h][x], i, k, j));
      }

      tmpChiSet = earleyTableIndexed.chiSetCache[earleyTableIndexed.eeMap[G][x]];
      if (tmpChiSet != null && !(tmpChiSet.length == 0)) {
        pool.mapFind_4_0(upsilon, earleyTableIndexed.eeMap[G][x], i, j, j);
        // System.out.printf("Adding to Upsilon via ee (%d, %d, %d, %d)\n", earleyTableIndexed.eeMap[h][x], i, j, j);
        // upsilon.add(new ARTChiBSR(earleyTableIndexed.eeMap[h][x], i, j, j));
      }

      if (H != -1) {
        // ARTEarleyConfiguration tmpConfiguration = new ARTEarleyConfiguration(earleyTableIndexed.outEdgeMap[h][x], i);
        if (pool.mapLookup_2(E[j], H, i) == 0) {
          // System.out.printf("Adding (%d, %d) to R[%d]\n", H, i, j);
          pool.mapFind_2_0(E[j], H, i);
          pool.listAdd_2(EList[j], H, i);
          pool.listAdd_2(R[j], H, i);
        }
      }
    } else {
      if (x == epsilon && earleyTableIndexed.select[G][t]) {
        tmpChiSet = earleyTableIndexed.chiSetCache[earleyTableIndexed.eeMap[G][x]];
        if (tmpChiSet != null && !(tmpChiSet.length == 0)) {
          pool.mapFind_4_0(upsilon, earleyTableIndexed.eeMap[G][x], i, j, j);
        }
      }
    }
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
