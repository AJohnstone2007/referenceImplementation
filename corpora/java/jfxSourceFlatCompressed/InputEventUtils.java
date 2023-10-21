package com.sun.javafx.scene.input;
import com.sun.javafx.scene.CameraHelper;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.SceneHelper;
import com.sun.javafx.scene.SceneUtils;
import com.sun.javafx.scene.SubSceneHelper;
import java.util.Arrays;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.input.PickResult;
import javafx.scene.input.TransferMode;
public class InputEventUtils {
public static Point3D recomputeCoordinates(PickResult result,
Object newSource) {
Point3D coordinates = result.getIntersectedPoint();
if (coordinates == null) {
return new Point3D(Double.NaN, Double.NaN, Double.NaN);
}
final Node oldSourceNode = result.getIntersectedNode();
final Node newSourceNode =
(newSource instanceof Node) ? (Node) newSource : null;
final SubScene oldSubScene =
(oldSourceNode == null ? null : NodeHelper.getSubScene(oldSourceNode));
final SubScene newSubScene =
(newSourceNode == null ? null : NodeHelper.getSubScene(newSourceNode));
final boolean subScenesDiffer = (oldSubScene != newSubScene);
if (oldSourceNode != null) {
coordinates = oldSourceNode.localToScene(coordinates);
if (subScenesDiffer && oldSubScene != null) {
coordinates = SceneUtils.subSceneToScene(oldSubScene, coordinates);
}
}
if (newSourceNode != null) {
if (subScenesDiffer && newSubScene != null) {
Point2D planeCoords = CameraHelper.project(
SceneHelper.getEffectiveCamera(newSourceNode.getScene()),
coordinates);
planeCoords = SceneUtils.sceneToSubScenePlane(newSubScene, planeCoords);
if (planeCoords == null) {
coordinates = null;
} else {
coordinates = CameraHelper.pickProjectPlane(
SubSceneHelper.getEffectiveCamera(newSubScene),
planeCoords.getX(), planeCoords.getY());
}
}
if (coordinates != null) {
coordinates = newSourceNode.sceneToLocal(coordinates);
}
if (coordinates == null) {
coordinates = new Point3D(Double.NaN, Double.NaN, Double.NaN);
}
}
return coordinates;
}
private static final List<TransferMode> TM_ANY = List.of(
TransferMode.COPY,
TransferMode.MOVE,
TransferMode.LINK);
private static final List<TransferMode> TM_COPY_OR_MOVE = List.of(
TransferMode.COPY,
TransferMode.MOVE);
public static List<TransferMode> safeTransferModes(TransferMode[] modes) {
if (modes == TransferMode.ANY) {
return TM_ANY;
} else if (modes == TransferMode.COPY_OR_MOVE) {
return TM_COPY_OR_MOVE;
} else {
return Arrays.asList(modes);
}
}
}
