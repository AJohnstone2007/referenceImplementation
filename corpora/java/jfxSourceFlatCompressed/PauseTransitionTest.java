package test.javafx.animation;
import javafx.animation.PauseTransition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import javafx.util.Duration;
import org.junit.Test;
public class PauseTransitionTest {
private static Duration DEFAULT_DURATION = Duration.millis(400);
private static double EPSILON = 1e-12;
private static Duration ONE_SEC = Duration.millis(1000);
private static Duration TWO_SECS = Duration.millis(2000);
@Test
public void testDefaultValues() {
final PauseTransition t0 = new PauseTransition();
assertEquals(DEFAULT_DURATION, t0.getDuration());
assertEquals(DEFAULT_DURATION, t0.getCycleDuration());
assertNull(t0.getOnFinished());
final PauseTransition t1 = new PauseTransition(ONE_SEC);
assertEquals(ONE_SEC, t1.getDuration());
assertNull(t1.getOnFinished());
}
@Test
public void testDefaultValuesFromProperties() {
final PauseTransition t0 = new PauseTransition();
assertEquals(DEFAULT_DURATION, t0.durationProperty().get());
assertNull(t0.onFinishedProperty().get());
final PauseTransition t1 = new PauseTransition(ONE_SEC);
assertEquals(ONE_SEC, t1.durationProperty().get());
assertNull(t1.onFinishedProperty().get());
}
}
