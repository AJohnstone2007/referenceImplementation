package uk.ac.rhul.cs.csle.art.util.graph;

import java.util.ArrayList;

public class ARTGraphVertex extends ARTAbstractVertex {

  final ArrayList<ARTGraphEdge> inEdges = new ArrayList<ARTGraphEdge>();
  final ArrayList<ARTGraphEdge> outEdges = new ArrayList<ARTGraphEdge>();

  public ARTGraphVertex(Object key, Object payload) {
    super(key, payload);
  }

  public void addInEdge(ARTGraphEdge edge) {
    inEdges.add(edge);
  }

  public void removeInEdge(ARTAbstractEdge edge) {
    inEdges.remove(edge);
  }

  public void addOutEdge(ARTGraphEdge edge) {
    outEdges.add(edge);
  }

  public void removeOutEdge(ARTAbstractEdge edge) {
    outEdges.remove(edge);
  }

  public void remove(ARTGraph parentGraph) {
    parentGraph.keyMap.remove(key);
  }

  public ArrayList<ARTGraphEdge> getInEdges() {
    return inEdges;
  }

  public ArrayList<ARTGraphEdge> getOutEdges() {
    return outEdges;
  }

}
