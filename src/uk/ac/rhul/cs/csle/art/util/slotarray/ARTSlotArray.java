package uk.ac.rhul.cs.csle.art.util.slotarray;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.element.ARTGrammarElementNonterminal;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.element.ARTGrammarElementTerminal;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstance;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstanceCat;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstanceEpsilon;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.instance.ARTGrammarInstanceSlot;
import uk.ac.rhul.cs.csle.art.v3.manager.mode.ARTModeGrammarKind;

// This is an array based grammar representation that can be for both ART internal parsers and also output in a form suitable for linking against the C or Java generated parsers
public class ARTSlotArray {
  /*
   * Basic ideas
   *
   * We establish a single mapping onto the naturals ordered as
   *
   * $ T1, T2, ... , Tt, # , N1, N2, ... Nn, P1, L1_1, L!_2, ..., P2, L2_1, P2_2, ... Pp
   *
   * where:
   *
   * - $ is the end-of-string symbol (eoS)
   *
   * - T1..Tt are the terminals in some arbitrary order
   *
   * - # is empty-string (epsilon)
   *
   * - N1..Nn are the nonterminals in some arbitrary order (but usually lexicographically sorted on name)
   *
   * - P1..Pp are the productions in some order (but usually the order they occur in the original spec: watch out for EBNF->BNF conversions and such like though
   *
   * - Lk_1..Lk_l are the l slots in prouction k in left to right order
   *
   * slotIndex[N][P] is a map from Nonterminal number N and production number PofN to the initial Production element in the enumeration for that production
   *
   * slotRightSymbols[L] is a map from slot L to the symbol after the dot, or 0 if there is no such symbol
   *
   * - note that the elements in slotRightSymbols from $ to Nn will be zero so there is guaranteed to be a zero preceding the value for L1. This allows the
   * length of a slot prefix to be computed as the distance from the preceding zero
   *
   * lhsSymbol[L] is a map from slot L to the nonterminal which is on the left hand side of the slot's dotted rule. LHS[0] is always zero
   *
   */
  public int epsilon;
  public int eoS;
  public int startSymbol;
  public int firstNonterminalNumber;
  public int firstSlotNumber;
  public int firstUnusedSlotNumber;
  public int[] instanceKeyToSlotNumberMap = null;
  public String[] symbolJavaStrings = null;
  public char[][] symbolStrings = null;
  public int[][] slotIndex = null; // A map from (symbol, production) to slot number; there is a zero sentinel
  public int[] slotRightSymbols = null; // A map from slot number to the symbol to the left of the slot.
  // The first slot in a production is preceded by 0
  // The symbol of the last slot in a production is 0
  public int[] slotLHSSymbols = null;
  public boolean[][] mergedSets;
  public int[] prefixLengths;
  public int[] slotFirstSetAddresses;
  public int[] slotGuardSetAddresses;
  public int[] nonterminalFollowSetAddresses;
  public int[] prefixSlotMap;
  public final ARTModeGrammarKind artGrammarKind;
  public final ARTGrammar artGrammar;

