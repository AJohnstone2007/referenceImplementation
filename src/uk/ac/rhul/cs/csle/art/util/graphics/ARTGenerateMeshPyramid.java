package uk.ac.rhul.cs.csle.art.util.graphics;

public class ARTGenerateMeshPyramid extends ARTGenerateMesh {

  public ARTGenerateMeshPyramid(float height, float side) {

    super();
    // @formatter off

    getPoints().addAll(0f, 0f, 0f, /**/
        0f, height, -side / 2, /**/
        -side / 2, height, 0f, /**/
        side / 2, height, 0f, /**/
        0f, height, side / 2);

    getFaces().addAll(0, 0, 2, 0, 1, 0, /**/
        0, 0, 1, 0, 3, 0, /**/
        0, 0, 3, 0, 4, 0, /**/
        0, 0, 4, 0, 2, 0, /**/
        4, 0, 1, 0, 2, 0, /**/
        4, 0, 3, 0, 1, 0);
    // @formatter on
  }

}
