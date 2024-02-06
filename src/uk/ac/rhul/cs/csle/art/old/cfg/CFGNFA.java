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

public class CFGNFA extends ARTGraph {
  private final Map<ARTGraphVertex, Set<ARTGrammarElementNonterminal>> acceptingStates = new HashMap<>(); // Map to the set of terms that are accepted

  public CFGNFA(Object label) {
    super(label); // Creat ART Graph labelled label - usually a string
  }

  public void addAccepting(ARTGraphVertex state, ARTGrammarElementNonterminal pt) {
    if (getAcceptingStates().get(state) == null) getAcceptingStates().put(state, new HashSet<>());
    getAcceptingStates().get(state).add(pt);
  }

  @Override
  public void printDotBody(ARTAbstractGraph graph, PrintWriter printWriter) {
    for (ARTGraphVertex vertex : keyMap.values()) {
      printDotVertex(graph, printWriter, vertex);
      for (ARTGraphEdge edge : vertex.getOutEdges())
        printDotEdge(graph, printWriter, edge);
    }
  }

  private void printDotVertex(ARTAbstractGraph graph, PrintWriter printWriter, ARTGraphVertex vertex) {
    String colour = ""; // Default is no colour which uses color from graph declaration
    String acceptingLabel = ""; // Default is no colour which uses color from graph declaration
    String style = "style=rounded ";

    if (roots.contains(vertex)) colour = " color=green";
    if (getAcceptingStates().containsKey(vertex)) {
      style = "";
      colour = " color=red";
      acceptingLabel = "\n Accept: ";
      // for (Integer t : acceptingStates.get(vertex))
      // acceptingLabel += t == null ? "?null" : tt.toString(t) + " ";
    }
    printWriter.print("\n\"" + vertex.getKey() + "\"  [label=\"" + vertex.getKey() + (vertex.getPayload() != null ? (": " + vertex.getPayload()) : "")
        + acceptingLabel + "\"" + style + colour + "]");
  }

  public void printDotEdge(ARTAbstractGraph graph, PrintWriter printWriter, ARTGraphEdge edge) {
    String label = edge.getPayload() == null ? "< &#1013;>" : "\" " + edge.getPayload() + "\"";
    String colour = edge.getPayload() == null ? "gray" : "blue";

    printWriter.print("\n\"" + edge.getSrc().getKey() + "\"->\"" + edge.getDst().getKey() + "\"" + ("[label=" + label + " fontcolor=" + colour + "]") + ";");
  }

  public Map<ARTGraphVertex, Set<ARTGrammarElementNonterminal>> getAcceptingStates() {
    return acceptingStates;
  }

}
