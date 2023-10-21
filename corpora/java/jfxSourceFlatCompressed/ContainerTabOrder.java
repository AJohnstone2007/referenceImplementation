package com.sun.javafx.scene.traversal;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import static com.sun.javafx.scene.traversal.Direction.*;
public class ContainerTabOrder implements Algorithm {
ContainerTabOrder() {
}
public Node select(Node node, Direction dir, TraversalContext context) {
switch (dir) {
case NEXT:
case NEXT_IN_LINE:
return TabOrderHelper.findNextFocusablePeer(node, context.getRoot(), dir == NEXT);
case PREVIOUS:
return TabOrderHelper.findPreviousFocusablePeer(node, context.getRoot());
case UP:
case DOWN:
case LEFT:
case RIGHT:
List<Node> nodes = context.getAllTargetNodes();
int target = trav2D(context.getSceneLayoutBounds(node), dir, nodes, context);
if (target != -1) {
return nodes.get(target);
}
}
return null;
}
@Override
public Node selectFirst(TraversalContext context) {
return TabOrderHelper.getFirstTargetNode(context.getRoot());
}
@Override
public Node selectLast(TraversalContext context) {
return TabOrderHelper.getLastTargetNode(context.getRoot());
}
private int trav2D(Bounds origin, Direction dir, List<Node> peers, TraversalContext context) {
Bounds bestBounds = null;
double bestMetric = 0.0;
int bestIndex = -1;
for (int i = 0; i < peers.size(); i++) {
final Bounds targetBounds = context.getSceneLayoutBounds(peers.get(i));
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
}
