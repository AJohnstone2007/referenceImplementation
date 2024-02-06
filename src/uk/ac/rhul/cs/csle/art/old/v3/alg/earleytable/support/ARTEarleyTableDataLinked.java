package uk.ac.rhul.cs.csle.art.old.v3.alg.earleytable.support;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.util.cache.ARTCache;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementEoS;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementEpsilon;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstance;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceCat;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceSlot;

public class ARTEarleyTableDataLinked {
  /*
   * An Earley NFA is a graph whose nodes are labelled with a unique set of gramamr slots and whose edges are labbelled with grammar symbols Each edge has three
   * associated sets: EPN, EE and RED. These slots are stored in maps within the vertices
   *
   */

  private final ARTCache<ARTChiSet> chiSetCache = new ARTCache<>();
  private final ARTCache<Set<ARTGrammarElement>> redSetCache = new ARTCache<>();

  final Map<Set<ARTGrammarInstanceSlot>, ARTEarleyNFAVertex> nfa = new LinkedHashMap<>();
  final Map<Integer, ARTEarleyNFAVertex> states = new LinkedHashMap<>();

  // Map<LinkedList<ARTGrammarElement>, ARTGrammarInstanceSlot> prefixStringMap = new HashMap<>();
  // Map<ARTGrammarInstanceSlot, ARTGrammarInstanceSlot> prefixSlotMap = new HashMap<>();

  private final LinkedList<ARTEarleyNFAVertex> workList = new LinkedList<>();
  private Set<ARTGrammarElementNonterminal> nonterminalWorkSet;
  private LinkedList<ARTGrammarElementNonterminal> nonterminalWorkList;
  private final ARTGrammarElementEpsilon epsilon;
  private final ARTGrammarElementEoS eos;
  private int nextFreeVertexNumber = 0;
  private final Set<ARTGrammarInstanceCat> acceptingProductions = new HashSet<>();
  private final ARTGrammarElementNonterminal startSymbol;
  private final ARTGrammar grammar;

  public ARTEarleyNFAVertex getState(int stateNumber) {
    return states.get(stateNumber);
  }

  ARTGrammarElementEpsilon getEpsilon() {
    return epsilon;
  }

  public ARTEarleyTableDataLinked(ARTGrammar grammar) {
    this.grammar = grammar;
    epsilon = grammar.getEpsilon();
    eos = grammar.getEoS();

    startSymbol = (ARTGrammarElementNonterminal) grammar.getDefaultStartNonterminal().getProductions().get(0).getChild().getSibling().getPayload();

    // System.err.println("NFA start symbol is " + startSymbol);

    for (ARTGrammarInstance p : startSymbol.getProductions())
      acceptingProductions.add((ARTGrammarInstanceCat) p);

    // Make the start node
    Set<ARTGrammarInstanceSlot> M0 = new HashSet<>();
    // Locate S' ::= .S
    ARTGrammarInstanceSlot initialSlot = (ARTGrammarInstanceSlot) grammar.leftmostElementRec(grammar.getDefaultStartNonterminal().getProductions().get(0));
    M0.add(initialSlot);
    Set<ARTGrammarInstanceSlot> start = computeMepsilon(M0);
    // Take out S' ::= .S and S' ::= S .
    start.remove(initialSlot);
    start.remove(initialSlot.getSibling().getSibling());
    extendNFA(null, epsilon, start);

    // Build the NFA
    while (!workList.isEmpty()) {
      ARTEarleyNFAVertex g = workList.removeLast();
      Set<ARTGrammarInstanceSlot> Glabel = g.getLabel();
      for (ARTGrammarElementTerminal t : grammar.getTerminals())
        extendNFA(g, t, computeM(Glabel, t));
      for (ARTGrammarElementNonterminal n : grammar.getNonterminals())
        extendNFA(g, n, computeM(Glabel, n));
      if (!g.isConstructedByEpsilonMove()) extendNFA(g, epsilon, computeMepsilon(Glabel));
    }

    // Go through NFA states, building EPN, EE and red sets
    for (ARTEarleyNFAVertex v : nfa.values()) {
      // System.err.println("Computing sets for vertex " + v.getNumber());
      v.computePayload(grammar.getPrefixSlotMap(), epsilon, getChiSetCache(), redSetCache);
    }
  }

