package uk.ac.rhul.cs.csle.art.term.mesh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.scene.paint.Color;
import javafx.scene.shape.ObservableFaceArray;
import javafx.scene.shape.TriangleMesh;
import uk.ac.rhul.cs.csle.art.term.ValueException;

public class AleroMesh extends TriangleMesh {

  public boolean isLOM = false;
  public Color colour = Color.BLANCHEDALMOND;

  public AleroMesh() {
  }

  // Normalised AleroMesh from CSG -- redo using sorting and arrays to remove need for hash/equals in subclasses
  public AleroMesh(CSG csg) {
    int faceCount = 0;
    Map<Vertex, Integer> pointMap = new HashMap<>();
    int nextFreePoint = 0;
    for (Polygon p : csg.polygons) // Check if polygon has more than three faces and triangulate!
      for (Vertex v : p.vertices) {
        if (pointMap.get(v) == null) pointMap.put(v, nextFreePoint++);
        faceCount += p.vertices.size() - 2; // Number of triangle needed for this polygon
      }

    ObservableFaceArray faces = getFaces();
    ObservableFloatArray points = getPoints();

    faces.resize(faceCount * 6);
    points.resize(3 * pointMap.keySet().size());

    getTexCoords().resize(2);
    for (int i = 0; i < getTexCoords().size(); i++)
      getTexCoords().set(i, 0f);

    getFaceSmoothingGroups().resize(faceCount);
    for (int i = 0; i < getFaceSmoothingGroups().size(); i++)
      getFaceSmoothingGroups().set(i, 0);

    for (Vertex k : pointMap.keySet()) {
      int pointIndex = pointMap.get(k) * 3;
      points.set(pointIndex, (float) k.pos.x);
      points.set(pointIndex + 1, (float) k.pos.y);
      points.set(pointIndex + 2, (float) k.pos.z);
    }

    int fi = 0;
    for (Polygon p : csg.polygons) // Check if polygon has more than three faces and triangulate!
      for (int i = p.vertices.size() - 1; i > 1; i--) {
        faces.set(fi++, pointMap.get(p.vertices.get(i)));
        faces.set(fi++, 0);
        faces.set(fi++, pointMap.get(p.vertices.get(i - 1)));
        faces.set(fi++, 0);
        faces.set(fi++, pointMap.get(p.vertices.get(0)));
        faces.set(fi++, 0);
      }
  }

  public AleroMesh(String filename) throws ValueException {
    this(new File(filename));
  }