  public ARTSlotArray(ARTGrammar grammar) {
    Map<Integer, ARTGrammarInstance> slotToInstanceMap = new HashMap<>();
    Map<ARTGrammarInstance, Integer> instanceToSlotMap = new HashMap<>();
    artGrammar = grammar;
    artGrammarKind = grammar.getGrammarKind();

    if (artGrammarKind != ARTModeGrammarKind.BNF) return;

    // Set up special symbols and firstSlotNumber
    startSymbol = grammar.getDefaultStartNonterminal().getElementNumber();
    epsilon = grammar.getEpsilon().getElementNumber();
    eoS = grammar.getEoS().getElementNumber();
    firstNonterminalNumber = grammar.getFirstNonterminalElementNumber();
    firstSlotNumber = grammar.getLastNonterminalElementNumber() + 1;

    // Now create merged sets array
    // System.out.println("Grammar sets: " + grammar.getMergedSets());
    mergedSets = new boolean[grammar.getMergedSets().size() + 1][];
    // Iterate over the keyset for mergedSets: this is the set of sets that we are using
    for (Set<ARTGrammarElement> s : grammar.getMergedSets().keySet()) {
      int i = grammar.getMergedSets().get(s); // Get the set number for this set
      mergedSets[i] = new boolean[grammar.getLastNonterminalElementNumber() + 1]; // allocate space for this set number
      for (ARTGrammarElement e : s) { // iterate over the elements of this set
        mergedSets[i][e.getElementNumber()] = true; // set this element e in this set number i to be true
        // System.out.println("Setting merged set " + i + " element " + e.getElementNumber());
      }
    }

    // Size and allocate slotRightSymbols and slotIndex: make two passes
    // Also load symbolJavaStrings, slotLHSSymbols, firstSetAddresses and followSetAddresses
    slotIndex = new int[grammar.getLastNonterminalElementNumber() + 1][];

    for (int pass = 0; pass < 2; pass++) {
      int slotNumber = firstSlotNumber;
      // Process all of the nonterminals
      for (ARTGrammarElementNonterminal n : grammar.getNonterminals()) {
        if (symbolJavaStrings != null) {
          symbolJavaStrings[n.getElementNumber()] = n.toString();
          slotRightSymbols[n.getElementNumber()] = 0;
          slotFirstSetAddresses[n.getElementNumber()] = grammar.getMergedSets().get(n.getFirst()); // lookup merged set number for the first set, and load to
          // firstSetAddresses for this nonterminal
          nonterminalFollowSetAddresses[n.getElementNumber()] = grammar.getMergedSets().get(n.getFollow()); // similarly for follow
        }
        slotIndex[n.getElementNumber()] = new int[n.getProductions().size() + 1];
        // Process the production p in n
        int productionNumber = 0;
        for (ARTGrammarInstanceCat p : n.getProductions()) {
          slotIndex[n.getElementNumber()][productionNumber] = slotNumber;
          if (slotRightSymbols != null) {
            symbolJavaStrings[slotNumber] = "Production " + p.toGrammarString(".");
            instanceToSlotMap.put(p, slotNumber);
          }

          slotNumber++; // Skip past production place holder
          ARTGrammarInstance i;
          for (i = p.getChild(); i.getSibling() != null; i = i.getSibling().getSibling()) {

            if (slotRightSymbols != null) {
              if (i.getSibling() instanceof ARTGrammarInstanceEpsilon)
                slotRightSymbols[slotNumber] = epsilon;
              else
                slotRightSymbols[slotNumber] = i.getSibling().getPayload().getElementNumber();

              slotLHSSymbols[slotNumber] = n.getElementNumber();
              symbolJavaStrings[slotNumber] = i.toGrammarString(".");
              prefixLengths[slotNumber] = i.getPrefixLength();
              slotFirstSetAddresses[slotNumber] = grammar.getMergedSets().get(i.getFirst());
              slotGuardSetAddresses[slotNumber] = grammar.getMergedSets().get(i.getGuard());
              // Now do the payload - this looks odd; surely only for terminals?
              if (i.getSibling().getPayload() != null) {
                int payloadNumber = i.getSibling().getPayload().getElementNumber();
                slotFirstSetAddresses[payloadNumber] = slotFirstSetAddresses[slotNumber];
              }
            }

            if (slotRightSymbols == null) {// do this on the first pass only!
              slotToInstanceMap.put(slotNumber, i);
              instanceToSlotMap.put(i, slotNumber);
            }
            slotNumber++;
            if (i.getSibling() instanceof ARTGrammarInstanceEpsilon) break;
          }
          // Now do the final slot, the one which has no right hand side instance: here - add first and follow sets
          if (!(i.getSibling() instanceof ARTGrammarInstanceEpsilon)) {
            if (slotRightSymbols != null) {
              slotRightSymbols[slotNumber] = 0; // sentinel at end of production
              slotLHSSymbols[slotNumber] = n.getElementNumber();
              symbolJavaStrings[slotNumber] = i.toGrammarString(".");
              slotFirstSetAddresses[slotNumber] = grammar.getMergedSets().get(i.getFirst());
              slotGuardSetAddresses[slotNumber] = grammar.getMergedSets().get(i.getGuard());
            }
            slotNumber++;
          }
          productionNumber++;
        }
        slotIndex[n.getElementNumber()][productionNumber] = 0; // Sentinel to mark end of production list under nonterminal
      }
      if (slotRightSymbols == null) { // At the end of the first pass when we have all the numbers, allocate memory
        firstUnusedSlotNumber = slotNumber;
        slotRightSymbols = new int[slotNumber];
        slotLHSSymbols = new int[slotNumber];
        symbolJavaStrings = new String[slotNumber];
        symbolStrings = new char[slotNumber][];
        prefixLengths = new int[slotNumber];
        slotFirstSetAddresses = new int[slotNumber];
        slotGuardSetAddresses = new int[slotNumber];
        nonterminalFollowSetAddresses = new int[slotNumber];
        prefixSlotMap = new int[slotNumber];
      }
    }

    // Load strings for special symbols
    symbolJavaStrings[epsilon] = "#";
    symbolJavaStrings[eoS] = "$";

    // Load strings for terminals
    for (ARTGrammarElementTerminal t : grammar.getTerminals()) {
      symbolJavaStrings[t.getElementNumber()] = t.getId();
      symbolStrings[t.getElementNumber()] = (t.getId() + "\0").toCharArray();
    }

    // Build prefix slot map

    for (ARTGrammarInstanceSlot i : grammar.getPrefixSlotMap().keySet())
      prefixSlotMap[instanceToSlotMap.get(i)] = instanceToSlotMap.get(grammar.getPrefixSlotMap().get(i));

    // Build instanceKeyToSlotNumber map
    int maxInstanceKey = 0;
    for (ARTGrammarInstance i : instanceToSlotMap.keySet())
      if ((int) i.getKey() > maxInstanceKey) maxInstanceKey = (int) i.getKey();

    instanceKeyToSlotNumberMap = new int[maxInstanceKey + 1];
    for (ARTGrammarInstance i : instanceToSlotMap.keySet()) {
      instanceKeyToSlotNumberMap[(int) i.getKey()] = instanceToSlotMap.get(i);
      // System.out.println("Mapping instance " + (int) i.getKey() + " to " + instanceKeyToSlotNumberMap[(int) i.getKey()]);
    }

    // System.out.println("Slot array start\n" + toString() + "\nSlot array end");
  }

