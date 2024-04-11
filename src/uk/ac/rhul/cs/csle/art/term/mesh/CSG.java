package uk.ac.rhul.cs.csle.art.term.mesh;

import java.util.ArrayList;

import javafx.collections.ObservableFloatArray;
import javafx.scene.shape.ObservableFaceArray;
import javafx.scene.shape.TriangleMesh;

public class CSG {

  public ArrayList<Polygon> polygons;

  CSG() {
    polygons = new ArrayList<>();
  }

  // CSG(ArrayList<Polygon> polygons) {
  // this.polygons = polygons;
  // };

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("CSG polygons");
    sb.append("\n");
    int pn = 1;
    for (Polygon p : polygons)
      sb.append("P" + (pn++) + p + "\n");
    return sb.toString();
  }

  public CSG(TriangleMesh mesh) { // Load the triangles from a triangle mesh as polygons
    this.polygons = new ArrayList<>();
    ObservableFaceArray faces = mesh.getFaces();
    ObservableFloatArray points = mesh.getPoints();
    for (int f = 0; f < faces.size(); f += 6) { // Iterate over faces: each triangular face has three points and three texture coords, so step by 6
      ArrayList<Vertex> faceAsVertexList = new ArrayList<>();
      int pi;
      pi = faces.get(f) * 3; // 3 coordinates for each face
      /* For each face, extract the three coordinate points of each vertex */
      Point p1 = new Point(points.get(pi + 0), points.get(pi + 1), points.get(pi + 2));

      pi = faces.get(f + 2) * 3; // 3 coordinates for each face
      Point p2 = new Point(points.get(pi + 0), points.get(pi + 1), points.get(pi + 2));

      pi = faces.get(f + 4) * 3; // 3 coordinates for each face
      Point p3 = new Point(points.get(pi + 0), points.get(pi + 1), points.get(pi + 2));

      faceAsVertexList.add(new Vertex(p1));
      faceAsVertexList.add(new Vertex(p2));
      faceAsVertexList.add(new Vertex(p3));
      // System.out.println("CSG: adding polygon for face " + f + ": " + faceAsVertexList);
      polygons.add(new Polygon(faceAsVertexList));
    }
  }

  @Override
  public CSG clone() { // Surely this should be a constructor?
    CSG ret = new CSG();
    for (Polygon p : polygons)
      ret.polygons.add(p.clone());
    return ret;
  }

  public static CSG fromPolygons(ArrayList<Polygon> polygons) { // Surely this should be a constructor?

    CSG csg = new CSG();
    csg.polygons = polygons;

    return csg;
  }

  // public List<Polygon> toPolygons() {
  // return polygons;
  // }

  public CSG union(CSG csg) {
    var a = new BSPnode(this.clone().polygons);
    var b = new BSPnode(csg.polygons);
    a.clipTo(b);
    b.clipTo(a);
    b.invert();
    b.clipTo(a);
    b.invert();
    a.build(b.allPolygons());
    return fromPolygons(a.allPolygons());
  }

  public CSG difference(CSG csg) {
    var a = new BSPnode(this.clone().polygons);
    var b = new BSPnode(csg.clone().polygons);
    a.invert();
    a.clipTo(b);
    b.clipTo(a);
    b.invert();
    b.clipTo(a);
    b.invert();
    a.build(b.allPolygons());
    a.invert();
    return fromPolygons(a.allPolygons());
  }

  public CSG intersection(CSG csg) {
    var a = new BSPnode(this.clone().polygons);
    var b = new BSPnode(csg.clone().polygons);
    a.invert();
    b.clipTo(a);
    b.invert();
    a.clipTo(b);
    b.clipTo(a);
    a.build(b.allPolygons());
    a.invert();
    return fromPolygons(a.allPolygons());
  }
}
