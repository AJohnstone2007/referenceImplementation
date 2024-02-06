package uk.ac.rhul.cs.csle.art.old.util.graphics;

import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.scene.shape.ObservableFaceArray;

public class ARTGenerateMeshExtrudedPolygon extends ARTGenerateMesh {

  public ARTGenerateMeshExtrudedPolygon(float bottomOuterRadius, float topOuterRadius, float bottomInnerRadius, float topInnerRadius, float height,
      int facetMax, int facetLimit, float outerChamferRatio) {
    super();

    getTexCoords().addAll(0, 0);

    if (facetLimit > facetMax) facetLimit = facetMax;

    float facetAngle = (float) (2 * Math.PI / facetMax);
    ObservableFloatArray points = getPoints();
    ObservableFaceArray faces = getFaces();
    ObservableIntegerArray smoothingGroups = getFaceSmoothingGroups();
    float halfHeight = height / 2.0f;
    int facetBreak = facetLimit * 2 + 2;

    // First and second points are the centres of the bottom and top faces respectively - indexed as 0 and 1
    points.addAll(0, 0, -halfHeight, 0, 0, halfHeight);

    // Third and fourth points are the leading edge of the initial facet - indexed as 2 and 3
    points.addAll(0, bottomOuterRadius, -halfHeight, 0, topOuterRadius, halfHeight);

    // Now add facetCount-1 pairs of points for the top and bottom edge of each facet, creating the faces as we go in trailing order
    // We start with edge 2 (fN=4), since 'edge' 0 contains the centre points at x=y=0 and 'edge' 1 is the leasding edge at x = 0;
    float angle = facetAngle;
    int fN = 4;
    while (true) {
      System.out.println("fN = " + fN + " at angle " + angle);
      float xb = bottomOuterRadius * (float) Math.sin(angle);
      float yb = bottomOuterRadius * (float) Math.cos(angle);
      float xt = topOuterRadius * (float) Math.sin(angle);
      float yt = topOuterRadius * (float) Math.cos(angle);

      points.addAll(xb, yb, -halfHeight); // initial bottom point at index facetNumber + 4
      points.addAll(xt, yt, halfHeight); // initial top Point at at index facetNumber + 5
      // Each facet involves six points and four triangles. Let fN be the facetNumber * 2
      // Centre bottom - 0
      // Centre top - 1

      // Bottom left point - fN - 2
      // Top left point - fN - 1
      // Bottom right point - fN
      // Top right point - fN + 1

      // Taking the first facet with fN2 = 4 as an example, we have BL = 2 TL = 3 and BR = 4 and TR = 5

      // The rendered front face of a triangle is denoted by the counter-clockwise (right hand rule) winding order

      // The four triangles then are as follows. Let's normalise so that the highest point comes first. Numbers below are for the first facet
      // 1. bottom-right, bottom-left, top-left (4, 2, 3)
      faces.addAll(fN, 0, fN - 2, 0, fN - 1, 0);
      // 2. top-right, bottom-left, bottom-right (5, 4, 3)
      faces.addAll(fN + 1, 0, fN, 0, fN - 1, 0);
      // 3. bottom-left, bottom-right, bottom-centre (4, 0, 2)
      faces.addAll(fN, 0, 0, 0, fN - 2, 0);
      // 4. top-left, top-right, top-centre (5, 3, 1)
      faces.addAll(fN + 1, 0, fN - 1, 0, 1, 0);

      smoothingGroups.addAll(0, 0, 0, 0); // switch off smoothing for these four triangles

      // Note that each face has been mapped to texture coordinate 0

      if (fN >= facetBreak) break;
      fN += 2;
      angle += facetAngle;
    }

    if (facetLimit < facetMax) { // make interior facets
      faces.addAll(fN, 0, fN + 1, 0, 1, 0); // bottom-right, top-right, top-centre
      faces.addAll(fN, 0, 1, 0, 0, 0); // bottom-right, top-centre, bottom-centre
      faces.addAll(1, 0, 3, 0, 2, 0); // first-edge-bottom-right, first-edge-top-right, top-centre
      faces.addAll(0, 0, 1, 0, 2, 0); // first-edge-bottom-right, top-centre, bottom-centre
      smoothingGroups.addAll(0, 0, 0, 0); // switch off smoothing for these four triangles
    }
  }
}
