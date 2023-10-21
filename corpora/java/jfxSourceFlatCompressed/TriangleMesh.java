package javafx.scene.shape;
import com.sun.javafx.scene.shape.ObservableFaceArrayImpl;
import com.sun.javafx.collections.FloatArraySyncer;
import com.sun.javafx.collections.IntegerArraySyncer;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.BoxBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.scene.shape.TriangleMeshHelper;
import com.sun.javafx.sg.prism.NGTriangleMesh;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ArrayChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.input.PickResult;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import com.sun.javafx.logging.PlatformLogger;
public class TriangleMesh extends Mesh {
static {
TriangleMeshHelper.setTriangleMeshAccessor(new TriangleMeshHelper.TriangleMeshAccessor() {
@Override
public boolean doComputeIntersects(Mesh mesh, PickRay pickRay,
PickResultChooser pickResult, Node candidate, CullFace cullFace,
boolean reportFace) {
return ((TriangleMesh) mesh).doComputeIntersects(pickRay,
pickResult, candidate, cullFace, reportFace);
}
});
}
private final ObservableFloatArray points = FXCollections.observableFloatArray();
private final ObservableFloatArray normals = FXCollections.observableFloatArray();
private final ObservableFloatArray texCoords = FXCollections.observableFloatArray();
private final ObservableFaceArray faces = new ObservableFaceArrayImpl();
private final ObservableIntegerArray faceSmoothingGroups = FXCollections.observableIntegerArray();
private final Listener pointsSyncer = new Listener(points);
private final Listener normalsSyncer = new Listener(normals);
private final Listener texCoordsSyncer = new Listener(texCoords);
private final Listener facesSyncer = new Listener(faces);
private final Listener faceSmoothingGroupsSyncer = new Listener(faceSmoothingGroups);
private final boolean isPredefinedShape;
private boolean isValidDirty = true;
private boolean isPointsValid, isNormalsValid, isTexCoordsValid, isFacesValid, isFaceSmoothingGroupValid;
private int refCount = 1;
private BaseBounds cachedBounds;
public TriangleMesh() {
this(false);
TriangleMeshHelper.initHelper(this);
}
public TriangleMesh(VertexFormat vertexFormat) {
this(false);
this.setVertexFormat(vertexFormat);
TriangleMeshHelper.initHelper(this);
}
TriangleMesh(boolean isPredefinedShape) {
this.isPredefinedShape = isPredefinedShape;
if (isPredefinedShape) {
isPointsValid = true;
isNormalsValid = true;
isTexCoordsValid = true;
isFacesValid = true;
isFaceSmoothingGroupValid = true;
} else {
isPointsValid = false;
isNormalsValid = false;
isTexCoordsValid = false;
isFacesValid = false;
isFaceSmoothingGroupValid = false;
}
TriangleMeshHelper.initHelper(this);
}
private ObjectProperty<VertexFormat> vertexFormat;
public final void setVertexFormat(VertexFormat value) {
vertexFormatProperty().set(value);
}
public final VertexFormat getVertexFormat() {
return vertexFormat == null ? VertexFormat.POINT_TEXCOORD : vertexFormat.get();
}
public final ObjectProperty<VertexFormat> vertexFormatProperty() {
if (vertexFormat == null) {
vertexFormat = new SimpleObjectProperty<VertexFormat>(TriangleMesh.this, "vertexFormat") {
@Override
protected void invalidated() {
setDirty(true);
facesSyncer.setDirty(true);
faceSmoothingGroupsSyncer.setDirty(true);
}
};
}
return vertexFormat;
}
public final int getPointElementSize() {
return getVertexFormat().getPointElementSize();
}
public final int getNormalElementSize() {
return getVertexFormat().getNormalElementSize();
}
public final int getTexCoordElementSize() {
return getVertexFormat().getTexCoordElementSize();
}
public final int getFaceElementSize() {
return getVertexFormat().getVertexIndexSize() * 3;
}
public final ObservableFloatArray getPoints() {
return points;
}
public final ObservableFloatArray getNormals() {
return normals;
}
public final ObservableFloatArray getTexCoords() {
return texCoords;
}
public final ObservableFaceArray getFaces() {
return faces;
}
public final ObservableIntegerArray getFaceSmoothingGroups() {
return faceSmoothingGroups;
}
@Override void setDirty(boolean value) {
super.setDirty(value);
if (!value) {
pointsSyncer.setDirty(false);
normalsSyncer.setDirty(false);
texCoordsSyncer.setDirty(false);
facesSyncer.setDirty(false);
faceSmoothingGroupsSyncer.setDirty(false);
}
}
int getRefCount() {
return refCount;
}
synchronized void incRef() {
this.refCount += 1;
}
synchronized void decRef() {
this.refCount -= 1;
}
private NGTriangleMesh peer;
NGTriangleMesh getPGTriangleMesh() {
if (peer == null) {
peer = new NGTriangleMesh();
}
return peer;
}
@Override
NGTriangleMesh getPGMesh() {
return getPGTriangleMesh();
}
private boolean validatePoints() {
if (points.size() == 0) {
return false;
}
if ((points.size() % getVertexFormat().getPointElementSize()) != 0) {
String logname = TriangleMesh.class.getName();
PlatformLogger.getLogger(logname).warning("points.size() has "
+ "to be divisible by getPointElementSize(). It is to"
+ " store multiple x, y, and z coordinates of this mesh");
return false;
}
return true;
}
private boolean validateNormals() {
if (getVertexFormat() != VertexFormat.POINT_NORMAL_TEXCOORD) return true;
if (normals.size() == 0) {
return false;
}
if ((normals.size() % getVertexFormat().getNormalElementSize()) != 0) {
String logname = TriangleMesh.class.getName();
PlatformLogger.getLogger(logname).warning("normals.size() has "
+ "to be divisible by getNormalElementSize(). It is to"
+ " store multiple nx, ny, and nz coordinates of this mesh");
return false;
}
return true;
}
private boolean validateTexCoords() {
if (texCoords.size() == 0) {
return false;
}
if ((texCoords.size() % getVertexFormat().getTexCoordElementSize()) != 0) {
String logname = TriangleMesh.class.getName();
PlatformLogger.getLogger(logname).warning("texCoords.size() "
+ "has to be divisible by getTexCoordElementSize()."
+ " It is to store multiple u and v texture coordinates"
+ " of this mesh");
return false;
}
return true;
}
private boolean validateFaces() {
if (faces.size() == 0) {
return false;
}
String logname = TriangleMesh.class.getName();
if ((faces.size() % getFaceElementSize()) != 0) {
PlatformLogger.getLogger(logname).warning("faces.size() has "
+ "to be divisible by getFaceElementSize().");
return false;
}
if (getVertexFormat() == VertexFormat.POINT_TEXCOORD) {
int nVerts = points.size() / getVertexFormat().getPointElementSize();
int nTVerts = texCoords.size() / getVertexFormat().getTexCoordElementSize();
for (int i = 0; i < faces.size(); i++) {
if (i % 2 == 0 && (faces.get(i) >= nVerts || faces.get(i) < 0)
|| (i % 2 != 0 && (faces.get(i) >= nTVerts || faces.get(i) < 0))) {
PlatformLogger.getLogger(logname).warning("The values in the "
+ "faces array must be within the range of the number "
+ "of vertices in the points array (0 to points.length / 3 - 1) "
+ "for the point indices and within the range of the "
+ "number of the vertices in the texCoords array (0 to "
+ "texCoords.length / 2 - 1) for the texture coordinate indices.");
return false;
}
}
} else if (getVertexFormat() == VertexFormat.POINT_NORMAL_TEXCOORD) {
int nVerts = points.size() / getVertexFormat().getPointElementSize();
int nNVerts = normals.size() / getVertexFormat().getNormalElementSize();
int nTVerts = texCoords.size() / getVertexFormat().getTexCoordElementSize();
for (int i = 0; i < faces.size(); i+=3) {
if ((faces.get(i) >= nVerts || faces.get(i) < 0)
|| (faces.get(i + 1) >= nNVerts || faces.get(i + 1) < 0)
|| (faces.get(i + 2) >= nTVerts || faces.get(i + 2) < 0)) {
PlatformLogger.getLogger(logname).warning("The values in the "
+ "faces array must be within the range of the number "
+ "of vertices in the points array (0 to points.length / 3 - 1) "
+ "for the point indices, and within the range of the "
+ "number of the vertices in the normals array (0 to "
+ "normals.length / 3 - 1) for the normals indices, and "
+ "number of the vertices in the texCoords array (0 to "
+ "texCoords.length / 2 - 1) for the texture coordinate indices.");
return false;
}
}
} else {
PlatformLogger.getLogger(logname).warning("Unsupported VertexFormat: " + getVertexFormat().toString());
return false;
}
return true;
}
private boolean validateFaceSmoothingGroups() {
if (faceSmoothingGroups.size() != 0
&& faceSmoothingGroups.size() != (faces.size() / getFaceElementSize())) {
String logname = TriangleMesh.class.getName();
PlatformLogger.getLogger(logname).warning("faceSmoothingGroups.size()"
+ " has to equal to number of faces.");
return false;
}
return true;
}
private boolean validate() {
if (isPredefinedShape) {
return true;
}
if (isValidDirty) {
if (pointsSyncer.dirtyInFull) {
isPointsValid = validatePoints();
}
if (normalsSyncer.dirtyInFull) {
isNormalsValid = validateNormals();
}
if (texCoordsSyncer.dirtyInFull) {
isTexCoordsValid = validateTexCoords();
}
if (facesSyncer.dirty || pointsSyncer.dirtyInFull
|| normalsSyncer.dirtyInFull || texCoordsSyncer.dirtyInFull) {
isFacesValid = isPointsValid && isNormalsValid
&& isTexCoordsValid && validateFaces();
}
if (faceSmoothingGroupsSyncer.dirtyInFull || facesSyncer.dirtyInFull) {
isFaceSmoothingGroupValid = isFacesValid && validateFaceSmoothingGroups();
}
isValidDirty = false;
}
return isPointsValid && isNormalsValid && isTexCoordsValid
&& isFaceSmoothingGroupValid && isFacesValid;
}
@Override
void updatePG() {
if (!isDirty()) {
return;
}
final NGTriangleMesh pgTriMesh = getPGTriangleMesh();
if (validate()) {
pgTriMesh.setUserDefinedNormals(getVertexFormat() == VertexFormat.POINT_NORMAL_TEXCOORD);
pgTriMesh.syncPoints(pointsSyncer);
pgTriMesh.syncNormals(normalsSyncer);
pgTriMesh.syncTexCoords(texCoordsSyncer);
pgTriMesh.syncFaces(facesSyncer);
pgTriMesh.syncFaceSmoothingGroups(faceSmoothingGroupsSyncer);
} else {
pgTriMesh.setUserDefinedNormals(false);
pgTriMesh.syncPoints(null);
pgTriMesh.syncNormals(null);
pgTriMesh.syncTexCoords(null);
pgTriMesh.syncFaces(null);
pgTriMesh.syncFaceSmoothingGroups(null);
}
setDirty(false);
}
@Override
BaseBounds computeBounds(BaseBounds bounds) {
if (isDirty() || cachedBounds == null) {
cachedBounds = new BoxBounds();
if (validate()) {
final int len = points.size();
final int pointElementSize = getVertexFormat().getPointElementSize();
for (int i = 0; i < len; i += pointElementSize) {
cachedBounds.add(points.get(i), points.get(i + 1), points.get(i + 2));
}
}
}
return bounds.deriveWithNewBounds(cachedBounds);
}
private Point3D computeCentroid(
double v0x, double v0y, double v0z,
double v1x, double v1y, double v1z,
double v2x, double v2y, double v2z) {
return new Point3D(
v0x + (v2x + (v1x - v2x) / 2.0 - v0x) / 3.0,
v0y + (v2y + (v1y - v2y) / 2.0 - v0y) / 3.0,
v0z + (v2z + (v1z - v2z) / 2.0 - v0z) / 3.0);
}
private Point2D computeCentroid(Point2D v0, Point2D v1, Point2D v2) {
Point2D center = v1.midpoint(v2);
Point2D vec = center.subtract(v0);
return v0.add(new Point2D(vec.getX() / 3.0, vec.getY() / 3.0));
}
private boolean computeIntersectsFace(
PickRay pickRay, Vec3d origin, Vec3d dir, int faceIndex,
CullFace cullFace, Node candidate, boolean reportFace, PickResultChooser result) {
int vertexIndexSize = getVertexFormat().getVertexIndexSize();
int pointElementSize = getVertexFormat().getPointElementSize();
final int v0Idx = faces.get(faceIndex) * pointElementSize;
final int v1Idx = faces.get(faceIndex + vertexIndexSize) * pointElementSize;
final int v2Idx = faces.get(faceIndex + (2 * vertexIndexSize)) * pointElementSize;
final float v0x = points.get(v0Idx);
final float v0y = points.get(v0Idx + 1);
final float v0z = points.get(v0Idx + 2);
final float v1x = points.get(v1Idx);
final float v1y = points.get(v1Idx + 1);
final float v1z = points.get(v1Idx + 2);
final float v2x = points.get(v2Idx);
final float v2y = points.get(v2Idx + 1);
final float v2z = points.get(v2Idx + 2);
final float e1x = v1x - v0x;
final float e1y = v1y - v0y;
final float e1z = v1z - v0z;
final float e2x = v2x - v0x;
final float e2y = v2y - v0y;
final float e2z = v2z - v0z;
final double hx = dir.y * e2z - dir.z * e2y;
final double hy = dir.z * e2x - dir.x * e2z;
final double hz = dir.x * e2y - dir.y * e2x;
final double a = e1x * hx + e1y * hy + e1z * hz;
if (a == 0.0) {
return false;
}
final double f = 1.0 / a;
final double sx = origin.x - v0x;
final double sy = origin.y - v0y;
final double sz = origin.z - v0z;
final double u = f * (sx * hx + sy * hy + sz * hz);
if (u < 0.0 || u > 1.0) {
return false;
}
final double qx = sy * e1z - sz * e1y;
final double qy = sz * e1x - sx * e1z;
final double qz = sx * e1y - sy * e1x;
double v = f * (dir.x * qx + dir.y * qy + dir.z * qz);
if (v < 0.0 || u + v > 1.0) {
return false;
}
final double t = f * (e2x * qx + e2y * qy + e2z * qz);
if (t >= pickRay.getNearClip() && t <= pickRay.getFarClip()) {
if (cullFace != CullFace.NONE) {
final Point3D normal = new Point3D(
e1y * e2z - e1z * e2y,
e1z * e2x - e1x * e2z,
e1x * e2y - e1y * e2x);
final double nangle = normal.angle(
new Point3D(-dir.x, -dir.y, -dir.z));
if ((nangle >= 90 || cullFace != CullFace.BACK) &&
(nangle <= 90 || cullFace != CullFace.FRONT)) {
return false;
}
}
if (Double.isInfinite(t) || Double.isNaN(t)) {
return false;
}
if (result == null || !result.isCloser(t)) {
return true;
}
Point3D point = PickResultChooser.computePoint(pickRay, t);
final Point3D centroid = computeCentroid(
v0x, v0y, v0z,
v1x, v1y, v1z,
v2x, v2y, v2z);
final Point3D cv0 = new Point3D(
v0x - centroid.getX(),
v0y - centroid.getY(),
v0z - centroid.getZ());
final Point3D cv1 = new Point3D(
v1x - centroid.getX(),
v1y - centroid.getY(),
v1z - centroid.getZ());
final Point3D cv2 = new Point3D(
v2x - centroid.getX(),
v2y - centroid.getY(),
v2z - centroid.getZ());
final Point3D ce1 = cv1.subtract(cv0);
final Point3D ce2 = cv2.subtract(cv0);
Point3D n = ce1.crossProduct(ce2);
if (n.getZ() < 0) {
n = new Point3D(-n.getX(), -n.getY(), -n.getZ());
}
final Point3D ax = n.crossProduct(Rotate.Z_AXIS);
final double angle = Math.atan2(ax.magnitude(), n.dotProduct(Rotate.Z_AXIS));
Rotate r = new Rotate(Math.toDegrees(angle), ax);
final Point3D crv0 = r.transform(cv0);
final Point3D crv1 = r.transform(cv1);
final Point3D crv2 = r.transform(cv2);
final Point3D rPoint = r.transform(point.subtract(centroid));
final Point2D flatV0 = new Point2D(crv0.getX(), crv0.getY());
final Point2D flatV1 = new Point2D(crv1.getX(), crv1.getY());
final Point2D flatV2 = new Point2D(crv2.getX(), crv2.getY());
final Point2D flatPoint = new Point2D(rPoint.getX(), rPoint.getY());
int texCoordElementSize = getVertexFormat().getTexCoordElementSize();
int texCoordOffset = getVertexFormat().getTexCoordIndexOffset();
final int t0Idx = faces.get(faceIndex + texCoordOffset) * texCoordElementSize;
final int t1Idx = faces.get(faceIndex + vertexIndexSize + texCoordOffset) * texCoordElementSize;
final int t2Idx = faces.get(faceIndex + (vertexIndexSize * 2) + texCoordOffset) * texCoordElementSize;
final Point2D u0 = new Point2D(texCoords.get(t0Idx), texCoords.get(t0Idx + 1));
final Point2D u1 = new Point2D(texCoords.get(t1Idx), texCoords.get(t1Idx + 1));
final Point2D u2 = new Point2D(texCoords.get(t2Idx), texCoords.get(t2Idx + 1));
final Point2D txCentroid = computeCentroid(u0, u1, u2);
final Point2D cu0 = u0.subtract(txCentroid);
final Point2D cu1 = u1.subtract(txCentroid);
final Point2D cu2 = u2.subtract(txCentroid);
final Affine src = new Affine(
flatV0.getX(), flatV1.getX(), flatV2.getX(),
flatV0.getY(), flatV1.getY(), flatV2.getY());
final Affine trg = new Affine(
cu0.getX(), cu1.getX(), cu2.getX(),
cu0.getY(), cu1.getY(), cu2.getY());
Point2D txCoords = null;
try {
src.invert();
trg.append(src);
txCoords = txCentroid.add(trg.transform(flatPoint));
} catch (NonInvertibleTransformException e) {
}
result.offer(candidate, t,
reportFace ? faceIndex / getFaceElementSize() : PickResult.FACE_UNDEFINED,
point, txCoords);
return true;
}
return false;
}
private boolean doComputeIntersects(PickRay pickRay, PickResultChooser pickResult,
Node candidate, CullFace cullFace, boolean reportFace) {
boolean found = false;
if (validate()) {
final int size = faces.size();
final Vec3d o = pickRay.getOriginNoClone();
final Vec3d d = pickRay.getDirectionNoClone();
for (int i = 0; i < size; i += getFaceElementSize()) {
if (computeIntersectsFace(pickRay, o, d, i, cullFace, candidate,
reportFace, pickResult)) {
found = true;
}
}
}
return found;
}
private class Listener<T extends ObservableArray<T>> implements ArrayChangeListener<T>, FloatArraySyncer, IntegerArraySyncer {
protected final T array;
protected boolean dirty = true;
protected boolean dirtyInFull = true;
protected int dirtyRangeFrom;
protected int dirtyRangeLength;
public Listener(T array) {
this.array = array;
array.addListener(this);
}
protected final void addDirtyRange(int from, int length) {
if (length > 0 && !dirtyInFull) {
markDirty();
if (dirtyRangeLength == 0) {
dirtyRangeFrom = from;
dirtyRangeLength = length;
} else {
int fromIndex = Math.min(dirtyRangeFrom, from);
int toIndex = Math.max(dirtyRangeFrom + dirtyRangeLength, from + length);
dirtyRangeFrom = fromIndex;
dirtyRangeLength = toIndex - fromIndex;
}
}
}
protected void markDirty() {
dirty = true;
TriangleMesh.this.setDirty(true);
}
@Override
public void onChanged(T observableArray, boolean sizeChanged, int from, int to) {
if (sizeChanged) {
setDirty(true);
} else {
addDirtyRange(from, to - from);
}
isValidDirty = true;
}
public final void setDirty(boolean dirty) {
this.dirtyInFull = dirty;
if (dirty) {
markDirty();
dirtyRangeFrom = 0;
dirtyRangeLength = array.size();
} else {
this.dirty = false;
dirtyRangeFrom = dirtyRangeLength = 0;
}
}
@Override
public float[] syncTo(float[] array, int[] fromAndLengthIndices) {
assert ((fromAndLengthIndices != null) && (fromAndLengthIndices.length == 2));
ObservableFloatArray floatArray = (ObservableFloatArray) this.array;
if (dirtyInFull || array == null || array.length != floatArray.size()) {
fromAndLengthIndices[0] = 0;
fromAndLengthIndices[1] = floatArray.size();
return floatArray.toArray(null);
}
fromAndLengthIndices[0] = dirtyRangeFrom;
fromAndLengthIndices[1] = dirtyRangeLength;
floatArray.copyTo(dirtyRangeFrom, array, dirtyRangeFrom, dirtyRangeLength);
return array;
}
@Override
public int[] syncTo(int[] array, int[] fromAndLengthIndices) {
assert ((fromAndLengthIndices != null) && (fromAndLengthIndices.length == 2));
ObservableIntegerArray intArray = (ObservableIntegerArray) this.array;
if (dirtyInFull || array == null || array.length != intArray.size()) {
fromAndLengthIndices[0] = 0;
fromAndLengthIndices[1] = intArray.size();
return intArray.toArray(null);
}
fromAndLengthIndices[0] = dirtyRangeFrom;
fromAndLengthIndices[1] = dirtyRangeLength;
intArray.copyTo(dirtyRangeFrom, array, dirtyRangeFrom, dirtyRangeLength);
return array;
}
}
}
