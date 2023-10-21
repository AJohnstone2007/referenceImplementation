package test.com.sun.scenario.animation;
import com.sun.scenario.animation.SplineInterpolator;
import javafx.animation.Interpolator;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
public class SplineInterpolatorTest {
private SplineInterpolator interpolator;
@Before
public void setUp() throws Exception {
interpolator = new SplineInterpolator(0.2, 0.1, 0.3, 0.4);
}
private static void testEqualsAndHashCode(Interpolator one, Interpolator another) {
assertTrue(one.equals(another));
assertTrue(another.equals(one));
assertEquals(one.hashCode(), another.hashCode());
}
private static void testNotEqualsAndHashCode(Interpolator one, Interpolator another) {
assertFalse(one.equals(another));
assertFalse(another.equals(one));
assertFalse(one.hashCode() == another.hashCode());
}
@Test
public void testEqualsAndHashCode() {
Interpolator another = new SplineInterpolator(0.2, 0.1, 0.3, 0.4);
testEqualsAndHashCode(interpolator, another);
}
@Test
public void testNotEqualsAndHashCode() {
Interpolator another = new SplineInterpolator(0.2, 0.1, 0.3, 0.5);
testNotEqualsAndHashCode(interpolator, another);
another = new SplineInterpolator(0.3, 0.5, 0.2, 0.1);
testNotEqualsAndHashCode(interpolator, another);
another = new SplineInterpolator(0.2, 0.1, 0.6, 0.4);
testNotEqualsAndHashCode(interpolator, another);
another = new SplineInterpolator(0.2, 0.14, 0.3, 0.4);
testNotEqualsAndHashCode(interpolator, another);
another = new SplineInterpolator(0.25, 0.1, 0.3, 0.4);
testNotEqualsAndHashCode(interpolator, another);
}
}
