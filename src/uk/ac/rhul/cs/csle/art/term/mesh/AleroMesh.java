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
import javafx.scene.shape.ObservableFaceArray;
import javafx.scene.shape.TriangleMesh;
import uk.ac.rhul.cs.csle.art.term.ValueException;

public class AleroMesh extends TriangleMesh {

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
    sb.append(getVertexFormat());
    sb.append("Points array has " + p.size() + " elements");
    for (int i = 0; i < p.size(); i += 3)
      sb.append("<" + p.get(i) + "," + p.get(i + 1) + "," + p.get(i + 2) + ">");

    ObservableIntegerArray f = getFaces();
    sb.append("\nFaces array has " + f.size() + " elements");
    for (int i = 0; i < f.size(); i++)
      sb.append(" " + i + ":" + f.get(i));
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

  /*** Transformations hard wired into the points array ***/
  public void scale(double factor) {
    ObservableFloatArray p = getPoints();
    for (int i = 0; i < p.size(); i++)
      p.set(i, (float) (p.get(i) * factor));
  }

  public void translate(float offsetX, float offsetY, float offsetZ) {
    ObservableFloatArray p = getPoints();
    for (int i = 0; i < p.size(); i += 3) {
      p.set(i, p.get(i) + offsetX);
      p.set(i + 1, p.get(i + 1) + offsetY);
      p.set(i + 2, p.get(i + 2) + offsetZ);
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

}
