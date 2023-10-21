package test.javafx.util.converter;
import javafx.util.converter.CharacterStringConverter;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
public class CharacterStringConverterTest {
private CharacterStringConverter converter;
private final char char_c = 'c';
private final char char_C = 'C';
@Before public void setup() {
converter = new CharacterStringConverter();
}
@Test public void fromString_testValidStringInput_lowercase() {
assertEquals((Object)char_c, converter.fromString("c"));
}
@Test public void fromString_testValidStringInput_uppercase() {
assertEquals((Object)char_C, converter.fromString("C"));
}
@Test public void fromString_testValidStringInput_differentCase_one() {
assertNotSame((Object)char_C, converter.fromString("c"));
}
@Test public void fromString_testValidStringInput_differentCase_two() {
assertNotSame((Object)char_c, converter.fromString("C"));
}
@Test public void fromString_testValidStringInputWithWhiteSpace_lowercase() {
assertEquals((Object)char_c, converter.fromString("     c     "));
}
@Test public void fromString_testValidStringInputWithWhiteSpace_uppercase() {
assertEquals((Object)char_C, converter.fromString("     C     "));
}
@Test public void toString_lowercase() {
assertEquals("c", converter.toString(char_c));
}
@Test public void toString_uppercase() {
assertEquals("C", converter.toString(char_C));
}
}