  // Classification methods below here

  public boolean isEpsilon(int symbol) {
    return symbol == epsilon;
  }

  // CARE!! we treat EOS as epsilon too for compatability with empty string semantics
  public boolean isEpsilonOrZero(int symbol) {
    return symbol == 0 || symbol == epsilon;
  }

  public boolean isTerminal(int symbol) {
    return symbol > eoS && symbol < epsilon;
  }

  public boolean isNonterminal(int symbol) {
    return symbol > epsilon && symbol < firstSlotNumber;
  }

  // CARE!! we treat EOS as epsilon too for compatability with empty string semantics
  public boolean isNonterminalOrEpsilon(int symbol) {
    return symbol == 0 || (symbol >= epsilon && symbol < firstSlotNumber);
  }

  public boolean isSlot(int symbol) {
    return symbol >= firstSlotNumber;
  }

  public boolean isEpsilonSlot(int slot) {
    return isEpsilon(slotRightSymbols[slot]);
  }

  public boolean isNonterminalSlot(int slot) {
    return isNonterminal(slotRightSymbols[slot]);
  }

  public boolean isNonterminalOrEpsilonSlot(int slot) {
    return isNonterminalOrEpsilon(slotRightSymbols[slot]);
  }

  public boolean isTerminalSlot(int slot) {
    return isTerminal(slotRightSymbols[slot]);
  }

  public boolean isZeroSlot(int slot) {
    return slotRightSymbols[slot] == 0;
  }

  public boolean isEpsilonSlotOrZeroSlot(int slot) {
    return isEpsilonOrZero(slotRightSymbols[slot]);
  }

  public void toCString(ARTText t) {

    t.print("/* Slot Array generated by ARTSLotArray.java */\n\n");
    t.print("const int eoS = " + eoS + ";\n");
    t.print("const int epsilon = " + epsilon + ";\n");
    t.print("const int startSymbol = " + startSymbol + ";\n");
    t.print("const int firstSlotNumber = " + firstSlotNumber + ";\n");
    t.print("const char*" + " symbolStrings[" + symbolJavaStrings.length + "] = {");
    for (int i = 0; i < symbolJavaStrings.length; i++)
      t.print("\n  \"" + symbolJavaStrings[i] + "\"," + "\t// " + i);
    t.print("\n};\n\n");

    for (int i = 0; i < slotIndex.length; i++) {
      if (slotIndex[i] != null) {
        t.print("const int slotIndex" + i + "[" + slotIndex[i].length + "] = {");
        for (int j = 0; j < slotIndex[i].length; j++)
          t.print(slotIndex[i][j] + ",");
        t.print("};\n");
      }
    }

    t.print("const int* slotIndex[" + slotIndex.length + "] = {\n");
    for (int i = 0; i < slotIndex.length; i++)
      if (slotIndex[i] == null)
        t.print("  0,\n");
      else
        t.print("  slotIndex" + i + ",\n");
    t.print("};\n\n");

    t.print("const int slotRightSymbols[" + slotRightSymbols.length + "] = {\n");
    for (int i = 0; i < slotRightSymbols.length; i++)
      t.print("  " + slotRightSymbols[i] + ",\n");
    t.print("};\n\n");

    t.print("const int slotLHSSymbols[" + slotLHSSymbols.length + "] = {\n");
    for (int i = 0; i < slotLHSSymbols.length; i++)
      t.print("  " + slotLHSSymbols[i] + ",\n");
    t.print("};\n\n");

    t.print("const int slotGuardSetAddresses[" + slotGuardSetAddresses.length + "] = {\n");
    for (int i = 0; i < slotGuardSetAddresses.length; i++)
      t.print("  " + slotGuardSetAddresses[i] + ",\n");
    t.print("};\n\n");

    t.print("const bool mergedSets[" + mergedSets.length + "][" + mergedSets[1].length + "] = {\n");
    for (int i = 0; i < mergedSets.length; i++) {
      t.print("  {\n");
      for (int j = 0; j < mergedSets[1].length; j++)
        if (mergedSets[i] == null)
          t.print("    false,\n");
        else {
          t.print("    " + (mergedSets[i][j] ? "true" : "false") + ",\n");
        }
      t.print("  },\n");
    }
    t.print("};\n");
  }

