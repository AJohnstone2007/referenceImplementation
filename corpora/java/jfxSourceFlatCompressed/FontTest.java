package test.javafx.scene.text;
import com.sun.javafx.scene.text.FontHelper;
import static test.com.sun.javafx.test.TestHelper.assertImmutableList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;
import java.util.List;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.junit.Test;
public class FontTest {
@Test
public void testGetFamilies() {
List<String> families = Font.getFamilies();
assertNotNull(families);
assertImmutableList(families);
}
@Test
public void testGetAllFontNames() {
List<String> names = Font.getFontNames();
assertNotNull(names);
assertImmutableList(names);
}
@Test
public void testGetFontNames() {
String family = Font.getFamilies().get(0);
List<String> names = Font.getFontNames(family);
assertNotNull(names);
assertImmutableList(names);
}
@Test
public void testFontFactory1() {
Font font = Font.font("Amble", FontWeight.NORMAL,
FontPosture.ITALIC, 30);
assertEquals("Amble", font.getFamily());
assertEquals("Amble Italic", font.getName());
assertEquals(30f, (float) font.getSize());
font = Font.font(null, null, null, -1);
assertTrue(0 < font.getSize());
}
@Test
public void testFontFactory2() {
Font font = Font.font("Amble", FontWeight.BOLD, 30);
assertEquals("Amble", font.getFamily());
assertEquals("Amble Bold", font.getName());
assertEquals(30f, (float) font.getSize());
}
@Test
public void testFontFactory3() {
Font font = Font.font("Amble", FontPosture.ITALIC, 30);
assertEquals("Amble", font.getFamily());
assertEquals("Amble Italic", font.getName());
assertEquals(30f, (float) font.getSize());
}
@Test
public void testDefault() {
Font font = Font.getDefault();
}
@Test
public void testCtor2() {
Font font = new Font(20);
assertEquals(20f, (float) font.getSize());
}
@Test
public void testCtor3() {
Font font = new Font("Amble Bold", 32);
assertEquals("Amble", font.getFamily());
assertEquals(32f, (float) font.getSize());
assertEquals("Amble Bold", font.getName());
Font def = new Font(null, -1);
assertTrue(0 < def.getSize());
}
@Test
public void testToString() {
assertNotNull(new Font(12).toString());
}
@Test
public void testEqualsHashCode() {
Font f1 = new Font(12);
Font f2 = new Font(12);
assertEquals(f1, f2);
assertEquals(f1.hashCode(), f2.hashCode());
Font f3 = new Font(40);
assertNotSame(f1, f3);
assertNotSame(f1.hashCode(), f3.hashCode());
Font f4 = new Font(40);
assertEquals(f3, f4);
}
@Test
public void testSetNative() {
FontHelper.setNativeFont(new Font(12), new Object(), "", "", "");
}
}
