package test.javafx.animation;
import javafx.animation.AnimationShim;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.StrokeTransition;
import javafx.animation.TransitionShim;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import org.junit.Before;
import org.junit.Test;
public class StrokeTransitionTest {
private static Duration DEFAULT_DURATION = Duration.millis(400);
private static Interpolator DEFAULT_INTERPOLATOR = Interpolator.EASE_BOTH;
private static float EPSILON = 1e-6f;
private static Duration ONE_SEC = Duration.millis(1000);
private static Duration TWO_SECS = Duration.millis(2000);
private Shape shape;
@Before
public void setUp() {
shape = new Rectangle();
}
private void assertColorEquals(Color expected, Paint actualPaint) {
assertTrue(actualPaint instanceof Color);
final Color actual = (Color)actualPaint;
assertEquals(expected.getRed(), actual.getRed(), EPSILON);
assertEquals(expected.getGreen(), actual.getGreen(), EPSILON);
assertEquals(expected.getBlue(), actual.getBlue(), EPSILON);
assertEquals(expected.getOpacity(), actual.getOpacity(), EPSILON);
}
@Test
public void testDefaultValues() {
StrokeTransition t0 = new StrokeTransition();
assertEquals(DEFAULT_DURATION, t0.getDuration());
assertEquals(DEFAULT_DURATION, t0.getCycleDuration());
assertNull(t0.getFromValue());
assertNull(t0.getToValue());
assertNull(t0.getShape());
assertEquals(DEFAULT_INTERPOLATOR, t0.getInterpolator());
assertNull(t0.getOnFinished());
t0 = new StrokeTransition(ONE_SEC);
assertEquals(ONE_SEC, t0.getDuration());
assertNull(t0.getFromValue());
assertNull(t0.getToValue());
assertNull(t0.getShape());
assertEquals(DEFAULT_INTERPOLATOR, t0.getInterpolator());
assertNull(t0.getOnFinished());
t0 = new StrokeTransition(TWO_SECS, shape);
assertEquals(TWO_SECS, t0.getDuration());
assertNull(t0.getFromValue());
assertNull(t0.getToValue());
assertEquals(shape, t0.getShape());
assertEquals(DEFAULT_INTERPOLATOR, t0.getInterpolator());
assertNull(t0.getOnFinished());
t0 = new StrokeTransition(TWO_SECS, null, Color.BLACK, Color.WHITE);
assertEquals(TWO_SECS, t0.getDuration());
assertColorEquals(Color.BLACK, t0.getFromValue());
assertColorEquals(Color.WHITE, t0.getToValue());
assertNull(t0.getShape());
assertEquals(DEFAULT_INTERPOLATOR, t0.getInterpolator());
assertNull(t0.getOnFinished());
t0 = new StrokeTransition(TWO_SECS, shape, Color.BLACK, Color.WHITE);
assertEquals(TWO_SECS, t0.getDuration());
assertColorEquals(Color.BLACK, t0.getFromValue());
assertColorEquals(Color.WHITE, t0.getToValue());
assertEquals(shape, t0.getShape());
assertEquals(DEFAULT_INTERPOLATOR, t0.getInterpolator());
assertNull(t0.getOnFinished());
}
@Test
public void testInterpolate() {
final Color fromValue = Color.color(0.2, 0.3, 0.7, 0.1);
final Color toValue = Color.color(0.8, 0.4, 0.2, 0.9);
final StrokeTransition t0 = new StrokeTransition(ONE_SEC, shape, fromValue, toValue);
assertTrue(AnimationShim.startable(t0,true));
AnimationShim.doStart(t0,true);
TransitionShim.interpolate(t0,0.0);
assertColorEquals(Color.color(0.2, 0.3, 0.7, 0.1), shape.getStroke());
TransitionShim.interpolate(t0,0.4);
assertColorEquals(Color.color(0.44, 0.34, 0.5, 0.42), shape.getStroke());
TransitionShim.interpolate(t0,1.0);
assertColorEquals(Color.color(0.8, 0.4, 0.2, 0.9), shape.getStroke());
AnimationShim.finished(t0);
}
@Test
public void testRedValueCombinations() {
final StrokeTransition t0 = new StrokeTransition(ONE_SEC, shape, null, Color.WHITE);
final double originalRed = 0.6;
final double fromRed = 0.4;
final Color originalValue = Color.color(originalRed, 1.0, 1.0);
final Color fromValue = Color.color(fromRed, 1.0, 1.0);
shape.setStroke(originalValue);
t0.setFromValue(null);
assertTrue(AnimationShim.startable(t0,true));
AnimationShim.doStart(t0,true);
TransitionShim.interpolate(t0,0.0);
assertColorEquals(originalValue, shape.getStroke());
AnimationShim.finished(t0);
shape.setStroke(originalValue);
t0.setFromValue(fromValue);
assertTrue(AnimationShim.startable(t0,true));
AnimationShim.doStart(t0,true);
TransitionShim.interpolate(t0,0.0);
assertColorEquals(fromValue, shape.getStroke());
AnimationShim.finished(t0);
}
@Test
public void testGetTargetNode() {
final Color fromValue = Color.color(0.0, 0.4, 0.8, 1.0);
final Color toValue = Color.color(1.0, 0.8, 0.6, 0.4);
final StrokeTransition ft = new StrokeTransition(ONE_SEC, shape, fromValue, toValue);
ft.setInterpolator(Interpolator.LINEAR);
final Shape shape2 = new Rectangle();
final ParallelTransition pt = new ParallelTransition();
pt.getChildren().add(ft);
pt.setNode(shape2);
shape.setStroke(Color.WHITE);
shape2.setStroke(Color.WHITE);
assertTrue(AnimationShim.startable(ft,true));
AnimationShim.doStart(ft,true);
TransitionShim.interpolate(ft,0.5);
assertColorEquals(Color.color(0.5, 0.6, 0.7, 0.7), shape.getStroke());
assertColorEquals(Color.WHITE, shape2.getStroke());
AnimationShim.finished(ft);
ft.setShape(null);
assertTrue(AnimationShim.startable(ft,true));
AnimationShim.doStart(ft,true);
TransitionShim.interpolate(ft,0.4);
assertColorEquals(Color.color(0.5, 0.6, 0.7, 0.7), shape.getStroke());
assertColorEquals(Color.color(0.4, 0.56, 0.72, 0.76), shape2.getStroke());
AnimationShim.finished(ft);
pt.setNode(new Group());
assertFalse(AnimationShim.startable(ft,true));
pt.setNode(null);
assertFalse(AnimationShim.startable(ft,true));
}
@Test
public void testCachedValues() {
final Color fromValue = Color.color(0.0, 0.4, 0.8, 0.2);
final Color toValue = Color.color(1.0, 0.8, 0.6, 0.4);
final StrokeTransition ft = new StrokeTransition(ONE_SEC, shape, fromValue, toValue);
ft.setInterpolator(Interpolator.LINEAR);
assertTrue(AnimationShim.startable(ft,true));
AnimationShim.doStart(ft,true);
ft.setFromValue(Color.WHITE);
TransitionShim.interpolate(ft,0.5);
assertColorEquals(Color.color(0.5, 0.6, 0.7, 0.3), shape.getStroke());
AnimationShim.finished(ft);
ft.setFromValue(fromValue);
assertTrue(AnimationShim.startable(ft,true));
AnimationShim.doStart(ft,true);
ft.setToValue(Color.BLACK);
TransitionShim.interpolate(ft,0.2);
assertColorEquals(Color.color(0.2, 0.48, 0.76, 0.24), shape.getStroke());
AnimationShim.finished(ft);
ft.setToValue(toValue);
assertTrue(AnimationShim.startable(ft,true));
AnimationShim.doStart(ft,true);
ft.setShape(null);
TransitionShim.interpolate(ft,0.7);
assertColorEquals(Color.color(0.7, 0.68, 0.66, 0.34), shape.getStroke());
AnimationShim.finished(ft);
ft.setShape(shape);
assertTrue(AnimationShim.startable(ft,true));
AnimationShim.doStart(ft,true);
ft.setInterpolator(null);
TransitionShim.interpolate(ft,0.1);
assertColorEquals(Color.color(0.1, 0.44, 0.78, 0.22), shape.getStroke());
AnimationShim.finished(ft);
ft.setInterpolator(Interpolator.LINEAR);
}
@Test
public void testStartable() {
final StrokeTransition t0 = new StrokeTransition(Duration.ONE, shape, Color.WHITE, Color.BLACK);
final Paint paint2 = new LinearGradient(0, 0, 1, 1, false, null,
new Stop[] { new Stop(0, Color.RED) });
assertTrue(AnimationShim.startable(t0,true));
t0.setDuration(Duration.ZERO);
assertFalse(AnimationShim.startable(t0,true));
t0.setDuration(Duration.ONE);
assertTrue(AnimationShim.startable(t0,true));
t0.setShape(null);
assertFalse(AnimationShim.startable(t0,true));
t0.setShape(shape);
assertTrue(AnimationShim.startable(t0,true));
t0.setInterpolator(null);
assertFalse(AnimationShim.startable(t0,true));
t0.setInterpolator(Interpolator.LINEAR);
assertTrue(AnimationShim.startable(t0,true));
t0.setFromValue(null);
shape.setStroke(paint2);
assertFalse(AnimationShim.startable(t0,true));
shape.setStroke(Color.BLACK);
assertTrue(AnimationShim.startable(t0,true));
t0.setFromValue(Color.WHITE);
shape.setStroke(paint2);
assertTrue(AnimationShim.startable(t0,true));
t0.setToValue(null);
assertFalse(AnimationShim.startable(t0,true));
t0.setToValue(Color.BLACK);
assertTrue(AnimationShim.startable(t0,true));
}
@Test
public void testEvaluateStartValue() {
final StrokeTransition t0 = new StrokeTransition(Duration.INDEFINITE, shape, null, Color.WHITE);
shape.setStroke(Color.GREY);
assertTrue(AnimationShim.startable(t0,true));
AnimationShim.doStart(t0,true);
shape.setStroke(Color.TRANSPARENT);
TransitionShim.interpolate(t0,0.0);
assertColorEquals(Color.GREY, shape.getStroke());
AnimationShim.finished(t0);
shape.setStroke(Color.BLACK);
assertTrue(AnimationShim.startable(t0,true));
AnimationShim.doStart(t0,true);
shape.setStroke(Color.WHITE);
TransitionShim.interpolate(t0,0.0);
assertColorEquals(Color.BLACK, shape.getStroke());
AnimationShim.finished(t0);
}
}