  private void extendNFA(ARTEarleyNFAVertex sourceVertex, ARTGrammarElement edgeLabel, Set<ARTGrammarInstanceSlot> destinationLabel) {
    if (destinationLabel.isEmpty()) return;
    ARTEarleyNFAVertex vertex = new ARTEarleyNFAVertex(destinationLabel, edgeLabel.equals(epsilon));
    if (!nfa.containsKey(destinationLabel)) {
      vertex.setNumber(nextFreeVertexNumber++);
      states.put(vertex.getNumber(), vertex);
      nfa.put(destinationLabel, vertex);
      workList.add(vertex);
    }
    if (sourceVertex != null) sourceVertex.getOutEdgeMap().put(edgeLabel, nfa.get(destinationLabel));
  }

  private void initialiseNonterminalWorkSet() {
    nonterminalWorkSet = new HashSet<>();
    nonterminalWorkList = new LinkedList<>();
  }

  private void extendNonterminalWorkSet(ARTGrammarElementNonterminal n) {
    // System.err.print("extendNonterminalWorkSet on " + n);
    if (!nonterminalWorkSet.contains(n)) {
      nonterminalWorkSet.add(n);
      nonterminalWorkList.add(n);
    }
    // System.err.println("- nonterminalWorkList updated to " + nonterminalWorkList);
  }

  private Set<ARTGrammarInstanceSlot> computeMepsilon(Set<ARTGrammarInstanceSlot> m) {
    Set<ARTGrammarInstanceSlot> ret = new HashSet<>();
    initialiseNonterminalWorkSet();
    // System.err.println("ComputeMepsilon on " + m);
    for (ARTGrammarInstanceSlot s : m)
      if (s.getSibling() instanceof ARTGrammarInstanceNonterminal) extendNonterminalWorkSet((ARTGrammarElementNonterminal) s.getSibling().getPayload());

    while (!nonterminalWorkList.isEmpty()) {
      ARTGrammarElementNonterminal n = nonterminalWorkList.removeLast();
      // System.err.println("ComputeMepsilon processing nonterminal " + n);
      for (ARTGrammarInstance p : n.getProductions())
        for (ARTGrammarInstance e = p.getChild(); e != null; e = e.getSibling()) {
          // System.err.println("ComputeMepsilon processing RHS instance " + e);
          if (e instanceof ARTGrammarInstanceSlot)
            ret.add((ARTGrammarInstanceSlot) e);
          else if (e instanceof ARTGrammarInstanceNonterminal) {
            extendNonterminalWorkSet((ARTGrammarElementNonterminal) e.getPayload());
            if (!(e.getFirst().contains(epsilon))) break;
          } else
            break;
        }
    }
    return ret;
  }

  private Set<ARTGrammarInstanceSlot> computeM(Set<ARTGrammarInstanceSlot> m, ARTGrammarElement t) {
    Set<ARTGrammarInstanceSlot> ret = new HashSet<>();
    for (ARTGrammarInstanceSlot s : m) {
      ARTGrammarInstance sibling = s.getSibling();
      if (sibling != null) {
        ARTGrammarElement payload = sibling.getPayload();
        if (payload != null && payload.equals(t)) for (ARTGrammarInstance e = s.getSibling().getSibling(); e != null; e = e.getSibling()) {

          if (e instanceof ARTGrammarInstanceSlot)
            ret.add((ARTGrammarInstanceSlot) e);
          else if (!(e instanceof ARTGrammarInstanceNonterminal && e.getFirst().contains(epsilon))) break;
        }
      }
    }
    return ret;
  }

  @Override
  public String toString() {
    StringWriter sw = new StringWriter();
    sw.write("Earley Table Linked dump start\n");
    sw.write("Earley NFA\n\nState labels\n\n");
    for (Set<ARTGrammarInstanceSlot> s : nfa.keySet()) {
      sw.write(nfa.get(s) + "\n");
      sw.write("select = " + nfa.get(s).getSelect() + "\n");
      sw.write("rLHS = " + nfa.get(s).getrLHS() + "\n\n");
    }

    sw.write("Transitions\n\n");
    for (Set<ARTGrammarInstanceSlot> s : nfa.keySet()) {
      ARTEarleyNFAVertex vertex = nfa.get(s);
      sw.write("G" + vertex.getNumber() + "\n");
      for (ARTGrammarElement d : vertex.getOutEdgeMap().keySet())
        sw.write(d + "->G" + vertex.getOutEdgeMap().get(d).getNumber() + "\nepn: X" + vertex.getEpnMap().get(d) + " = "
            + getChiSetCache().get(vertex.getEpnMap().get(d)) + "\nee: X" + vertex.getEeMap().get(d) + " = " + getChiSetCache().get(vertex.getEeMap().get(d))
            + "\n");

      sw.write("epnEpsilon: X" + vertex.getEpnMap().get(epsilon) + " = " + getChiSetCache().get(vertex.getEpnMap().get(epsilon)) + "\n");
      sw.write("eeEpsilon: X" + vertex.getEeMap().get(epsilon) + " = " + getChiSetCache().get(vertex.getEeMap().get(epsilon)) + "\n");
      for (ARTGrammarElement e : vertex.getRedMap().keySet())
        sw.write("red(" + e + "): R" + vertex.getRedMap().get(e) + " = " + redSetCache.get(vertex.getRedMap().get(e)) + "\n");
      sw.write("\n");
    }

    sw.write("Caches\n\n");
    for (int i : getChiSetCache().getFromIndexMap().keySet())
      sw.write("X" + i + " = " + getChiSetCache().get(i) + "\n");
    sw.write("\n");
    for (int i : redSetCache.getFromIndexMap().keySet())
      sw.write("R" + i + " = " + redSetCache.get(i) + "\n");

    sw.write("AcceptingProductions\n\n");
    for (ARTGrammarInstanceCat p : acceptingProductions)
      sw.write(p + " -> " + p.toGrammarString() + "\n");
    sw.write("\n");

    sw.write("Earley Table Linked dump end\n");

    return sw.toString();
  }

