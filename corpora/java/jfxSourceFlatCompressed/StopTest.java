package test.javafx.scene.paint;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
public class StopTest {
@Test
public void testStop() {
Color color = Color.rgb(0xAA, 0xBB, 0xCC);
Stop stop = new Stop(0.5f, color);
assertEquals(color.getRed(), stop.getColor().getRed(), 0.0001);
assertEquals(color.getGreen(), stop.getColor().getGreen(), 0.0001);
assertEquals(color.getBlue(), stop.getColor().getBlue(), 0.0001);
assertEquals(0.5f, stop.getOffset(), 0.0001);
}
@Test
public void testEquals() {
Color color1 = Color.rgb(0xAA, 0xBB, 0xCC);
Color color2 = Color.rgb(0, 0, 0);
Stop basic = new Stop(0.2f, color1);
Stop equal = new Stop(0.2f, color1);
Stop diffColor = new Stop(0.2f, color2);
Stop diffOffset = new Stop(0.4f, color1);
Stop nullColor = new Stop(0.2f, null);
Stop nullColor2 = new Stop(0.2f, null);
assertFalse(basic.equals(null));
assertFalse(basic.equals(new Object()));
assertTrue(basic.equals(basic));
assertTrue(basic.equals(equal));
assertFalse(basic.equals(diffColor));
assertFalse(basic.equals(diffOffset));
assertTrue(nullColor.equals(nullColor2));
assertFalse(nullColor.equals(basic));
}
@Test
public void testHashCode() {
Color color1 = Color.rgb(0xAA, 0xBB, 0xCC);
Color color2 = Color.rgb(0xAA, 0xBB, 0xCC);
Color color3 = Color.rgb(0, 0, 0);
Stop basic = new Stop(0.2f, color1);
Stop equal = new Stop(0.2f, color2);
Stop different1 = new Stop(0.4f, color1);
Stop different2 = new Stop(0.2f, color3);
int code = basic.hashCode();
int second = basic.hashCode();
assertTrue(code == second);
assertTrue(code == equal.hashCode());
assertFalse(code == different1.hashCode());
assertFalse(code == different2.hashCode());
}
@Test
public void testToString() {
Stop empty = new Stop(0, Color.TRANSPARENT);
Stop nonempty = new Stop(0.5f, Color.rgb(0xAA, 0xBB, 0xCC, 0.5f));
String s = empty.toString();
assertNotNull(s);
assertFalse(s.isEmpty());
s = nonempty.toString();
assertNotNull(s);
assertFalse(s.isEmpty());
}
}