  public void toCStringForEarleyTable(ARTText t) {
    t.print("/* Slot Array generated by ARTSLotArray.java */\n\n");
    t.print("const int eoS = " + eoS + ";\n");
    t.print("const int epsilon = " + epsilon + ";\n");
    t.print("const int startSymbol = " + startSymbol + ";\n");
    t.print("const int firstSlotNumber = " + firstSlotNumber + ";\n");
    t.print("const char*" + " symbolStrings[" + symbolJavaStrings.length + "] = {");
    for (int i = 0; i < symbolJavaStrings.length; i++)
      t.print("\n  \"" + symbolJavaStrings[i] + "\"," + "\t// " + i);
    t.print("\n};\n\n");
  }

  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append("Slot array dump");

    for (int i = 0; i < slotRightSymbols.length; i++)
      s.append("\nSymbol " + i + " renders as " + symbolJavaStrings[i] + " and has slotRight symbol " + slotRightSymbols[i] + " ("
          + (slotRightSymbols[i] == -1 ? "---" : symbolJavaStrings[slotRightSymbols[i]]) + ")");

    for (int i = 0; i < slotIndex.length; i++)
      if (slotIndex[i] != null) {
        s.append("\nNonterminal: " + symbolJavaStrings[i]);
        for (int production = 0; slotIndex[i][production] != 0; production++) {
          s.append("\nProduction " + production + ": " + symbolJavaStrings[i] + " ::=");
          if (slotRightSymbols[slotIndex[i][production]] == 0) // Special case for zero length production
            s.append(" " + "#");
          else
            for (int slot = slotIndex[i][production]; slotRightSymbols[slot] != 0; slot++)
              s.append(" " + symbolJavaStrings[slotRightSymbols[slot]]);
        }
      }
    s.append("\n\nStart symbol: " + symbolJavaStrings[startSymbol] + "\n");

    // Output tables
    s.append("Enumeration elements\n");
    for (int i = 0; i < symbolJavaStrings.length; i++)
      s.append(i + ": " + symbolJavaStrings[i] + " lhs symbol = " + symbolJavaStrings[slotLHSSymbols[i]] + " right symbol = "
          + symbolJavaStrings[slotRightSymbols[i]] + " first = " + slotFirstSetAddresses[i] + " follow = " + nonterminalFollowSetAddresses[i] + "\n");

    s.append("Merged sets\n");
    for (int i = 1; i < mergedSets.length; i++) {
      s.append(i + ": { ");

      for (int ee = 0; ee < epsilon + 1; ee++)
        if (mergedSets[i][ee]) s.append(ee + ":" + symbolJavaStrings[ee] + " ");
      s.append("}\n");
    }
    s.append("Prefix slot map: right hand side is canonical slot\n");
    for (int i = 0; i < prefixSlotMap.length; i++)
      s.append(i + "->" + prefixSlotMap[i] + "\n");

    return s.toString();
  }

  public int slotGetProductionL(int enumerationElement) {
    int ret = enumerationElement;

    while (slotRightSymbols[--ret] != 0)
      ;

    return ret;
  }

  public String setToString(boolean[] set) {
    if (set == null) {
      return "null";
    }
    String ret = "{ ";
    for (int i = 0; i < set.length; i++)
      if (set[i]) ret += symbolJavaStrings[i] + " ";
    return ret + "}";
  }
}