  public AleroMesh(File file) throws ValueException {
    // First work out if this is an ASCII or a binary STL file
    String intro = "";
    try (FileInputStream fileInputStream = new FileInputStream(file)) {
      int singleCharInt;
      for (int i = 0; i < 5 && ((singleCharInt = fileInputStream.read()) != -1); i++)
        intro += (char) singleCharInt;
    } catch (FileNotFoundException e) {
      throw new ValueException(e.getMessage() + " Unable to locate STL file " + file.getPath());
    } catch (IOException e) {
      throw new ValueException("I/O exeption whilst reading STL file " + file.getPath());
    }

    isASCII = intro.equals("solid");

    if (isASCII)
      try {
        scanner = new Scanner(file);
        scanner.next("solid"); // Skip the intro string
        scanner.next(); // Skip model name
        while (scanner.hasNext("facet")) // Read all the facets
          readFacetASCII();
        scanner.next("endsolid");// Skip the outro string
        scanner.close();
      } catch (IOException e) {
        throw new ValueException("Format error in ASCII STL file " + e.getMessage());
      }
    else
      try {
        this.inputStream = new FileInputStream(file);
        inputStream.read(buffer, 0, 80); // Skip the 80 byte header

        int facetCount = readInt();
        for (int i = 0; i < facetCount; i++)
          readFacetBinary();
      } catch (FileNotFoundException e) {
        throw new ValueException("Unable to open binary STL file ");
      } catch (IOException e) {
        throw new ValueException("I/O error in binary STL file");
      }

    getPoints().resize(3 * pointMap.keySet().size());
    for (Point v : pointMap.keySet()) {
      int pointIndex = v.number * 3;
      getPoints().set(pointIndex + 0, v.x);
      getPoints().set(pointIndex + 1, v.y);
      getPoints().set(pointIndex + 2, v.z);
    }

    getTexCoords().resize(2);
    for (int i = 0; i < getTexCoords().size(); i++)
      getTexCoords().set(i, 0f);

    getFaces().resize(6 * faceMap.keySet().size());
    for (int i = 0; i < getFaceSmoothingGroups().size(); i++)
      getFaces().set(i, 0);

    getFaceSmoothingGroups().resize(faceMap.keySet().size());
    for (int i = 0; i < getFaceSmoothingGroups().size(); i++)
      getFaceSmoothingGroups().set(i, 0);

    int facetNumber = 0;
    for (Face f : faceMap.keySet()) {
      getFaces().set(facetNumber + 0, f.a);
      getFaces().set(facetNumber + 2, f.b);
      getFaces().set(facetNumber + 4, f.c);
      facetNumber += 6;
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    ObservableFloatArray p = getPoints();
    ObservableIntegerArray f = getFaces();
    sb.append("AleroMesh(" + p.size() + " points, " + f.size() / 6 + " triangles)");
    return sb.toString();
  }

  public String toStringFull() {
    if (isLOM) return toStringLOM();
    StringBuilder sb = new StringBuilder();

    ObservableFloatArray p = getPoints();
    ObservableIntegerArray f = getFaces();
    sb.append("AleroMesh(" + p.size() + " points, " + f.size() / 6 + " triangles\n");
    for (int i = 0; i < p.size(); i += 3)
      sb.append("<" + p.get(i) + "," + p.get(i + 1) + "," + p.get(i + 2) + ">");

    for (int i = 0; i < f.size(); i++)
      sb.append(" " + i + ":" + f.get(i));
    sb.append(")");
    return sb.toString();
  }

  /*
   * Read an ASCII or binary STL file collecting the set of vertices and facets so that we can make minimum size meshes
   *
   */
  private boolean isASCII;
  private InputStream inputStream;
  private final byte[] buffer = new byte[80];
  private Scanner scanner;
  private Point pointA, pointB, pointC;
  public Map<Point, Point> pointMap = new HashMap<>();
  public Map<Face, Face> faceMap = new HashMap<>();

  private int vertexNumber = 0;

  private Point findVertex(float x, float y, float z) {
    Point point = new Point(x, y, z, vertexNumber);
    Point ret = pointMap.get(point);
    if (ret == null) {
      ret = point;
      pointMap.put(point, point);
      vertexNumber++;
    }
    return ret;
  }

  private Face findFace(Face facet) {
    Face ret = faceMap.get(facet);
    if (ret == null) {
      ret = facet;
      faceMap.put(facet, facet);
    }
    return ret;
  }

  private void readFacetASCII() {
    scanner.next("facet");
    scanner.next("normal");
    scanner.nextFloat();
    scanner.nextFloat();
    scanner.nextFloat(); // Normal - discard
    scanner.next("outer");
    scanner.next("loop");

    scanner.next("vertex");
    pointA = findVertex(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat());
    scanner.next("vertex");
    pointB = findVertex(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat());
    scanner.next("vertex");
    pointC = findVertex(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat());

    scanner.next("endloop");
    scanner.next("endfacet");
    findFace(new Face(pointA.number, pointB.number, pointC.number));
  }

  private void readFacetBinary() throws IOException {
    readFloat();
    readFloat();
    readFloat(); // Normal - discard

    pointA = findVertex(readFloat(), readFloat(), readFloat());
    pointB = findVertex(readFloat(), readFloat(), readFloat());
    pointC = findVertex(readFloat(), readFloat(), readFloat());
    inputStream.read(buffer, 0, 2); // Attributes - discard
    findFace(new Face(pointA.number, pointB.number, pointC.number));
  }

  private float readFloat() throws IOException {
    return Float.intBitsToFloat(readInt());
  }

  private int readInt() throws IOException {
    inputStream.read(buffer, 0, 4);
    return ((0xFF & buffer[3]) << 24) | ((0xFF & buffer[2]) << 16) | ((0xFF & buffer[1]) << 8) | ((0xFF & buffer[0]));
  }

  /*** Analysis routines ***/

  /*
   * Thoughts on analyses
   *
   * We already have an array of vertices that are sorted by x,y,z coordinates
   *
   * Each triangle has three vertices and three edges: triangle a,b,c has edges ab, bc and ca
   *
   * We can make a map from edges (vertex pairs) to faces. Each edge should be in exactly two faces. Where an edge is in other numbers of faces, turn those
   * faces red.
   *
   * Simplify3D identifies orphaned faces, duplicate facesand holes. It also flips triangles as necessary.
   *
   * Self intersections
   *
   * Very narrow triangles
   *
   * A complex vertex is one neigbours do not form a disk
   *
   * A complex edge is one with more than two faces
   *
   *
   */

  public void analyse() {
    analyseCloseVertices();
    analyseEdges();
    analyseWindingOrders();
  }

  public Set<Point> analyseCloseVertices() {
    System.out.println("STL close vertex analyser report");
    System.out.println(faceMap.keySet().size() + " facets using " + pointMap.keySet().size() + " unique vertices");
    Set<Point> ret = new HashSet<>();
    /* make an array of the vertices and sort it by x then y then z to allow fast location of nearby vertices */
    Point[] verticesArray = new Point[pointMap.keySet().size()];

    int i = 0;
    for (Point v : pointMap.keySet())
      verticesArray[i++] = v;
    Arrays.sort(verticesArray);

    double snap = 0.001;

    for (i = 1; i < verticesArray.length; i++)
      if (delta(verticesArray[i].x, verticesArray[i - 1].x) <= snap && delta(verticesArray[i].y, verticesArray[i - 1].y) <= snap
          && delta(verticesArray[i].z, verticesArray[i - 1].z) <= snap) {
        System.out.println("Close vertices " + verticesArray[i - 1] + " " + verticesArray[i]);

        ret.add(verticesArray[i]);
        ret.add(verticesArray[i - 1]);
      }

    return ret;
  }

  static double delta(double l, double r) {
    if (l > r)
      return l - r;
    else
      return r - l;
  }

  private void analyseWindingOrders() {
    // TODO Auto-generated method stub

  }

  private void analyseEdges() {
    // TODO Auto-generated method stub

  }

  /*** Transformations hard wired into the points array - these are slow, but allow me to develop my understanding ***/
  public void scale(float scaleX, float scaleY, float scaleZ) {
    ObservableFloatArray p = getPoints();
    for (int i = 0; i < p.size(); i += 3) {
      p.set(i, p.get(i) * scaleX);
      p.set(i + 1, p.get(i + 1) * scaleY);
      p.set(i + 2, p.get(i + 2) * scaleZ);
    }
  }

  public void translate(float offsetX, float offsetY, float offsetZ) {
    ObservableFloatArray p = getPoints();
    for (int i = 0; i < p.size(); i += 3) {
      p.set(i, p.get(i) + offsetX);
      p.set(i + 1, p.get(i + 1) + offsetY);
      p.set(i + 2, p.get(i + 2) + offsetZ);
    }
  }

  //@formatter:off
  /*
   * Rotation matrices for cardinal axes R_x(T)
   *
   * R_x:
   *  1     0     0
   *  0     cosT  sinT
   *  0    -sinT  cosT
   *
   * R_y:
   *  cosT 0     -sinT
   *  0    1      0
   *  sinT 0      cosT
   *
   * R_z:
   *  cosT  sinT  0
   * -sinT  cosT  0
   *  0     0     1
   *
   */
  //@formatter:on
  public void rotateX(float theta) { // rotate around the X axis. When looking from the origin towards X+, positive angle implies clockwise rotation
    if (theta == 0) return;
    ObservableFloatArray p = getPoints();
    double thetaR = Math.toRadians(theta);
    float cosTheta = (float) Math.cos(thetaR);
    float sinTheta = (float) Math.sin(thetaR);
    for (int i = 0; i < p.size(); i += 3) {
      int yi = i + 1;
      float y = p.get(yi);
      int zi = i + 2;
      float z = p.get(zi);
      // x point unchanged
      p.set(yi, y * cosTheta - z * sinTheta); // y point
      p.set(zi, y * sinTheta + z * cosTheta); // z point
    }
  }

  public void rotateY(float theta) { // rotate around the Y axis. When looking from the origin towards Y+, positive angle implies clockwise rotation
    if (theta == 0) return;
    ObservableFloatArray p = getPoints();
    double thetaR = Math.toRadians(theta);
    float cosTheta = (float) Math.cos(thetaR);
    float sinTheta = (float) Math.sin(thetaR);
    for (int i = 0; i < p.size(); i += 3) {
      float x = p.get(i);
      int zi = i + 2;
      float z = p.get(zi);
      p.set(i, x * cosTheta + z * sinTheta); // x point
      // y point unchanged
      p.set(zi, x * -sinTheta + z * cosTheta); // z point
    }
  }

  public void rotateZ(float theta) { // rotate around the Z axis. When looking from the origin towards Z+, positive angle implies clockwise rotation
    if (theta == 0) return;
    ObservableFloatArray p = getPoints();
    double thetaR = Math.toRadians(theta);
    float cosTheta = (float) Math.cos(thetaR);
    float sinTheta = (float) Math.sin(thetaR);
    for (int i = 0; i < p.size(); i += 3) {
      float x = p.get(i);
      int yi = i + 1;
      float y = p.get(yi);
      p.set(i, x * cosTheta - y * sinTheta); // x point
      p.set(yi, x * sinTheta + y * cosTheta); // y point
      // z point unchanged
    }
  }

  public float minX = Float.MAX_VALUE;
  public float minY = Float.MAX_VALUE;
  public float minZ = Float.MAX_VALUE;

  public float maxX = Float.MIN_VALUE;
  public float maxY = Float.MIN_VALUE;
  public float maxZ = Float.MIN_VALUE;

  public void computeBoundingBox() {
    minX = Float.MAX_VALUE;
    minY = Float.MAX_VALUE;
    minZ = Float.MAX_VALUE;

    maxX = Float.MIN_VALUE;
    maxY = Float.MIN_VALUE;
    maxZ = Float.MIN_VALUE;

    ObservableFloatArray p = getPoints();
    for (int i = 0; i < p.size(); i += 3) {
      float x = p.get(i);
      float y = p.get(i + 1);
      float z = p.get(i + 2);

      if (x < minX) minX = x;
      if (y < minY) minY = y;
      if (z < minZ) minZ = z;

      if (x > maxX) maxX = x;
      if (y > maxY) maxY = y;
      if (z > maxZ) maxZ = z;
    }
  }

  public void centreBoundingBox() {
    computeBoundingBox();
    translate(-(minX + (maxX - minX) / 2), -(minY + (maxY - minY) / 2), -(minZ + (maxZ - minZ) / 2));
  }

  public Double volume() {
    return ((double) maxX - (double) minX) * ((double) maxY - (double) minY) * ((double) maxZ - (double) minZ);
  }

  class Point implements Comparable<Object> {
    public float x;
    public float y;
    public float z;
    public int number;

    Point(float x, float y, float z, int number) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.number = number;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Float.floatToIntBits(x);
      result = prime * result + Float.floatToIntBits(y);
      result = prime * result + Float.floatToIntBits(z);
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      Point other = (Point) obj;
      if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) return false;
      if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y)) return false;
      if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z)) return false;
      return true;
    }

    @Override
    public String toString() {
      return (number + ":(" + x + "," + y + "," + z + ")");
    }

    @Override
    public int compareTo(Object o) {
      Point other = (Point) o;

      if (x < other.x) return -1;
      if (x > other.x) return 1;

      if (y < other.y) return -1;
      if (y > other.y) return 1;

      if (z < other.z) return -1;
      if (z > other.z) return 1;

      return 0;
    }
  }

  public class Face {
    final int a, b, c;

    Face(int a, int b, int c) {
      this.a = a;
      this.b = b;
      this.c = c;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + a;
      result = prime * result + b;
      result = prime * result + c;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      Face other = (Face) obj;
      if (a != other.a) return false;
      if (b != other.b) return false;
      if (c != other.c) return false;
      return true;
    }

    @Override
    public String toString() {
      return "<" + a + "," + b + "," + c + ">";
    }
  }

  /* LOM stuff belowthis line */
  /*
   * A LOM is a standard mesh, but organised so that we can easily locate connected triangles
   *
   * The core idea is that a LOM comprises a sequence of rings of points. Each ring is called a layer, and the layers are arranged within the mesh points array
   * in order.
   *
   * Each LOM can also have a generating expression which specifies whether the transition between layers is governed by linear, circular or bezier transforms,
   * with or without rotations.
   *
   * A LOM without a generating expression is called a low-level LOM or LLOM
   *
   * The intention is that LOMs and LLOMs may be manipulated under mouse control
   *
   */

  int ringVertexCount, extrusionVertexCount;

  // Generate mesh from basePath extruded through extrurionPath with scaleFactors applied: no other transform allowed
  public AleroMesh(float[] basePath, float[] extrusionPath) throws ValueException {
    isLOM = true;
    ringVertexCount = basePath.length / 3;
    extrusionVertexCount = extrusionPath.length / 3;

    int meshVertexCount = ringVertexCount * (1 + extrusionVertexCount);
    int meshFacetCount = 2 * ringVertexCount * extrusionVertexCount // layer facets
        + 2 * (ringVertexCount - 2); // base and top facets

    // 1: parameter consistency checking
    if (basePath.length < 3) throw new ValueException("LayeredOrderedMesh: basePath array size must be at least three");
    if (basePath.length % 3 != 0) throw new ValueException("LayeredOrderedMesh: basePath array size " + basePath.length + " is not a multiple of three");
    if (extrusionPath.length % 3 != 0)
      throw new ValueException("LayeredOrderedMesh: extrusionPath array size " + extrusionPath.length + " is not a multiple of three");

    getTexCoords().addAll(0, 1, // 0
        1, 1, // 1
        1, 0, // 2
        0, 0); // 3

    // Load points array
    ObservableFloatArray points = getPoints();
    points.resize(3 * meshVertexCount);
    int pi = 0; // running index into the points array

    for (int p = 0; p < basePath.length; p++) // Load base points
      points.set(pi++, basePath[p]);

    for (int l = 0; l < extrusionVertexCount; l++) // Load rigCount layers of extruded points
      for (int p = 0; p < ringVertexCount; p++) {
        points.set(pi, points.get(pi - basePath.length) + extrusionPath[l * 3]); // x
        pi++;
        points.set(pi, points.get(pi - basePath.length) + extrusionPath[l * 3 + 1]); // y
        pi++;
        points.set(pi, points.get(pi - basePath.length) + extrusionPath[l * 3 + 2]); // z
        pi++;
      }

    // Load facets array: note that the outside is defined by counterclockwise coordinates as you are looking at the face
    ObservableFaceArray faces = getFaces();
    faces.resize(6 * meshFacetCount); // Each facet needs 6 integers; 3 space coordinates and 3 texture coordinates

    int fi = 0; // running index into the faces array
    for (int l = 0; l < extrusionVertexCount; l++) { // Side faces
      for (int p = 0; p < ringVertexCount; p++) {
        faces.set(fi++, (ringVertexCount * l) + p);
        faces.set(fi++, 0);
        faces.set(fi++, (ringVertexCount * l) + (p + 1) % ringVertexCount); // *
        faces.set(fi++, 1);
        faces.set(fi++, (ringVertexCount * (l + 1)) + p);
        faces.set(fi++, 3);

        faces.set(fi++, (ringVertexCount * l) + (p + 1) % ringVertexCount); // *
        faces.set(fi++, 1);
        faces.set(fi++, (ringVertexCount * (l + 1)) + (p + 1) % ringVertexCount); // *
        faces.set(fi++, 2);
        faces.set(fi++, (ringVertexCount * (l + 1)) + p);
        faces.set(fi++, 3);
      }
    }

    for (int p = 2; p < ringVertexCount; p++) { // bottom faces
      faces.set(fi++, p);
      faces.set(fi++, 0);
      faces.set(fi++, p - 1);
      faces.set(fi++, 2);
      faces.set(fi++, 0);
      faces.set(fi++, 3);
    }

    for (int p = meshVertexCount - 3; p >= meshVertexCount - ringVertexCount; p--) { // top faces
      faces.set(fi++, p);
      faces.set(fi++, 0);
      faces.set(fi++, p + 1);
      faces.set(fi++, 2);
      faces.set(fi++, meshVertexCount - 1);
      faces.set(fi++, 3);
    }
  }

  public String toStringLOM() {
    StringBuilder sb = new StringBuilder();
    sb.append("LOM with " + ringVertexCount + " ring vertices, " + extrusionVertexCount + " extrusion path "
        + (extrusionVertexCount == 1 ? "vertex" : "vertices") + "\n");
    sb.append("Points array\n" + getPoints() + "\nFace array\n" + getFaces());
    for (int i = 0; i < getPoints().size(); i++) {
      if (i % (3 * ringVertexCount) == 0) sb.append("\nPoint ring " + i / (3 * ringVertexCount));
      if (i % 3 == 0) sb.append("\nP" + i / 3);
      sb.append(" " + getPoints().get(i));
    }
    int i;
    for (i = 0; i < 6 * ringVertexCount * (extrusionVertexCount + 1); i += 2) {
      if (i % (ringVertexCount * 12) == 0) sb.append("\nFace ring " + i / (ringVertexCount * 12));
      if (i % 6 == 0) sb.append("\nF" + i / 6);
      sb.append(" " + getFaces().get(i) + ":" + pointToString(getFaces().get(i) * 3) + "(T" + getFaces().get(i + 1) + ":"
          + textureToString(getFaces().get(i + 1) * 2) + ")");
    }

    int startAt = i;
    sb.append("\nInitial");
    for (; i < startAt + 6 * (ringVertexCount - 2); i += 2) {
      if (i % 6 == 0) sb.append("\nF" + i / 6);
      sb.append(" " + getFaces().get(i) + ":" + pointToString(getFaces().get(i) * 3) + "(T" + getFaces().get(i + 1) + ":"
          + textureToString(getFaces().get(i + 1) * 2) + ")");
    }
    startAt = i;
    sb.append("\nFinal");
    for (; i < startAt + 6 * (ringVertexCount - 2); i += 2) {
      if (i % 6 == 0) sb.append("\nF" + i / 6);
      sb.append(" " + getFaces().get(i) + ":" + pointToString(getFaces().get(i) * 3) + "(T" + getFaces().get(i + 1) + ":"
          + textureToString(getFaces().get(i + 1) * 2) + ")");
    }
    sb.append("\n");

    return sb.toString();
  }

  private String textureToString(int i) {
    return "[" + getTexCoords().get(i) + "," + getTexCoords().get(i + 1) + "]";
  }

  private String pointToString(int i) {
    return "[" + getPoints().get(i) + "," + getPoints().get(i + 1) + "," + getPoints().get(i + 2) + "]";
  }

}
