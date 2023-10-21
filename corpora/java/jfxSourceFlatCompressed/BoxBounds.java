package com.sun.javafx.geom;
import com.sun.javafx.geom.transform.Affine3D;
public class BoxBounds extends BaseBounds {
private float minX;
private float maxX;
private float minY;
private float maxY;
private float minZ;
private float maxZ;
public BoxBounds() {
minX = minY = minZ = 0.0f;
maxX = maxY = maxZ = -1.0f;
}
public BaseBounds copy() {
return new BoxBounds(minX, minY, minZ, maxX, maxY, maxZ);
}
public BoxBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
setBounds(minX, minY, minZ, maxX, maxY, maxZ);
}
public BoxBounds(BoxBounds other) {
setBounds(other);
}
public BoundsType getBoundsType() {
return BoundsType.BOX;
}
public boolean is2D() {
return (Affine3D.almostZero(minZ) && Affine3D.almostZero(maxZ));
}
public float getWidth() {
return maxX - minX;
}
public float getHeight() {
return maxY - minY;
}
public float getDepth() {
return maxZ - minZ;
}
public float getMinX() {
return minX;
}
public void setMinX(float minX) {
this.minX = minX;
}
public float getMinY() {
return minY;
}
public void setMinY(float minY) {
this.minY = minY;
}
public float getMinZ() {
return minZ;
}
public void setMinZ(float minZ) {
this.minZ = minZ;
}
public float getMaxX() {
return maxX;
}
public void setMaxX(float maxX) {
this.maxX = maxX;
}
public float getMaxY() {
return maxY;
}
public void setMaxY(float maxY) {
this.maxY = maxY;
}
public float getMaxZ() {
return maxZ;
}
public void setMaxZ(float maxZ) {
this.maxZ = maxZ;
}
public Vec2f getMin(Vec2f min) {
if (min == null) {
min = new Vec2f();
}
min.x = minX;
min.y = minY;
return min;
}
public Vec2f getMax(Vec2f max) {
if (max == null) {
max = new Vec2f();
}
max.x = maxX;
max.y = maxY;
return max;
}
public Vec3f getMin(Vec3f min) {
if (min == null) {
min = new Vec3f();
}
min.x = minX;
min.y = minY;
min.z = minZ;
return min;
}
public Vec3f getMax(Vec3f max) {
if (max == null) {
max = new Vec3f();
}
max.x = maxX;
max.y = maxY;
max.z = maxZ;
return max;
}
public BaseBounds deriveWithUnion(BaseBounds other) {
if ((other.getBoundsType() == BoundsType.RECTANGLE) ||
(other.getBoundsType() == BoundsType.BOX)) {
unionWith(other);
} else {
throw new UnsupportedOperationException("Unknown BoundsType");
}
return this;
}
public BaseBounds deriveWithNewBounds(Rectangle other) {
if (other.width < 0 || other.height < 0) return makeEmpty();
setBounds(other.x, other.y, 0,
other.x + other.width, other.y + other.height, 0);
return this;
}
public BaseBounds deriveWithNewBounds(BaseBounds other) {
if (other.isEmpty()) return makeEmpty();
if ((other.getBoundsType() == BoundsType.RECTANGLE) ||
(other.getBoundsType() == BoundsType.BOX)) {
minX = other.getMinX();
minY = other.getMinY();
minZ = other.getMinZ();
maxX = other.getMaxX();
maxY = other.getMaxY();
maxZ = other.getMaxZ();
} else {
throw new UnsupportedOperationException("Unknown BoundsType");
}
return this;
}
public BaseBounds deriveWithNewBounds(float minX, float minY, float minZ,
float maxX, float maxY, float maxZ) {
if ((maxX < minX) || (maxY < minY) || (maxZ < minZ)) return makeEmpty();
this.minX = minX;
this.minY = minY;
this.minZ = minZ;
this.maxX = maxX;
this.maxY = maxY;
this.maxZ = maxZ;
return this;
}
public BaseBounds deriveWithNewBoundsAndSort(float minX, float minY, float minZ,
float maxX, float maxY, float maxZ) {
setBoundsAndSort(minX, minY, minZ, maxX, maxY, maxZ);
return this;
}
@Override public RectBounds flattenInto(RectBounds bounds) {
if (bounds == null) bounds = new RectBounds();
if (isEmpty()) return bounds.makeEmpty();
bounds.setBounds(minX, minY, maxX, maxY);
return bounds;
}
public final void setBounds(BaseBounds other) {
minX = other.getMinX();
minY = other.getMinY();
minZ = other.getMinZ();
maxX = other.getMaxX();
maxY = other.getMaxY();
maxZ = other.getMaxZ();
}
public final void setBounds(float minX, float minY, float minZ,
float maxX, float maxY, float maxZ) {
this.minX = minX;
this.minY = minY;
this.minZ = minZ;
this.maxX = maxX;
this.maxY = maxY;
this.maxZ = maxZ;
}
public void setBoundsAndSort(float minX, float minY, float minZ,
float maxX, float maxY, float maxZ) {
setBounds(minX, minY, minZ, maxX, maxY, maxZ);
sortMinMax();
}
public void setBoundsAndSort(Point2D p1, Point2D p2) {
setBoundsAndSort(p1.x, p1.y, 0.0f, p2.x, p2.y, 0.0f);
}
public void unionWith(BaseBounds other) {
if (other.isEmpty()) return;
if (this.isEmpty()) {
setBounds(other);
return;
}
minX = Math.min(minX, other.getMinX());
minY = Math.min(minY, other.getMinY());
minZ = Math.min(minZ, other.getMinZ());
maxX = Math.max(maxX, other.getMaxX());
maxY = Math.max(maxY, other.getMaxY());
maxZ = Math.max(maxZ, other.getMaxZ());
}
public void unionWith(float minX, float minY, float minZ,
float maxX, float maxY, float maxZ) {
if ((maxX < minX) || (maxY < minY) || (maxZ < minZ)) return;
if (this.isEmpty()) {
setBounds(minX, minY, minZ, maxX, maxY, maxZ);
return;
}
this.minX = Math.min(this.minX, minX);
this.minY = Math.min(this.minY, minY);
this.minZ = Math.min(this.minZ, minZ);
this.maxX = Math.max(this.maxX, maxX);
this.maxY = Math.max(this.maxY, maxY);
this.maxZ = Math.max(this.maxZ, maxZ);
}
public void add(float x, float y, float z) {
unionWith(x, y, z, x, y, z);
}
public void add(Point2D p) {
add(p.x, p.y, 0.0f);
}
public void intersectWith(Rectangle other) {
float x = other.x;
float y = other.y;
intersectWith(x, y, 0,
x + other.width, y + other.height, 0);
}
public void intersectWith(BaseBounds other) {
if (this.isEmpty()) return;
if (other.isEmpty()) {
makeEmpty();
return;
}
minX = Math.max(minX, other.getMinX());
minY = Math.max(minY, other.getMinY());
minZ = Math.max(minZ, other.getMinZ());
maxX = Math.min(maxX, other.getMaxX());
maxY = Math.min(maxY, other.getMaxY());
maxZ = Math.min(maxZ, other.getMaxZ());
}
public void intersectWith(float minX, float minY, float minZ,
float maxX, float maxY, float maxZ) {
if (this.isEmpty()) return;
if ((maxX < minX) || (maxY < minY) || (maxZ < minZ)) {
makeEmpty();
return;
}
this.minX = Math.max(this.minX, minX);
this.minY = Math.max(this.minY, minY);
this.minZ = Math.max(this.minZ, minZ);
this.maxX = Math.min(this.maxX, maxX);
this.maxY = Math.min(this.maxY, maxY);
this.maxZ = Math.min(this.maxZ, maxZ);
}
public boolean contains(Point2D p) {
if ((p == null) || isEmpty()) return false;
return contains(p.x, p.y, 0.0f);
}
public boolean contains(float x, float y) {
if (isEmpty()) return false;
return contains(x, y, 0.0f);
}
public boolean contains(float x, float y, float z) {
if (isEmpty()) return false;
return (x >= minX && x <= maxX && y >= minY && y <= maxY
&& z >= minZ && z <= maxZ);
}
public boolean contains(float x, float y, float z,
float width, float height, float depth) {
if (isEmpty()) return false;
return contains(x, y, z) && contains(x+width, y+height, z+depth);
}
public boolean intersects(float x, float y, float width, float height) {
return intersects(x, y, 0.0f, width, height, 0.0f);
}
public boolean intersects(float x, float y, float z,
float width, float height, float depth) {
if (isEmpty()) return false;
return (x + width >= minX &&
y + height >= minY &&
z + depth >= minZ &&
x <= maxX &&
y <= maxY &&
z <= maxZ);
}
public boolean intersects(BaseBounds other) {
if ((other == null) || other.isEmpty() || isEmpty()) {
return false;
}
return (other.getMaxX() >= minX &&
other.getMaxY() >= minY &&
other.getMaxZ() >= minZ &&
other.getMinX() <= maxX &&
other.getMinY() <= maxY &&
other.getMinZ() <= maxZ);
}
public boolean disjoint(float x, float y, float width, float height) {
return disjoint(x, y, 0f, width, height, 0f);
}
public boolean disjoint(float x, float y, float z,
float width, float height, float depth) {
if (isEmpty()) return true;
return (x + width < minX ||
y + height < minY ||
z + depth < minZ ||
x > maxX ||
y > maxY ||
z > maxZ);
}
public boolean isEmpty() {
return maxX < minX || maxY < minY || maxZ < minZ;
}
public void roundOut() {
minX = (float) Math.floor(minX);
minY = (float) Math.floor(minY);
minZ = (float) Math.floor(minZ);
maxX = (float) Math.ceil(maxX);
maxY = (float) Math.ceil(maxY);
maxZ = (float) Math.ceil(maxZ);
}
public void grow(float h, float v, float d) {
minX -= h;
maxX += h;
minY -= v;
maxY += v;
minZ -= d;
maxZ += d;
}
public BaseBounds deriveWithPadding(float h, float v, float d) {
grow(h, v, d);
return this;
}
public BoxBounds makeEmpty() {
minX = minY = minZ = 0.0f;
maxX = maxY = maxZ = -1.0f;
return this;
}
protected void sortMinMax() {
if (minX > maxX) {
float tmp = maxX;
maxX = minX;
minX = tmp;
}
if (minY > maxY) {
float tmp = maxY;
maxY = minY;
minY = tmp;
}
if (minZ > maxZ) {
float tmp = maxZ;
maxZ = minZ;
minZ = tmp;
}
}
@Override
public void translate(float x, float y, float z) {
setMinX(getMinX() + x);
setMinY(getMinY() + y);
setMaxX(getMaxX() + x);
setMaxY(getMaxY() + y);
}
@Override
public boolean equals(Object obj) {
if (obj == null) return false;
if (getClass() != obj.getClass()) return false;
final BoxBounds other = (BoxBounds) obj;
if (minX != other.getMinX()) return false;
if (minY != other.getMinY()) return false;
if (minZ != other.getMinZ()) return false;
if (maxX != other.getMaxX()) return false;
if (maxY != other.getMaxY()) return false;
if (maxZ != other.getMaxZ()) return false;
return true;
}
@Override
public int hashCode() {
int hash = 7;
hash = 79 * hash + Float.floatToIntBits(minX);
hash = 79 * hash + Float.floatToIntBits(minY);
hash = 79 * hash + Float.floatToIntBits(minZ);
hash = 79 * hash + Float.floatToIntBits(maxX);
hash = 79 * hash + Float.floatToIntBits(maxY);
hash = 79 * hash + Float.floatToIntBits(maxZ);
return hash;
}
@Override
public String toString() {
return "BoxBounds { minX:" + minX + ", minY:" + minY + ", minZ:" + minZ + ", maxX:" + maxX + ", maxY:" + maxY + ", maxZ:" + maxZ + "}";
}
}
