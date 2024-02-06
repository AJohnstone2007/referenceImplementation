package uk.ac.rhul.cs.csle.art.old.cfg;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.util.graph.ARTAbstractGraph;
import uk.ac.rhul.cs.csle.art.old.util.graph.ARTGraph;
import uk.ac.rhul.cs.csle.art.old.util.graph.ARTGraphEdge;
import uk.ac.rhul.cs.csle.art.old.util.graph.ARTGraphVertex;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementNonterminal;

public class CFGDFA extends ARTGraph {
  Map<ARTGraphVertex, Set<ARTGrammarElementNonterminal>> acceptingStates = new HashMap<>(); // Map to the set of terms that are accepted

  public CFGDFA(Object label) {
    super(label); // Creat ART Graph labelled label - usually a string
  }

  public void addAccepting(ARTGraphVertex state, Set<ARTGrammarElementNonterminal> terms) {
    if (acceptingStates.get(state) == null) acceptingStates.put(state, new HashSet<>());
    acceptingStates.get(state).addAll(terms);
  }

  @SuppressWarnings("unchecked")
  public void updateAcceptingStates(CFGNFA nfa) {
    for (ARTGraphVertex vertex : keyMap.values()) {
      for (ARTGraphVertex element : (Set<ARTGraphVertex>) vertex.getKey())
        if (nfa.getAcceptingStates().containsKey(element)) addAccepting(vertex, nfa.getAcceptingStates().get(element));
    }
  }

  @Override
  public void printDotBody(ARTAbstractGraph graph, PrintWriter printWriter) {
    // System.err.println("DFA roots: " + roots);
    for (ARTGraphVertex vertex : keyMap.values()) {
      printDotVertex(graph, printWriter, vertex);
      for (ARTGraphEdge edge : vertex.getOutEdges())
        printDotEdge(graph, printWriter, edge);
    }
  }

  @SuppressWarnings("unchecked")
  private void printDotVertex(ARTAbstractGraph graph, PrintWriter printWriter, ARTGraphVertex vertex) {
    String colour = ""; // Default is no colour which uses color from graph declaration
    String acceptingLabel = ""; // Default is no colour which uses color from graph declaration
    String label = vertex.getPayload() + ": {  ";
    String style = "style=rounded ";

    int elementsPerLine = 5;
    int elements = 0;
    for (ARTGraphVertex v : (Set<ARTGraphVertex>) vertex.getKey()) {
      if (elements++ == elementsPerLine) {
        label += "\n";
        elements = 0;
      }
      label += v.getKey().toString() + "  ";
    }
    label += "}";

    if (acceptingStates.containsKey(vertex)) {
      style = "";
      colour = " color=red";
      acceptingLabel = "\n Accept: ";
      for (ARTGrammarElementNonterminal t : acceptingStates.get(vertex))
        acceptingLabel += t + " ";
    }

    if (roots.contains(vertex)) colour = " color=green";

    printWriter.print("\n\"" + vertex.getKey() + "\"  [label=\"" + label + acceptingLabel + "\"" + style + colour + "]");
  }

  public void printDotEdge(ARTAbstractGraph graph, PrintWriter printWriter, ARTGraphEdge edge) {
    String label = edge.getPayload() == null ? "< &#1013;>" : "\" " + edge.getPayload() + "\"";
    String colour = edge.getPayload() == null ? "gray" : "blue";

    printWriter.print("\n\"" + edge.getSrc().getKey() + "\"->\"" + edge.getDst().getKey() + "\"" + ("[label=" + label + " fontcolor=" + colour + "]") + ";");
  }

}