package com.sun.javafx.scene.input;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.SubSceneHelper;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.input.PickResult;
public class PickResultChooser {
private double distance = Double.POSITIVE_INFINITY;
private Node node;
private int face = -1;
private Point3D point;
private Point3D normal;
private Point2D texCoord;
private boolean empty = true;
private boolean closed = false;
public static Point3D computePoint(PickRay ray, double distance) {
Vec3d origin = ray.getOriginNoClone();
Vec3d dir = ray.getDirectionNoClone();
return new Point3D(
origin.x + dir.x * distance,
origin.y + dir.y * distance,
origin.z + dir.z * distance);
}
public PickResult toPickResult() {
if (empty) {
return null;
}
return new PickResult(node, point, distance, face, normal, texCoord);
}
public boolean isCloser(double distance) {
return distance < this.distance || empty;
}
public boolean isEmpty() {
return empty;
}
public boolean isClosed() {
return closed;
}
public boolean offer(Node node, double distance, int face, Point3D point, Point2D texCoord) {
return processOffer(node, node, distance, point, face, normal, texCoord);
}
public boolean offer(Node node, double distance, Point3D point) {
return processOffer(node, node, distance, point, PickResult.FACE_UNDEFINED, null, null);
}
public boolean offerSubScenePickResult(SubScene subScene, PickResult pickResult, double distance) {
if (pickResult == null) {
return false;
}
return processOffer(pickResult.getIntersectedNode(), subScene, distance,
pickResult.getIntersectedPoint(), pickResult.getIntersectedFace(),
pickResult.getIntersectedNormal(), pickResult.getIntersectedTexCoord());
}
private boolean processOffer(Node node, Node depthTestNode, double distance,
Point3D point, int face, Point3D normal, Point2D texCoord) {
final SubScene subScene = NodeHelper.getSubScene(depthTestNode);
final boolean hasDepthBuffer = Platform.isSupported(ConditionalFeature.SCENE3D)
? (subScene != null
? SubSceneHelper.isDepthBuffer(subScene)
: depthTestNode.getScene().isDepthBuffer())
: false;
final boolean hasDepthTest =
hasDepthBuffer && NodeHelper.isDerivedDepthTest(depthTestNode);
boolean accepted = false;
if ((empty || (hasDepthTest && distance < this.distance)) && !closed) {
this.node = node;
this.distance = distance;
this.face = face;
this.point = point;
this.normal = normal;
this.texCoord = texCoord;
this.empty = false;
accepted = true;
}
if (!hasDepthTest) {
this.closed = true;
}
return accepted;
}
public final Node getIntersectedNode() {
return node;
}
public final double getIntersectedDistance() {
return distance;
}
public final int getIntersectedFace() {
return face;
}
public final Point3D getIntersectedPoint() {
return point;
}
public final Point3D getIntersectedNormal() {
return normal;
}
public final javafx.geometry.Point2D getIntersectedTexCoord() {
return texCoord;
}
}
