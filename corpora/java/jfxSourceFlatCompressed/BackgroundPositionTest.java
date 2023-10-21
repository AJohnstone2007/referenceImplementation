package test.javafx.scene.layout;
import javafx.geometry.Side;
import javafx.scene.layout.BackgroundPosition;
import org.junit.Test;
import static org.junit.Assert.*;
public class BackgroundPositionTest {
@Test public void valuesAreCorrectAfterConstruction() {
BackgroundPosition pos = new BackgroundPosition(Side.LEFT, 10, false, Side.TOP, 20, false);
assertEquals(Side.LEFT, pos.getHorizontalSide());
assertEquals(10, pos.getHorizontalPosition(), 0);
assertEquals(false, pos.isHorizontalAsPercentage());
assertEquals(Side.TOP, pos.getVerticalSide());
assertEquals(20, pos.getVerticalPosition(), 0);
assertEquals(false, pos.isVerticalAsPercentage());
}
@Test public void valuesAreCorrectAfterConstruction2() {
BackgroundPosition pos = new BackgroundPosition(Side.RIGHT, 10, true, Side.BOTTOM, 20, true);
assertEquals(Side.RIGHT, pos.getHorizontalSide());
assertEquals(10, pos.getHorizontalPosition(), 0);
assertEquals(true, pos.isHorizontalAsPercentage());
assertEquals(Side.BOTTOM, pos.getVerticalSide());
assertEquals(20, pos.getVerticalPosition(), 0);
assertEquals(true, pos.isVerticalAsPercentage());
}
@Test public void nullHorizontalSideEqualsLEFT() {
BackgroundPosition pos = new BackgroundPosition(null, 10, true, Side.BOTTOM, 20, true);
assertEquals(Side.LEFT, pos.getHorizontalSide());
}
@Test(expected = IllegalArgumentException.class)
public void TOPHorizontalSideFails() {
new BackgroundPosition(Side.TOP, 10, true, Side.BOTTOM, 20, true);
}
@Test(expected = IllegalArgumentException.class)
public void BOTTOMHorizontalSideFails() {
new BackgroundPosition(Side.BOTTOM, 10, true, Side.BOTTOM, 20, true);
}
@Test public void negativeHorizontalPositionOK() {
BackgroundPosition pos = new BackgroundPosition(null, -10, true, Side.BOTTOM, 20, true);
assertEquals(-10, pos.getHorizontalPosition(), 0);
}
@Test public void nullVerticalSideEqualsTOP() {
BackgroundPosition pos = new BackgroundPosition(Side.LEFT, 10, true, null, 20, true);
assertEquals(Side.TOP, pos.getVerticalSide());
}
@Test(expected = IllegalArgumentException.class)
public void LEFTVerticalSideFails() {
new BackgroundPosition(Side.LEFT, 10, true, Side.LEFT, 20, true);
}
@Test(expected = IllegalArgumentException.class)
public void RIGHTVerticalSideFails() {
new BackgroundPosition(Side.LEFT, 10, true, Side.RIGHT, 20, true);
}
@Test public void negativeVerticalPositionOK() {
BackgroundPosition pos = new BackgroundPosition(Side.LEFT, 10, true, Side.BOTTOM, -20, true);
assertEquals(-20, pos.getVerticalPosition(), 0);
}
@Test public void equivalence() {
BackgroundPosition pos = new BackgroundPosition(Side.LEFT, 0, true, Side.TOP, 0, true);
assertEquals(BackgroundPosition.DEFAULT, pos);
}
@Test public void equivalence2() {
BackgroundPosition a = new BackgroundPosition(Side.LEFT, 10, false, Side.TOP, 20, false);
BackgroundPosition b = new BackgroundPosition(Side.LEFT, 10, false, Side.TOP, 20, false);
assertEquals(a, b);
}
@Test public void unequal() {
BackgroundPosition a = new BackgroundPosition(Side.LEFT, 10, false, Side.TOP, 20, false);
BackgroundPosition b = new BackgroundPosition(Side.RIGHT, 10, false, Side.TOP, 20, false);
assertFalse(a.equals(b));
}
@Test public void unequal2() {
BackgroundPosition a = new BackgroundPosition(Side.LEFT, 10, false, Side.TOP, 20, false);
BackgroundPosition b = new BackgroundPosition(Side.LEFT, 0, false, Side.TOP, 20, false);
assertFalse(a.equals(b));
}
@Test public void unequal3() {
BackgroundPosition a = new BackgroundPosition(Side.LEFT, 10, false, Side.TOP, 20, false);
BackgroundPosition b = new BackgroundPosition(Side.LEFT, 10, true, Side.TOP, 20, false);
assertFalse(a.equals(b));
}
@Test public void unequal4() {
BackgroundPosition a = new BackgroundPosition(Side.LEFT, 10, false, Side.TOP, 20, false);
BackgroundPosition b = new BackgroundPosition(Side.LEFT, 10, false, Side.BOTTOM, 20, false);
assertFalse(a.equals(b));
}
@Test public void unequal5() {
BackgroundPosition a = new BackgroundPosition(Side.LEFT, 10, false, Side.TOP, 20, false);
BackgroundPosition b = new BackgroundPosition(Side.LEFT, 10, false, Side.TOP, 0, false);
assertFalse(a.equals(b));
}
@Test public void unequal6() {
BackgroundPosition a = new BackgroundPosition(Side.LEFT, 10, false, Side.TOP, 20, false);
BackgroundPosition b = new BackgroundPosition(Side.LEFT, 10, false, Side.TOP, 20, true);
assertFalse(a.equals(b));
}
@Test public void notEqualWithNull() {
BackgroundPosition a = new BackgroundPosition(Side.LEFT, 10, false, Side.TOP, 20, false);
assertFalse(a.equals(null));
}
@Test public void notEqualWithRandom() {
BackgroundPosition a = new BackgroundPosition(Side.LEFT, 10, false, Side.TOP, 20, false);
assertFalse(a.equals("Random Object"));
}
@Test public void equalPositionsHaveSameHashCode() {
BackgroundPosition pos = new BackgroundPosition(Side.LEFT, 0, true, Side.TOP, 0, true);
assertEquals(BackgroundPosition.DEFAULT.hashCode(), pos.hashCode());
}
@Test public void CENTER() {
assertEquals(Side.LEFT, BackgroundPosition.CENTER.getHorizontalSide());
assertEquals(.5, BackgroundPosition.CENTER.getHorizontalPosition(), 0);
assertTrue(BackgroundPosition.CENTER.isHorizontalAsPercentage());
assertEquals(Side.TOP, BackgroundPosition.CENTER.getVerticalSide());
assertEquals(.5, BackgroundPosition.CENTER.getVerticalPosition(), 0);
assertTrue(BackgroundPosition.CENTER.isVerticalAsPercentage());
}
}
