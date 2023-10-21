package com.sun.javafx.scene.traversal;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import static com.sun.javafx.scene.traversal.Direction.*;
public class WeightedClosestCorner implements Algorithm {
WeightedClosestCorner() {
}
private boolean isOnAxis(Direction dir, Bounds cur, Bounds tgt) {
final double cmin, cmax, tmin, tmax;
if (dir == UP || dir == DOWN) {
cmin = cur.getMinX();
cmax = cur.getMaxX();
tmin = tgt.getMinX();
tmax = tgt.getMaxX();
}
else {
cmin = cur.getMinY();
cmax = cur.getMaxY();
tmin = tgt.getMinY();
tmax = tgt.getMaxY();
}
return tmin <= cmax && tmax >= cmin;
}
private double outDistance(Direction dir, Bounds cur, Bounds tgt) {
final double distance;
if (dir == UP) {
distance = cur.getMinY() - tgt.getMaxY();
}
else if (dir == DOWN) {
distance = tgt.getMinY() - cur.getMaxY();
}
else if (dir == LEFT) {
distance = cur.getMinX() - tgt.getMaxX();
}
else {
distance = tgt.getMinX() - cur.getMaxX();
}
return distance;
}
private double centerSideDistance(Direction dir, Bounds cur, Bounds tgt) {
final double cc;
final double tc;
if (dir == UP || dir == DOWN) {
cc = cur.getMinX() + cur.getWidth() / 2.0f;
tc = tgt.getMinX() + tgt.getWidth() / 2.0f;
}
else {
cc = cur.getMinY() + cur.getHeight() / 2.0f;
tc = tgt.getMinY() + tgt.getHeight() / 2.0f;
}
return Math.abs(tc - cc);
}
private double cornerSideDistance(Direction dir, Bounds cur, Bounds tgt) {
final double distance;
if (dir == UP || dir == DOWN) {
if (tgt.getMinX() > cur.getMaxX()) {
distance = tgt.getMinX() - cur.getMaxX();
}
else {
distance = cur.getMinX() - tgt.getMaxX();
}
}
else {
if (tgt.getMinY() > cur.getMaxY()) {
distance = tgt.getMinY() - cur.getMaxY();
}
else {
distance = cur.getMinY() - tgt.getMaxY();
}
}
return distance;
}
public Node select(Node node, Direction dir, TraversalContext context) {
Node newNode = null;
List<Node> nodes = context.getAllTargetNodes();
int target = traverse(context.getSceneLayoutBounds(node), dir, nodes, context);
if (target != -1) {
newNode = nodes.get(target);
}
return newNode;
}
@Override
public Node selectFirst(TraversalContext context) {
List<Node> nodes = context.getAllTargetNodes();
Point2D zeroZero = new Point2D(0,0);
if (nodes.size() > 0) {
int nodeIndex;
Node nearestNode = nodes.get(0);
double nearestDistance = zeroZero.distance(context.getSceneLayoutBounds(nodes.get(0)).getMinX(),
context.getSceneLayoutBounds(nodes.get(0)).getMinY());
double distance;
for (nodeIndex = 1; nodeIndex < nodes.size(); nodeIndex++) {
distance = zeroZero.distance(context.getSceneLayoutBounds(nodes.get(nodeIndex)).getMinX(),
context.getSceneLayoutBounds(nodes.get(nodeIndex)).getMinY());
if (nearestDistance > distance) {
nearestDistance = distance;
nearestNode = nodes.get(nodeIndex);
}
}
return nearestNode;
}
return null;
}
@Override
public Node selectLast(TraversalContext context) {
return null;
}
public int traverse(Bounds origin, Direction dir, List<Node> targets, TraversalContext context) {
final int target;
if (dir == NEXT || dir == NEXT_IN_LINE || dir == PREVIOUS) {
target = trav1D(origin, dir, targets, context);
} else {
target = trav2D(origin, dir, targets, context);
}
return target;
}
private int trav2D(Bounds origin, Direction dir, List<Node> targets, TraversalContext context) {
Bounds bestBounds = null;
double bestMetric = 0.0;
int bestIndex = -1;
for (int i = 0; i < targets.size(); i++) {
final Bounds targetBounds = context.getSceneLayoutBounds(targets.get(i));
final double outd = outDistance(dir, origin, targetBounds);
final double metric;
if (isOnAxis(dir, origin, targetBounds)) {
metric = outd + centerSideDistance(dir, origin, targetBounds) / 100;
}
else {
final double cosd = cornerSideDistance(dir, origin, targetBounds);
metric = 100000 + outd*outd + 9*cosd*cosd;
}
if (outd < 0.0) {
continue;
}
if (bestBounds == null || metric < bestMetric) {
bestBounds = targetBounds;
bestMetric = metric;
bestIndex = i;
}
}
return bestIndex;
}
private int compare1D(Bounds a, Bounds b) {
int res = 0;
final double metric1a = (a.getMinY() + a.getMaxY()) / 2;
final double metric1b = (b.getMinY() + b.getMaxY()) / 2;
final double metric2a = (a.getMinX() + a.getMaxX()) / 2;
final double metric2b = (b.getMinX() + b.getMaxX()) / 2;
final double metric3a = a.hashCode();
final double metric3b = b.hashCode();
if (metric1a < metric1b) {
res = -1;
}
else if (metric1a > metric1b) {
res = 1;
}
else if (metric2a < metric2b) {
res = -1;
}
else if (metric2a > metric2b) {
res = 1;
}
else if (metric3a < metric3b) {
res = -1;
}
else if (metric3a > metric3b) {
res = 1;
}
return res;
}
private int compare1D(Bounds a, Bounds b, Direction dir) {
return (dir != PREVIOUS) ? -compare1D(a, b) : compare1D(a, b);
}
private int trav1D(Bounds origin, Direction dir, List<Node> targets, TraversalContext context) {
int bestSoFar = -1;
int leastSoFar = -1;
for (int i = 0; i < targets.size(); i++) {
if (leastSoFar == -1 ||
compare1D(context.getSceneLayoutBounds(targets.get(i)),
context.getSceneLayoutBounds(targets.get(leastSoFar)), dir) < 0) {
leastSoFar = i;
}
if (compare1D(context.getSceneLayoutBounds(targets.get(i)), origin, dir) < 0) {
continue;
}
if (bestSoFar == -1 ||
compare1D(context.getSceneLayoutBounds(targets.get(i)), context.getSceneLayoutBounds(targets.get(bestSoFar)), dir) < 0) {
bestSoFar = i;
}
}
return (bestSoFar == -1) ? leastSoFar : bestSoFar;
}
}
