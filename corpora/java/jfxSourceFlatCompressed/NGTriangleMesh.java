package com.sun.javafx.sg.prism;
import com.sun.javafx.collections.FloatArraySyncer;
import com.sun.javafx.collections.IntegerArraySyncer;
import com.sun.prism.Mesh;
import com.sun.prism.ResourceFactory;
public class NGTriangleMesh {
private boolean meshDirty = true;
private Mesh mesh;
private boolean userDefinedNormals = false;
private float[] points;
private int[] pointsFromAndLengthIndices = new int[2];
private float[] normals;
private int[] normalsFromAndLengthIndices = new int[2];
private float[] texCoords;
private int[] texCoordsFromAndLengthIndices = new int[2];
private int[] faces;
private int[] facesFromAndLengthIndices = new int[2];
private int[] faceSmoothingGroups;
private int[] faceSmoothingGroupsFromAndLengthIndices = new int[2];
Mesh createMesh(ResourceFactory rf) {
if (mesh != null && !mesh.isValid()) {
mesh.dispose();
mesh = null;
}
if (mesh == null) {
mesh = rf.createMesh();
meshDirty = true;
}
return mesh;
}
boolean validate() {
if (points == null || texCoords == null || faces == null || faceSmoothingGroups == null
|| (userDefinedNormals && (normals == null))) {
return false;
}
if (meshDirty) {
if (!mesh.buildGeometry(userDefinedNormals,
points, pointsFromAndLengthIndices,
normals, normalsFromAndLengthIndices,
texCoords, texCoordsFromAndLengthIndices,
faces, facesFromAndLengthIndices,
faceSmoothingGroups, faceSmoothingGroupsFromAndLengthIndices)) {
throw new RuntimeException("NGTriangleMesh: buildGeometry failed");
}
meshDirty = false;
}
return true;
}
void setPointsByRef(float[] points) {
meshDirty = true;
this.points = points;
}
void setNormalsByRef(float[] normals) {
meshDirty = true;
this.normals = normals;
}
void setTexCoordsByRef(float[] texCoords) {
meshDirty = true;
this.texCoords = texCoords;
}
void setFacesByRef(int[] faces) {
meshDirty = true;
this.faces = faces;
}
void setFaceSmoothingGroupsByRef(int[] faceSmoothingGroups) {
meshDirty = true;
this.faceSmoothingGroups = faceSmoothingGroups;
}
public void setUserDefinedNormals(boolean userDefinedNormals) {
this.userDefinedNormals = userDefinedNormals;
}
public boolean isUserDefinedNormals() {
return userDefinedNormals;
}
public void syncPoints(FloatArraySyncer array) {
meshDirty = true;
points = array != null ? array.syncTo(points, pointsFromAndLengthIndices) : null;
}
public void syncNormals(FloatArraySyncer array) {
meshDirty = true;
normals = array != null ? array.syncTo(normals, normalsFromAndLengthIndices) : null;
}
public void syncTexCoords(FloatArraySyncer array) {
meshDirty = true;
texCoords = array != null ? array.syncTo(texCoords, texCoordsFromAndLengthIndices) : null;
}
public void syncFaces(IntegerArraySyncer array) {
meshDirty = true;
faces = array != null ? array.syncTo(faces, facesFromAndLengthIndices) : null;
}
public void syncFaceSmoothingGroups(IntegerArraySyncer array) {
meshDirty = true;
faceSmoothingGroups = array != null ? array.syncTo(faceSmoothingGroups, faceSmoothingGroupsFromAndLengthIndices) : null;
}
int[] test_getFaceSmoothingGroups() {
return this.faceSmoothingGroups;
}
int[] test_getFaces() {
return this.faces;
}
float[] test_getPoints() {
return this.points;
}
float[] test_getNormals() {
return this.normals;
}
float[] test_getTexCoords() {
return this.texCoords;
}
Mesh test_getMesh() {
return this.mesh;
}
}
