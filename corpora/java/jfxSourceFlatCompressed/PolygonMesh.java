package com.javafx.experiments.shape3d;
import javafx.collections.FXCollections;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
public class PolygonMesh {
private final ObservableFloatArray points = FXCollections.observableFloatArray();
private final ObservableFloatArray texCoords = FXCollections.observableFloatArray();
public int[][] faces = new int[0][0];
private final ObservableIntegerArray faceSmoothingGroups = FXCollections.observableIntegerArray();
protected int numEdgesInFaces = -1;
public PolygonMesh() {}
public PolygonMesh(float[] points, float[] texCoords, int[][] faces) {
this.points.addAll(points);
this.texCoords.addAll(texCoords);
this.faces = faces;
}
public ObservableFloatArray getPoints() {
return points;
}
public ObservableFloatArray getTexCoords() {
return texCoords;
}
public ObservableIntegerArray getFaceSmoothingGroups() {
return faceSmoothingGroups;
}
public int getNumEdgesInFaces() {
if (numEdgesInFaces == -1) {
numEdgesInFaces = 0;
for(int[] face : faces) {
numEdgesInFaces += face.length;
}
numEdgesInFaces /= 2;
}
return numEdgesInFaces;
}
private static final int NUM_COMPONENTS_PER_POINT = 3;
private static final int NUM_COMPONENTS_PER_TEXCOORD = 2;
private static final int NUM_COMPONENTS_PER_FACE = 6;
public int getPointElementSize() {
return NUM_COMPONENTS_PER_POINT;
}
public int getTexCoordElementSize() {
return NUM_COMPONENTS_PER_TEXCOORD;
}
public int getFaceElementSize() {
return NUM_COMPONENTS_PER_FACE;
}
}
