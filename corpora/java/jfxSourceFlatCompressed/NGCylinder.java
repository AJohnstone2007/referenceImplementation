package com.sun.javafx.sg.prism;
public class NGCylinder extends NGShape3D {
public void updateMesh(NGTriangleMesh mesh) {
this.mesh = mesh;
invalidate();
}
}
