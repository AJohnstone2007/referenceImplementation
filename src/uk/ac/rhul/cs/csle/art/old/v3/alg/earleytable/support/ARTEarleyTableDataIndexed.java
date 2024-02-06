package uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.support;

import java.util.Arrays;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.util.slotarray.ARTSlotArray;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstance;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceCat;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceSlot;
import uk.ac.rhul.cs.csle.art.old.v3.manager.mode.ARTModeGrammarKind;

public class ARTEarleyTableDataIndexed {
  public boolean[] acceptingProductions;

  public int[][] chiSetCache;
  public int[][] redSetCache;

  public int[][] epnMap;
  public int[][] eeMap;
  public int[][] redMap;
  public int[][] outEdgeMap;
  public boolean[][] select;
  public int[][] rLHS;
  public ARTSlotArray slotArray;

  private final ARTGrammar grammar;
  private ARTEarleyTableDataLinked earleyTableLinked;

  ARTEarleyTableDataIndexed(ARTEarleyTableDataLinked earleyTableLinked) {
    // System.out.println("Building EarleyTableIndexed structure from:" + earleyTableLinked);
    this.earleyTableLinked = earleyTableLinked;
    grammar = earleyTableLinked.getGrammar();

    if (getGrammar().getGrammarKind() != ARTModeGrammarKind.BNF) return;

    slotArray = new ARTSlotArray(getGrammar());
    epnMap = new int[earleyTableLinked.nfa.size()][];
    eeMap = new int[earleyTableLinked.nfa.size()][];
    redMap = new int[earleyTableLinked.nfa.size()][];
    outEdgeMap = new int[earleyTableLinked.nfa.size()][];
    select = new boolean[earleyTableLinked.nfa.size()][];
    rLHS = new int[earleyTableLinked.nfa.size()][];
    int firstUnusedSlotNumber = slotArray.firstUnusedSlotNumber;

    acceptingProductions = new boolean[firstUnusedSlotNumber];
    for (ARTGrammarInstanceCat c : earleyTableLinked.getAcceptingProductions()) {
      // System.out.println("Setting accept for instance " + c + "with node number" + c.getKey() + " which maps to "
      // + slotArray.instanceKeyToSlotNumberMap[(Integer) c.getKey()]);
      acceptingProductions[slotArray.instanceKeyToSlotNumberMap[(Integer) c.getKey()]] = true;
    }

    chiSetCache = new int[earleyTableLinked.getChiSetCache().getFromIndexMap().size() + 1][];
    for (int c : earleyTableLinked.getChiSetCache().getFromIndexMap().keySet()) {
      Set<ARTGrammarInstance> instanceSet = earleyTableLinked.getChiSetCache().getFromIndexMap().get(c).getSet();
      chiSetCache[c] = new int[instanceSet.size()];
      int instanceSetElement = 0;
      for (ARTGrammarInstance i : instanceSet)
        chiSetCache[c][instanceSetElement++] = slotArray.instanceKeyToSlotNumberMap[(int) i.getKey()];
    }

    redSetCache = new int[earleyTableLinked.getRedSetCache().getFromIndexMap().keySet().size() + 1][];
    for (int r : earleyTableLinked.getRedSetCache().getFromIndexMap().keySet()) {
      Set<ARTGrammarElement> elementSet = earleyTableLinked.getRedSetCache().getFromIndexMap().get(r);
      redSetCache[r] = new int[elementSet.size()];
      int elementSetElement = 0;
      for (ARTGrammarElement e : elementSet)
        redSetCache[r][elementSetElement++] = e.getElementNumber();
    }

    rLHS = new int[earleyTableLinked.states.keySet().size() + 1][];
    for (int s : earleyTableLinked.states.keySet()) {
      rLHS[s] = new int[earleyTableLinked.states.get(s).getrLHS().size()];
      int elementSetElement = 0;
      for (ARTGrammarElementNonterminal e : earleyTableLinked.states.get(s).getrLHS())
        rLHS[s][elementSetElement++] = e.getElementNumber();
    }

    System.out.printf("Earley table data: %d states by %d vector length x 4 x 6 = %d bytes (%d Mbytes)\n", earleyTableLinked.nfa.size(), firstUnusedSlotNumber,
        6 * earleyTableLinked.nfa.size() * firstUnusedSlotNumber * 4, (6 * earleyTableLinked.nfa.size() * firstUnusedSlotNumber * 4) / (1024 * 2014));

    for (Set<ARTGrammarInstanceSlot> v : earleyTableLinked.nfa.keySet()) {
      ARTEarleyNFAVertex vertex = earleyTableLinked.nfa.get(v);
      int stateNumber = vertex.getNumber();

      epnMap[stateNumber] = new int[firstUnusedSlotNumber];
      eeMap[stateNumber] = new int[firstUnusedSlotNumber];
      redMap[stateNumber] = new int[firstUnusedSlotNumber];
      outEdgeMap[stateNumber] = new int[firstUnusedSlotNumber];
      select[stateNumber] = new boolean[firstUnusedSlotNumber];

      Arrays.fill(outEdgeMap[stateNumber], -1);

      for (ARTGrammarElement s : vertex.getEpnMap().keySet())
        epnMap[stateNumber][s.getElementNumber()] = vertex.getEpnMap().get(s);

      for (ARTGrammarElement s : vertex.getEeMap().keySet())
        eeMap[stateNumber][s.getElementNumber()] = vertex.getEeMap().get(s);

      for (ARTGrammarElement s : vertex.getRedMap().keySet())
        redMap[stateNumber][s.getElementNumber()] = vertex.getRedMap().get(s);

      for (ARTGrammarElement s : vertex.getOutEdgeMap().keySet())
        outEdgeMap[stateNumber][s.getElementNumber()] = vertex.getOutEdgeMap().get(s).getNumber();

      for (ARTGrammarElement s : vertex.getSelect())
        select[stateNumber][s.getElementNumber()] = true;

    }
    // System.out.println(this);
  }

