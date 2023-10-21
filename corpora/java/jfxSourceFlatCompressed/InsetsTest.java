package test.javafx.geometry;
import javafx.geometry.Insets;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
public class InsetsTest {
@Test
public void testEquals() {
Insets p1 = new Insets(0, 0, 0, 0);
Insets p2 = new Insets(0, 1, 1, 1);
Insets p3 = new Insets(1, 0, 1, 1);
assertTrue(p1.equals(p1));
assertTrue(p1.equals(new Insets(0, 0, 0, 0)));
assertFalse(p1.equals(new Object()));
assertFalse(p1.equals(p2));
assertFalse(p1.equals(p3));
}
@Test
public void testHash() {
Insets p1 = new Insets(0, 0, 0, 0);
Insets p2 = new Insets(0, 1, 0, 0);
Insets p3 = new Insets(0, 1, 0, 0);
assertEquals(p3.hashCode(), p2.hashCode());
assertEquals(p3.hashCode(), p2.hashCode());
assertFalse(p1.hashCode() == p2.hashCode());
}
@Test
public void testToString() {
Insets p1 = new Insets(0, 0, 0, 0);
assertNotNull(p1.toString());
}
}