  public void generateDot(ARTText text) {
    text.println("digraph \"Earley NFA\" {\n" + "node[fontname=Helvetica fontsize=9 shape=box height = 0 width = 0 margin= 0.04]\n" + "graph[ordering=out]"
        + "edge[arrowsize = 0.3 fontname=Helvetica fontsize=9]");
    for (Set<ARTGrammarInstanceSlot> s : nfa.keySet()) {
      ARTEarleyNFAVertex vertex = nfa.get(s);
      text.print("\"" + vertex.getNumber() + "\" [label=\"G" + vertex.getNumber() + "\\n");
      Object[] slotArray = vertex.getLabel().toArray();
      Arrays.sort(slotArray);
      for (Object ss : slotArray)
        text.print(((ARTGrammarInstance) ss).toGrammarString(".") + "\\l");
      text.println("\"]\n");
      for (ARTGrammarElement e : vertex.getOutEdgeMap().keySet()) {
        text.println("\"" + vertex.getNumber() + "\"->\"" + vertex.getOutEdgeMap().get(e).getNumber() + "\" [taillabel = \"" + e + "\"]");
      }
    }
    text.println("}");
  }

  public Set<ARTGrammarInstanceCat> getAcceptingProductions() {
    return acceptingProductions;
  }

  public ARTGrammarElementEoS getEoS() {
    return eos;
  }

  public ARTGrammar getGrammar() {
    return grammar;
  }

  public ARTCache<ARTChiSet> getChiSetCache() {
    return chiSetCache;
  }

  public ARTCache<Set<ARTGrammarElement>> getRedSetCache() {
    return redSetCache;
  }

  public ARTChiSet getChiSet(int chiSetIndex) {
    return getChiSetCache().getFromIndexMap().get(chiSetIndex);
  }

