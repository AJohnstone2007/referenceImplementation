package uk.ac.rhul.cs.csle.art.term;

import uk.ac.rhul.cs.csle.art.term.mesh.AleroMesh;
import uk.ac.rhul.cs.csle.art.term.mesh.CSG;

public class __mesh extends Value {
  final AleroMesh javaValue;

  @Override
  public AleroMesh javaValue() {
    return javaValue;
  }

  public __mesh(AleroMesh aleroMesh) {
    javaValue = aleroMesh;
  }

  // LOM constructor
  // A LOM expects to receive a basePath and an extrsionPath, represented by lists of 3-lists
  public __mesh(Value basePath, Value extrusionPath) {
    System.out.println("__lom constructor called with basePath\n" + basePath + "\nand extrusionPath\n" + extrusionPath);
    // Type check
    if (!(basePath instanceof __list)) throw new ValueException("__mesh basePath must be a __list of __list of three numbers: found " + basePath);

    int basePathLength = ((int) basePath.__size().javaValue());
    int extrusionPathLength = ((int) extrusionPath.__size().javaValue());

    float[] extrusionPathArray = new float[extrusionPathLength * 3];

    float[] basePathArray = new float[basePathLength * 3];

    loadArray(basePath, basePathArray);
    loadArray(extrusionPath, extrusionPathArray);

    javaValue = new AleroMesh(basePathArray, extrusionPathArray);
  }

  private void loadArray(Value pathList, float[] pathArray) {
    System.out.println("loadArray called with pathList " + pathList);
    int pi = 0;
    for (Value p : ((__list) pathList).javaValue()) {
      if (!(p instanceof __list && ((int) p.__size().javaValue()) == 3))
        throw new ValueException("__mesh basePath and extrusionPath must be a __list of __list of three numbers");
      for (Value pe : ((__list) p).javaValue()) {
        if (pe instanceof __real64)
          pathArray[pi++] = (float) pe.javaValue();
        else if (pe instanceof __int32)
          pathArray[pi++] = (int) pe.javaValue();
        else
          throw new ValueException("__mesh basePath and extrusionPath must be a __list of __list of three numbers");
      }
    }
  }

  @Override
  public Value __union(Value r) {
    return new __mesh(new AleroMesh(new CSG(javaValue).union(new CSG(((__mesh) r).javaValue))));
  }

  @Override
  public Value __difference(Value r) {
    return new __mesh(new AleroMesh(new CSG(javaValue).difference(new CSG(((__mesh) r).javaValue))));
  }

  @Override
  public Value __intersection(Value r) {
    return new __mesh(new AleroMesh(new CSG(javaValue).intersection(new CSG(((__mesh) r).javaValue))));
  }
}