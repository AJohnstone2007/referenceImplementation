package uk.ac.rhul.cs.csle.art.old.util.graphics;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javafx.scene.paint.Material;
import javafx.scene.shape.ObservableFaceArray;
import javafx.scene.shape.TriangleMesh;

/* This is the superclass for special purpose generators. It contains basic methods for mesh generation */

public abstract class ARTGenerateMesh extends TriangleMesh {

  static ARTMaterial artMaterial = null;

  public ARTGenerateMesh() {
    super();
    if (artMaterial == null) artMaterial = new ARTMaterial();

    int pixels = artMaterial.getPixels();
    float boundary = 1f / pixels;
    for (int pixel = 0; pixel < pixels; pixel++)
      getTexCoords().addAll((pixel + .5f) * boundary, .5f);
  }

  public void setColour(int faceNumber, String name) {
    int colourNumber = artMaterial.getColourNumber(name);
    ObservableFaceArray faces = getFaces();
    faces.set(faceNumber * 6 + 1, colourNumber);
    faces.set(faceNumber * 6 + 3, colourNumber);
    faces.set(faceNumber * 6 + 5, colourNumber);
  }

  public void setColour(String name) {
    int colourNumber = artMaterial.getColourNumber(name);
    ObservableFaceArray faces = getFaces();
    for (int faceNumber = 0; faceNumber < faces.size() / 6; faceNumber++) {
      faces.set(faceNumber * 6 + 1, colourNumber);
      faces.set(faceNumber * 6 + 3, colourNumber);
      faces.set(faceNumber * 6 + 5, colourNumber);
    }
  }

  public void setColourCyclic() {
    int colourNumber = 0;
    ObservableFaceArray faces = getFaces();
    for (int faceNumber = 0; faceNumber < faces.size() / 6; faceNumber++) {
      faces.set(faceNumber * 6 + 1, colourNumber);
      faces.set(faceNumber * 6 + 3, colourNumber);
      faces.set(faceNumber * 6 + 5, colourNumber);

      colourNumber = (colourNumber + 1) % artMaterial.getPixels();
    }
  }

  // // Here are some experimental meshes. These will be pushed off into the constructors of subclasses soon
  // public TriangleMesh makeRightAngledTriangle(float xExtent, float yExtent) {
  // TriangleMesh triangleMesh = new TriangleMesh();
//    // @formatter:off
//    triangleMesh.getPoints().addAll(0f, 0f, 0f,       /* (0,0,0) */
//                                    xExtent, 0f, 0f,  /* (x,0,0) */
//                                    0f, yExtent, 0f); /* (0,y,0) */
//    loadTextureCoordinates(triangleMesh);
//    // Faces are represented by an 1-D array of integer containing (point,textureCoord) pairs. We initially set all textureCoords to zero
//    triangleMesh.getFaces().addAll(0, 0,
//                                   1, 0,
//                                   2, 0);
//    // @formatter:on
  //
  // return triangleMesh;
  // }
  //
  // public TriangleMesh makeSquare(float extent) {
  // TriangleMesh triangleMesh = new TriangleMesh();
//    // @formatter:off
//    triangleMesh.getPoints().addAll(0f, 0f, 0f, /**/
//                                    extent, 0f, 0f, /**/
//                                    0f, extent, 0f, /**/
//                                    extent, extent, 0f); // (0,0,0), (x,0,0), (0,y,0)
//    // @formatter:on
  // loadTextureCoordinates(triangleMesh);
//    // @formatter:off
//    triangleMesh.getFaces().addAll(0, 0,
//                                   1, 0,
//                                   2, 0, /**/
//                                   1, 0,
//                                   3, 0,
//                                   2, 0);
//    // @formatter:on
  //
  // return triangleMesh;
  // }
  //
  // public TriangleMesh makeRightAngledTetrahedron(float xExtent, float yExtent, float zExtent) {
  // TriangleMesh triangleMesh = new TriangleMesh();
  // triangleMesh.getPoints().addAll(0f, 0f, 0f, /**/ xExtent, 0f, 0f, /**/ 0f, yExtent, 0f, /**/ 0f, 0f, zExtent); // (0,0,0), (x,0,0), (0,y,0), (0,0,z)
  // loadTextureCoordinates(triangleMesh);
  // triangleMesh.getFaces().addAll(0, 0, 1, 0, 2, 0, /**/ 0, 0, 1, 0, 3, 0, /**/ 0, 0, 2, 0, 3, 0, /**/ 1, 0, 2, 0, 3, 0);
  //
  // return triangleMesh;
  // }
  //
  public Material getMaterial() {
    return artMaterial;
  }

  public void writeASCIISTL(String filename, String name) throws FileNotFoundException {
    PrintWriter printWriter = new PrintWriter(filename);

    printWriter.println("solid " + name);
    for (int i = 0; i < getFaces().size(); i += 6) {
      printWriter.println("  facet normal 0 0 0");
      printWriter.println("    outer loop");
      int xcoordIndex = getFaces().get(i + 0) * 3;
      printWriter.println("      vertex " + getPoints().get(xcoordIndex) + " " + getPoints().get(xcoordIndex + 1) + " " + getPoints().get(xcoordIndex + 2));
      xcoordIndex = getFaces().get(i + 2) * 3;
      printWriter.println("      vertex " + getPoints().get(xcoordIndex) + " " + getPoints().get(xcoordIndex + 1) + " " + getPoints().get(xcoordIndex + 2));
      xcoordIndex = getFaces().get(i + 4) * 3;
      printWriter.println("      vertex " + getPoints().get(xcoordIndex) + " " + getPoints().get(xcoordIndex + 1) + " " + getPoints().get(xcoordIndex + 2));
      printWriter.println("    endloop");
      printWriter.println("  endfacet");
    }
    printWriter.println("endsolid " + name);

    printWriter.close();
  }
}
