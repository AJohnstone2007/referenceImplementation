package uk.ac.rhul.cs.csle.art.term.mesh;

import java.util.ArrayList;

public class Plane {
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((normal == null) ? 0 : normal.hashCode());
    long temp;
    temp = Double.doubleToLongBits(w);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Plane other = (Plane) obj;
    if (normal == null) {
      if (other.normal != null) return false;
    } else if (!normal.equals(other.normal)) return false;
    if (Double.doubleToLongBits(w) != Double.doubleToLongBits(other.w)) return false;
    return true;
  }

  Point normal;
  double w;

  Plane(Point normal, double w) {
    this.normal = normal;
    this.w = w;
  };

  @Override
  public String toString() {
    return "Plane " + w + ":" + normal;
  }

  public final static double EPSILON = 1e-5;

  static Plane fromPoints(Point a, Point b, Point c) {
    var n = b.minus(a).cross(c.minus(a)).unit();
    return new Plane(n, n.dot(a));
  };

  @Override
  public Plane clone() {
    return new Plane(normal.clone(), w);
  }

  public void flip() {
    this.normal = this.normal.negated();
    this.w = -this.w;
  }

  // Split `polygon` by this plane if needed, then put the polygon or polygon
  // fragments in the appropriate lists. Coplanar polygons go into either
  // `coplanarFront` or `coplanarBack` depending on their orientation with
  // respect to this plane. Polygons in front or in back of this plane go into
  // either `front` or `back`.
  void splitPolygon(Polygon polygon, ArrayList<Polygon> coplanarFront, ArrayList<Polygon> coplanarBack, ArrayList<Polygon> front, ArrayList<Polygon> back) {
    final int COPLANAR = 0;
    final int FRONT = 1;
    final int BACK = 2;
    final int SPANNING = 3;

    // Classify each point as well as the entire polygon into one of the above
    // four classes.
    var polygonType = 0;
    var types = new ArrayList<Integer>();
    for (var i = 0; i < polygon.vertices.size(); i++) {
      double t = this.normal.dot(polygon.vertices.get(i).pos) - this.w;
      var type = (t < -Plane.EPSILON) ? BACK : (t > Plane.EPSILON) ? FRONT : COPLANAR;
      polygonType |= type;
      types.add(type);
    }

    // Put the polygon in the correct list, splitting it when necessary.
    switch (polygonType) {
    case COPLANAR:
      (normal.dot(polygon.plane.normal) > 0 ? coplanarFront : coplanarBack).add(polygon);
      break;
    case FRONT:
      front.add(polygon);
      break;
    case BACK:
      back.add(polygon);
      break;
    case SPANNING:
      var f = new ArrayList<Vertex>();
      var b = new ArrayList<Vertex>();
      for (var i = 0; i < polygon.vertices.size(); i++) {
        var j = (i + 1) % polygon.vertices.size();
        var ti = types.get(i);
        var tj = types.get(j);
        var vi = polygon.vertices.get(i);
        var vj = polygon.vertices.get(j);
        if (ti != BACK) f.add(vi);
        if (ti != FRONT) b.add(ti != BACK ? vi.clone() : vi);
        if ((ti | tj) == SPANNING) {
          var t = (w - normal.dot(vi.pos)) / normal.dot(vj.pos.minus(vi.pos));
          var v = vi.interpolate(vj, t);
          f.add(v);
          b.add(v.clone());
        }
      }
      if (f.size() >= 3) front.add(new Polygon(f));
      if (b.size() >= 3) back.add(new Polygon(b));
      break;
    }
  }
};
