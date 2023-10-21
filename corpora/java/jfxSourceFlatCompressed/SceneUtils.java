package com.sun.javafx.scene;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.SubScene;
public class SceneUtils {
public static Point3D subSceneToScene(SubScene subScene, Point3D point) {
Node n = subScene;
while(n != null) {
final Point2D projection = CameraHelper.project(
SubSceneHelper.getEffectiveCamera(subScene), point);
point = n.localToScene(projection.getX(), projection.getY(), 0.0);
n = NodeHelper.getSubScene(n);
}
return point;
}
public static Point2D sceneToSubScenePlane(SubScene subScene, Point2D point) {
point = computeSubSceneCoordinates(point.getX(), point.getY(), subScene);
return point;
}
private static Point2D computeSubSceneCoordinates(
double x, double y, SubScene subScene) {
SubScene outer = NodeHelper.getSubScene(subScene);
if (outer == null) {
return CameraHelper.pickNodeXYPlane(
SceneHelper.getEffectiveCamera(subScene.getScene()),
subScene, x, y);
} else {
Point2D coords = computeSubSceneCoordinates(x, y, outer);
if (coords != null) {
coords = CameraHelper.pickNodeXYPlane(
SubSceneHelper.getEffectiveCamera(outer),
subScene, coords.getX(), coords.getY());
}
return coords;
}
}
}
