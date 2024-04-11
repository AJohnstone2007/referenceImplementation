package uk.ac.rhul.cs.csle.art.term.mesh;

import java.util.ArrayList;
import java.util.Collections;

public class Polygon {
  public ArrayList<Vertex> vertices;
  Plane plane;

  Polygon(ArrayList<Vertex> vertices) {
    this.vertices = vertices;
    this.plane = Plane.fromPoints(vertices.get(0).pos, vertices.get(1).pos, vertices.get(2).pos);
  }

  @Override
  public Polygon clone() { // Surely this should be a constructor?
    ArrayList<Vertex> newVertices = new ArrayList<>();
    for (Vertex v : vertices)
      newVertices.add(v.clone());
    return new Polygon(newVertices);
  }

  public void flip() {
    Collections.reverse(vertices);
    // for (var v : vertices)
    // v.flip();// no longer needed
    plane.flip();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("<");
    boolean first = true;
    for (Vertex v : vertices) {
      if (first)
        first = false;
      else
        sb.append(" ; ");
      sb.append(v);
    }
    sb.append(">");
    return sb.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((plane == null) ? 0 : plane.hashCode());
    result = prime * result + ((vertices == null) ? 0 : vertices.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Polygon other = (Polygon) obj;
    if (plane == null) {
      if (other.plane != null) return false;
    } else if (!plane.equals(other.plane)) return false;
    if (vertices == null) {
      if (other.vertices != null) return false;
    } else if (!vertices.equals(other.vertices)) return false;
    return true;
  }
}