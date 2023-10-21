package test.javafx.animation;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import org.junit.Before;
import org.junit.Test;
public class PathTransitionTest {
private static Duration DEFAULT_DURATION = Duration.millis(400);
private static Interpolator DEFAULT_INTERPOLATOR = Interpolator.EASE_BOTH;
private static Duration ONE_SEC = Duration.millis(1000);
private Shape path;
private Node node;
@Before
public void setUp() {
path = new Circle();
node = new Rectangle();
}
@Test
public void testDefaultValues() {
final PathTransition t0 = new PathTransition();
assertEquals(DEFAULT_DURATION, t0.getDuration());
assertEquals(DEFAULT_DURATION, t0.getCycleDuration());
assertNull(t0.getNode());
assertNull(t0.nodeProperty().get());
assertNull(t0.getPath());
assertNull(t0.pathProperty().get());
assertEquals(DEFAULT_INTERPOLATOR, t0.getInterpolator());
assertNull(t0.getOnFinished());
final PathTransition t1 = new PathTransition(ONE_SEC, path);
assertEquals(ONE_SEC, t1.getTotalDuration());
assertNull(t1.getNode());
assertNull(t1.nodeProperty().get());
assertEquals(path, t1.getPath());
assertEquals(path, t1.pathProperty().get());
assertEquals(DEFAULT_INTERPOLATOR, t1.getInterpolator());
assertNull(t1.getOnFinished());
final PathTransition t2 = new PathTransition(ONE_SEC, path, node);
assertEquals(ONE_SEC, t2.getTotalDuration());
assertEquals(node, t2.getNode());
assertEquals(node, t2.nodeProperty().get());
assertEquals(path, t2.getPath());
assertEquals(path, t2.pathProperty().get());
assertEquals(DEFAULT_INTERPOLATOR, t2.getInterpolator());
assertNull(t2.getOnFinished());
}
}
