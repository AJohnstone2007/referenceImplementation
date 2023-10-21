package com.sun.glass.ui.monocle;
import java.util.Arrays;
import java.util.Comparator;
class TouchState {
static class Point {
int id;
int x;
int y;
void copyTo(Point target) {
target.id = id;
target.x = x;
target.y = y;
}
@Override
public String toString() {
return "TouchState.Point[id=" + id + ",x=" + x + ",y=" + y + "]";
}
}
private static Comparator<Point> pointIdComparator = (p1, p2) -> p1.id - p2.id;
private Point[] points = new Point[1];
private int pointCount = 0;
private int primaryID = -1;
private MonocleWindow window;
MonocleWindow getWindow(boolean recalculateCache, MonocleWindow fallback) {
if (window == null) {
window = fallback;
}
if (recalculateCache) {
window = fallback;
if (primaryID >= 0) {
Point p = getPointForID(primaryID);
if (p != null) {
window = (MonocleWindow)
MonocleWindowManager.getInstance()
.getWindowForLocation(p.x, p.y);
}
}
}
return window;
}
Point getPoint(int index) {
return points[index];
}
Point getPointForID(int id) {
for (int i = 0; i < pointCount; i++) {
if (id == -1 || points[i].id == id) {
return points[i];
}
}
return null;
}
int getPrimaryID() {
return primaryID;
}
void assignPrimaryID() {
if (pointCount == 0) {
primaryID = -1;
} else if (primaryID <= 0) {
primaryID = points[0].id;
} else {
for (int i = 0; i < pointCount; i++) {
if (points[i].id == primaryID) {
return;
}
}
primaryID = points[0].id;
}
}
int getPointCount() {
return pointCount;
}
void clear() {
pointCount = 0;
}
void clearWindow() {
window = null;
}
Point addPoint(Point p) {
if (points.length == pointCount) {
points = Arrays.copyOf(points, points.length * 2);
}
if (points[pointCount] == null) {
points[pointCount] = new Point();
}
if (p != null) {
p.copyTo(points[pointCount]);
}
return points[pointCount++];
}
void removePointForID(int id) {
for (int i = 0; i < pointCount; i++) {
if (points[i].id == id) {
if (i < pointCount - 1) {
System.arraycopy(points, i + 1, points, i, pointCount - i - 1);
points[pointCount - 1] = null;
}
pointCount --;
}
}
}
void setPoint(int index, Point p) {
if (index >= pointCount) {
throw new IndexOutOfBoundsException();
}
p.copyTo(points[index]);
}
void copyTo(TouchState target) {
target.clear();
for (int i = 0; i < pointCount; i++) {
target.addPoint(points[i]);
}
target.primaryID = primaryID;
target.window = window;
}
@Override
public String toString() {
StringBuffer sb = new StringBuffer("TouchState[" + pointCount);
for (int i = 0; i < pointCount; i++) {
sb.append(",");
sb.append(points[i]);
}
sb.append("]");
return sb.toString();
}
void sortPointsByID() {
Arrays.sort(points, 0, pointCount, pointIdComparator);
}
boolean equalsSorted(TouchState ts) {
if (ts.pointCount == pointCount
&& ts.primaryID == primaryID
&& ts.window == window) {
for (int i = 0; i < pointCount; i++) {
Point p1 = ts.points[i];
Point p2 = points[i];
if (p1.x != p2.x || p1.y != p2.y || p1.id != p2.id) {
return false;
}
}
return true;
} else {
return false;
}
}
boolean canBeFoldedWith(TouchState ts, boolean ignoreIDs) {
if (ts.pointCount != pointCount) {
return false;
}
if (ignoreIDs) {
return true;
}
for (int i = 0; i < pointCount; i++) {
if (ts.points[i].id != points[i].id) {
return false;
}
}
return true;
}
}
