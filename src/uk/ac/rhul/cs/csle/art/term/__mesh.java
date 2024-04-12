package uk.ac.rhul.cs.csle.art.term;

import uk.ac.rhul.cs.csle.art.term.mesh.AleroMesh;

public class __mesh extends Value {
  final AleroMesh javaValue;

  @Override
  public AleroMesh javaValue() {
    return javaValue;
  }

  // LOM constructor
  // A LOM expects to receive a basePath and an extrsionPath, represented by lists of 3-lists
  public __mesh(Value basePath, Value extrusionPath) {
    System.out.println("__lom constructor called with basePath " + basePath + " and extrusionPath " + extrusionPath);
    // Type check
    if (!(basePath instanceof __list)) throw new ValueException("__mesh basePath and extrusionPath must be a __list of __list of three numbers");

    int basePathLength = ((int) basePath.__size().javaValue());
    int extrusionPathLength = ((int) extrusionPath.__size().javaValue());

    float[] extrusionPathArray = new float[extrusionPathLength * 3];

    float[] basePathArray = new float[basePathLength * 3];

    loadArray(basePath, basePathArray);
    loadArray(extrusionPath, extrusionPathArray);

    javaValue = new AleroMesh(basePathArray, extrusionPathArray);
  }

  private void loadArray(Value pathList, float[] pathArray) {
    int pi = 0;
    for (Value p : ((__list) pathList).javaValue()) {
      if (!(p instanceof __list && ((int) p.__size().javaValue()) != 3))
        throw new ValueException("__mesh basePath and extrusionPath must be a __list of __list of three numbers");
      for (Value pe : ((__list) p).javaValue()) {
        if (!(pe.javaValue() instanceof Number)) throw new ValueException("__mesh basePath and extrusionPath must be a __list of __list of three numbers");
        pathArray[pi++] = (float) pe.javaValue();
      }
    }
  }
}