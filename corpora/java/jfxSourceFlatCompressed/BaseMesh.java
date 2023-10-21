package com.sun.prism.impl;
import com.sun.javafx.geom.Quat4f;
import com.sun.javafx.geom.Vec2f;
import com.sun.javafx.geom.Vec3f;
import com.sun.prism.Mesh;
import java.util.Arrays;
import java.util.HashMap;
import javafx.scene.shape.VertexFormat;
import com.sun.javafx.logging.PlatformLogger;
public abstract class BaseMesh extends BaseGraphicsResource implements Mesh {
private int nVerts;
private int nTVerts;
private int nFaces;
private float[] pos;
private float[] uv;
private int[] faces;
private int[] smoothing;
private boolean allSameSmoothing;
private boolean allHardEdges;
protected static final int POINT_SIZE = 3;
protected static final int NORMAL_SIZE = 3;
protected static final int TEXCOORD_SIZE = 2;
protected static final int POINT_SIZE_VB = 3;
protected static final int TEXCOORD_SIZE_VB = 2;
protected static final int NORMAL_SIZE_VB = 4;
protected static final int VERTEX_SIZE_VB = 9;
public static enum FaceMembers {
POINT0, TEXCOORD0, POINT1, TEXCOORD1, POINT2, TEXCOORD2, SMOOTHING_GROUP
};
public static final int FACE_MEMBERS_SIZE = 7;
protected BaseMesh(Disposer.Record disposerRecord) {
super(disposerRecord);
}
public abstract boolean buildNativeGeometry(float[] vertexBuffer,
int vertexBufferLength, int[] indexBufferInt, int indexBufferLength);
public abstract boolean buildNativeGeometry(float[] vertexBuffer,
int vertexBufferLength, short[] indexBufferShort, int indexBufferLength);
private boolean[] dirtyVertices;
private float[] cachedNormals;
private float[] cachedTangents;
private float[] cachedBitangents;
private float[] vertexBuffer;
private int[] indexBuffer;
private short[] indexBufferShort;
private int indexBufferSize;
private int numberOfVertices;
private HashMap<Integer, MeshGeomComp2VB> point2vbMap;
private HashMap<Integer, MeshGeomComp2VB> normal2vbMap;
private HashMap<Integer, MeshGeomComp2VB> texCoord2vbMap;
private void convertNormalsToQuats(MeshTempState instance, int numberOfVertices,
float[] normals, float[] tangents, float[] bitangents,
float[] vertexBuffer, boolean[] dirtys) {
Vec3f normal = instance.vec3f1;
Vec3f tangent = instance.vec3f2;
Vec3f bitangent = instance.vec3f3;
for (int i = 0, vbIndex = 0; i < numberOfVertices; i++, vbIndex += VERTEX_SIZE_VB) {
if (dirtys == null || dirtys[i]) {
int index = i * NORMAL_SIZE;
normal.x = normals[index];
normal.y = normals[index + 1];
normal.z = normals[index + 2];
normal.normalize();
tangent.x = tangents[index];
tangent.y = tangents[index + 1];
tangent.z = tangents[index + 2];
bitangent.x = bitangents[index];
bitangent.y = bitangents[index + 1];
bitangent.z = bitangents[index + 2];
instance.triNormals[0].set(normal);
instance.triNormals[1].set(tangent);
instance.triNormals[2].set(bitangent);
MeshUtil.fixTSpace(instance.triNormals);
buildVSQuat(instance.triNormals, instance.quat);
vertexBuffer[vbIndex + 5] = instance.quat.x;
vertexBuffer[vbIndex + 6] = instance.quat.y;
vertexBuffer[vbIndex + 7] = instance.quat.z;
vertexBuffer[vbIndex + 8] = instance.quat.w;
}
}
}
private boolean doBuildPNTGeometry(float[] points, float[] normals,
float[] texCoords, int[] faces) {
if (point2vbMap == null) {
point2vbMap = new HashMap();
} else {
point2vbMap.clear();
}
if (normal2vbMap == null) {
normal2vbMap = new HashMap();
} else {
normal2vbMap.clear();
}
if (texCoord2vbMap == null) {
texCoord2vbMap = new HashMap();
} else {
texCoord2vbMap.clear();
}
int vertexIndexSize = VertexFormat.POINT_NORMAL_TEXCOORD.getVertexIndexSize();
int faceIndexSize = vertexIndexSize * 3;
int pointIndexOffset = VertexFormat.POINT_NORMAL_TEXCOORD.getPointIndexOffset();
int normalIndexOffset = VertexFormat.POINT_NORMAL_TEXCOORD.getNormalIndexOffset();
int texCoordIndexOffset = VertexFormat.POINT_NORMAL_TEXCOORD.getTexCoordIndexOffset();
int numPoints = points.length / POINT_SIZE;
int numNormals = normals.length / NORMAL_SIZE;
int numTexCoords = texCoords.length / TEXCOORD_SIZE;
int numFaces = faces.length / faceIndexSize;
assert numPoints > 0 && numNormals > 0 && numTexCoords > 0 && numFaces > 0;
Integer mf2vb;
BaseMesh.MeshGeomComp2VB mp2vb;
BaseMesh.MeshGeomComp2VB mn2vb;
BaseMesh.MeshGeomComp2VB mt2vb;
cachedNormals = new float[numPoints * NORMAL_SIZE];
cachedTangents = new float[numPoints * NORMAL_SIZE];
cachedBitangents = new float[numPoints * NORMAL_SIZE];
vertexBuffer = new float[numPoints * VERTEX_SIZE_VB];
indexBuffer = new int[numFaces * 3];
int ibCount = 0;
int vbCount = 0;
MeshTempState instance = MeshTempState.getInstance();
for (int i = 0; i < 3; i++) {
if (instance.triPoints[i] == null) {
instance.triPoints[i] = new Vec3f();
}
if (instance.triTexCoords[i] == null) {
instance.triTexCoords[i] = new Vec2f();
}
}
for (int faceCount = 0; faceCount < numFaces; faceCount++) {
int faceIndex = faceCount * faceIndexSize;
for (int i = 0; i < 3; i++) {
int vertexIndex = faceIndex + (i * vertexIndexSize);
int pointIndex = vertexIndex + pointIndexOffset;
int normalIndex = vertexIndex + normalIndexOffset;
int texCoordIndex = vertexIndex + texCoordIndexOffset;
mf2vb = vbCount / VERTEX_SIZE_VB;
if (vertexBuffer.length <= vbCount) {
int numVertices = vbCount / VERTEX_SIZE_VB;
final int newNumVertices = numVertices + Math.max((numVertices >> 3), 6);
float[] temp = new float[newNumVertices * VERTEX_SIZE_VB];
System.arraycopy(vertexBuffer, 0, temp, 0, vertexBuffer.length);
vertexBuffer = temp;
temp = new float[newNumVertices * 3];
System.arraycopy(cachedNormals, 0, temp, 0, cachedNormals.length);
cachedNormals = temp;
temp = new float[newNumVertices * 3];
System.arraycopy(cachedTangents, 0, temp, 0, cachedTangents.length);
cachedTangents = temp;
temp = new float[newNumVertices * 3];
System.arraycopy(cachedBitangents, 0, temp, 0, cachedBitangents.length);
cachedBitangents = temp;
}
int pointOffset = faces[pointIndex] * POINT_SIZE;
int normalOffset = faces[normalIndex] * NORMAL_SIZE;
int texCoordOffset = faces[texCoordIndex] * TEXCOORD_SIZE;
instance.triPointIndex[i] = pointOffset;
instance.triTexCoordIndex[i] = texCoordOffset;
instance.triVerts[i] = vbCount / VERTEX_SIZE_VB;
vertexBuffer[vbCount] = points[pointOffset];
vertexBuffer[vbCount + 1] = points[pointOffset + 1];
vertexBuffer[vbCount + 2] = points[pointOffset + 2];
vertexBuffer[vbCount + 3] = texCoords[texCoordOffset];
vertexBuffer[vbCount + 4] = texCoords[texCoordOffset + 1];
int index = instance.triVerts[i] * NORMAL_SIZE;
cachedNormals[index] = normals[normalOffset];
cachedNormals[index + 1] = normals[normalOffset + 1];
cachedNormals[index + 2] = normals[normalOffset + 2];
vbCount += VERTEX_SIZE_VB;
mp2vb = point2vbMap.get(pointOffset);
if (mp2vb == null) {
mp2vb = new MeshGeomComp2VB(pointOffset, mf2vb);
point2vbMap.put(pointOffset, mp2vb);
} else {
mp2vb.addLoc(mf2vb);
}
mn2vb = normal2vbMap.get(normalOffset);
if (mn2vb == null) {
mn2vb = new MeshGeomComp2VB(normalOffset, mf2vb);
normal2vbMap.put(normalOffset, mn2vb);
} else {
mn2vb.addLoc(mf2vb);
}
mt2vb = texCoord2vbMap.get(texCoordOffset);
if (mt2vb == null) {
mt2vb = new MeshGeomComp2VB(texCoordOffset, mf2vb);
texCoord2vbMap.put(texCoordOffset, mt2vb);
} else {
mt2vb.addLoc(mf2vb);
}
indexBuffer[ibCount++] = mf2vb;
}
for (int i = 0; i < 3; i++) {
instance.triPoints[i].x = points[instance.triPointIndex[i]];
instance.triPoints[i].y = points[instance.triPointIndex[i] + 1];
instance.triPoints[i].z = points[instance.triPointIndex[i] + 2];
instance.triTexCoords[i].x = texCoords[instance.triTexCoordIndex[i]];
instance.triTexCoords[i].y = texCoords[instance.triTexCoordIndex[i] + 1];
}
MeshUtil.computeTBNNormalized(instance.triPoints[0], instance.triPoints[1],
instance.triPoints[2], instance.triTexCoords[0],
instance.triTexCoords[1], instance.triTexCoords[2],
instance.triNormals);
for (int i = 0; i < 3; i++) {
int index = instance.triVerts[i] * NORMAL_SIZE;
cachedTangents[index] = instance.triNormals[1].x;
cachedTangents[index + 1] = instance.triNormals[1].y;
cachedTangents[index + 2] = instance.triNormals[1].z;
cachedBitangents[index] = instance.triNormals[2].x;
cachedBitangents[index + 1] = instance.triNormals[2].y;
cachedBitangents[index + 2] = instance.triNormals[2].z;
}
}
numberOfVertices = vbCount / VERTEX_SIZE_VB;
convertNormalsToQuats(instance, numberOfVertices,
cachedNormals, cachedTangents, cachedBitangents, vertexBuffer, null);
indexBufferSize = numFaces * 3;
if (numberOfVertices > 0x10000) {
return buildNativeGeometry(vertexBuffer,
numberOfVertices * VERTEX_SIZE_VB, indexBuffer, indexBufferSize);
} else {
if (indexBufferShort == null || indexBufferShort.length < indexBufferSize) {
indexBufferShort = new short[indexBufferSize];
}
int ii = 0;
for (int i = 0; i < numFaces; i++) {
indexBufferShort[ii] = (short) indexBuffer[ii++];
indexBufferShort[ii] = (short) indexBuffer[ii++];
indexBufferShort[ii] = (short) indexBuffer[ii++];
}
indexBuffer = null;
return buildNativeGeometry(vertexBuffer,
numberOfVertices * VERTEX_SIZE_VB, indexBufferShort, indexBufferSize);
}
}
private boolean updatePNTGeometry(float[] points, int[] pointsFromAndLengthIndices,
float[] normals, int[] normalsFromAndLengthIndices,
float[] texCoords, int[] texCoordsFromAndLengthIndices) {
if (dirtyVertices == null) {
dirtyVertices = new boolean[numberOfVertices];
}
Arrays.fill(dirtyVertices, false);
int startPoint = pointsFromAndLengthIndices[0] / POINT_SIZE;
int numPoints = (pointsFromAndLengthIndices[1] / POINT_SIZE);
if ((pointsFromAndLengthIndices[1] % POINT_SIZE) > 0) {
numPoints++;
}
if (numPoints > 0) {
for (int i = 0; i < numPoints; i++) {
int pointOffset = (startPoint + i) * POINT_SIZE;
MeshGeomComp2VB mp2vb = (MeshGeomComp2VB) point2vbMap.get(pointOffset);
assert mp2vb != null;
if (mp2vb != null) {
int[] locs = mp2vb.getLocs();
int validLocs = mp2vb.getValidLocs();
if (locs != null) {
for (int j = 0; j < validLocs; j++) {
int vbIndex = locs[j] * VERTEX_SIZE_VB;
vertexBuffer[vbIndex] = points[pointOffset];
vertexBuffer[vbIndex + 1] = points[pointOffset + 1];
vertexBuffer[vbIndex + 2] = points[pointOffset + 2];
dirtyVertices[locs[j]] = true;
}
} else {
int loc = mp2vb.getLoc();
int vbIndex = loc * VERTEX_SIZE_VB;
vertexBuffer[vbIndex] = points[pointOffset];
vertexBuffer[vbIndex + 1] = points[pointOffset + 1];
vertexBuffer[vbIndex + 2] = points[pointOffset + 2];
dirtyVertices[loc] = true;
}
}
}
}
int startTexCoord = texCoordsFromAndLengthIndices[0] / TEXCOORD_SIZE;
int numTexCoords = (texCoordsFromAndLengthIndices[1] / TEXCOORD_SIZE);
if ((texCoordsFromAndLengthIndices[1] % TEXCOORD_SIZE) > 0) {
numTexCoords++;
}
if (numTexCoords > 0) {
for (int i = 0; i < numTexCoords; i++) {
int texCoordOffset = (startTexCoord + i) * TEXCOORD_SIZE;
MeshGeomComp2VB mt2vb = (MeshGeomComp2VB) texCoord2vbMap.get(texCoordOffset);
assert mt2vb != null;
if (mt2vb != null) {
int[] locs = mt2vb.getLocs();
int validLocs = mt2vb.getValidLocs();
if (locs != null) {
for (int j = 0; j < validLocs; j++) {
int vbIndex = (locs[j] * VERTEX_SIZE_VB) + POINT_SIZE_VB;
vertexBuffer[vbIndex] = texCoords[texCoordOffset];
vertexBuffer[vbIndex + 1] = texCoords[texCoordOffset + 1];
dirtyVertices[locs[j]] = true;
}
} else {
int loc = mt2vb.getLoc();
int vbIndex = (loc * VERTEX_SIZE_VB) + POINT_SIZE_VB;
vertexBuffer[vbIndex] = texCoords[texCoordOffset];
vertexBuffer[vbIndex + 1] = texCoords[texCoordOffset + 1];
dirtyVertices[loc] = true;
}
}
}
}
int startNormal = normalsFromAndLengthIndices[0] / NORMAL_SIZE;
int numNormals = (normalsFromAndLengthIndices[1] / NORMAL_SIZE);
if ((normalsFromAndLengthIndices[1] % NORMAL_SIZE) > 0) {
numNormals++;
}
if (numNormals > 0) {
MeshTempState instance = MeshTempState.getInstance();
for (int i = 0; i < numNormals; i++) {
int normalOffset = (startNormal + i) * NORMAL_SIZE;
MeshGeomComp2VB mn2vb = (MeshGeomComp2VB) normal2vbMap.get(normalOffset);
assert mn2vb != null;
if (mn2vb != null) {
int[] locs = mn2vb.getLocs();
int validLocs = mn2vb.getValidLocs();
if (locs != null) {
for (int j = 0; j < validLocs; j++) {
int index = locs[j] * NORMAL_SIZE;
cachedNormals[index] = normals[normalOffset];
cachedNormals[index + 1] = normals[normalOffset + 1];
cachedNormals[index + 2] = normals[normalOffset + 2];
dirtyVertices[locs[j]] = true;
}
} else {
int loc = mn2vb.getLoc();
int index = loc * NORMAL_SIZE;
cachedNormals[index] = normals[normalOffset];
cachedNormals[index + 1] = normals[normalOffset + 1];
cachedNormals[index + 2] = normals[normalOffset + 2];
dirtyVertices[loc] = true;
}
}
}
}
MeshTempState instance = MeshTempState.getInstance();
for (int i = 0; i < 3; i++) {
if (instance.triPoints[i] == null) {
instance.triPoints[i] = new Vec3f();
}
if (instance.triTexCoords[i] == null) {
instance.triTexCoords[i] = new Vec2f();
}
}
for (int j = 0; j < numberOfVertices; j += 3) {
if (dirtyVertices[j] || dirtyVertices[j+1] || dirtyVertices[j+2]) {
int vbIndex = j * VERTEX_SIZE_VB;
for (int i = 0; i < 3; i++) {
instance.triPoints[i].x = vertexBuffer[vbIndex];
instance.triPoints[i].y = vertexBuffer[vbIndex + 1];
instance.triPoints[i].z = vertexBuffer[vbIndex + 2];
instance.triTexCoords[i].x = vertexBuffer[vbIndex + POINT_SIZE_VB];
instance.triTexCoords[i].y = vertexBuffer[vbIndex + POINT_SIZE_VB + 1];
vbIndex += VERTEX_SIZE_VB;
}
MeshUtil.computeTBNNormalized(instance.triPoints[0], instance.triPoints[1],
instance.triPoints[2], instance.triTexCoords[0],
instance.triTexCoords[1], instance.triTexCoords[2],
instance.triNormals);
int index = j * NORMAL_SIZE;
for (int i = 0; i < 3; i++) {
cachedTangents[index] = instance.triNormals[1].x;
cachedTangents[index + 1] = instance.triNormals[1].y;
cachedTangents[index + 2] = instance.triNormals[1].z;
cachedBitangents[index] = instance.triNormals[2].x;
cachedBitangents[index + 1] = instance.triNormals[2].y;
cachedBitangents[index + 2] = instance.triNormals[2].z;
index += NORMAL_SIZE;
}
}
}
convertNormalsToQuats(instance, numberOfVertices,
cachedNormals, cachedTangents, cachedBitangents, vertexBuffer, dirtyVertices);
if (indexBuffer != null) {
return buildNativeGeometry(vertexBuffer,
numberOfVertices * VERTEX_SIZE_VB, indexBuffer, indexBufferSize);
} else {
return buildNativeGeometry(vertexBuffer,
numberOfVertices * VERTEX_SIZE_VB, indexBufferShort, indexBufferSize);
}
}
@Override
public boolean buildGeometry(boolean userDefinedNormals,
float[] points, int[] pointsFromAndLengthIndices,
float[] normals, int[] normalsFromAndLengthIndices,
float[] texCoords, int[] texCoordsFromAndLengthIndices,
int[] faces, int[] facesFromAndLengthIndices,
int[] faceSmoothingGroups, int[] faceSmoothingGroupsFromAndLengthIndices) {
if (userDefinedNormals) {
return buildPNTGeometry(points, pointsFromAndLengthIndices,
normals, normalsFromAndLengthIndices,
texCoords, texCoordsFromAndLengthIndices,
faces, facesFromAndLengthIndices);
} else {
return buildPTGeometry(points, texCoords, faces, faceSmoothingGroups);
}
}
private boolean buildPNTGeometry(
float[] points, int[] pointsFromAndLengthIndices,
float[] normals, int[] normalsFromAndLengthIndices,
float[] texCoords, int[] texCoordsFromAndLengthIndices,
int[] faces, int[] facesFromAndLengthIndices) {
boolean updatePoints = pointsFromAndLengthIndices[1] > 0;
boolean updateNormals = normalsFromAndLengthIndices[1] > 0;
boolean updateTexCoords = texCoordsFromAndLengthIndices[1] > 0;
boolean updateFaces = facesFromAndLengthIndices[1] > 0;
boolean buildGeom = !(updatePoints || updateNormals || updateTexCoords || updateFaces);
if (updateFaces) {
buildGeom = true;
}
if ((!buildGeom) && (vertexBuffer != null)
&& ((indexBuffer != null) || (indexBufferShort != null))) {
return updatePNTGeometry(points, pointsFromAndLengthIndices,
normals, normalsFromAndLengthIndices,
texCoords, texCoordsFromAndLengthIndices);
}
return doBuildPNTGeometry(points, normals, texCoords, faces);
}
private boolean buildPTGeometry(float[] pos, float[] uv, int[] faces, int[] smoothing) {
nVerts = pos.length / 3;
nTVerts = uv.length / 2;
nFaces = faces.length / (VertexFormat.POINT_TEXCOORD.getVertexIndexSize() * 3);
assert nVerts > 0 && nFaces > 0 && nTVerts > 0;
this.pos = pos;
this.uv = uv;
this.faces = faces;
this.smoothing = smoothing.length == nFaces ? smoothing : null;
MeshTempState instance = MeshTempState.getInstance();
if (instance.pool == null || instance.pool.length < nFaces * 3) {
instance.pool = new MeshVertex[nFaces * 3];
}
if (instance.indexBuffer == null || instance.indexBuffer.length < nFaces * 3) {
instance.indexBuffer = new int[nFaces * 3];
}
if (instance.pVertex == null || instance.pVertex.length < nVerts) {
instance.pVertex = new MeshVertex[nVerts];
} else {
Arrays.fill(instance.pVertex, 0, instance.pVertex.length, null);
}
checkSmoothingGroup();
computeTBNormal(instance.pool, instance.pVertex, instance.indexBuffer);
int nNewVerts = MeshVertex.processVertices(instance.pVertex, nVerts,
allHardEdges, allSameSmoothing);
if (instance.vertexBuffer == null
|| instance.vertexBuffer.length < nNewVerts * VERTEX_SIZE_VB) {
instance.vertexBuffer = new float[nNewVerts * VERTEX_SIZE_VB];
}
buildVertexBuffer(instance.pVertex, instance.vertexBuffer);
if (nNewVerts > 0x10000) {
buildIndexBuffer(instance.pool, instance.indexBuffer, null);
return buildNativeGeometry(instance.vertexBuffer,
nNewVerts * VERTEX_SIZE_VB, instance.indexBuffer, nFaces * 3);
} else {
if (instance.indexBufferShort == null || instance.indexBufferShort.length < nFaces * 3) {
instance.indexBufferShort = new short[nFaces * 3];
}
buildIndexBuffer(instance.pool, instance.indexBuffer, instance.indexBufferShort);
return buildNativeGeometry(instance.vertexBuffer,
nNewVerts * VERTEX_SIZE_VB, instance.indexBufferShort, nFaces * 3);
}
}
private void computeTBNormal(MeshVertex[] pool, MeshVertex[] pVertex, int[] indexBuffer) {
MeshTempState instance = MeshTempState.getInstance();
int[] smFace = instance.smFace;
int[] triVerts = instance.triVerts;
Vec3f[] triPoints = instance.triPoints;
Vec2f[] triTexCoords = instance.triTexCoords;
Vec3f[] triNormals = instance.triNormals;
final String logname = BaseMesh.class.getName();
for (int f = 0, nDeadFaces = 0, poolIndex = 0; f < nFaces; f++) {
int index = f * 3;
smFace = getFace(f, smFace);
triVerts[0] = smFace[BaseMesh.FaceMembers.POINT0.ordinal()];
triVerts[1] = smFace[BaseMesh.FaceMembers.POINT1.ordinal()];
triVerts[2] = smFace[BaseMesh.FaceMembers.POINT2.ordinal()];
if (MeshUtil.isDeadFace(triVerts)
&& PlatformLogger.getLogger(logname).isLoggable(PlatformLogger.Level.FINE)) {
nDeadFaces++;
PlatformLogger.getLogger(logname).fine("Dead face ["
+ triVerts[0] + ", " + triVerts[1] + ", " + triVerts[2]
+ "] @ face group " + f + "; nEmptyFaces = " + nDeadFaces);
}
for (int i = 0; i < 3; i++) {
triPoints[i] = getVertex(triVerts[i], triPoints[i]);
}
triVerts[0] = smFace[BaseMesh.FaceMembers.TEXCOORD0.ordinal()];
triVerts[1] = smFace[BaseMesh.FaceMembers.TEXCOORD1.ordinal()];
triVerts[2] = smFace[BaseMesh.FaceMembers.TEXCOORD2.ordinal()];
for (int i = 0; i < 3; i++) {
triTexCoords[i] = getTVertex(triVerts[i], triTexCoords[i]);
}
MeshUtil.computeTBNNormalized(triPoints[0], triPoints[1], triPoints[2],
triTexCoords[0], triTexCoords[1], triTexCoords[2],
triNormals);
for (int j = 0; j < 3; ++j) {
pool[poolIndex] = (pool[poolIndex] == null) ? new MeshVertex() : pool[poolIndex];
for (int i = 0; i < 3; ++i) {
pool[poolIndex].norm[i].set(triNormals[i]);
}
pool[poolIndex].smGroup = smFace[BaseMesh.FaceMembers.SMOOTHING_GROUP.ordinal()];
pool[poolIndex].fIdx = f;
pool[poolIndex].tVert = triVerts[j];
pool[poolIndex].index = MeshVertex.IDX_UNDEFINED;
int ii = j == 0 ? BaseMesh.FaceMembers.POINT0.ordinal()
: j == 1 ? BaseMesh.FaceMembers.POINT1.ordinal()
: BaseMesh.FaceMembers.POINT2.ordinal();
int pIdx = smFace[ii];
pool[poolIndex].pVert = pIdx;
indexBuffer[index + j] = pIdx;
pool[poolIndex].next = pVertex[pIdx];
pVertex[pIdx] = pool[poolIndex];
poolIndex++;
}
}
}
private void buildVSQuat(Vec3f[] tm, Quat4f quat) {
Vec3f v = MeshTempState.getInstance().vec3f1;
v.cross(tm[1], tm[2]);
float d = tm[0].dot(v);
if (d < 0) {
tm[2].mul(-1);
}
MeshUtil.buildQuat(tm, quat);
if (d < 0) {
if (quat.w == 0) {
quat.w = MeshUtil.MAGIC_SMALL;
}
quat.scale(-1);
}
}
private void buildVertexBuffer(MeshVertex[] pVerts, float[] vertexBuffer) {
Quat4f quat = MeshTempState.getInstance().quat;
int idLast = 0;
for (int i = 0, index = 0; i < nVerts; ++i) {
MeshVertex v = pVerts[i];
for (; v != null; v = v.next) {
if (v.index == idLast) {
int ind = v.pVert * 3;
vertexBuffer[index++] = pos[ind];
vertexBuffer[index++] = pos[ind + 1];
vertexBuffer[index++] = pos[ind + 2];
ind = v.tVert * 2;
vertexBuffer[index++] = uv[ind];
vertexBuffer[index++] = uv[ind + 1];
buildVSQuat(v.norm, quat);
vertexBuffer[index++] = quat.x;
vertexBuffer[index++] = quat.y;
vertexBuffer[index++] = quat.z;
vertexBuffer[index++] = quat.w;
idLast++;
}
}
}
}
private void buildIndexBuffer(MeshVertex[] pool, int[] indexBuffer, short[] indexBufferShort) {
for (int i = 0; i < nFaces; ++i) {
int index = i * 3;
if (indexBuffer[index] != MeshVertex.IDX_UNDEFINED) {
for (int j = 0; j < 3; ++j) {
assert (pool[index].fIdx == i);
if (indexBufferShort != null) {
indexBufferShort[index + j] = (short) pool[index + j].index;
} else {
indexBuffer[index + j] = pool[index + j].index;
}
pool[index + j].next = null;
}
} else {
for (int j = 0; j < 3; ++j) {
if (indexBufferShort != null) {
indexBufferShort[index + j] = 0;
} else {
indexBuffer[index + j] = 0;
}
}
}
}
}
public int getNumVerts() {
return nVerts;
}
public int getNumTVerts() {
return nTVerts;
}
public int getNumFaces() {
return nFaces;
}
public Vec3f getVertex(int pIdx, Vec3f vertex) {
if (vertex == null) {
vertex = new Vec3f();
}
int index = pIdx * 3;
vertex.set(pos[index], pos[index + 1], pos[index + 2]);
return vertex;
}
public Vec2f getTVertex(int tIdx, Vec2f texCoord) {
if (texCoord == null) {
texCoord = new Vec2f();
}
int index = tIdx * 2;
texCoord.set(uv[index], uv[index + 1]);
return texCoord;
}
private void checkSmoothingGroup() {
if (smoothing == null || smoothing.length == 0) {
allSameSmoothing = true;
allHardEdges = false;
return;
}
for (int i = 0; i + 1 < smoothing.length; i++) {
if (smoothing[i] != smoothing[i + 1]) {
allSameSmoothing = false;
allHardEdges = false;
return;
}
}
if (smoothing[0] == 0) {
allSameSmoothing = false;
allHardEdges = true;
} else {
allSameSmoothing = true;
allHardEdges = false;
}
}
public int[] getFace(int fIdx, int[] face) {
int index = fIdx * 6;
if ((face == null) || (face.length < FACE_MEMBERS_SIZE)) {
face = new int[FACE_MEMBERS_SIZE];
}
for (int i = 0; i < 6; i++) {
face[i] = faces[index + i];
}
face[6] = smoothing != null ? smoothing[fIdx] : 1;
return face;
}
@Override
public boolean isValid() {
return true;
}
boolean test_isVertexBufferNull() {
return vertexBuffer == null;
}
int test_getVertexBufferLength() {
return vertexBuffer.length;
}
int test_getNumberOfVertices() {
return numberOfVertices;
}
class MeshGeomComp2VB {
private final int key;
private final int loc;
private int[] locs;
private int validLocs;
MeshGeomComp2VB(int key, int loc) {
assert loc >= 0;
this.key = key;
this.loc = loc;
locs = null;
validLocs = 0;
}
void addLoc(int loc) {
if (locs == null) {
locs = new int[3];
locs[0] = this.loc;
locs[1] = loc;
this.validLocs = 2;
} else if (locs.length > validLocs) {
locs[validLocs] = loc;
validLocs++;
} else {
int[] temp = new int[validLocs * 2];
System.arraycopy(locs, 0, temp, 0, locs.length);
locs = temp;
locs[validLocs] = loc;
validLocs++;
}
}
int getKey() {
return key;
}
int getLoc() {
return loc;
}
int[] getLocs() {
return locs;
}
int getValidLocs() {
return validLocs;
}
}
}
