package test.com.sun.javafx.css;
import com.sun.javafx.css.ParsedValueImpl;
import static org.junit.Assert.*;
import javafx.css.ParsedValue;
import javafx.scene.Cursor;
import javafx.scene.text.Font;
import org.junit.Test;
import javafx.css.converter.CursorConverter;
public class CursorTypeTest {
public CursorTypeTest() {
}
@Test
public void testConvert() {
ParsedValue<String,Cursor> value = new ParsedValueImpl<String,Cursor>("hand", CursorConverter.getInstance());
Font font = null;
Cursor expResult = Cursor.HAND;
Cursor result = value.convert(font);
assertEquals(expResult, result);
}
@Test
public void testConvert_with_hyphen() {
ParsedValue<String,Cursor> value = new ParsedValueImpl<String,Cursor>("open-hand", CursorConverter.getInstance());
Font font = null;
Cursor expResult = Cursor.OPEN_HAND;
Cursor result = value.convert(font);
assertEquals(expResult, result);
}
@Test
public void testConvert_with_Cursor_dot() {
ParsedValue<String,Cursor> value = new ParsedValueImpl<String,Cursor>("Cursor.open-hand", CursorConverter.getInstance());
Font font = null;
Cursor expResult = Cursor.OPEN_HAND;
Cursor result = value.convert(font);
assertEquals(expResult, result);
}
@Test
public void testConvert_with_package_name() {
ParsedValue<String,Cursor> value = new ParsedValueImpl<String,Cursor>("javafx.scene.Cursor.open-hand", CursorConverter.getInstance());
Font font = null;
Cursor expResult = Cursor.OPEN_HAND;
Cursor result = value.convert(font);
assertEquals(expResult, result);
}
@Test
public void testConvert_with_package_name_only() {
ParsedValue<String,Cursor> value = new ParsedValueImpl<String,Cursor>("javafx.scene.Cursor.", CursorConverter.getInstance());
Font font = null;
Cursor expResult = Cursor.DEFAULT;
Cursor result = value.convert(font);
assertEquals(expResult, result);
}
@Test
public void testConvert_with_empty_string() {
ParsedValue<String,Cursor> value = new ParsedValueImpl<String,Cursor>("", CursorConverter.getInstance());
Font font = null;
Cursor expResult = Cursor.DEFAULT;
Cursor result = value.convert(font);
assertEquals(expResult, result);
}
@Test
public void testConvert_with_null() {
ParsedValue<String,Cursor> value = new ParsedValueImpl<String,Cursor>(null, CursorConverter.getInstance());
Font font = null;
Cursor expResult = Cursor.DEFAULT;
Cursor result = value.convert(font);
assertEquals(expResult, result);
}
@Test
public void testConvert_with_bogus_value() {
ParsedValue<String,Cursor> value = new ParsedValueImpl<String,Cursor>("bogus", CursorConverter.getInstance());
Font font = null;
Cursor expResult = Cursor.DEFAULT;
Cursor result = value.convert(font);
assertEquals(expResult, result);
}
}
