package javafx.scene.shape;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.scene.shape.BoxHelper;
import com.sun.javafx.sg.prism.NGBox;
import com.sun.javafx.sg.prism.NGNode;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.input.PickResult;
public class Box extends Shape3D {
static {
BoxHelper.setBoxAccessor(new BoxHelper.BoxAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((Box) node).doCreatePeer();
}
@Override
public void doUpdatePeer(Node node) {
((Box) node).doUpdatePeer();
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((Box) node).doComputeGeomBounds(bounds, tx);
}
@Override
public boolean doComputeContains(Node node, double localX, double localY) {
return ((Box) node).doComputeContains(localX, localY);
}
@Override
public boolean doComputeIntersects(Node node, PickRay pickRay,
PickResultChooser pickResult) {
return ((Box) node).doComputeIntersects(pickRay, pickResult);
}
});
}
private TriangleMesh mesh;
@Deprecated(since = "18", forRemoval = true)
public static final double DEFAULT_SIZE = 2;
{
BoxHelper.initHelper(this);
}
public Box() {
this(DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_SIZE);
}
public Box(double width, double height, double depth) {
setWidth(width);
setHeight(height);
setDepth(depth);
}
private DoubleProperty depth;
public final void setDepth(double value) {
depthProperty().set(value);
}
public final double getDepth() {
return depth == null ? 2 : depth.get();
}
public final DoubleProperty depthProperty() {
if (depth == null) {
depth = new SimpleDoubleProperty(Box.this, "depth", DEFAULT_SIZE) {
@Override
public void invalidated() {
NodeHelper.markDirty(Box.this, DirtyBits.MESH_GEOM);
manager.invalidateBoxMesh(key);
key = null;
NodeHelper.geomChanged(Box.this);
}
};
}
return depth;
}
private DoubleProperty height;
public final void setHeight(double value) {
heightProperty().set(value);
}
public final double getHeight() {
return height == null ? 2 : height.get();
}
public final DoubleProperty heightProperty() {
if (height == null) {
height = new SimpleDoubleProperty(Box.this, "height", DEFAULT_SIZE) {
@Override
public void invalidated() {
NodeHelper.markDirty(Box.this, DirtyBits.MESH_GEOM);
manager.invalidateBoxMesh(key);
key = null;
NodeHelper.geomChanged(Box.this);
}
};
}
return height;
}
private DoubleProperty width;
public final void setWidth(double value) {
widthProperty().set(value);
}
public final double getWidth() {
return width == null ? 2 : width.get();
}
public final DoubleProperty widthProperty() {
if (width == null) {
width = new SimpleDoubleProperty(Box.this, "width", DEFAULT_SIZE) {
@Override
public void invalidated() {
NodeHelper.markDirty(Box.this, DirtyBits.MESH_GEOM);
manager.invalidateBoxMesh(key);
key = null;
NodeHelper.geomChanged(Box.this);
}
};
}
return width;
}
private NGNode doCreatePeer() {
return new NGBox();
}
private void doUpdatePeer() {
if (NodeHelper.isDirty(this, DirtyBits.MESH_GEOM)) {
NGBox peer = NodeHelper.getPeer(this);
final float w = (float) getWidth();
final float h = (float) getHeight();
final float d = (float) getDepth();
if (w < 0 || h < 0 || d < 0) {
peer.updateMesh(null);
} else {
if (key == null) {
key = new BoxKey(w, h, d);
}
mesh = manager.getBoxMesh(w, h, d, key);
mesh.updatePG();
peer.updateMesh(mesh.getPGTriangleMesh());
}
}
}
private BaseBounds doComputeGeomBounds(BaseBounds bounds, BaseTransform tx) {
final float w = (float) getWidth();
final float h = (float) getHeight();
final float d = (float) getDepth();
if (w < 0 || h < 0 || d < 0) {
return bounds.makeEmpty();
}
final float hw = w * 0.5f;
final float hh = h * 0.5f;
final float hd = d * 0.5f;
bounds = bounds.deriveWithNewBounds(-hw, -hh, -hd, hw, hh, hd);
bounds = tx.transform(bounds, bounds);
return bounds;
}
private boolean doComputeContains(double localX, double localY) {
double w = getWidth();
double h = getHeight();
return -w <= localX && localX <= w &&
-h <= localY && localY <= h;
}
private boolean doComputeIntersects(PickRay pickRay, PickResultChooser pickResult) {
final double w = getWidth();
final double h = getHeight();
final double d = getDepth();
final double hWidth = w / 2.0;
final double hHeight = h / 2.0;
final double hDepth = d / 2.0;
final Vec3d dir = pickRay.getDirectionNoClone();
final double invDirX = dir.x == 0.0 ? Double.POSITIVE_INFINITY : (1.0 / dir.x);
final double invDirY = dir.y == 0.0 ? Double.POSITIVE_INFINITY : (1.0 / dir.y);
final double invDirZ = dir.z == 0.0 ? Double.POSITIVE_INFINITY : (1.0 / dir.z);
final Vec3d origin = pickRay.getOriginNoClone();
final double originX = origin.x;
final double originY = origin.y;
final double originZ = origin.z;
final boolean signX = invDirX < 0.0;
final boolean signY = invDirY < 0.0;
final boolean signZ = invDirZ < 0.0;
double t0 = Double.NEGATIVE_INFINITY;
double t1 = Double.POSITIVE_INFINITY;
char side0 = '0';
char side1 = '0';
if (Double.isInfinite(invDirX)) {
if (-hWidth <= originX && hWidth >= originX) {
} else {
return false;
}
} else {
t0 = ((signX ? hWidth : -hWidth) - originX) * invDirX;
t1 = ((signX ? -hWidth : hWidth) - originX) * invDirX;
side0 = signX ? 'X' : 'x';
side1 = signX ? 'x' : 'X';
}
if (Double.isInfinite(invDirY)) {
if (-hHeight <= originY && hHeight >= originY) {
} else {
return false;
}
} else {
final double ty0 = ((signY ? hHeight : -hHeight) - originY) * invDirY;
final double ty1 = ((signY ? -hHeight : hHeight) - originY) * invDirY;
if ((t0 > ty1) || (ty0 > t1)) {
return false;
}
if (ty0 > t0) {
side0 = signY ? 'Y' : 'y';
t0 = ty0;
}
if (ty1 < t1) {
side1 = signY ? 'y' : 'Y';
t1 = ty1;
}
}
if (Double.isInfinite(invDirZ)) {
if (-hDepth <= originZ && hDepth >= originZ) {
} else {
return false;
}
} else {
double tz0 = ((signZ ? hDepth : -hDepth) - originZ) * invDirZ;
double tz1 = ((signZ ? -hDepth : hDepth) - originZ) * invDirZ;
if ((t0 > tz1) || (tz0 > t1)) {
return false;
}
if (tz0 > t0) {
side0 = signZ ? 'Z' : 'z';
t0 = tz0;
}
if (tz1 < t1) {
side1 = signZ ? 'z' : 'Z';
t1 = tz1;
}
}
char side = side0;
double t = t0;
final CullFace cullFace = getCullFace();
final double minDistance = pickRay.getNearClip();
final double maxDistance = pickRay.getFarClip();
if (t0 > maxDistance) {
return false;
}
if (t0 < minDistance || cullFace == CullFace.FRONT) {
if (t1 >= minDistance && t1 <= maxDistance && cullFace != CullFace.BACK) {
side = side1;
t = t1;
} else {
return false;
}
}
if (Double.isInfinite(t) || Double.isNaN(t)) {
return false;
}
if (pickResult != null && pickResult.isCloser(t)) {
Point3D point = PickResultChooser.computePoint(pickRay, t);
Point2D txtCoords = null;
switch (side) {
case 'x':
txtCoords = new Point2D(
0.5 - point.getZ() / d,
0.5 + point.getY() / h);
break;
case 'X':
txtCoords = new Point2D(
0.5 + point.getZ() / d,
0.5 + point.getY() / h);
break;
case 'y':
txtCoords = new Point2D(
0.5 + point.getX() / w,
0.5 - point.getZ() / d);
break;
case 'Y':
txtCoords = new Point2D(
0.5 + point.getX() / w,
0.5 + point.getZ() / d);
break;
case 'z':
txtCoords = new Point2D(
0.5 + point.getX() / w,
0.5 + point.getY() / h);
break;
case 'Z':
txtCoords = new Point2D(
0.5 - point.getX() / w,
0.5 + point.getY() / h);
break;
default:
return false;
}
pickResult.offer(this, t, PickResult.FACE_UNDEFINED, point, txtCoords);
}
return true;
}
static TriangleMesh createMesh(float w, float h, float d) {
float hw = w / 2f;
float hh = h / 2f;
float hd = d / 2f;
float points[] = {
-hw, -hh, -hd,
hw, -hh, -hd,
hw, hh, -hd,
-hw, hh, -hd,
-hw, -hh, hd,
hw, -hh, hd,
hw, hh, hd,
-hw, hh, hd};
float texCoords[] = {0, 0, 1, 0, 1, 1, 0, 1};
int faceSmoothingGroups[] = {
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
};
int faces[] = {
0, 0, 2, 2, 1, 1,
2, 2, 0, 0, 3, 3,
1, 0, 6, 2, 5, 1,
6, 2, 1, 0, 2, 3,
5, 0, 7, 2, 4, 1,
7, 2, 5, 0, 6, 3,
4, 0, 3, 2, 0, 1,
3, 2, 4, 0, 7, 3,
3, 0, 6, 2, 2, 1,
6, 2, 3, 0, 7, 3,
4, 0, 1, 2, 5, 1,
1, 2, 4, 0, 0, 3,
};
TriangleMesh mesh = new TriangleMesh(true);
mesh.getPoints().setAll(points);
mesh.getTexCoords().setAll(texCoords);
mesh.getFaces().setAll(faces);
mesh.getFaceSmoothingGroups().setAll(faceSmoothingGroups);
return mesh;
}
private static class BoxKey extends Key {
final double width, height, depth;
private BoxKey(double width, double height, double depth) {
this.width = width;
this.height = height;
this.depth = depth;
}
@Override
public int hashCode() {
long bits = 7L;
bits = 31L * bits + Double.doubleToLongBits(depth);
bits = 31L * bits + Double.doubleToLongBits(height);
bits = 31L * bits + Double.doubleToLongBits(width);
return Long.hashCode(bits);
}
@Override
public boolean equals(Object obj) {
if (this == obj) {
return true;
}
if (obj == null) {
return false;
}
if (!(obj instanceof BoxKey)) {
return false;
}
BoxKey other = (BoxKey) obj;
if (Double.compare(depth, other.depth) != 0) {
return false;
}
if (Double.compare(height, other.height) != 0) {
return false;
}
if (Double.compare(width, other.width) != 0) {
return false;
}
return true;
}
}
}
