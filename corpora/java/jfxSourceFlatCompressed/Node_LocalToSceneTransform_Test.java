package test.javafx.scene;
import test.com.sun.javafx.test.TransformHelper;
import javafx.scene.transform.Translate;
import javafx.scene.shape.Rectangle;
import javafx.beans.Observable;
import java.lang.reflect.Method;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.NodeShim;
import javafx.scene.transform.Transform;
import org.junit.Test;
import static org.junit.Assert.*;
public class Node_LocalToSceneTransform_Test {
private boolean notified;
@Test
public void notTransformedNodeShouldReturnIdentity() {
Node n = new Rectangle(20, 20);
TransformHelper.assertMatrix(n.getLocalToSceneTransform(),
1, 0, 0, 0,
0, 1, 0, 0,
0, 0, 1, 0);
}
@Test
public void noListenersShouldBeAddedByDefault() throws Exception {
Node r = new Rectangle(20, 20);
Group n = new Group(r);
Group p = new Group(n);
p.getLocalToSceneTransform();
n.getLocalToSceneTransform();
r.getLocalToSceneTransform();
p.setTranslateX(10);
p.setRotate(80);
TransformHelper.assertMatrix(NodeShim.getCurrentLocalToSceneTransformState(n),
1, 0, 0, 0,
0, 1, 0, 0,
0, 0, 1, 0);
}
@Test
public void shouldConsiderAllParents() {
Node n = new Rectangle(20, 20);
Group p1 = new Group(n);
Group p2 = new Group(p1);
n.setTranslateX(10);
p1.setTranslateY(20);
p2.getTransforms().add(new Translate(5, 6));
TransformHelper.assertMatrix(n.getLocalToSceneTransform(),
1, 0, 0, 15,
0, 1, 0, 26,
0, 0, 1, 0);
}
@Test
public void shouldBeUpToDate() {
Node n = new Rectangle(20, 20);
Group p1 = new Group(n);
Group p2 = new Group(p1);
n.setTranslateX(10);
p1.setTranslateY(20);
p2.getTransforms().add(new Translate(5, 6));
TransformHelper.assertMatrix(n.getLocalToSceneTransform(),
1, 0, 0, 15,
0, 1, 0, 26,
0, 0, 1, 0);
n.setTranslateX(0);
p1.setTranslateY(0);
p2.getTransforms().clear();
p2.setScaleY(10);
TransformHelper.assertMatrix(n.getLocalToSceneTransform(),
1, 0, 0, 0,
0, 10, 0, -90,
0, 0, 1, 0);
}
@Test
public void shouldBeNotifiedWhenThisTransforms() {
final Node n = new Rectangle(20, 20);
n.localToSceneTransformProperty().addListener(o -> {
notified = true;
TransformHelper.assertMatrix(n.getLocalToSceneTransform(),
1, 0, 0, 10,
0, 1, 0, 20,
0, 0, 1, 0);
});
notified = false;
n.getTransforms().add(new Translate(10, 20));
assertTrue(notified);
}
@Test
public void shouldBeNotifiedWhenParentTransforms() {
final Node n = new Rectangle(20, 20);
final Group g = new Group(n);
n.localToSceneTransformProperty().addListener(o -> {
notified = true;
TransformHelper.assertMatrix(n.getLocalToSceneTransform(),
1, 0, 0, 10,
0, 1, 0, 20,
0, 0, 1, 0);
});
notified = false;
g.getTransforms().add(new Translate(10, 20));
assertTrue(notified);
}
@Test
public void shouldBeNotifiedOnReparent() {
final Node n = new Rectangle(20, 20);
final Group g = new Group(n);
final Group g2 = new Group();
g2.setTranslateX(100);
n.localToSceneTransformProperty().addListener(o -> {
if (n.getParent() != null) {
notified = true;
TransformHelper.assertMatrix(n.getLocalToSceneTransform(),
1, 0, 0, 100,
0, 1, 0, 0,
0, 0, 1, 0);
} else {
TransformHelper.assertMatrix(n.getLocalToSceneTransform(),
1, 0, 0, 0,
0, 1, 0, 0,
0, 0, 1, 0);
}
});
notified = false;
g2.getChildren().add(n);
assertTrue(notified);
}
@Test
public void shouldBeNotifiedWhenParentTransformsAfterReparent() {
final Node n = new Rectangle(20, 20);
final Group g = new Group(n);
final Group g2 = new Group();
g.setTranslateX(50);
g2.setTranslateX(100);
n.localToSceneTransformProperty().addListener(o -> {
if (!notified) {
notified = true;
TransformHelper.assertMatrix(n.getLocalToSceneTransform(),
1, 0, 0, 60,
0, 1, 0, 0,
0, 0, 1, 0);
} else {
n.getLocalToSceneTransform();
}
});
notified = true;
g2.getChildren().add(n);
notified = false;
g2.setTranslateX(60);
assertTrue(notified);
}
@Test
public void shouldUnregisterListenersWhenNotNeeded() {
final Node n = new Rectangle(20, 20);
final Group p1 = new Group(n);
final Group p2 = new Group(p1);
InvalidationListener lstnr = o -> {
n.getLocalToSceneTransform();
};
n.localToSceneTransformProperty().addListener(lstnr);
p2.setTranslateX(30);
TransformHelper.assertMatrix(NodeShim.getCurrentLocalToSceneTransformState(p1),
1, 0, 0, 30,
0, 1, 0, 0,
0, 0, 1, 0);
n.localToSceneTransformProperty().removeListener(lstnr);
p2.setTranslateX(60);
TransformHelper.assertMatrix(NodeShim.getCurrentLocalToSceneTransformState(p1),
1, 0, 0, 30,
0, 1, 0, 0,
0, 0, 1, 0);
n.localToSceneTransformProperty().addListener(lstnr);
p2.setTranslateX(90);
TransformHelper.assertMatrix(NodeShim.getCurrentLocalToSceneTransformState(p1),
1, 0, 0, 90,
0, 1, 0, 0,
0, 0, 1, 0);
}
@Test
public void shouldUnregisterListenersWhenNotNeededButStillUpdate() {
final Node n = new Rectangle(20, 20);
final Group p1 = new Group(n);
final Group p2 = new Group(p1);
InvalidationListener lstnr = o -> {
n.getLocalToSceneTransform();
};
n.localToSceneTransformProperty().addListener(lstnr);
p2.setTranslateX(30);
TransformHelper.assertMatrix(NodeShim.getCurrentLocalToSceneTransformState(p1),
1, 0, 0, 30,
0, 1, 0, 0,
0, 0, 1, 0);
n.localToSceneTransformProperty().removeListener(lstnr);
p2.setTranslateX(60);
TransformHelper.assertMatrix(NodeShim.getCurrentLocalToSceneTransformState(p1),
1, 0, 0, 30,
0, 1, 0, 0,
0, 0, 1, 0);
TransformHelper.assertMatrix(p1.getLocalToSceneTransform(),
1, 0, 0, 60,
0, 1, 0, 0,
0, 0, 1, 0);
}
@Test
public void shouldUnregisterListenersWhenReparent() {
final Node n = new Rectangle(20, 20);
final Group p1 = new Group(n);
final Group p2 = new Group(p1);
final Group g = new Group();
InvalidationListener lstnr = o -> {
n.getLocalToSceneTransform();
};
n.localToSceneTransformProperty().addListener(lstnr);
p2.setTranslateX(30);
TransformHelper.assertMatrix(NodeShim.getCurrentLocalToSceneTransformState(p1),
1, 0, 0, 30,
0, 1, 0, 0,
0, 0, 1, 0);
g.getChildren().add(n);
p2.setTranslateX(60);
TransformHelper.assertMatrix(NodeShim.getCurrentLocalToSceneTransformState(p1),
1, 0, 0, 30,
0, 1, 0, 0,
0, 0, 1, 0);
p1.getChildren().add(n);
p2.setTranslateX(90);
TransformHelper.assertMatrix(NodeShim.getCurrentLocalToSceneTransformState(p1),
1, 0, 0, 90,
0, 1, 0, 0,
0, 0, 1, 0);
}
@Test
public void shouldNotUnregisterListenerIfThereIsOtherReason() {
final Node n = new Rectangle(20, 20);
final Group p1 = new Group(n);
final Group p2 = new Group(p1);
final Group g = new Group();
InvalidationListener nlstnr = o -> {
n.getLocalToSceneTransform();
};
InvalidationListener plstnr = o -> {
p1.getLocalToSceneTransform();
};
n.localToSceneTransformProperty().addListener(nlstnr);
p1.localToSceneTransformProperty().addListener(plstnr);
p2.setTranslateX(30);
TransformHelper.assertMatrix(NodeShim.getCurrentLocalToSceneTransformState(p1),
1, 0, 0, 30,
0, 1, 0, 0,
0, 0, 1, 0);
g.getChildren().add(n);
p2.setTranslateX(60);
TransformHelper.assertMatrix(NodeShim.getCurrentLocalToSceneTransformState(p1),
1, 0, 0, 60,
0, 1, 0, 0,
0, 0, 1, 0);
p1.localToSceneTransformProperty().removeListener(plstnr);
p2.setTranslateX(90);
TransformHelper.assertMatrix(NodeShim.getCurrentLocalToSceneTransformState(p1),
1, 0, 0, 60,
0, 1, 0, 0,
0, 0, 1, 0);
p1.localToSceneTransformProperty().addListener(plstnr);
p1.getChildren().add(n);
p1.localToSceneTransformProperty().removeListener(plstnr);
p2.setTranslateX(45);
TransformHelper.assertMatrix(NodeShim.getCurrentLocalToSceneTransformState(p1),
1, 0, 0, 45,
0, 1, 0, 0,
0, 0, 1, 0);
g.getChildren().add(n);
p2.setTranslateX(25);
TransformHelper.assertMatrix(NodeShim.getCurrentLocalToSceneTransformState(p1),
1, 0, 0, 45,
0, 1, 0, 0,
0, 0, 1, 0);
}
@Test
public void shouldNotBeReusedWhenReferenceGivenToUser() {
final Node n = new Rectangle(20, 20);
final Group g = new Group(n);
g.setTranslateX(200);
Transform t1 = n.getLocalToSceneTransform();
TransformHelper.assertMatrix(t1,
1, 0, 0, 200,
0, 1, 0, 0,
0, 0, 1, 0);
g.setTranslateX(300);
Transform t2 = n.getLocalToSceneTransform();
TransformHelper.assertMatrix(t2,
1, 0, 0, 300,
0, 1, 0, 0,
0, 0, 1, 0);
assertFalse(t1 == t2);
TransformHelper.assertMatrix(t1,
1, 0, 0, 200,
0, 1, 0, 0,
0, 0, 1, 0);
}
@Test
public void shouldGetProperValueWhenSiblingValidatesParent() {
final Node n = new Rectangle(20, 20);
final Node n2 = new Rectangle(20, 20);
final Group g = new Group(n, n2);
g.setTranslateX(50);
n.localToSceneTransformProperty().addListener(o -> {
notified = true;
TransformHelper.assertMatrix(n.getLocalToSceneTransform(),
1, 0, 0, 60,
0, 1, 0, 0,
0, 0, 1, 0);
});
TransformHelper.assertMatrix(n2.getLocalToSceneTransform(),
1, 0, 0, 50,
0, 1, 0, 0,
0, 0, 1, 0);
notified = false;
g.setTranslateX(60);
assertTrue(notified);
TransformHelper.assertMatrix(n2.getLocalToSceneTransform(),
1, 0, 0, 60,
0, 1, 0, 0,
0, 0, 1, 0);
}
}
