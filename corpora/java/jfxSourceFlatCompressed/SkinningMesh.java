package com.javafx.experiments.shape3d;
import com.javafx.experiments.importers.maya.Joint;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableFloatArray;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.transform.Affine;
import javafx.scene.transform.MatrixType;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
public class SkinningMesh extends PolygonMesh {
private final float[][] relativePoints;
private final float[][] weights;
private final List<Integer>[] weightIndices;
private final List<JointIndex> jointIndexForest;
private boolean jointsTransformDirty = true;
private Transform bindGlobalInverseTransform;
private final Transform[] jointToRootTransforms;
private final int nPoints;
private final int nJoints;
public SkinningMesh(PolygonMesh mesh, float[][] weights, Affine[] bindTransforms, Affine bindGlobalTransform, List<Joint> joints, List<Parent> jointForest) {
this.getPoints().addAll(mesh.getPoints());
this.getTexCoords().addAll(mesh.getTexCoords());
this.faces = mesh.faces;
this.getFaceSmoothingGroups().addAll(mesh.getFaceSmoothingGroups());
this.weights = weights;
nJoints = joints.size();
nPoints = getPoints().size()/ getPointElementSize();
jointIndexForest = new ArrayList<JointIndex>(jointForest.size());
for (Parent jointRoot : jointForest) {
jointIndexForest.add(new JointIndex(jointRoot, joints.indexOf(jointRoot), joints));
}
try {
bindGlobalInverseTransform = bindGlobalTransform.createInverse();
} catch (NonInvertibleTransformException ex) {
System.err.println("Caught NonInvertibleTransformException: " + ex.getMessage());
}
jointToRootTransforms = new Transform[nJoints];
weightIndices = new List[nJoints];
for (int j = 0; j < nJoints; j++) {
weightIndices[j] = new ArrayList<Integer>();
for (int i = 0; i < nPoints; i++) {
if (weights[j][i] != 0.0f) {
weightIndices[j].add(i);
}
}
}
ObservableFloatArray points = getPoints();
relativePoints = new float[nJoints][nPoints*3];
for (int j = 0; j < nJoints; j++) {
Transform postBindTransform = bindTransforms[j].createConcatenation(bindGlobalTransform);
for (int i = 0; i < nPoints; i++) {
Point3D relativePoint = postBindTransform.transform(points.get(3*i), points.get(3*i+1), points.get(3*i+2));
relativePoints[j][3*i] = (float) relativePoint.getX();
relativePoints[j][3*i+1] = (float) relativePoint.getY();
relativePoints[j][3*i+2] = (float) relativePoint.getZ();
}
}
Set<Node> processedNodes = new HashSet<Node>(joints.size());
InvalidationListener invalidationListener = observable -> jointsTransformDirty = true;
for (int j = 0; j < joints.size(); j++) {
Node node = joints.get(j);
while (!processedNodes.contains(node)) {
node.localToParentTransformProperty().addListener(invalidationListener);
processedNodes.add(node);
if (jointForest.contains(node)) {
break;
}
node = node.getParent();
}
}
}
private class JointIndex {
public Node node;
public int index;
public List<JointIndex> children = new ArrayList<JointIndex>();
public JointIndex parent = null;
public Transform localToGlobalTransform;
public JointIndex(Node n, int ind, List<Joint> orderedJoints) {
node = n;
index = ind;
if (node instanceof Parent) {
for (Node childJoint : ((Parent)node).getChildrenUnmodifiable()) {
if (childJoint instanceof Parent) {
int childInd = orderedJoints.indexOf(childJoint);
JointIndex childJointIndex = new JointIndex(childJoint, childInd, orderedJoints);
childJointIndex.parent = this;
children.add(childJointIndex);
}
}
}
}
}
private void updateLocalToGlobalTransforms(List<JointIndex> jointIndexForest) {
for (JointIndex jointIndex : jointIndexForest) {
if (jointIndex.parent == null) {
jointIndex.localToGlobalTransform = bindGlobalInverseTransform.createConcatenation(jointIndex.node.getLocalToParentTransform());
} else {
jointIndex.localToGlobalTransform = jointIndex.parent.localToGlobalTransform.createConcatenation(jointIndex.node.getLocalToParentTransform());
}
if (jointIndex.index != -1) {
jointToRootTransforms[jointIndex.index] = jointIndex.localToGlobalTransform;
}
updateLocalToGlobalTransforms(jointIndex.children);
}
}
public void update() {
if (!jointsTransformDirty) {
return;
}
updateLocalToGlobalTransforms(jointIndexForest);
float[] points = new float[nPoints*3];
double[] t = new double[12];
float[] relativePoint;
for (int j = 0; j < nJoints; j++) {
jointToRootTransforms[j].toArray(MatrixType.MT_3D_3x4, t);
relativePoint = relativePoints[j];
for (Integer i : weightIndices[j]) {
points[3*i] += weights[j][i] * (t[0] * relativePoint[3*i] + t[1] * relativePoint[3*i+1] + t[2] * relativePoint[3*i+2] + t[3]);
points[3*i+1] += weights[j][i] * (t[4] * relativePoint[3*i] + t[5] * relativePoint[3*i+1] + t[6] * relativePoint[3*i+2] + t[7]);
points[3*i+2] += weights[j][i] * (t[8] * relativePoint[3*i] + t[9] * relativePoint[3*i+1] + t[10] * relativePoint[3*i+2] + t[11]);
}
}
getPoints().set(0, points, 0, points.length);
jointsTransformDirty = false;
}
}