  public String toANSIC() {
    int symbolCount = grammar.getTerminals().size() + grammar.getNonterminals().size() + 2;
    int symbolLengthSum = 0;
    for (ARTGrammarElementTerminal x : grammar.getTerminals())
      symbolLengthSum += x.getId().length() + 1;
    for (ARTGrammarElementNonterminal x : grammar.getNonterminals())
      symbolLengthSum += x.getId().length() + 1;
    symbolLengthSum += 4; // For $ and #

    int stateCount = states.keySet().size();

    int chiSetCount = chiSetCache.getFromIndexMap().keySet().size() + 1;
    int chiSetCardinalitySum = 1; // Initial empty set
    for (ARTChiSet x : chiSetCache.getFromIndexMap().values())
      chiSetCardinalitySum += (x.getSet().size() + 1);

    int redSetCount = redSetCache.getFromIndexMap().keySet().size() + 1;
    int redSetCardinalitySum = 1; // Initial empty set
    for (Set<ARTGrammarElement> x : redSetCache.getFromIndexMap().values())
      redSetCardinalitySum += (x.size() + 1);

    // System.out.println("Writing Earley table");
    // System.out.println(symbolCount + " symbols");
    // System.out.println(stateCount + " states");
    // System.out.println(chiSetCount + " Chi Sets");
    // System.out.println(chiSetCardinalitySum + " sum over Chi set cardinalities");
    // System.out.println(redSetCount + " red sets");
    // System.out.println(redSetCardinalitySum + " sum over red set cardinalities");

    int pSize = 8 /* 32-bit = 4; 64-bit = 8 */, iSize = 4, cSize = 1;
    int symbolsAllocation = pSize * symbolCount + symbolLengthSum * cSize;
    int chiSetAllocation = pSize * chiSetCount + chiSetCardinalitySum * iSize;
    int redSetAllocation = pSize * redSetCount + redSetCardinalitySum * iSize;
    int tableAllocation = pSize * stateCount + stateCount * symbolCount * 4 * iSize;

    // System.out.println("Total storage required is: " + (symbolsAllocation + chiSetAllocation + redSetAllocation + tableAllocation) + " bytes");
    // System.out.println("Symbol: " + symbolsAllocation + "\nChi sets: " + chiSetAllocation + "\nred sets: " + redSetAllocation + "\ntable: " +
    // tableAllocation);

    StringWriter sw = new StringWriter();

    sw.write("/* Earley table generated from ART */ \n\n");

    sw.write("#define EOS 0\n");
    sw.write("#define EPSILON " + (grammar.getTerminals().size() + 1) + "\n");
    sw.write("#define STATECOUNT " + states.keySet().size() + "\n");
    sw.write("#define SYMBOLCOUNT " + (grammar.getTerminals().size() + grammar.getNonterminals().size() + 2) + "\n\n");
    sw.write("unsigned empty[] = {0};\n\n");

    sw.write("char* elements[] = {\n  \"$\"");

    for (ARTGrammarElementTerminal x : grammar.getTerminals())
      sw.write(",\n  \"" + x.getId() + "\"");

    sw.write(",\n  \"#\"");

    for (ARTGrammarElementNonterminal x : grammar.getNonterminals())
      sw.write(",\n  \"" + x.getId() + "\"");
    sw.write("\n};\n\n");

    sw.write("unsigned chiSet0[] = { 0 };\n");
    for (Integer x : chiSetCache.getFromIndexMap().keySet()) {
      sw.write("unsigned chiSet" + x + "[] = { ");
      for (ARTGrammarInstance y : chiSetCache.getFromIndexMap().get(x).getSet())
        sw.write(y.getKey() + ", ");
      sw.write("0 };\n");
    }

    sw.write("unsigned* chiSets[] = { chiSet0");
    for (Integer x : chiSetCache.getFromIndexMap().keySet())
      sw.write(", chiSet" + x);
    sw.write(" };\n\n");

    sw.write("unsigned redSet0[] = { 0 };\n");
    for (Integer x : redSetCache.getFromIndexMap().keySet()) {
      sw.write("unsigned redSet" + x + "[] = { ");
      for (ARTGrammarElement y : redSetCache.getFromIndexMap().get(x))
        sw.write(y.getElementNumber() + ", ");
      sw.write("0 };\n");
    }

    sw.write("unsigned* redSets[] = { redSet0");
    for (Integer x : redSetCache.getFromIndexMap().keySet())
      sw.write(", redSet" + x);
    sw.write(" };\n\n");

    int emptyCellCount = 0;

    sw.write("unsigned table[STATECOUNT][SYMBOLCOUNT*4] = {\n");
    for (Integer x : states.keySet()) {
      sw.write("{ /* state " + x + " */\n");
      ARTEarleyNFAVertex state = states.get(x);
      for (ARTGrammarElementTerminal y : grammar.getTerminals()) {
        int cellSum = 0;
        sw.write("  " + (cellSum += (stateMap(state.getOutEdgeMap().get(y) == null ? null : state.getOutEdgeMap().get(y).getNumber()))) + ", ");
        sw.write((cellSum += (stateMap(state.getEpnMap().get(y)))) + ", ");
        sw.write((cellSum += (stateMap(state.getEeMap().get(y)))) + ", ");
        sw.write((cellSum += (stateMap(state.getRedMap().get(y)))) + ", /* " + y + " */\n");

        if (cellSum == 0) emptyCellCount++;
      }

      for (ARTGrammarElementNonterminal y : grammar.getNonterminals()) {
        sw.write("  " + stateMap(state.getOutEdgeMap().get(y) == null ? null : state.getOutEdgeMap().get(y).getNumber()) + ", ");
        sw.write(stateMap(state.getEpnMap().get(y)) + ", ");
        sw.write(stateMap(state.getEeMap().get(y)) + ", ");
        sw.write(stateMap(state.getRedMap().get(y)) + ", /* " + y + " */\n");
      }
      sw.write("},\n");
    }
    sw.write("};\n");
    // System.out.println(emptyCellCount + " empty cells; potential saving of " + 6 * emptyCellCount + " bytes");
    return sw.toString();
  }

  private int stateMap(Integer integer) {
    if (integer == null)
      return 0;
    else
      return integer;
  }
}
