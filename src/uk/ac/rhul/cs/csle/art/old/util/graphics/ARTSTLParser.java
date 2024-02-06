package uk.ac.rhul.cs.csle.art.old.util.graphics;

public abstract class ARTSTLParser {
  public abstract void readFacet(ARTCoord normal, ARTCoord vertex1, ARTCoord vertex2, ARTCoord vertex3);

  public abstract int getFacetCount();
}
