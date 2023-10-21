package test.javafx.scene;
import test.com.sun.javafx.test.TransformHelper;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.scene.shape.Rectangle;
import javafx.beans.Observable;
import javafx.beans.InvalidationListener;
import javafx.scene.Node;
import org.junit.Test;
import static org.junit.Assert.*;
public class Node_LocalToParentTransform_Test {
private boolean notified;
@Test
public void notTransformedNodeShouldReturnIdentity() {
Node n = new Rectangle(20, 20);
TransformHelper.assertMatrix(n.getLocalToParentTransform(),
1, 0, 0, 0,
0, 1, 0, 0,
0, 0, 1, 0);
}
@Test
public void shouldConsiderBothTransformListAndConvenientTransforms() {
Node n = new Rectangle(20, 20);
n.setTranslateX(10);
n.setScaleY(10);
n.getTransforms().add(new Translate(5, 6));
TransformHelper.assertMatrix(n.getLocalToParentTransform(),
1, 0, 0, 15,
0, 10, 0, -30,
0, 0, 1, 0);
}
@Test
public void shouldBeUpToDate() {
Node n = new Rectangle(20, 20);
n.setTranslateX(10);
TransformHelper.assertMatrix(n.getLocalToParentTransform(),
1, 0, 0, 10,
0, 1, 0, 0,
0, 0, 1, 0);
n.setTranslateX(20);
TransformHelper.assertMatrix(n.getLocalToParentTransform(),
1, 0, 0, 20,
0, 1, 0, 0,
0, 0, 1, 0);
}
@Test
public void shouldBeNotifiedWhenNodeTransforms() {
final Node n = new Rectangle(20, 20);
n.localToParentTransformProperty().addListener(o -> {
if (!notified) {
notified = true;
TransformHelper.assertMatrix(n.getLocalToParentTransform(),
1, 0, 0, 10,
0, 1, 0, 20,
0, 0, 1, 0);
}
});
notified = false;
n.getTransforms().add(new Translate(10, 20));
assertTrue(notified);
n.getTransforms().clear();
n.getTransforms().add(new Translate(10, 0));
n.getLocalToParentTransform();
notified = false;
n.setTranslateY(20);
assertTrue(notified);
}
@Test
public void shouldNotBeReusedWhenReferenceGivenToUser() {
final Node n = new Rectangle(20, 20);
n.setTranslateX(200);
Transform t1 = n.getLocalToParentTransform();
TransformHelper.assertMatrix(t1,
1, 0, 0, 200,
0, 1, 0, 0,
0, 0, 1, 0);
n.setTranslateX(300);
Transform t2 = n.getLocalToParentTransform();
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
}
