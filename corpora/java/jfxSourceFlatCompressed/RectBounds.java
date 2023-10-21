package com.sun.javafx.geom;
public final class RectBounds extends BaseBounds {
private float minX;
private float maxX;
private float minY;
private float maxY;
public RectBounds() {
minX = minY = 0.0f;
maxX = maxY = -1.0f;
}
@Override public BaseBounds copy() {
return new RectBounds(minX, minY, maxX, maxY);
}
public RectBounds(float minX, float minY, float maxX, float maxY) {
setBounds(minX, minY, maxX, maxY);
}
public RectBounds(RectBounds other) {
setBounds(other);
}
public RectBounds(Rectangle other) {
setBounds(other.x, other.y,
other.x + other.width, other.y + other.height);
}
@Override public BoundsType getBoundsType() {
return BoundsType.RECTANGLE;
}
@Override public boolean is2D() {
return true;
}
@Override public float getWidth() {
return maxX - minX;
}
@Override public float getHeight() {
return maxY - minY;
}
@Override public float getDepth() {
return 0.0f;
}
@Override public float getMinX() {
return minX;
}
public void setMinX(float minX) {
this.minX = minX;
}
@Override public float getMinY() {
return minY;
}
public void setMinY(float minY) {
this.minY = minY;
}
@Override public float getMinZ() {
return 0.0f;
}
@Override public float getMaxX() {
return maxX;
}
public void setMaxX(float maxX) {
this.maxX = maxX;
}
@Override public float getMaxY() {
return maxY;
}
public void setMaxY(float maxY) {
this.maxY = maxY;
}
@Override public float getMaxZ() {
return 0.0f;
}
@Override public Vec2f getMin(Vec2f min) {
if (min == null) {
min = new Vec2f();
}
min.x = minX;
min.y = minY;
return min;
}
@Override public Vec2f getMax(Vec2f max) {
if (max == null) {
max = new Vec2f();
}
max.x = maxX;
max.y = maxY;
return max;
}
@Override public Vec3f getMin(Vec3f min) {
if (min == null) {
min = new Vec3f();
}
min.x = minX;
min.y = minY;
min.z = 0.0f;
return min;
}
@Override public Vec3f getMax(Vec3f max) {
if (max == null) {
max = new Vec3f();
}
max.x = maxX;
max.y = maxY;
max.z = 0.0f;
return max;
}
@Override public BaseBounds deriveWithUnion(BaseBounds other) {
if (other.getBoundsType() == BoundsType.RECTANGLE) {
RectBounds rb = (RectBounds) other;
unionWith(rb);
} else if (other.getBoundsType() == BoundsType.BOX) {
BoxBounds bb = new BoxBounds((BoxBounds) other);
bb.unionWith(this);
return bb;
} else {
throw new UnsupportedOperationException("Unknown BoundsType");
}
return this;
}
@Override public BaseBounds deriveWithNewBounds(Rectangle other) {
if (other.width < 0 || other.height < 0) return makeEmpty();
setBounds(other.x, other.y,
other.x + other.width, other.y + other.height);
return this;
}
@Override public BaseBounds deriveWithNewBounds(BaseBounds other) {
if (other.isEmpty()) return makeEmpty();
if (other.getBoundsType() == BoundsType.RECTANGLE) {
RectBounds rb = (RectBounds) other;
minX = rb.getMinX();
minY = rb.getMinY();
maxX = rb.getMaxX();
maxY = rb.getMaxY();
} else if (other.getBoundsType() == BoundsType.BOX) {
return new BoxBounds((BoxBounds) other);
} else {
throw new UnsupportedOperationException("Unknown BoundsType");
}
return this;
}
@Override public BaseBounds deriveWithNewBounds(float minX, float minY, float minZ,
float maxX, float maxY, float maxZ) {
if ((maxX < minX) || (maxY < minY) || (maxZ < minZ)) return makeEmpty();
if ((minZ == 0) && (maxZ == 0)) {
this.minX = minX;
this.minY = minY;
this.maxX = maxX;
this.maxY = maxY;
return this;
}
return new BoxBounds(minX, minY, minZ, maxX, maxY, maxZ);
}
@Override public BaseBounds deriveWithNewBoundsAndSort(float minX, float minY, float minZ,
float maxX, float maxY, float maxZ) {
if ((minZ == 0) && (maxZ == 0)) {
setBoundsAndSort(minX, minY, minZ, maxX, maxY, maxZ);
return this;
}
BaseBounds bb = new BoxBounds();
bb.setBoundsAndSort(minX, minY, minZ, maxX, maxY, maxZ);
return bb;
}
public final void setBounds(RectBounds other) {
minX = other.getMinX();
minY = other.getMinY();
maxX = other.getMaxX();
maxY = other.getMaxY();
}
public final void setBounds(float minX, float minY, float maxX, float maxY) {
this.minX = minX;
this.minY = minY;
this.maxX = maxX;
this.maxY = maxY;
}
public void setBoundsAndSort(float minX, float minY, float maxX, float maxY) {
setBounds(minX, minY, maxX, maxY);
sortMinMax();
}
@Override public void setBoundsAndSort(float minX, float minY, float minZ,
float maxX, float maxY, float maxZ) {
if (minZ != 0 || maxZ != 0) {
throw new UnsupportedOperationException("Unknown BoundsType");
}
setBounds(minX, minY, maxX, maxY);
sortMinMax();
}
@Override public void setBoundsAndSort(Point2D p1, Point2D p2) {
setBoundsAndSort(p1.x, p1.y, p2.x, p2.y);
}
@Override public RectBounds flattenInto(RectBounds bounds) {
if (bounds == null) bounds = new RectBounds();
if (isEmpty()) return bounds.makeEmpty();
bounds.setBounds(minX, minY, maxX, maxY);
return bounds;
}
public void unionWith(RectBounds other) {
if (other.isEmpty()) return;
if (this.isEmpty()) {
setBounds(other);
return;
}
minX = Math.min(minX, other.getMinX());
minY = Math.min(minY, other.getMinY());
maxX = Math.max(maxX, other.getMaxX());
maxY = Math.max(maxY, other.getMaxY());
}
public void unionWith(float minX, float minY, float maxX, float maxY) {
if ((maxX < minX) || (maxY < minY)) return;
if (this.isEmpty()) {
setBounds(minX, minY, maxX, maxY);
return;
}
this.minX = Math.min(this.minX, minX);
this.minY = Math.min(this.minY, minY);
this.maxX = Math.max(this.maxX, maxX);
this.maxY = Math.max(this.maxY, maxY);
}
@Override public void add(float x, float y, float z) {
if (z != 0) {
throw new UnsupportedOperationException("Unknown BoundsType");
}
unionWith(x, y, x, y);
}
public void add(float x, float y) {
unionWith(x, y, x, y);
}
@Override public void add(Point2D p) {
add(p.x, p.y);
}
@Override public void intersectWith(BaseBounds other) {
if (this.isEmpty()) return;
if (other.isEmpty()) {
makeEmpty();
return;
}
minX = Math.max(minX, other.getMinX());
minY = Math.max(minY, other.getMinY());
maxX = Math.min(maxX, other.getMaxX());
maxY = Math.min(maxY, other.getMaxY());
}
@Override public void intersectWith(Rectangle other) {
float x = other.x;
float y = other.y;
intersectWith(x, y, x + other.width, y + other.height);
}
public void intersectWith(float minX, float minY, float maxX, float maxY) {
if (this.isEmpty()) return;
if ((maxX < minX) || (maxY < minY)) {
makeEmpty();
return;
}
this.minX = Math.max(this.minX, minX);
this.minY = Math.max(this.minY, minY);
this.maxX = Math.min(this.maxX, maxX);
this.maxY = Math.min(this.maxY, maxY);
}
@Override public void intersectWith(float minX, float minY, float minZ,
float maxX, float maxY, float maxZ) {
if (this.isEmpty()) return;
if ((maxX < minX) || (maxY < minY) || (maxZ < minZ)) {
makeEmpty();
return;
}
this.minX = Math.max(this.minX, minX);
this.minY = Math.max(this.minY, minY);
this.maxX = Math.min(this.maxX, maxX);
this.maxY = Math.min(this.maxY, maxY);
}
@Override public boolean contains(Point2D p) {
if ((p == null) || isEmpty()) return false;
return (p.x >= minX && p.x <= maxX && p.y >= minY && p.y <= maxY);
}
@Override public boolean contains(float x, float y) {
if (isEmpty()) return false;
return (x >= minX && x <= maxX && y >= minY && y <= maxY);
}
public boolean contains(RectBounds other) {
if (isEmpty() || other.isEmpty()) return false;
return minX <= other.minX && maxX >= other.maxX && minY <= other.minY && maxY >= other.maxY;
}
@Override public boolean intersects(float x, float y, float width, float height) {
if (isEmpty()) return false;
return (x + width >= minX &&
y + height >= minY &&
x <= maxX &&
y <= maxY);
}
public boolean intersects(BaseBounds other) {
if ((other == null) || other.isEmpty() || isEmpty()) {
return false;
}
return (other.getMaxX() >= minX &&
other.getMaxY() >= minY &&
other.getMaxZ() >= getMinZ() &&
other.getMinX() <= maxX &&
other.getMinY() <= maxY &&
other.getMinZ() <= getMaxZ());
}
@Override public boolean disjoint(float x, float y, float width, float height) {
if (isEmpty()) return true;
return (x + width < minX ||
y + height < minY ||
x > maxX ||
y > maxY);
}
public boolean disjoint(RectBounds other) {
if ((other == null) || other.isEmpty() || isEmpty()) {
return true;
}
return (other.getMaxX() < minX ||
other.getMaxY() < minY ||
other.getMinX() > maxX ||
other.getMinY() > maxY);
}
@Override public boolean isEmpty() {
return !(maxX >= minX && maxY >= minY);
}
@Override public void roundOut() {
minX = (float) Math.floor(minX);
minY = (float) Math.floor(minY);
maxX = (float) Math.ceil(maxX);
maxY = (float) Math.ceil(maxY);
}
public void grow(float h, float v) {
minX -= h;
maxX += h;
minY -= v;
maxY += v;
}
@Override public BaseBounds deriveWithPadding(float h, float v, float d) {
if (d == 0) {
grow(h, v);
return this;
}
BoxBounds bb = new BoxBounds(minX, minY, 0, maxX, maxY, 0);
bb.grow(h, v, d);
return bb;
}
@Override public RectBounds makeEmpty() {
minX = minY = 0.0f;
maxX = maxY = -1.0f;
return this;
}
@Override protected void sortMinMax() {
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
}
@Override public void translate(float x, float y, float z) {
setMinX(getMinX() + x);
setMinY(getMinY() + y);
setMaxX(getMaxX() + x);
setMaxY(getMaxY() + y);
}
@Override public boolean equals(Object obj) {
if (obj == null) return false;
if (getClass() != obj.getClass()) return false;
final RectBounds other = (RectBounds) obj;
if (minX != other.getMinX()) return false;
if (minY != other.getMinY()) return false;
if (maxX != other.getMaxX()) return false;
if (maxY != other.getMaxY()) return false;
return true;
}
@Override public int hashCode() {
int hash = 7;
hash = 79 * hash + Float.floatToIntBits(minX);
hash = 79 * hash + Float.floatToIntBits(minY);
hash = 79 * hash + Float.floatToIntBits(maxX);
hash = 79 * hash + Float.floatToIntBits(maxY);
return hash;
}
@Override public String toString() {
return "RectBounds { minX:" + minX + ", minY:" + minY + ", maxX:" + maxX + ", maxY:" + maxY + "} (w:" + (maxX-minX) + ", h:" + (maxY-minY) +")";
}
}
