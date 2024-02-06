package uk.ac.rhul.cs.csle.art.old.v3.alg.cnp.generatedpool;

import java.util.ArrayList;

import uk.ac.rhul.cs.csle.art.old.util.slotarray.ARTSlotArray;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTGrammar;

public class ARTCNPGenerator {

  private final ARTGrammar artGrammar;
  private final ARTSlotArray slotArray;
  private ARTText t = null;
  private final int segmentThreshold = 1000;
  private final boolean trace = true;

  public ARTCNPGenerator(ARTGrammar artGrammar) {
    this.artGrammar = artGrammar;
    slotArray = new ARTSlotArray(artGrammar);
    // System.out.printf("CNP generator initialised with grammar:\n%s\n and slotArray: \n%s\n", artGrammar, slotArray);
  }

  public void generateParser(ARTText artStringText) {
    // System.out.println("- generating parser");
    t = artStringText;
    if (artGrammar.isEBNF()) {
      t.println("import uk.ac.rhul.cs.csle.art.v3.alg.cnp.generatedpool.ARTCNPGeneratedPool;\n"
          + "import uk.ac.rhul.cs.csle.art.v3.manager.mode.ARTModeGrammarKind;\n" + "import uk.ac.rhul.cs.csle.art.util.ARTException;\n\n"
          + "public class ARTGeneratedParser extends ARTCNPGeneratedPool {");

      t.print("  public ARTGeneratedParser(ARTGeneratedLexer artGeneratedLexer) {\n" + "    this();\n" + "  }\n" + "\n" + "public ARTGeneratedParser() {\n");
      t.print("    artGeneratedFromBNF = false;\n");
      t.print("    artGrammarKind = ARTModeGrammarKind.EBNF;\n  }\n\n");
      t.println("  protected void artCNPParseBody() throws ARTException {\n  }\n}\n");

      return;
    }

    // Compute segment thresholds
    ArrayList<Integer> segments = new ArrayList<>();

    int segmentCount = 0;
    int slot = slotArray.firstSlotNumber;

    while (++slot < slotArray.firstUnusedSlotNumber) {
      if (++segmentCount > segmentThreshold) { // Find first end of production before the threshold was crossed
        while (slotArray.slotRightSymbols[--slot] != 0)
          ;
        segments.add(slot);
        segmentCount = 0;
      }
    }
    segments.add(Integer.MAX_VALUE);

    // System.out.println("Segment boundaries: " + segments);

    t.println(
        "import uk.ac.rhul.cs.csle.art.v3.alg.cnp.generatedpool.ARTCNPGeneratedPool;\n" + "import uk.ac.rhul.cs.csle.art.v3.manager.mode.ARTModeGrammarKind;\n"
            + "import uk.ac.rhul.cs.csle.art.util.ARTException;\n\n" + "public class ARTGeneratedParser extends ARTCNPGeneratedPool {");

    t.print("    public ARTGeneratedParser(ARTGeneratedLexer artGeneratedLexer) {\n" + "    this();\n" + "  }\n" + "\n" + "public ARTGeneratedParser() {\n"
        + "    stringInitialiser();\n" + "    setInitialiser();\n" + "    productionFirstSlotsInitialiser();\n" + "    slotFirstSetsInitialiser();\n"
        + "    nonterminalFollowSetsInitialiser();\n" + "    artGrammarKind = ARTModeGrammarKind." + slotArray.artGrammarKind + ";\n" + "    epsilonSlot = "
        + slotArray.epsilon + ";\n" + "    startSymbol = " + slotArray.startSymbol + ";\n    startProductions = intArray(");

    for (int pi = 0; slotArray.slotIndex[slotArray.startSymbol][pi] != 0; pi++)
      t.print(slotArray.slotIndex[slotArray.startSymbol][pi] + ", ");
    t.println("0);\n    slotFunctionsInitialiser();\n}\n\n");

    t.println("  private void trace() {\n" + "    if (ARTTRACE) artTraceText.println(\"cI=\" + cI + \": \" + artLabelStrings[artSlot]);\n" + "  }\n" + "");

    t.println("  private void stringInitialiser() {");
    t.println("    artLabelStrings = new String[" + slotArray.firstUnusedSlotNumber + "];");
    int segmentNumber = 0;
    for (int i = 0; i < slotArray.firstUnusedSlotNumber; i++) {
      if (i > segments.get(segmentNumber)) {
        t.println("  stringInitialiser" + (segmentNumber + 1) + "();\n  }\n  private void stringInitialiser" + (segmentNumber + 1) + "() {");
        segmentNumber++;
      }
      t.println("    artLabelStrings[" + i + "] = \"" + slotArray.symbolJavaStrings[i] + "\";");
    }
    t.println("  }\n");

    t.println("  private void setInitialiser() {");
    t.println("    sets = new boolean[" + slotArray.mergedSets.length + "][];");
    for (int i = 0; i < slotArray.mergedSets.length; i++)
      if (slotArray.mergedSets[i] != null) t.println("    setInit" + i + "();");
    t.println("  }\n");

    for (int i = 0; i < slotArray.mergedSets.length; i++) {
      if (slotArray.mergedSets[i] != null) {
        t.println("  private boolean[] set" + i + ";\n");
        t.println("  private void setInit" + i + "() {");
        t.println("    set" + i + " = sets[" + i + "] = new boolean[" + slotArray.mergedSets[i].length + "];");
        for (int j = 0; j < slotArray.mergedSets[i].length; j++)
          if (slotArray.mergedSets[i][j]) t.println("    set" + i + "[" + j + "] = true;");
        t.println("  }\n");
      }
    }

    t.println("  void productionFirstSlotsInitialiser() {\n    productionFirstSlots = new int[" + slotArray.firstSlotNumber + "][];");
    for (int n = slotArray.firstNonterminalNumber; n < slotArray.firstSlotNumber; n++) {
      t.print("    productionFirstSlots[" + n + "] = intArray(");
      for (int p = 0; slotArray.slotIndex[n] != null && p < slotArray.slotIndex[n].length - 1; p++)
        t.print(slotArray.slotIndex[n][p] + 1 + ", ");

      t.print("0); // " + "productions(" + slotArray.symbolJavaStrings[n] + ") = { ");
      for (int p = 0; slotArray.slotIndex[n] != null && p < slotArray.slotIndex[n].length - 1; p++)
        t.print(slotArray.symbolJavaStrings[slotArray.slotIndex[n][p] + 1] + " ");
      t.println("}");
    }
    t.println("  }\n");

    t.println("  void slotFirstSetsInitialiser() {\n    slotFirstSets = new boolean[" + slotArray.firstUnusedSlotNumber + "][];");
    for (int n = slotArray.firstSlotNumber; n < slotArray.firstUnusedSlotNumber; n++)
      if (slotArray.slotFirstSetAddresses[n] != 0) t.println("    slotFirstSets[" + n + "] = set" + slotArray.slotFirstSetAddresses[n] + "; // first("
          + slotArray.symbolJavaStrings[n] + ") = " + slotArray.setToString(slotArray.mergedSets[slotArray.slotFirstSetAddresses[n]]));
    t.println("  }\n");

    t.println("  void nonterminalFollowSetsInitialiser() {\n    nonterminalFollowSets = new boolean[" + slotArray.firstSlotNumber + "][];");
    for (int n = slotArray.firstNonterminalNumber; n < slotArray.firstSlotNumber; n++)
      t.println("    nonterminalFollowSets[" + n + "] = set" + slotArray.nonterminalFollowSetAddresses[n] + "; // follow(" + slotArray.symbolJavaStrings[n]
          + ") = " + slotArray.setToString(slotArray.mergedSets[slotArray.nonterminalFollowSetAddresses[n]]));
    t.println("  }\n");

    t.println("  void slotFunctionsInitialiser() {");
    t.println("    slotRightSymbols = new int[" + slotArray.firstUnusedSlotNumber + "];");
    t.println("    slotProductionL = new int[" + slotArray.firstUnusedSlotNumber + "];");
    t.println("    prefixLengths = new int[" + slotArray.firstUnusedSlotNumber + "];");
    t.println("    prefixSlotMap = new int[" + slotArray.firstUnusedSlotNumber + "];");
    t.println("    isEpsilonSlotOrZeroSlot = new boolean[" + slotArray.firstUnusedSlotNumber + "];");

    segmentNumber = 0;
    for (int n = slotArray.firstSlotNumber; n < slotArray.firstUnusedSlotNumber; n++) {
      if (n > segments.get(segmentNumber)) {
        t.println("  slotFunctionsInitialiser" + (segmentNumber + 1) + "();\n  }\n  private void slotFunctionsInitialiser" + (segmentNumber + 1) + "() {");
        segmentNumber++;
      }

      t.println("    // Slot " + slotArray.symbolJavaStrings[n]);
      t.println("    slotRightSymbols[" + n + "] = " + slotArray.slotRightSymbols[n] + "; // " + slotArray.symbolJavaStrings[slotArray.slotRightSymbols[n]]);
      t.println("    slotProductionL[" + n + "] = " + slotArray.slotGetProductionL(n) + "; // " + slotArray.symbolJavaStrings[slotArray.slotGetProductionL(n)]);
      t.println("    prefixLengths[" + n + "] = " + slotArray.prefixLengths[n] + ";");
      t.println("    prefixSlotMap[" + n + "] = " + slotArray.prefixSlotMap[n] + "; // " + slotArray.symbolJavaStrings[slotArray.prefixSlotMap[n]]);
      t.println("    isEpsilonSlotOrZeroSlot[" + n + "] = " + slotArray.isEpsilonSlotOrZeroSlot(n) + ";");
    }
    t.println("  }\n");

    t.println("  protected void artCNPParseBody() throws ARTException {\n");

    for (int i = segments.size() - 2; i >= 0; i--)
      t.println("  if (artSlot > " + segments.get(i) + ") segment" + (i + 1) + "();\n  else");

    int segment = 0;

    t.println("  switch(artSlot) {");
    for (int s = slotArray.firstSlotNumber; s < slotArray.firstUnusedSlotNumber; s++) {
      if (s > segments.get(segment)) t.println("}\n}\n\n  void segment" + (1 + segment++) + "() throws ARTException {\n    switch(artSlot) {\n");
      if (slotArray.isZeroSlot(s) && s < slotArray.firstUnusedSlotNumber - 1 && !slotArray.isZeroSlot(s + 1)) continue;

      t.println("\n    case " + s + ": " + " // " + slotArray.symbolJavaStrings[s] + (trace ? "\n      trace();" : ""));

      if (slotArray.isEpsilonSlot(s))
        t.println("      pool.mapFind_4_0(upsilon, " + slotArray.slotGetProductionL(s) + ", cI, cI, cI);"
            + " // doesn't need to be mapped because it is a production, not a prefix\n" + "      bsrFinds++;\n" + "      if (set"
            + slotArray.nonterminalFollowSetAddresses[slotArray.slotLHSSymbols[s]] + "[input[cI]])\n" + "      rtn(" + slotArray.slotLHSSymbols[s]
            + ", cU, cI);\n" + "      return;");
      else {
        if (slotArray.isZeroSlot(s - 1)) {// We are the first slot in a production
          if (slotArray.isTerminalSlot(s)) {

            t.println("      bsrAdd(" + (s + 1) + ", cU, cI, cI + 1);\n" + "      cI = cI + 1;");
          } else if (slotArray.isNonterminalSlot(s)) {
            t.println("      call(" + (s + 1) + ", cU, cI);\n      return;");
          }
          if (slotArray.isZeroSlot(s)) {
            t.println("if (set[" + slotArray.nonterminalFollowSetAddresses[slotArray.slotLHSSymbols[s]] + "[input[cI]])\n" + "        rtn("
                + slotArray.slotLHSSymbols[s] + ", cU, cI);\n" + "      return;");
          }
          continue;
        } else {
          if (slotArray.isTerminalSlot(s)) {
            t.println("      if (!testSelect(input[cI], " + slotArray.slotLHSSymbols[s] + ", set" + slotArray.slotFirstSetAddresses[s] + ", set"
                + slotArray.nonterminalFollowSetAddresses[slotArray.slotLHSSymbols[s]] + ")) return;");
            t.println("      bsrAdd(" + (s + 1) + ", cU, cI, cI + 1);\n" + "      cI = cI + 1;");
          } else if (slotArray.isNonterminalSlot(s)) {
            t.println("      if (!testSelect(input[cI], " + slotArray.slotLHSSymbols[s] + ", set" + slotArray.slotFirstSetAddresses[s] + ", set"
                + slotArray.nonterminalFollowSetAddresses[slotArray.slotLHSSymbols[s]] + ")) return;");
            t.println("      call(" + (s + 1) + ", cU, cI);\n      return;");
          }
          if (slotArray.isZeroSlot(s)) {
            t.println("      if (set" + slotArray.nonterminalFollowSetAddresses[slotArray.slotLHSSymbols[s]] + "[input[cI]])\n" + "        rtn("
                + slotArray.slotLHSSymbols[s] + ", cU, cI);\n" + "      return;");
          }
        }
      }
    }

    t.println(
        "\n    default:\n      throw new ARTException(\"Nonslot (\" + artLabelStrings[artSlot] + \") encountered in CNP template execution\");\n" + "    }");
    t.println("  }");
    t.println("}");

  }
}
