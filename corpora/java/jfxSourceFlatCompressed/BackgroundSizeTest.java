package test.javafx.scene.layout;
import javafx.scene.layout.BackgroundSize;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
public class BackgroundSizeTest {
@Test public void instanceCreation() {
BackgroundSize size = new BackgroundSize(1, 2, true, false, true, false);
assertEquals(1, size.getWidth(), 0);
assertEquals(2, size.getHeight(), 0);
assertTrue(size.isWidthAsPercentage());
assertFalse(size.isHeightAsPercentage());
assertTrue(size.isContain());
assertFalse(size.isCover());
}
@Test public void instanceCreation2() {
BackgroundSize size = new BackgroundSize(0, Double.MAX_VALUE, false, true, false, true);
assertEquals(0, size.getWidth(), 0);
assertEquals(Double.MAX_VALUE, size.getHeight(), 0);
assertFalse(size.isWidthAsPercentage());
assertTrue(size.isHeightAsPercentage());
assertFalse(size.isContain());
assertTrue(size.isCover());
}
@Test public void instanceCreation3() {
BackgroundSize size = new BackgroundSize(.5, .5, true, true, false, false);
assertEquals(.5, size.getWidth(), 0);
assertEquals(.5, size.getHeight(), 0);
assertTrue(size.isWidthAsPercentage());
assertTrue(size.isHeightAsPercentage());
assertFalse(size.isContain());
assertFalse(size.isCover());
}
@Test(expected = IllegalArgumentException.class)
public void negativeWidthThrowsException() {
new BackgroundSize(-.2, 1, true, true, false, false);
}
@Test(expected = IllegalArgumentException.class)
public void negativeWidthThrowsException2() {
new BackgroundSize(-2, 1, true, true, false, false);
}
@Ignore("JDK-8234090")
@Test(expected = IllegalArgumentException.class)
public void positiveInfinityWidthThrowsException() {
new BackgroundSize(Double.POSITIVE_INFINITY, 1, true, true, false, false);
}
@Ignore("JDK-8234090")
@Test(expected = IllegalArgumentException.class)
public void negativeInfinityWidthThrowsException() {
new BackgroundSize(Double.NEGATIVE_INFINITY, 1, true, true, false, false);
}
@Ignore("JDK-8234090")
@Test(expected = IllegalArgumentException.class)
public void nanWidthThrowsException() {
new BackgroundSize(Double.NaN, 1, true, true, false, false);
}
@Test public void negativeZeroWidthIsOK() {
BackgroundSize size = new BackgroundSize(-0, 1, true, true, false, false);
assertEquals(0, size.getWidth(), 0);
}
@Test public void autoWidthIsOK() {
BackgroundSize size = new BackgroundSize(-1, 1, true, true, false, false);
assertEquals(BackgroundSize.AUTO, size.getWidth(), 0);
}
@Test(expected = IllegalArgumentException.class)
public void negativeHeightThrowsException() {
new BackgroundSize(1, -.1, true, true, false, false);
}
@Test(expected = IllegalArgumentException.class)
public void negativeHeightThrowsException2() {
new BackgroundSize(1, -2, true, true, false, false);
}
@Ignore("JDK-8234090")
@Test(expected = IllegalArgumentException.class)
public void positiveInfinityHeightThrowsException() {
new BackgroundSize(1, Double.POSITIVE_INFINITY, true, true, false, false);
}
@Ignore("JDK-8234090")
@Test(expected = IllegalArgumentException.class)
public void negativeInfinityHeightThrowsException() {
new BackgroundSize(1, Double.NEGATIVE_INFINITY, true, true, false, false);
}
@Ignore("JDK-8234090")
@Test(expected = IllegalArgumentException.class)
public void nanHeightThrowsException() {
new BackgroundSize(1, Double.NaN, true, true, false, false);
}
@Test public void negativeZeroHeightIsOK() {
BackgroundSize size = new BackgroundSize(1, -0, true, true, false, false);
assertEquals(0, size.getHeight(), 0);
}
@Test public void autoHeightIsOK() {
BackgroundSize size = new BackgroundSize(1, -1, true, true, false, false);
assertEquals(BackgroundSize.AUTO, size.getHeight(), 0);
}
@Test public void equivalent() {
BackgroundSize a = new BackgroundSize(1, .5, true, true, true, true);
BackgroundSize b = new BackgroundSize(1, .5, true, true, true, true);
assertEquals(a, b);
}
@Test public void equivalent2() {
BackgroundSize a = new BackgroundSize(1, .5, false, true, true, true);
BackgroundSize b = new BackgroundSize(1, .5, false, true, true, true);
assertEquals(a, b);
}
@Test public void equivalent3() {
BackgroundSize a = new BackgroundSize(1, .5, true, false, true, true);
BackgroundSize b = new BackgroundSize(1, .5, true, false, true, true);
assertEquals(a, b);
}
@Test public void equivalent4() {
BackgroundSize a = new BackgroundSize(1, .5, true, true, false, true);
BackgroundSize b = new BackgroundSize(1, .5, true, true, false, true);
assertEquals(a, b);
}
@Test public void equivalent5() {
BackgroundSize a = new BackgroundSize(1, .5, true, true, true, false);
BackgroundSize b = new BackgroundSize(1, .5, true, true, true, false);
assertEquals(a, b);
}
@Test public void equivalentHaveSameHashCode() {
BackgroundSize a = new BackgroundSize(1, .5, true, true, true, true);
BackgroundSize b = new BackgroundSize(1, .5, true, true, true, true);
assertEquals(a.hashCode(), b.hashCode());
}
@Test public void equivalentHaveSameHashCode2() {
BackgroundSize a = new BackgroundSize(1, .5, false, true, true, true);
BackgroundSize b = new BackgroundSize(1, .5, false, true, true, true);
assertEquals(a.hashCode(), b.hashCode());
}
@Test public void equivalentHaveSameHashCode3() {
BackgroundSize a = new BackgroundSize(1, .5, true, false, true, true);
BackgroundSize b = new BackgroundSize(1, .5, true, false, true, true);
assertEquals(a.hashCode(), b.hashCode());
}
@Test public void equivalentHaveSameHashCode4() {
BackgroundSize a = new BackgroundSize(1, .5, true, true, false, true);
BackgroundSize b = new BackgroundSize(1, .5, true, true, false, true);
assertEquals(a.hashCode(), b.hashCode());
}
@Test public void equivalentHaveSameHashCode5() {
BackgroundSize a = new BackgroundSize(1, .5, true, true, true, false);
BackgroundSize b = new BackgroundSize(1, .5, true, true, true, false);
assertEquals(a.hashCode(), b.hashCode());
}
@Test public void notEquivalent() {
BackgroundSize a = new BackgroundSize(0, .5, true, true, true, true);
BackgroundSize b = new BackgroundSize(1, .5, true, true, true, true);
assertFalse(a.equals(b));
}
@Test public void notEquivalent2() {
BackgroundSize a = new BackgroundSize(1, 1, true, true, true, true);
BackgroundSize b = new BackgroundSize(1, .5, true, true, true, true);
assertFalse(a.equals(b));
}
@Test public void notEquivalent3() {
BackgroundSize a = new BackgroundSize(1, .5, false, true, true, true);
BackgroundSize b = new BackgroundSize(1, .5, true, true, true, true);
assertFalse(a.equals(b));
}
@Test public void notEquivalent4() {
BackgroundSize a = new BackgroundSize(1, .5, true, false, true, true);
BackgroundSize b = new BackgroundSize(1, .5, true, true, true, true);
assertFalse(a.equals(b));
}
@Test public void notEquivalent5() {
BackgroundSize a = new BackgroundSize(1, .5, true, true, false, true);
BackgroundSize b = new BackgroundSize(1, .5, true, true, true, true);
assertFalse(a.equals(b));
}
@Test public void notEquivalent6() {
BackgroundSize a = new BackgroundSize(1, .5, true, true, true, false);
BackgroundSize b = new BackgroundSize(1, .5, true, true, true, true);
assertFalse(a.equals(b));
}
@Test public void notEqualToNull() {
BackgroundSize a = new BackgroundSize(1, .5, true, true, true, false);
assertFalse(a.equals(null));
}
@Test public void notEqualToRandom() {
BackgroundSize a = new BackgroundSize(1, .5, true, true, true, false);
assertFalse(a.equals("Some random object"));
}
}
