package test.javafx.animation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.junit.Before;
import org.junit.Test;
public class SequentialTransitionTest {
private static Interpolator DEFAULT_INTERPOLATOR = Interpolator.LINEAR;
private static Duration ONE_SEC = Duration.millis(1000);
private static Duration TWO_SECS = Duration.millis(2000);
private static Duration THREE_SECS = Duration.millis(3000);
private static Duration SIX_SECS = Duration.millis(6000);
private Node node;
private Animation child1;
private Animation child2;
private Animation child3;
@Before
public void setUp() {
node = new Rectangle();
child1 = new AnimationDummy(ONE_SEC);
child2 = new AnimationDummy(TWO_SECS);
child3 = new AnimationDummy(THREE_SECS);
}
@Test
public void testDefaultValues() {
final SequentialTransition t0 = new SequentialTransition();
assertEquals(Duration.ZERO, t0.getTotalDuration());
assertNull(t0.getNode());
assertNull(t0.nodeProperty().get());
assertTrue(t0.getChildren().isEmpty());
assertEquals(DEFAULT_INTERPOLATOR, t0.getInterpolator());
assertNull(t0.getOnFinished());
final SequentialTransition t1 = new SequentialTransition(node);
assertEquals(Duration.ZERO, t1.getTotalDuration());
assertEquals(node, t1.getNode());
assertEquals(node, t1.nodeProperty().get());
assertTrue(t1.getChildren().isEmpty());
assertEquals(DEFAULT_INTERPOLATOR, t1.getInterpolator());
assertNull(t1.getOnFinished());
final SequentialTransition t2 = new SequentialTransition(child1, child2, child3);
assertEquals(SIX_SECS, t2.getTotalDuration());
assertNull(t2.getNode());
assertNull(t2.nodeProperty().get());
assertEquals(Arrays.asList(child1, child2, child3), t2.getChildren());
assertEquals(DEFAULT_INTERPOLATOR, t2.getInterpolator());
assertNull(t2.getOnFinished());
final SequentialTransition t3 = new SequentialTransition(node, child1, child2, child3);
assertEquals(SIX_SECS, t3.getTotalDuration());
assertEquals(node, t3.getNode());
assertEquals(node, t3.nodeProperty().get());
assertEquals(Arrays.asList(child1, child2, child3), t3.getChildren());
assertEquals(DEFAULT_INTERPOLATOR, t3.getInterpolator());
assertNull(t3.getOnFinished());
}
}
