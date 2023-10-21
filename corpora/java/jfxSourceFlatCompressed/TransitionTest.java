package test.javafx.animation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import com.sun.javafx.tk.Toolkit;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.animation.TransitionShim;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.junit.Before;
import org.junit.Test;
public class TransitionTest {
private static Interpolator DEFAULT_INTERPOLATOR = Interpolator.EASE_BOTH;
private static double EPSILON = 1e-12;
private TransitionImpl transition;
@Before
public void setUp() {
transition = new TransitionImpl(Duration.millis(1000));
}
@Test
public void testDefaultValues() {
Transition t0 = new TransitionImpl(Duration.millis(1000));
assertEquals(DEFAULT_INTERPOLATOR, t0.getInterpolator());
assertEquals(6000.0 / Toolkit.getToolkit().getPrimaryTimer().getDefaultResolution(), t0.getTargetFramerate(), EPSILON);
Transition t1 = new TransitionImpl(Duration.millis(1000), 10);
assertEquals(DEFAULT_INTERPOLATOR, t1.getInterpolator());
assertEquals(10, t1.getTargetFramerate(), EPSILON);
}
@Test
public void testDefaultValuesFromProperties() {
assertEquals(DEFAULT_INTERPOLATOR, transition.interpolatorProperty().get());
}
@Test
public void testGetParentTargetNode() {
final Rectangle node = new Rectangle();
final ParallelTransition parent = new ParallelTransition();
parent.getChildren().add(transition);
parent.setNode(node);
assertEquals(node, transition.getParentTargetNode());
parent.setNode(null);
assertNull(transition.getParentTargetNode());
parent.setNode(node);
parent.getChildren().clear();
assertNull(transition.getParentTargetNode());
}
@Test
public void testStart() {
transition.doStart(true);
transition.setInterpolator(Interpolator.DISCRETE);
assertEquals(DEFAULT_INTERPOLATOR, transition.getCachedInterpolator());
transition.shim_impl_finished();
transition.doStart(true);
assertEquals(Interpolator.DISCRETE, transition.getCachedInterpolator());
transition.shim_impl_finished();
}
@Test
public void testPlayTo() {
assertTrue(transition.startable(true));
transition.setInterpolator(Interpolator.LINEAR);
transition.doStart(true);
transition.doPlayTo(0, 2);
assertEquals(0.0, transition.frac, EPSILON);
transition.doPlayTo(1, 2);
assertEquals(0.5, transition.frac, EPSILON);
transition.doPlayTo(2, 2);
assertEquals(1.0, transition.frac, EPSILON);
transition.shim_impl_finished();
transition.setInterpolator(Interpolator.DISCRETE);
transition.doStart(true);
transition.doPlayTo(0, 2);
assertEquals(0.0, transition.frac, EPSILON);
transition.doPlayTo(1, 2);
assertEquals(0.0, transition.frac, EPSILON);
transition.doPlayTo(2, 2);
assertEquals(1.0, transition.frac, EPSILON);
transition.shim_impl_finished();
}
@Test
public void testJumpTo() {
transition.doJumpTo(0, 2, false);
assertEquals(0.0, transition.frac, EPSILON);
transition.doJumpTo(1, 2, false);
assertEquals(0.0, transition.frac, EPSILON);
transition.doJumpTo(2, 2, false);
assertEquals(0.0, transition.frac, EPSILON);
transition.setInterpolator(Interpolator.LINEAR);
assertTrue(transition.startable(true));
transition.doStart(true);
transition.doJumpTo(0, 2, false);
assertEquals(0.0, transition.frac, EPSILON);
transition.doJumpTo(1, 2, false);
assertEquals(0.5, transition.frac, EPSILON);
transition.doJumpTo(2, 2, false);
assertEquals(1.0, transition.frac, EPSILON);
transition.doPause();
transition.doJumpTo(0, 2, false);
assertEquals(0.0, transition.frac, EPSILON);
transition.doJumpTo(1, 2, false);
assertEquals(0.5, transition.frac, EPSILON);
transition.doJumpTo(2, 2, false);
assertEquals(1.0, transition.frac, EPSILON);
transition.shim_impl_finished();
transition.setInterpolator(Interpolator.DISCRETE);
assertTrue(transition.startable(true));
transition.doStart(true);
transition.doJumpTo(0, 2, false);
assertEquals(0.0, transition.frac, EPSILON);
transition.doJumpTo(1, 2, false);
assertEquals(0.0, transition.frac, EPSILON);
transition.doJumpTo(2, 2, false);
assertEquals(1.0, transition.frac, EPSILON);
transition.doPause();
transition.doJumpTo(0, 2, false);
assertEquals(0.0, transition.frac, EPSILON);
transition.doJumpTo(1, 2, false);
assertEquals(0.0, transition.frac, EPSILON);
transition.doJumpTo(2, 2, false);
assertEquals(1.0, transition.frac, EPSILON);
transition.shim_impl_finished();
}
@Test
public void testForcedJumpTo() {
transition.setInterpolator(Interpolator.LINEAR);
transition.doJumpTo(0, 2, true);
assertEquals(0.0, transition.frac, EPSILON);
transition.doJumpTo(1, 2, true);
assertEquals(0.5, transition.frac, EPSILON);
transition.doJumpTo(2, 2, true);
assertEquals(1.0, transition.frac, EPSILON);
assertTrue(transition.startable(true));
transition.doStart(true);
transition.doJumpTo(0, 2, true);
assertEquals(0.0, transition.frac, EPSILON);
transition.doJumpTo(1, 2, true);
assertEquals(0.5, transition.frac, EPSILON);
transition.doJumpTo(2, 2, true);
assertEquals(1.0, transition.frac, EPSILON);
transition.doPause();
transition.doJumpTo(0, 2, true);
assertEquals(0.0, transition.frac, EPSILON);
transition.doJumpTo(1, 2, true);
assertEquals(0.5, transition.frac, EPSILON);
transition.doJumpTo(2, 2, true);
assertEquals(1.0, transition.frac, EPSILON);
transition.shim_impl_finished();
}
private static class TransitionImpl extends TransitionShim {
private double frac;
private TransitionImpl(Duration duration) {
setCycleDuration(duration);
}
private TransitionImpl(Duration duration, double targetFramerate) {
super(targetFramerate);
setCycleDuration(duration);
}
public void impl_setCurrentTicks(long ticks) {
}
@Override
protected void interpolate(double frac) {
this.frac = frac;
}
}
}
