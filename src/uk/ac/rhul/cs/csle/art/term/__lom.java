package uk.ac.rhul.cs.csle.art.term;

import uk.ac.rhul.cs.csle.art.term.mesh.LOM;

public class __lom extends Value {
  final LOM javaValue;

  @Override
  public LOM javaValue() {
    return javaValue;
  }

  // A lom expects to receive two paths, represented by lists of 3-lists
  public __lom(Value basePath, Value extrusionPath) {
    System.out.println("__lom constructor called with basePath " + basePath + " and extrusionPath " + extrusionPath);
    // Type check
    if (!(basePath instanceof __list)) throw new ValueException("__lom basePath must be a list of 3-lists of number");

    int basePathLength = ((int) basePath.__size().javaValue());
    int extrusionPathLength = ((int) extrusionPath.__size().javaValue());

    // float[] basePathArray = new float[basePathLength * 3];
    // float[] extrusionPathArray = new float[extrusionPathLength * 3];

    float[] basePathArray = { 0f, 0f, 0f, 300f, 0f, 0f, 300f, 50f, 0f, 0f, 50f, 0f };
    float[] extrusionPathArray = { 0f, 0f, 90f };

    javaValue = new LOM(basePathArray, extrusionPathArray);
  }
}