  public ARTEarleyTableDataIndexed(ARTGrammar artGrammar) {
    this(new ARTEarleyTableDataLinked(artGrammar));
  }

  @Override
  public String toString() {
    String ret = "!!!Start of Earley table structure dump\n";
    ret += slotArray.toString();
    ret += "\nAccepting productions";
    for (int i = 0; i < acceptingProductions.length; i++)
      if (acceptingProductions[i]) ret += ("\n" + i + ": " + slotArray.symbolJavaStrings[i]);

    ret += "\nStates";
    for (int i = 0; i < epnMap.length; i++) {
      ret += ("\nG" + i);

      for (int j = 0; j < epnMap[i].length; j++) {
        if (outEdgeMap[i][j] != -1) {
          ret += "\n" + slotArray.symbolJavaStrings[j] + " -> G" + outEdgeMap[i][j];
          ret += "\nepn: " + epnMap[i][j];
          ret += "\nee: " + eeMap[i][j];
          ret += "\nred: " + redMap[i][j];
        }
      }
    }

    ret += "\nChi sets";
    for (int i = 0; i < chiSetCache.length; i++)
      ret += "\n" + chiSetCache[i];

    ret += "\n!!!End of Earley table structure dump\n";
    return ret;
  }

  private void outputCSet1(ARTText t, String name, boolean[] set) {
    t.print("const int " + name + "[] = {\n");
    for (int i = 0; i < set.length; i++)
      if (set[i]) t.print("  " + "" + i + ",\n");
    t.print(" 0};\n");
  }

  private void outputCSetInt2(ARTText t, String name, int[][] set) {
    t.print("const int " + name + "[" + set.length + "][" + set[1].length + "]" + " = {\n");
    for (int i = 0; i < set.length; i++) {
      t.print("  {\n");
      if (set[i] == null)
        t.print("  NULL,\n");
      else {
        for (int j = 0; j < set[i].length; j++)
          t.print("    " + set[i][j] + ",\n");
        t.print("  },\n");
      }
    }
    t.print("};\n");
  }

  private void outputCSetBool2(ARTText t, String name, boolean[][] set) {
    t.print("const bool " + name + "[" + set.length + "][" + set[1].length + "]" + " = {\n");
    for (int i = 0; i < set.length; i++) {
      t.print("  {\n");
      if (set[i] == null)
        t.print("  NULL,\n");
      else {
        for (int j = 0; j < set[i].length; j++)
          t.print("    " + set[i][j] + ",\n");
        t.print("  },\n");
      }
    }
    t.print("};\n");
  }

  private void outputCSetInt2Zero(ARTText t, String name, int[][] set) {
    t.print("int **" + name + ";\nvoid " + name + "Initialiser(void){\n");
    t.print("  " + name + " = (int**) calloc(" + set.length + ", sizeof(int*));\n");
    for (int i = 0; i < set.length; i++) {
      if (set[i] != null) {
        t.print("  " + name + "[" + i + "] = (int*) calloc(" + (set[i].length + 1) + ", sizeof(int));\n");
        for (int j = 0; j < set[i].length; j++)
          t.print("  " + name + "[" + i + "][" + j + "] = " + set[i][j] + ";\n");
      }
    }
    t.print("};\n");
  }

  public void toCString(ARTText t) {
    t.print("/* EarleyTable generated by" + this.getClass().getSimpleName() + "*/\n\n");
    t.print("#include<stdlib.h>\n");
    slotArray.toCStringForEarleyTable(t);

    outputCSet1(t, "acceptingProductions", acceptingProductions);
    outputCSetInt2(t, "outEdgeMap", outEdgeMap);
    outputCSetInt2(t, "epnMap", epnMap);
    outputCSetInt2(t, "eeMap", eeMap);
    outputCSetInt2(t, "redMap", redMap);
    outputCSetBool2(t, "ARTSelect", select);

    outputCSetInt2Zero(t, "chiSetCache", chiSetCache);
    outputCSetInt2Zero(t, "redSetCache", redSetCache);
    outputCSetInt2Zero(t, "rLHS", rLHS);
  }

  public ARTGrammar getGrammar() {
    return grammar;
  }

}
