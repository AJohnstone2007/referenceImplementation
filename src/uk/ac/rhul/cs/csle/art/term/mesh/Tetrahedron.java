package uk.ac.rhul.cs.csle.art.term.mesh;

public class Tetrahedron extends AleroMesh {
  /*
   * This is a mesh generator for a tetrahedron with the square face on the YZ plane, and texture coordinates set up so that the image is placed on the base,
   * and wrapped across all four angled faces
   */

  public Tetrahedron(float x, float y, float z) {
    getPoints().addAll(-x / 2, -y / 2, -z / 2, // 0
        +x / 2, -y / 2, -z / 2, // 1
        +x / 2, +y / 2, -z / 2, // 2
        -x / 2, +y / 2, -z / 2, // 3
        0, 0, z / 2 // 4 peak
    );

    /*
     * Texture map: note that JavaFX Y axis gooes down the screen not up, hence Y is inverted here to give these corners in conventional coords 3---2 | | 0---1
     */
    getTexCoords().addAll(0, 1, // 0
        1, 1, // 1
        1, 0, // 2
        0, 0, // 3
        0.5f, 0.5f // 4 peak
    );

    getFaces().addAll(0, 0, 1, 1, 4, 4, // y- face
        1, 1, 2, 2, 4, 4, // x+ face
        2, 2, 3, 3, 4, 4, // y+ face
        3, 3, 0, 0, 4, 4, // x- face

        3, 0, 1, 2, 0, 3, // base face
        2, 1, 1, 2, 3, 0 // base face

    );
  }
}
