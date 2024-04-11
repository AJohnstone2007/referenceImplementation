package uk.ac.rhul.cs.csle.art.term.mesh;

public class Cuboid extends AleroMesh {
  /*
   * This is a mesh generator for a cuboid with texture coordinates set up so that the image is placed on all six faces
   */

  public Cuboid(float x, float y, float z) {
    getPoints().addAll(-x / 2, -y / 2, -z / 2, // 0
        +x / 2, -y / 2, -z / 2, // 1
        +x / 2, +y / 2, -z / 2, // 2
        -x / 2, +y / 2, -z / 2, // 3
        -x / 2, -y / 2, +z / 2, // 4
        +x / 2, -y / 2, +z / 2, // 5
        +x / 2, +y / 2, +z / 2, // 6
        -x / 2, +y / 2, +z / 2 // 7
    );

    /*
     * Texture map: note that JavaFX Y axis gooes down the screen not up, hence Y is inverted here to give these corners in conventional coords 3---2 | | 0---1
     */
    getTexCoords().addAll(0, 1, // 0
        1, 1, // 1
        1, 0, // 2
        0, 0 // 3
    );

    getFaces().addAll(3, 0, 1, 2, 0, 3, // XY-
        2, 1, 1, 2, 3, 0, // XY-

        4, 0, 6, 2, 7, 3, // XY+
        5, 1, 6, 2, 4, 0, // XY+

        0, 0, 5, 2, 4, 3, // Y-Z
        1, 1, 5, 2, 0, 0, // Y-Z

        7, 0, 2, 2, 3, 3, // Y+Z
        6, 1, 2, 2, 7, 0, // Y+Z

        3, 0, 4, 2, 7, 3, // YZ-
        0, 1, 4, 2, 3, 0, // YZ-

        1, 0, 6, 2, 5, 3, // YZ+
        2, 1, 6, 2, 1, 0 // YZ+

    );
  }
}
