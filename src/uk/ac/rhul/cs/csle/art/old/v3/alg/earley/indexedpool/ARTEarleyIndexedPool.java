/* Note that we iterate over the Earley sets, so we use the  variants of the map functions

/* Modifications for C
 *
 * Change FOUR language customisations from Java to C
 *
 * Change all instances of char[] to const char*
 * Change all instances of int[] to int*
 * Change both instances of Character.isWhiteSPace to isspace
 * Delete all instances of pool.
 * Delete all instances of artSlotArray.
 */

// For C...
/*
#include<stdlib.h>
#include<stdio.h>
#include<ctype.h>
#define String char*
#define public
#define private
#define static
#define final
#define boolean bool
#define null NULL
#define throws
#define FileNotFoundException

#include "ARTStaticSlotArray.h"
#include "ARTPool.cpp"
*/
// For Java...
package uk.ac.rhul.cs.csle.art.old.v3.alg.earley.indexedpool;

import uk.ac.rhul.cs.csle.art.old.util.pool.ARTPool;
import uk.ac.rhul.cs.csle.art.old.util.slotarray.ARTSlotArray;
import uk.ac.rhul.cs.csle.art.old.v3.alg.ARTParserBase;
import uk.ac.rhul.cs.csle.art.old.v3.alg.gll.support.ARTGLLAttributeBlock;

public class ARTEarleyIndexedPool extends ARTParserBase {
  // Pool - we use a single pool which is reinitialised for each call to parse()
  private ARTPool pool;
  // End of language customisation

  private int inputLength;
  private int epsilonSPPFNode;

  // An accepting slot is a single integer index into the tables in the
  // ARTSlotArray structure: that is a 1:0
  private int acceptingSlotsSet;

  // An Earley item is a slot x input position x SPPF node : nothing, that is a
  // 3:0
  private final int earleyItemSlotOffset = 0; // An element of the enumeration
  private final int earleyItemIndexOffset = 1;
  private final int earleyItemSPPFNodeOffset = 2;

  private int[] eSets; // An integer array holding an array of pool sets
  private int qSet, qPrimeSet, rSet, vSet;

  // An hMap is a map from nonterminal |-> SPPF node, that is a 1:1
  private final int hMapNonterminalOffset = 0;
  private final int hMapSPPFNodeOffset = 1;

  private int hMap;

  // An SPPF node is slot/nonterminal/terminal/epsilon x leftExtent x rightExtent
  // : (small) set of packedNodes, that is a 3:1
  private final int sppfNodeLabelOffset = 0; // An element of the enumeration
  private final int sppfNodeLeftExtentOffset = 1; // An integer offset
  private final int sppfNodeRightExtentOffset = 2; // An integer offset
  private final int sppfFamilyOffset = 3; // A table map with the packed nodes in it

  // A packed node is a pair of SPPF nodes, that is a 2:0
  private final int packedNodeLeftChildOffset = 0;
  private final int packedNodeRightChildOffset = 1;
  private int sppf;

  // Hash table sizes
  private final int acceptingSlotsBucketCount = 20;
  private final int sppfNodePerLevelBucketCount = 300;
  private final int sppfNodeFullBucketCount = 500000;
  private final int sppfNodeFamilyBucketCount = 5;
  private final int earleyItemPerLevelBucketCount = 40;
  private final int qPrimeBucketCount = 40;

  // For Java...
  public ARTEarleyIndexedPool(ARTSlotArray artSlotArray) {
    super(artSlotArray);
    artSlotArray.artGrammar.getARTManager().printMemory(this.getClass().getSimpleName() + " start of constructor");
  }
  // End of language customisation

  private String earleyItemToString(int item) {
    return item == 0 ? "null"
        : "(" + artSlotArray.symbolJavaStrings[pool.poolGet(item + earleyItemSlotOffset)] + ", " + pool.poolGet(item + earleyItemIndexOffset) + ", "
            + sppfNodeToString(pool.poolGet(item + earleyItemSPPFNodeOffset)) + ")";
  }

  private String sppfNodeToString(int node) {
    return node == 0 ? "null"
        : "(" + artSlotArray.symbolJavaStrings[pool.poolGet(node + sppfNodeLabelOffset)] + ", " + pool.poolGet(node + sppfNodeLeftExtentOffset) + ", "
            + pool.poolGet(node + sppfNodeRightExtentOffset) + ")";
  }

