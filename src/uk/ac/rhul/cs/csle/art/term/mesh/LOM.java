package uk.ac.rhul.cs.csle.art.term.mesh;

import javafx.collections.ObservableFloatArray;
import javafx.scene.shape.ObservableFaceArray;
import uk.ac.rhul.cs.csle.art.term.ValueException;

/*
 * A LOM is a standard mesh, but organised so that we can easily locate connected triangles
 *
 * The core idea is that a LOM comprises a sequence of rings of points. Each ring is called a layer,
 * and the layers are arranged within the mesh points array in order.
 *
 * Each LOM can also have a generating expression which specifies whether the transition between layers
 * is governed by linear, circular or bezier transforms, with or without rotations.
 *
 * A LOM without a generating expression is called a low-level LOM or LLOM
 *
 * The intention is that LOMs and LLOMs may be manipulated under mouse control
 *
*/

public class LOM extends AleroMesh {
  int ringVertexCount, extrusionVertexCount;

  // Generate mesh from basePath extruded through extrurionPath with scaleFactors applied: no other transform allowed
  public LOM(float[] basePath, float[] extrusionPath) throws ValueException {
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

  @Override
  public String toString() {
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
