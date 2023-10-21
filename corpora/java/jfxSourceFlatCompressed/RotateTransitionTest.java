package test.javafx.animation;
import javafx.animation.AnimationShim;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TransitionShim;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.junit.Before;
import org.junit.Test;
public class RotateTransitionTest {
private static Duration DEFAULT_DURATION = Duration.millis(400);
private static Interpolator DEFAULT_INTERPOLATOR = Interpolator.EASE_BOTH;
private static double EPSILON = 1e-12;
private static Duration ONE_SEC = Duration.millis(1000);
private static Duration TWO_SECS = Duration.millis(2000);
private Node node;
@Before
public void setUp() {
node = new Rectangle();
}
@Test
public void testDefaultValues() {
final RotateTransition t0 = new RotateTransition();
assertEquals(DEFAULT_DURATION, t0.getDuration());
assertEquals(DEFAULT_DURATION, t0.getCycleDuration());
assertTrue(Double.isNaN(t0.getFromAngle()));
assertTrue(Double.isNaN(t0.getToAngle()));
assertEquals(0.0, t0.getByAngle(), EPSILON);
assertNull(t0.getNode());
assertEquals(DEFAULT_INTERPOLATOR, t0.getInterpolator());
assertNull(t0.getOnFinished());
assertNull(t0.getAxis());
final RotateTransition t1 = new RotateTransition(ONE_SEC);
assertEquals(ONE_SEC, t1.getDuration());
assertTrue(Double.isNaN(t1.getFromAngle()));
assertTrue(Double.isNaN(t1.getToAngle()));
assertEquals(0.0, t1.getByAngle(), EPSILON);
assertNull(t1.getNode());
assertEquals(DEFAULT_INTERPOLATOR, t1.getInterpolator());
assertNull(t1.getOnFinished());
assertNull(t1.getAxis());
final RotateTransition t2 = new RotateTransition(TWO_SECS, node);
assertEquals(TWO_SECS, t2.getDuration());
assertTrue(Double.isNaN(t2.getFromAngle()));
assertTrue(Double.isNaN(t2.getToAngle()));
assertEquals(0.0, t2.getByAngle(), EPSILON);
assertEquals(node, t2.getNode());
assertEquals(DEFAULT_INTERPOLATOR, t2.getInterpolator());
assertNull(t2.getOnFinished());
assertNull(t2.getAxis());
}
@Test
public void testDefaultValuesFromProperties() {
final RotateTransition t0 = new RotateTransition();
assertEquals(DEFAULT_DURATION, t0.durationProperty().get());
assertTrue(Double.isNaN(t0.fromAngleProperty().get()));
assertTrue(Double.isNaN(t0.toAngleProperty().get()));
assertEquals(0.0, t0.byAngleProperty().get(), EPSILON);
assertNull(t0.nodeProperty().get());
assertEquals(DEFAULT_INTERPOLATOR, t0.interpolatorProperty().get());
assertNull(t0.onFinishedProperty().get());
assertNull(t0.axisProperty().get());
final RotateTransition t1 = new RotateTransition(ONE_SEC);
assertEquals(ONE_SEC, t1.durationProperty().get());
assertTrue(Double.isNaN(t1.fromAngleProperty().get()));
assertTrue(Double.isNaN(t1.toAngleProperty().get()));
assertEquals(0.0, t1.byAngleProperty().get(), EPSILON);
assertNull(t1.nodeProperty().get());
assertEquals(DEFAULT_INTERPOLATOR, t1.interpolatorProperty().get());
assertNull(t1.onFinishedProperty().get());
assertNull(t1.axisProperty().get());
final RotateTransition t2 = new RotateTransition(TWO_SECS, node);
assertEquals(TWO_SECS, t2.durationProperty().get());
assertTrue(Double.isNaN(t2.fromAngleProperty().get()));
assertTrue(Double.isNaN(t2.toAngleProperty().get()));
assertEquals(0.0, t2.byAngleProperty().get(), EPSILON);
assertEquals(node, t2.nodeProperty().get());
assertEquals(DEFAULT_INTERPOLATOR, t2.interpolatorProperty().get());
assertNull(t2.onFinishedProperty().get());
assertNull(t2.axisProperty().get());
}
@Test
public void testInterpolate() {
final RotateTransition t0 = new RotateTransition(ONE_SEC, node);
t0.setFromAngle(0.5);
t0.setToAngle(1.0);
assertTrue(AnimationShim.startable(t0,true));
AnimationShim.doStart(t0,true);
TransitionShim.interpolate(t0,0.0);
assertEquals(0.5, node.getRotate(), EPSILON);
TransitionShim.interpolate(t0,0.4);
assertEquals(0.7, node.getRotate(), EPSILON);
TransitionShim.interpolate(t0,1.0);
assertEquals(1.0, node.getRotate(), EPSILON);
AnimationShim.finished(t0);
}
@Test
public void testAxis() {
final Point3D defaultAxis = new Point3D(0.0, 0.0, 1.0);
final Point3D axis = new Point3D(1.0, 0.0, 0.0);
final RotateTransition t0 = new RotateTransition(ONE_SEC, node);
t0.setAxis(axis);
node.setRotationAxis(defaultAxis);
assertTrue(AnimationShim.startable(t0,true));
AnimationShim.doStart(t0,true);
assertEquals(axis, node.getRotationAxis());
AnimationShim.finished(t0);
t0.setAxis(null);
node.setRotationAxis(defaultAxis);
assertTrue(AnimationShim.startable(t0,true));
AnimationShim.doStart(t0,true);
assertEquals(defaultAxis, node.getRotationAxis());
AnimationShim.finished(t0);
}
@Test
public void testValueCombinations() {
final RotateTransition t0 = new RotateTransition(ONE_SEC, node);
final double originalAngle = 0.6;
final double fromAngle = 0.4;
final double toAngle = 0.9;
final double byAngle = -0.2;
node.setRotate(originalAngle);
t0.setFromAngle(Double.NaN);
t0.setToAngle(Double.NaN);
t0.setByAngle(0.0);
assertTrue(AnimationShim.startable(t0,true));
AnimationShim.doStart(t0,true);
TransitionShim.interpolate(t0,0.0);
assertEquals(originalAngle, node.getRotate(), EPSILON);
TransitionShim.interpolate(t0,1.0);
assertEquals(originalAngle, node.getRotate(), EPSILON);
AnimationShim.finished(t0);
node.setRotate(originalAngle);
t0.setFromAngle(fromAngle);
t0.setToAngle(Double.NaN);
t0.setByAngle(0.0);
assertTrue(AnimationShim.startable(t0,true));
AnimationShim.doStart(t0,true);
TransitionShim.interpolate(t0,0.0);
assertEquals(fromAngle, node.getRotate(), EPSILON);
TransitionShim.interpolate(t0,1.0);
assertEquals(fromAngle, node.getRotate(), EPSILON);
AnimationShim.finished(t0);
node.setRotate(originalAngle);
t0.setFromAngle(Double.NaN);
t0.setToAngle(toAngle);
t0.setByAngle(0.0);
assertTrue(AnimationShim.startable(t0,true));
AnimationShim.doStart(t0,true);
TransitionShim.interpolate(t0,0.0);
assertEquals(originalAngle, node.getRotate(), EPSILON);
TransitionShim.interpolate(t0,1.0);
assertEquals(toAngle, node.getRotate(), EPSILON);
AnimationShim.finished(t0);
node.setRotate(originalAngle);
t0.setFromAngle(Double.NaN);
t0.setToAngle(Double.NaN);
t0.setByAngle(byAngle);
assertTrue(AnimationShim.startable(t0,true));
AnimationShim.doStart(t0,true);
TransitionShim.interpolate(t0,0.0);
assertEquals(originalAngle, node.getRotate(), EPSILON);
TransitionShim.interpolate(t0,1.0);
assertEquals(originalAngle + byAngle, node.getRotate(), EPSILON);
AnimationShim.finished(t0);
node.setRotate(originalAngle);
t0.setFromAngle(fromAngle);
t0.setToAngle(toAngle);
t0.setByAngle(0.0);
assertTrue(AnimationShim.startable(t0,true));
AnimationShim.doStart(t0,true);
TransitionShim.interpolate(t0,0.0);
assertEquals(fromAngle, node.getRotate(), EPSILON);
TransitionShim.interpolate(t0,1.0);
assertEquals(toAngle, node.getRotate(), EPSILON);
AnimationShim.finished(t0);
node.setRotate(originalAngle);
t0.setFromAngle(fromAngle);
t0.setToAngle(Double.NaN);
t0.setByAngle(byAngle);
assertTrue(AnimationShim.startable(t0,true));
AnimationShim.doStart(t0,true);
TransitionShim.interpolate(t0,0.0);
assertEquals(fromAngle, node.getRotate(), EPSILON);
TransitionShim.interpolate(t0,1.0);
assertEquals(fromAngle + byAngle, node.getRotate(), EPSILON);
AnimationShim.finished(t0);
node.setRotate(originalAngle);
t0.setFromAngle(Double.NaN);
t0.setToAngle(toAngle);
t0.setByAngle(byAngle);
assertTrue(AnimationShim.startable(t0,true));
AnimationShim.doStart(t0,true);
TransitionShim.interpolate(t0,0.0);
assertEquals(originalAngle, node.getRotate(), EPSILON);
TransitionShim.interpolate(t0,1.0);
assertEquals(toAngle, node.getRotate(), EPSILON);
AnimationShim.finished(t0);
node.setRotate(originalAngle);
t0.setFromAngle(fromAngle);
t0.setToAngle(toAngle);
t0.setByAngle(byAngle);
assertTrue(AnimationShim.startable(t0,true));
AnimationShim.doStart(t0,true);
TransitionShim.interpolate(t0,0.0);
assertEquals(fromAngle, node.getRotate(), EPSILON);
TransitionShim.interpolate(t0,1.0);
assertEquals(toAngle, node.getRotate(), EPSILON);
AnimationShim.finished(t0);
}
@Test
public void testGetTargetNode() {
final RotateTransition rt = new RotateTransition(ONE_SEC, node);
rt.setInterpolator(Interpolator.LINEAR);
rt.setFromAngle(0.5);
rt.setToAngle(1.0);
final Rectangle node2 = new Rectangle();
final ParallelTransition pt = new ParallelTransition();
pt.getChildren().add(rt);
pt.setNode(node2);
assertTrue(AnimationShim.startable(rt,true));
AnimationShim.doStart(rt,true);
TransitionShim.interpolate(rt,0.5);
assertEquals(0.75, node.getRotate(), EPSILON);
assertEquals(0.0, node2.getRotate(), EPSILON);
AnimationShim.finished(rt);
rt.setNode(null);
assertTrue(AnimationShim.startable(rt,true));
AnimationShim.doStart(rt,true);
TransitionShim.interpolate(rt,0.4);
assertEquals(0.75, node.getRotate(), EPSILON);
assertEquals(0.7, node2.getRotate(), EPSILON);
AnimationShim.finished(rt);
pt.setNode(null);
assertFalse(AnimationShim.startable(rt,true));
}
@Test
public void testCachedValues() {
final RotateTransition rt = new RotateTransition(ONE_SEC, node);
rt.setInterpolator(Interpolator.LINEAR);
rt.setFromAngle(0.5);
rt.setToAngle(1.0);
assertTrue(AnimationShim.startable(rt,true));
AnimationShim.doStart(rt,true);
rt.setFromAngle(0.0);
TransitionShim.interpolate(rt,0.5);
assertEquals(0.75, node.getRotate(), EPSILON);
AnimationShim.finished(rt);
rt.setFromAngle(0.5);
assertTrue(AnimationShim.startable(rt,true));
AnimationShim.doStart(rt,true);
rt.setToAngle(0.0);
TransitionShim.interpolate(rt,0.2);
assertEquals(0.6, node.getRotate(), EPSILON);
AnimationShim.finished(rt);
rt.setToAngle(1.0);
assertTrue(AnimationShim.startable(rt,true));
AnimationShim.doStart(rt,true);
rt.setNode(null);
TransitionShim.interpolate(rt,0.7);
assertEquals(0.85, node.getRotate(), EPSILON);
AnimationShim.finished(rt);
rt.setNode(node);
assertTrue(AnimationShim.startable(rt,true));
AnimationShim.doStart(rt,true);
rt.setInterpolator(null);
TransitionShim.interpolate(rt,0.1);
assertEquals(0.55, node.getRotate(), EPSILON);
AnimationShim.finished(rt);
rt.setInterpolator(Interpolator.LINEAR);
}
@Test
public void testStartable() {
final RotateTransition t0 = new RotateTransition(Duration.ONE, node);
assertTrue(AnimationShim.startable(t0,true));
t0.setDuration(Duration.ZERO);
assertFalse(AnimationShim.startable(t0,true));
t0.setDuration(Duration.ONE);
assertTrue(AnimationShim.startable(t0,true));
t0.setNode(null);
assertFalse(AnimationShim.startable(t0,true));
t0.setNode(node);
assertTrue(AnimationShim.startable(t0,true));
t0.setInterpolator(null);
assertFalse(AnimationShim.startable(t0,true));
t0.setInterpolator(Interpolator.LINEAR);
assertTrue(AnimationShim.startable(t0,true));
}
@Test
public void testEvaluateStartValue() {
final RotateTransition t0 = new RotateTransition(Duration.INDEFINITE, node);
node.setRotate(0.6);
assertTrue(AnimationShim.startable(t0,true));
AnimationShim.doStart(t0,true);
node.setRotate(0.8);
TransitionShim.interpolate(t0,0.0);
assertEquals(0.6, node.getRotate(), EPSILON);
AnimationShim.finished(t0);
node.setRotate(0.2);
assertTrue(AnimationShim.startable(t0,true));
AnimationShim.doStart(t0,true);
node.setRotate(0.8);
TransitionShim.interpolate(t0,0.0);
assertEquals(0.2, node.getRotate(), EPSILON);
AnimationShim.finished(t0);
}
}
