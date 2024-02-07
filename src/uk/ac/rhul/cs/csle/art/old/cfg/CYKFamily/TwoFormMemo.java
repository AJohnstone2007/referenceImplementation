package uk.ac.rhul.cs.csle.art.old.cfg.CYKFamily;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.cfg.Parser;
import uk.ac.rhul.cs.csle.art.old.core.PipelineParamaters;

public class TwoFormMemo extends Parser {
  private Map<IntegerPair, Set<Integer>> rMap;
  private final PipelineParamaters pp;

  public TwoFormMemo(PipelineParamaters pp) {
    this.pp = pp;
  }

  Set<Integer> r(int i, int j) {
    IntegerPair index = new IntegerPair(i, j);

    if (rMap.get(index) == null) {
      Set<Integer> temp = new HashSet<>();
      if (i == j - 1) {
        for (int n : pp.mainModule.getCfgRules().keySet())
          for (int p : pp.mainModule.getCfgRules().get(n))
            if (pp.iTerms.getSubterm(p, 1) == pp.iTerms.findTerm("cfgCharacterTerminal(`" + pp.inputString.charAt(i - 1) + ")")) temp.add(n);
      } else {
        for (int n : pp.mainModule.getCfgRules().keySet()) // Iterate n over nonterminals
          for (int p : pp.mainModule.getCfgRules().get(n)) // Iterate p over productions of n
            for (int h = i + 1; h < j; h++) // Iterate j over pivots
              if (r(i, h).contains(pp.iTerms.getSubterm(p, 1)) && r(h, j).contains(pp.iTerms.getSubterm(p, 3))) temp.add(n);
      }
      rMap.put(index, temp);
    }
    return rMap.get(index);
  }

  private void printSet(IntegerPair i) {
    if (rMap.get(i) == null || rMap.get(i).size() == 0)
      System.out.print("  -  ");
    else {
      System.out.print("{ ");
      for (Integer t : rMap.get(i))
        System.out.print(pp.tt.toString(t) + " ");
      System.out.print("}");
    }
  }

  @Override
  public void parse() {
    rMap = new LinkedHashMap<>();
    System.out.println("TwoFormMemo on " + pp.inputString);
    pp.inLanguage = r(1, pp.inputString.length() + 1).contains(pp.mainModule.defaultStartNonterminal);

    for (IntegerPair i : rMap.keySet()) {
      System.out.print("r[" + i.i + "," + i.j + "] = ");
      printSet(i);
      System.out.println();
    }
    // Output visit map
    int visit = 0;
    int[][] visitOrder = new int[pp.inputString.length() + 2][pp.inputString.length() + 2];
    for (IntegerPair v : rMap.keySet())
      visitOrder[v.i][v.j] = ++visit;

    for (int i = 0; i < visitOrder[0].length; i++) {
      for (int j = 0; j < visitOrder[0].length; j++) {
        System.out.print(i + "," + j + ":" + (visitOrder[i][j] != 0 ? (visitOrder[i][j] + " ") : "  "));
        printSet(new IntegerPair(i, j));
        System.out.print("   ");
      }
      System.out.println();
    }
  }
}
