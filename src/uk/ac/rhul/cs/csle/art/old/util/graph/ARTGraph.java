package uk.ac.rhul.cs.csle.art.old.util.graph;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ARTGraph extends ARTAbstractGraph {
  public ARTGraph(Object label) {
    super(label);
  }

  protected final Map<Object, ARTGraphVertex> keyMap = new HashMap<Object, ARTGraphVertex>();

  public ARTGraphVertex addVertex(Object key, Object label) {
    ARTGraphVertex vertex = new ARTGraphVertex(key, label);
    keyMap.put(key, vertex);
    return vertex;
  }

  public ARTGraphVertex lookupVertex(Object key) {
    return keyMap.get(key);
  }

  public ARTGraphVertex findVertex(Object key, Object label) {
    ARTGraphVertex vertex = keyMap.get(key);
    if (vertex == null) vertex = addVertex(key, label);
    return vertex;
  }

  public int vertexCount() {
    return keyMap.keySet().size();
  }

  public ARTGraphEdge addEdge(ARTGraphVertex parent, ARTGraphVertex child, Object label) {
    return new ARTGraphEdge(parent, child, label);
  }

  public ARTGraphEdge addEdge(ARTGraphVertex parent, ARTGraphVertex child) {
    return addEdge(parent, child, null);
  }

  public ArrayList<ARTGraphVertex> findRoots() {
    ArrayList<ARTGraphVertex> roots = new ArrayList<ARTGraphVertex>();

    for (ARTGraphVertex v : keyMap.values())
      if (v.getInEdges().isEmpty()) roots.add((v));
    return roots;
  }

  @Override
  public String toString() {
    String ret = "Graph " + label;

    for (ARTGraphVertex v : keyMap.values()) {
      ret += "\n" + v.getPayload();

      for (ARTGraphEdge e : v.getOutEdges())
        ret += "-> " + e.getDst().getPayload();
    }
    return ret;
  }

  public Map<Object, ARTGraphVertex> getKeyMap() {
    return keyMap;
  }

  @Override
  public void printDotBody(ARTAbstractGraph graph, PrintWriter printWriter) {
    for (ARTGraphVertex vertex : keyMap.values()) {
      vertex.printDot(graph, printWriter);
      for (ARTGraphEdge edge : vertex.getOutEdges())
        edge.printDot(graph, printWriter);
    }
  }

  /* Start of subset construction */
  private List<Set<ARTGraphVertex>> workList;
  private Set<Set<ARTGraphVertex>> workListSet;
  private Integer dfaStateNumber = 0;

  private void workListAdd(Set<ARTGraphVertex> nfaVertexSet) {
    if (!workList.contains(nfaVertexSet)) {
      workList.add(nfaVertexSet);
      workListSet.add(nfaVertexSet);
    }
  }

  public void subsetConstruction(ARTGraph dfa) {
    workList = new LinkedList<>();
    workListSet = new HashSet<>();

    // Load the epsilon closure of the roots
    Set<ARTGraphVertex> rootSet = new HashSet<>();
    for (ARTAbstractVertex r : roots)
      rootSet.add((ARTGraphVertex) r);
    Set<ARTGraphVertex> dfaKey = closureOverEpsilon(rootSet);
    dfaKey.addAll(rootSet);
    dfaExtend(dfa, null, dfaKey, null); // null means no inedge to be created
    dfa.getRoots().add(dfa.keyMap.get(dfaKey));

    while (!workList.isEmpty()) {
      Set<ARTGraphVertex> nfaVertexSet = workList.remove(0);
      workListSet.remove(nfaVertexSet);
      ARTGraphVertex src = dfa.keyMap.get(nfaVertexSet); // used for creating in edges
      // System.out.println("Processing worklist element " + nfaVertexSet);
      // Gather out edge labels
      Set<Object> outEdgeLabels = new HashSet<>();
      for (ARTGraphVertex v : nfaVertexSet)
        for (ARTGraphEdge e : v.outEdges)
          if (e.payload != null) outEdgeLabels.add(e.payload);

      // Create closure for each label seen
      for (Object edgeLabel : outEdgeLabels)
        dfaExtend(dfa, src, closureOverEpsilon(closureOverLabel(nfaVertexSet, edgeLabel)), edgeLabel);
    }
  }

  private void dfaExtend(ARTGraph dfa, ARTGraphVertex src, Set<ARTGraphVertex> nfaVertexSet, Object edgeLabel) {
    // System.out.println("Extending DFA with source " + src + "\n using " + nfaVertexSet);
    if (!dfa.keyMap.containsKey(nfaVertexSet)) {
      // System.out.println("Adding new DFA vertex" + nfaVertexSet);
      ARTGraphVertex dst = dfa.addVertex(nfaVertexSet, ++dfaStateNumber);
      if (src != null) dfa.addEdge(src, dst, edgeLabel);
      workListAdd(nfaVertexSet);
    } else {
      // System.out.println("Vertex is already in DFA" + nfaVertexSet);
      if (src != null) dfa.addEdge(src, dfa.getKeyMap().get(nfaVertexSet), edgeLabel);
    }
  }

  Set<ARTGraphVertex> visited = new HashSet<>();

  private Set<ARTGraphVertex> closureOverLabel(Set<ARTGraphVertex> seed, Object label) {
    Set<ARTGraphVertex> closure = new HashSet<>();
    visited.clear();

    for (ARTGraphVertex s : seed)
      closureRec(closure, s, label);

    return closure;
  }

  private void closureRec(Set<ARTGraphVertex> closure, ARTGraphVertex v, Object label) {
    if (visited.contains(v)) return;
    visited.add(v);

    for (ARTGraphEdge e : v.getOutEdges())
      if (e.payload != null && e.payload.equals(label)) {
        closure.add(e.dst);
        closureRec(closure, e.dst, label);
      }
  }

  private Set<ARTGraphVertex> closureOverEpsilon(Set<ARTGraphVertex> seed) {
    Set<ARTGraphVertex> closure = new HashSet<>();
    closure.addAll(seed);

    visited.clear();

    for (ARTGraphVertex s : seed)
      closureRec(closure, s);

    return closure;
  }

  private void closureRec(Set<ARTGraphVertex> closure, ARTGraphVertex v) {
    if (visited.contains(v)) return;
    visited.add(v);

    for (ARTGraphEdge e : v.getOutEdges())
      if (e.payload == null) {
        closure.add(e.dst);
        closureRec(closure, e.dst);
      }
  }
}