  private String packedNodeToString(int node) {
    return "(" + sppfNodeToString(pool.poolGet(node + packedNodeLeftChildOffset)) + ", " + sppfNodeToString(pool.poolGet(node + packedNodeRightChildOffset))
        + ")";
  }

  // For C...

  // public boolean isNonterminal(int symbol) {
  // return symbol > epsilon && symbol < firstSlotNumber;
  // }

  // public boolean isNonterminalOrEpsilon(int symbol) {
  // return symbol == 0 || (symbol >= epsilon && symbol < firstSlotNumber);
  // }

  // public boolean isTerminal(int symbol) {
  // return symbol > eoS && symbol < epsilon;
  // }

  // void log(String inputFilename) {
  // }

  // End of language customisation

  private boolean inSigmaN(int p) {
    return artSlotArray.isNonterminalOrEpsilon(artSlotArray.slotRightSymbols[p]);
  }

  // This method uses iterator1: call at your risk...
  private String earleySetToString(int setBase) {
    String ret = "[";
    boolean first = true;
    for (int j = pool.mapIteratorFirst1(setBase); j != 0; j = pool.mapIteratorNext1()) {
      if (first)
        first = false;
      else
        ret += ", ";
      ret += earleyItemToString(j);
    }
    return ret + "]";
  }

  // This method uses iterator1: call at your risk...
  private void printSets() {
    artTraceText.println("Earley sets");
    for (int i = 0; i <= inputLength; i++) {
      for (int j = pool.mapIteratorFirst1(eSets[i]); j != 0; j = pool.mapIteratorNext1())
        artTraceText.println(i + ": " + earleyItemToString(j));
    }
    artTraceText.println("Q = " + earleySetToString(qSet) + "\nQ' = " + earleySetToString(qPrimeSet) + "\nR = " + earleySetToString(rSet));
  }

  // This is a quick and dirty lexicaliser which does not support ART's special
  // lexical features or character level tokens - startIndex specifies the first
  // live
  // elemeng of ret[]h
  // Used for quick test of simple parsers
  public int strlen(char[] str) {
    int ret = 0;
    if (str == null) return 0;
    while (str[ret] != 0)
      ret++;
    return ret;
  }

  private boolean isSubstringAt(char[] full, int start, char[] sub) {
    int subStart = 0;

    while (full[start] != 0 && sub[subStart] != 0 && full[start] == sub[subStart]) {
      start++;
      subStart++;
    }

    return sub[subStart] == 0;
  }

  private String charsToString(char[] c) {
    String ret = "";
    for (int i = 0; c[i] != 0; i++)
      ret += c[i];
    return ret;
  }

  // For Java...
  public int[] dynamicLexicaliseLongestMatch(String inputString, int startIndex) {
    char[] input = (inputString + "\0").toCharArray();
    // For C...
    // int* dynamicLexicaliseLongestMatch(String input, int startIndex) {
    // End of language customisation

    int inputStringLength = strlen(input);
    int[] ret = null;
    int stringStart, retIndex;

    for (int pass = 0; pass < 2; pass++) {
      retIndex = stringStart = 0;
      for (int i = startIndex; i > 0; i--) {
        if (ret != null) ret[retIndex] = artSlotArray.eoS; // Dummy EoS at element zero which is not used for Earley
        retIndex++;
      }

      int longestTerminal, longestTerminalLength;

      while (stringStart < inputStringLength && Character.isWhitespace(input[stringStart]))
        stringStart++;

      while (stringStart < inputStringLength) {
        longestTerminal = longestTerminalLength = 0;
        for (int t = artSlotArray.eoS + 1; t < artSlotArray.epsilon; t++) {

          if (isSubstringAt(input, stringStart, artSlotArray.symbolStrings[t])) {
            if (strlen(artSlotArray.symbolStrings[t]) > longestTerminalLength) {
              longestTerminal = t;
              longestTerminalLength = strlen(artSlotArray.symbolStrings[t]);
            }
          }
        }
        if (ret != null) ret[retIndex] = longestTerminal;
        if (longestTerminal == 0) return null; // lexicalisation error
        stringStart += strlen(artSlotArray.symbolStrings[longestTerminal]);
        // Just do whitespace for all terminals in this version... if (!(ret[retIndex]
        // instanceof ARTGrammarElementTerminalCharacter))
        while (stringStart < inputStringLength && Character.isWhitespace(input[stringStart]))
          stringStart++;
        retIndex++;
      }
      // set a_{n+1} = $
      if (ret != null) ret[retIndex] = artSlotArray.eoS;
      retIndex++;

      if (ret == null) ret = new int[retIndex];
    }
    return ret;
  }

