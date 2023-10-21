package com.sun.javafx.sg.prism;
import com.sun.prism.impl.BaseMesh;
public class NGTriangleMeshShim extends NGTriangleMesh {
@Override
public int[] test_getFaceSmoothingGroups() {
return super.test_getFaceSmoothingGroups();
}
@Override
public int[] test_getFaces() {
return super.test_getFaces();
}
@Override
public float[] test_getPoints() {
return super.test_getPoints();
}
@Override
public float[] test_getNormals() {
return super.test_getNormals();
}
@Override
public float[] test_getTexCoords() {
return super.test_getTexCoords();
}
public static BaseMesh test_getMesh(NGTriangleMesh triMesh) {
return (BaseMesh) triMesh.test_getMesh();
}
}
