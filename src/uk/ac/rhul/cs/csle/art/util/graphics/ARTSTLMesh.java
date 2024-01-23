package uk.ac.rhul.cs.csle.art.util.graphics;

import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.scene.shape.TriangleMesh;

public class ARTSTLMesh extends TriangleMesh {

  private float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE, maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE, maxZ = Float.MIN_VALUE;

  public float getMinX() {
    return minX;
  }

  public float getMinY() {
    return minY;
  }

  public float getMinZ() {
    return minZ;
  }

  public float getMaxX() {
    return maxX;
  }

  public float getMaxY() {
    return maxY;
  }

  public float getMaxZ() {
    return maxZ;
  }

  public ARTSTLMesh(ARTSTLParser stlParser) {
    this(stlParser, false, 0, 0, 0, 1);
  }

  public ARTSTLMesh(ARTSTLParser stlParser, float offsetX, float offsetY, float offsetZ, float scale) {
    this(stlParser, false, offsetX, offsetY, offsetZ, scale);
  }

  public ARTSTLMesh(ARTSTLParser stlParser, Boolean flipZ, float offsetX, float offsetY, float offsetZ, float scale) {
    super();

    int facetCount = stlParser.getFacetCount();

    float points[] = new float[facetCount * 9];
    float texCoords[] = { 0f, 0f };
    int faces[] = new int[facetCount * 6];

    ARTCoord normal = new ARTCoord(), vertex1 = new ARTCoord(), vertex2 = new ARTCoord(), vertex3 = new ARTCoord();

    for (int facetNumber = 0; facetNumber < facetCount; facetNumber++) {
      stlParser.readFacet(normal, vertex1, vertex2, vertex3);
      updateX(points[facetNumber * 9 + 0] = vertex1.getX() * scale + offsetX);
      updateY(points[facetNumber * 9 + 1] = vertex1.getY() * scale + offsetY);
      updateZ(points[facetNumber * 9 + 2] = vertex1.getZ() * scale + offsetZ);

      updateX(points[facetNumber * 9 + 3] = vertex2.getX() * scale + offsetX);
      updateY(points[facetNumber * 9 + 4] = vertex2.getY() * scale + offsetY);
      updateZ(points[facetNumber * 9 + 5] = vertex2.getZ() * scale + offsetZ);

      updateX(points[facetNumber * 9 + 6] = vertex3.getX() * scale + offsetX);
      updateY(points[facetNumber * 9 + 7] = vertex3.getY() * scale + offsetY);
      updateZ(points[facetNumber * 9 + 8] = vertex3.getZ() * scale + offsetZ);

      faces[facetNumber * 6 + 0] = facetNumber * 3 + 0;
      faces[facetNumber * 6 + 2] = facetNumber * 3 + 1;
      faces[facetNumber * 6 + 4] = facetNumber * 3 + 2;
    }

    getPoints().setAll(points);
    getTexCoords().setAll(texCoords);
    getFaces().setAll(faces);
    System.out.println("Facet count " + facetCount);
    System.out.println("Bounding box origin " + toBoundingBoxOriginString());
    System.out.println("Bounding box extent " + toBoundingBoxExtentString());

  }

  private void updateX(float f) {
    if (f < minX) minX = f;
    if (f > maxX) maxX = f;
  }

  private void updateY(float f) {
    if (f < minY) minY = f;
    if (f > maxY) maxY = f;
  }

  private void updateZ(float f) {
    if (f < minZ) minZ = f;
    if (f > maxZ) maxZ = f;
  }

  public String toBoundingBoxOriginString() {
    return "(" + minX + ", " + minY + ", " + minZ + ")";
  }

  public String toBoundingBoxExtentString() {
    return "(" + (maxX - minX) + ", " + (maxY - minY) + ", " + (maxZ - minZ) + ")";
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    ObservableFloatArray p = getPoints();
    sb.append("Points array has " + p.size() + " elememnts");
    for (int i = 0; i < p.size(); i += 3)
      sb.append("<" + p.get(i) + "," + p.get(i + 1) + "," + p.get(i + 2) + ">");

    ObservableIntegerArray f = getFaces();
    sb.append("\nFaces array has " + f.size() + " elememnts");
    for (int i = 0; i < f.size(); i++)
      sb.append(" " + i + ":" + f.get(i));
    return sb.toString();

  }
}