  // MAKE_NODE(B ::= αx · β, j, i, w, v, V) {
  int makeNode(int betaSlot, int j, int i, int w, int v, int vSet) {// vSet2 is reallt SPPF?
    if (artTrace > 0) artTraceText.println("MAKE_NODE(" + artSlotArray.symbolJavaStrings[betaSlot] + ", " + i + ", " + j + ")");

    // if β = epsilon { let s = B } else { let s = (B ::= αx · β) }
    int s;
    if (artSlotArray.slotRightSymbols[betaSlot] == 0)
      s = artSlotArray.slotLHSSymbols[betaSlot];
    else
      s = betaSlot;

    // if α = epsilon and β != epsilon { let y = v }
    int y; // SPPFNode
    int postAlphaSlot = betaSlot - 2;
    if (artSlotArray.slotRightSymbols[postAlphaSlot] == 0 && artSlotArray.slotRightSymbols[betaSlot] != 0) {
      y = v;
    }
    // else {
    else {
      // if there is no node y in V labelled (s, j, i) create one and add it to V
      y = pool.mapFind_3_1(sppf, s, j, i);

      // if w = null and y does not have a family of children (v) add one
      // First, ensure y has a set...
      if (pool.poolGet(y + sppfFamilyOffset) == 0) // Is there a set?
        pool.poolPut(y + sppfFamilyOffset, pool.mapMake(sppfNodeFamilyBucketCount));

      if (w == 0)
        pool.mapFind_2_0(pool.poolGet(y + sppfFamilyOffset), 0, v);
      // if w != null and y does not have a family of children (w, v) add one }
      else
        pool.mapFind_2_0(pool.poolGet(y + sppfFamilyOffset), w, v);
    }

    // return y
    return y;
  }

