package test.javafx.scene.layout;
import javafx.geometry.Insets;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import org.junit.Test;
import static org.junit.Assert.*;
public class BackgroundFillTest {
@Test public void nullPaintDefaultsToTransparent() {
BackgroundFill fill = new BackgroundFill(null, new CornerRadii(3), new Insets(4));
assertEquals(Color.TRANSPARENT, fill.getFill());
}
@Test public void nullRadiusDefaultsToEmpty() {
BackgroundFill fill = new BackgroundFill(Color.ORANGE, null, new Insets(2));
assertEquals(CornerRadii.EMPTY, fill.getRadii());
}
@Test public void nullInsetsDefaultsToEmpty() {
BackgroundFill fill = new BackgroundFill(Color.ORANGE, new CornerRadii(2), null);
assertEquals(Insets.EMPTY, fill.getInsets());
}
@Test public void equivalentFills() {
BackgroundFill a = new BackgroundFill(Color.ORANGE, new CornerRadii(2), new Insets(3));
BackgroundFill b = new BackgroundFill(Color.ORANGE, new CornerRadii(2), new Insets(3));
assertEquals(a, b);
}
@Test public void differentFills() {
BackgroundFill a = new BackgroundFill(Color.ORANGE, new CornerRadii(2), new Insets(3));
BackgroundFill b = new BackgroundFill(Color.RED, new CornerRadii(2), new Insets(3));
assertFalse(a.equals(b));
}
@Test public void differentFills2() {
BackgroundFill a = new BackgroundFill(Color.ORANGE, new CornerRadii(2), new Insets(3));
BackgroundFill b = new BackgroundFill(Color.ORANGE, new CornerRadii(1), new Insets(3));
assertFalse(a.equals(b));
}
@Test public void differentFills3() {
BackgroundFill a = new BackgroundFill(Color.ORANGE, new CornerRadii(2), new Insets(3));
BackgroundFill b = new BackgroundFill(Color.ORANGE, new CornerRadii(2), new Insets(1));
assertFalse(a.equals(b));
}
@Test public void equalsAgainstNull() {
BackgroundFill a = new BackgroundFill(Color.ORANGE, new CornerRadii(2), new Insets(3));
assertFalse(a.equals(null));
}
@Test public void equalsAgainstRandomObject() {
BackgroundFill a = new BackgroundFill(Color.ORANGE, new CornerRadii(2), new Insets(3));
assertFalse(a.equals("Some random object"));
}
@Test public void equivalentHaveSameHash() {
BackgroundFill a = new BackgroundFill(Color.ORANGE, new CornerRadii(2), new Insets(3));
BackgroundFill b = new BackgroundFill(Color.ORANGE, new CornerRadii(2), new Insets(3));
assertEquals(a.hashCode(), b.hashCode());
}
@Test public void toStringCausesNoError() {
BackgroundFill f = new BackgroundFill(null, null, null);
f.toString();
}
}
