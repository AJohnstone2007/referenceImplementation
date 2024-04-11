package uk.ac.rhul.cs.csle.art.term.mesh;

import java.util.ArrayList;

public class BSPnode {
  int number;
  static int nextFreeNumber = 0;
  Plane plane;
  ArrayList<Polygon> polygons;
  BSPnode front;
  BSPnode back;

  public void toStringIndented(StringBuilder sb, BSPnode node, int level) {
    for (int i = 0; i < level; i++)
      sb.append("  ");
    if (node == null) {
      sb.append("NULL\n");
      return;
    }
    sb.append("Node: " + node.number);
    sb.append("Plane: " + plane);
    sb.append(node.polygons.toString());
    sb.append("\n");
    toStringIndented(sb, node.front, level + 1);
    toStringIndented(sb, node.back, level + 1);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Node tree\n");
    toStringIndented(sb, this, 0);
    return sb.toString();
  }

  BSPnode() {
    number = nextFreeNumber++;
    // System.out.println("Created node " + number);
    plane = null;
    front = null;
    back = null;
    polygons = new ArrayList<>();
  }

  BSPnode(ArrayList<Polygon> polygons) {
    this();
    if (!polygons.isEmpty()) build(polygons);
  }

  @Override
  public BSPnode clone() {
    BSPnode node = new BSPnode();
    if (plane != null) node.plane = plane.clone();
    if (front != null) node.front = front.clone();
    if (back != null) node.back = back.clone();
    if (polygons != null) for (Polygon p : polygons)
      node.polygons.add(p.clone());
    return node;
  }

  void invert() {
    for (Polygon p : polygons)
      p.flip();
    this.plane.flip();
    if (front != null) front.invert();
    if (back != null) back.invert();
    var temp = this.front; // Swap front and back
    this.front = this.back;
    this.back = temp;
  }

  ArrayList<Polygon> clipPolygons(ArrayList<Polygon> polygons) {
    if (plane == null) return new ArrayList<>(polygons);
    ArrayList<Polygon> frontPolygons = new ArrayList<>();
    ArrayList<Polygon> backPolygons = new ArrayList<>();
    for (var p : polygons)
      this.plane.splitPolygon(p, frontPolygons, backPolygons, frontPolygons, backPolygons);

    if (this.front != null) frontPolygons = this.front.clipPolygons(frontPolygons);
    if (this.back != null)
      backPolygons = this.back.clipPolygons(backPolygons);
    else
      backPolygons = new ArrayList<>();
    frontPolygons.addAll(backPolygons);
    return frontPolygons;
  }

  void clipTo(BSPnode bsp) {
    polygons = bsp.clipPolygons(polygons);
    if (this.front != null) this.front.clipTo(bsp);
    if (this.back != null) this.back.clipTo(bsp);
  }

  ArrayList<Polygon> allPolygons() {
    var localPolygons = new ArrayList<>(polygons);
    if (front != null) localPolygons.addAll(front.allPolygons());
    if (back != null) localPolygons.addAll(back.allPolygons());
    return localPolygons;
  }

  void build(ArrayList<Polygon> polygons) {
    if (polygons.isEmpty()) return;
    if (plane == null) plane = polygons.get(0).plane.clone();
    var frontPolygons = new ArrayList<Polygon>();
    var backPolygons = new ArrayList<Polygon>();
    var iterationBase = new ArrayList<Polygon>(polygons);
    for (var p : iterationBase)
      this.plane.splitPolygon(p, this.polygons, this.polygons, frontPolygons, backPolygons);

    if (!frontPolygons.isEmpty()) {
      if (this.front == null) this.front = new BSPnode();
      this.front.build(frontPolygons);

    }
    if (!backPolygons.isEmpty()) {
      if (this.back == null) this.back = new BSPnode();
      this.back.build(backPolygons);
    }
  }
}