package com.sun.javafx.sg.prism;
public class NGSphere extends NGShape3D {
public void updateMesh(NGTriangleMesh mesh) {
this.mesh = mesh;
invalidate();
}
}