  @Override
  public void artParse(String stringInput, ARTGLLAttributeBlock startAttributes) {
    if (artNotBNF()) {
      System.out.println(this.getClass() + " called on EBNF grammar aborting");
      return;
    }

    // ARTTRACE = true;
    pool = new ARTPool(20, 1024); // 1024 x 1Mlocation blocks: at 32-buit integers that 4G of memory when fully
                                  // allocated
    epsilonSPPFNode = pool.poolAllocate(4);
    pool.poolPut(epsilonSPPFNode, artSlotArray.epsilon);

    int p;
    acceptingSlotsSet = pool.mapMake(acceptingSlotsBucketCount); // One per production of the start rule, so
    // just a small set
    for (int productionNumber = 0; (p = artSlotArray.slotIndex[artSlotArray.startSymbol][productionNumber]) != 0; productionNumber++) {
      // Kick past production
      p++;
      // !! update p to be the accepting slot!
      while (artSlotArray.slotRightSymbols[p] != 0)
        p++;

      pool.mapFind_1_0(acceptingSlotsSet, p);
    }

    int[] input = dynamicLexicaliseLongestMatch(stringInput, 1);

    if (input == null) {
      System.out.println("EarleySlotArrayPool: reject lexical");

      if (artTrace > 0) artTraceText.println("EarleySlotArrayPool: reject lexical");

      return;
    }

    inputLength = input.length - 2; // input[0] is not used and input[n+1] is $

    if (artTrace > 0) artTraceText.println("EarleySlotArrayPool runnng on " + inputLength + " tokens");

    // E0 , . . . , En , R, Q′ , V = ∅
    eSets = new int[inputLength + 1];
    for (int i = 0; i < inputLength + 1; i++)
      eSets[i] = pool.mapMake(earleyItemPerLevelBucketCount);
    rSet = pool.mapMake(earleyItemPerLevelBucketCount);
    qSet = pool.mapMake(earleyItemPerLevelBucketCount);
    qPrimeSet = pool.mapMake(qPrimeBucketCount);
    vSet = pool.mapMake(sppfNodePerLevelBucketCount);
    hMap = pool.mapMake(sppfNodePerLevelBucketCount);
    sppf = pool.mapMake(sppfNodeFullBucketCount);
    // sppf = pool.makeMap(7, 13); // debug set has only 19

    artSlotArray.artGrammar.getARTManager().printMemory(this.getClass().getSimpleName() + " start of parse");
    artRestartClock();

    // for all (S ::= α) ∈ P { if α ∈ ΣN add (S ::= ·α, 0, null) to E0
    // if α = a1 α′ add (S ::= ·α, 0, null) to Q′ } !! Q' is now Q[0] for this
    // initialisation phase
    for (int productionNumber = 0; (p = artSlotArray.slotIndex[artSlotArray.startSymbol][productionNumber]) != 0; productionNumber++) {
      if (inSigmaN(p)) {
        int temp = pool.mapFind_3_0(eSets[0], p + 1, 0, 0);
        if (artTrace > 0) artTraceText.println("1 Adding to Earley set [0] " + earleyItemToString(temp));
      }
      if (artSlotArray.slotRightSymbols[p] == input[1]) {
        int temp = pool.mapFind_3_0(qPrimeSet, p + 1, 0, 0);
        if (artTrace > 0) artTraceText.println("1 Adding to Q' set " + earleyItemToString(temp));
      }
    }
    // for 0 ≤ i ≤ n {
    for (int i = 0; i <= inputLength; i++) {
      // printSets();
      // H = ∅, R = Ei , Q = Q′
      pool.mapClear(hMap);
      pool.mapAssign(rSet, eSets[i]);
      qSet = qPrimeSet;
      qPrimeSet = pool.mapMake(qPrimeBucketCount);
      // Literal implementation
      // pool.mapAssign(qSet, qPrimeSet);
      //
      // // Q′ = ∅
      // pool.mapClear(qPrimeSet);

      // while R ! = ∅ {
      while (pool.mapCardinality(rSet) != 0) {
        if (artTrace > 0) artTraceText.println("At top of main loop, rSet is " + earleySetToString(rSet));

        // remove an element, Λ say, from R
        int lambda = pool.mapRemove(rSet);
        if (artTrace > 0) artTraceText.println("Processing " + earleyItemToString(lambda));

        int c = artSlotArray.slotRightSymbols[pool.poolGet(lambda + earleyItemSlotOffset)];
        int h = pool.poolGet(lambda + earleyItemIndexOffset);
        int w = pool.poolGet(lambda + earleyItemSPPFNodeOffset);

        // if Λ = (B ::= α · Cβ, h, w) {
        if (artSlotArray.isNonterminal(c)) {
          // for all (C ::= δ) ∈ P {
          int deltaSlot;
          for (int productionNumber = 0; (deltaSlot = artSlotArray.slotIndex[c][productionNumber]) != 0; productionNumber++) {
            // if δ ∈ ΣN and (C ::= ·δ, i, null) ! ∈ Ei {
            deltaSlot++; // Move to first child
            if (inSigmaN(deltaSlot) && pool.mapLookup_3(eSets[i], deltaSlot, i, 0) == 0) {
              // add (C ::= ·δ, i, null) to Ei and R }
              int temp = pool.mapFind_3_0(eSets[i], deltaSlot, i, 0);
              if (artTrace > 0) artTraceText.println("2 Adding to Earley set [" + i + "] " + earleyItemToString(temp));
              temp = pool.mapFind_3_0(rSet, deltaSlot, i, 0);
              if (artTrace > 0) artTraceText.println("2 Adding to R set " + earleyItemToString(temp));
            }
            // if δ = ai+1 δ' { add (C ::= ·δ, i, null) to Q } }
            if (artSlotArray.slotRightSymbols[deltaSlot] == input[i + 1]) {
              int temp = pool.mapFind_3_0(qSet, deltaSlot, i, 0);
              if (artTrace > 0) artTraceText.println("2 Adding to Q set " + earleyItemToString(temp));
            }
          }
          // if ((C, v) ∈ H) {
          int v;
          int hMapElement;
          if ((hMapElement = pool.mapLookup_1(hMap, c)) != 0) {
            v = pool.poolGet(hMapElement + hMapSPPFNodeOffset);
            // let y = MAKE_NODE(B ::= αC · β, h, i, w, v, V)
            int betaPreSlot = pool.poolGet(lambda + earleyItemSlotOffset) + 1;
            int y = makeNode(betaPreSlot, h, i, w, v, vSet);
            // if β ∈ ΣN and (B ::= αC · β, h, y) ! ∈ Ei {
            if (inSigmaN(betaPreSlot) && pool.mapLookup_3(eSets[i], betaPreSlot, h, y) == 0) {
              // add (B ::= αC · β, h, y) to Ei and R }
              int temp = pool.mapFind_3_0(eSets[i], betaPreSlot, h, y);
              if (artTrace > 0) artTraceText.println("3 Adding to Earley set [" + i + "] " + earleyItemToString(temp));
              temp = pool.mapFind_3_0(rSet, betaPreSlot, h, y);
              if (artTrace > 0) artTraceText.println("3 Adding to R set " + earleyItemToString(temp));
            }
            // if β = ai+1 β ′ { add (B ::= αC · β, h, y) to Q }
            int betaFirst = artSlotArray.slotRightSymbols[betaPreSlot];
            if (artSlotArray.isTerminal(betaFirst) && betaFirst == input[i + 1]) {
              int temp = pool.mapFind_3_0(qSet, betaPreSlot, h, y);
              if (artTrace > 0) artTraceText.println("3 Adding to Q set " + earleyItemToString(temp));
            }
          }
        }

        // if Λ = (D ::= α·, h, w) {
        int D = artSlotArray.slotLHSSymbols[pool.poolGet(lambda + earleyItemSlotOffset)];
        if (artSlotArray.slotRightSymbols[pool.poolGet(lambda + earleyItemSlotOffset)] == 0
            || artSlotArray.slotRightSymbols[pool.poolGet(lambda + earleyItemSlotOffset)] == artSlotArray.epsilon) {
          if (artTrace > 0) artTraceText.println("Processing end slot item " + earleyItemToString(lambda));
          // if w = null {
          if (w == 0) {
            // if there is no node v in V labelled (D, i, i) create one
            int v = pool.mapFind_3_1(sppf, D, i, i);

            // set w = v
            w = v;
            // if w does not have family (epsilon) add one }
            if (pool.poolGet(w + sppfFamilyOffset) == 0) // Is there a set?
              pool.poolPut(w + sppfFamilyOffset, pool.mapMake(sppfNodeFamilyBucketCount));
            pool.mapFind_2_0(pool.poolGet(w + sppfFamilyOffset), epsilonSPPFNode, 0);
          }
          // if h = i { add (D, w) to H }
          if (h == i) pool.mapFind_1_1(hMap, D, w);

          // for all (A ::= τ · Dδ, k, z) in Eh {
          for (int e = pool.mapIteratorFirst1(eSets[h]); e != 0; e = pool.mapIteratorNext1()) {
            // major inefficiency here: rework as a set of hashmaps from D to items
            if (artSlotArray.slotRightSymbols[pool.poolGet(e + earleyItemSlotOffset)] == D) {
              // let y = MAKE_NODE(A ::= τ D · δ, k, i, z, w, V)
              int k = pool.poolGet(e + earleyItemIndexOffset);
              int z = pool.poolGet(e + earleyItemSPPFNodeOffset);
              int deltaPreSlot = pool.poolGet(e + earleyItemSlotOffset) + 1;
              int y = makeNode(deltaPreSlot, k, i, z, w, vSet);
              // if δ ∈ ΣN and (A ::= τ D · δ, k, y) ! ∈ Ei {
              if (inSigmaN(deltaPreSlot) && pool.mapLookup_3(eSets[i], deltaPreSlot, k, y) == 0) {
                // add (A ::= τ D · δ, k, y) to Ei and R }
                int temp = pool.mapFind_3_0(eSets[i], deltaPreSlot, k, y);
                if (artTrace > 0) artTraceText.println("4 Adding to Earley set [" + i + "] " + earleyItemToString(temp));
                temp = pool.mapFind_3_0(rSet, deltaPreSlot, k, y);
                if (artTrace > 0) artTraceText.println("4 Adding to R set " + earleyItemToString(temp));
              }
              // if δ = ai+1 δ ′ { add (A ::= τ D · δ, k, y) to Q } }
              if (artSlotArray.slotRightSymbols[deltaPreSlot] != 0 && artSlotArray.slotRightSymbols[deltaPreSlot] == input[i + 1]) {
                int temp = pool.mapFind_3_0(qSet, deltaPreSlot, k, y);
                if (artTrace > 0) artTraceText.println("4 Adding to Q set " + earleyItemToString(temp));
              }
            }
          }
        }
      }
      // V=∅
      pool.mapClear(vSet);
      // create an SPPF node v labelled (ai+1 , i, i + 1)
      if (i != inputLength) {
        int v = pool.mapFind_3_1(sppf, input[i + 1], i, i + 1);
        // while Q ! = ∅ {
        while (pool.mapCardinality(qSet) != 0) {
          // remove an element, Λ = (B ::= α · ai+1 β, h, w) say, from Q
          int lambda = pool.mapRemove(qSet);

          int postAlphaSlot = pool.poolGet(lambda + earleyItemSlotOffset);
          int h = pool.poolGet(lambda + earleyItemIndexOffset);
          int w = pool.poolGet(lambda + earleyItemSPPFNodeOffset);
          // let y = MAKE_NODE(B ::= α ai+1 · β, h, i + 1, w, v, V)
          int preBetaSlot = postAlphaSlot + 1;
          int y = makeNode(preBetaSlot, h, i + 1, w, v, vSet);
          // if β ∈ ΣN { add (B ::= α ai+1 · β, h, y) to Ei+1 }
          if (inSigmaN(preBetaSlot)) {
            int temp = pool.mapFind_3_0(eSets[i + 1], preBetaSlot, h, y);
            if (artTrace > 0) artTraceText.println("5 Adding to Earley set [" + (i + 1) + "] " + earleyItemToString(temp));
          }
          // if β = ai+2 β′ { add (B ::= α ai+1 · β, h, y) to Q′ }
          if (artSlotArray.slotRightSymbols[preBetaSlot] != 0 && artSlotArray.slotRightSymbols[preBetaSlot] == input[i + 2]) {
            int temp = pool.mapFind_3_0(qPrimeSet, preBetaSlot, h, y);
            if (artTrace > 0) artTraceText.println("5 Adding to Q' set " + earleyItemToString(temp));
          }
        }
      }
    }

    artParseCompleteTime = artReadClock();
    artSlotArray.artGrammar.getARTManager().printMemory(this.getClass().getSimpleName() + " end of parse");

    // if (S ::= τ ·, 0, w) ∈ En return w

    // Scan eSets.get(inputLength) to look for accepting slots and some w, then
    // return w
    if (artTrace > 0) artTraceText.println(
        "input length is " + inputLength + "\naccepting slots are " + acceptingSlotsSet + "\nfinal Earley set is " + earleySetToString(eSets[inputLength]));

    // System.out.printf("Scannning final Earley set\n");
    for (int e = pool.mapIteratorFirst1(eSets[inputLength]); e != 0; e = pool.mapIteratorNext1()) {
      int offset = pool.poolGet(e + earleyItemIndexOffset);
      int slot = pool.poolGet(e + earleyItemSlotOffset);

      // System.out.printf("e=%d, offset = %d, slot = %d\n", e, offset, slot);

      if (offset == 0 && pool.mapLookup_1(acceptingSlotsSet, slot) != 0) {
        artIsInLanguage = true;
        System.out.println("EarleyIndexedPool " + (artIsInLanguage ? "accept" : "reject") + " in " + artParseCompleteTime * 1E-6 + "ms");
        if (artTrace > 0) {
          artTraceText.println("EarleyIndexedPool: accept");
          printSets();
          artTraceText.close();
        }
        // System.out.println("Accepting slot set statistics " + pool.mapStatistics(acceptingSlotsSet));
        // System.out.println("Q set statistics " + pool.mapStatistics(qSet));
        // System.out.println("Q' set statistics " + pool.mapStatistics(qPrimeSet));
        // System.out.println("R set statistics " + pool.mapStatistics(rSet));
        // System.out.println("V set statistics " + pool.mapStatistics(vSet));
        // for (int i = 0; i < inputLength + 1; i++)
        // System.out.println("Earley set [" + i + "] statistics " + pool.mapStatistics(eSets[i]));

        return /* w */ /* pool.poolGet(e + earleyItemSPPFNodeOffset) */;
      }
    }
    // else return failure
    {
      System.out.println("EarleyIndexedPool " + (artIsInLanguage ? "accept" : "reject") + " in " + artParseCompleteTime * 1E-6 + "ms");
      if (artTrace > 0) {
        artTraceText.println("EarleyIndexedPool: reject");
        printSets();
        artTraceText.close();
      }
      return;
    }
  }

  // For Java...

  @Override
  public void artParse(String inputString) {
    System.out.println("Internal error: no input supplied to " + this.getClass());
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
// End of language customisation
