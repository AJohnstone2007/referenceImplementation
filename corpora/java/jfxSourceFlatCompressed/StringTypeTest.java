package test.com.sun.javafx.css;
import com.sun.javafx.css.ParsedValueImpl;
import javafx.css.converter.StringConverter;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
public class StringTypeTest {
public StringTypeTest() {
}
@BeforeClass
public static void setUpClass() throws Exception {
}
@AfterClass
public static void tearDownClass() throws Exception {
}
@Test
public void testConvert() {
ParsedValue<String,String> value = new ParsedValueImpl<String,String>("test", StyleConverter.getStringConverter());
Font font = null;
String expResult = "test";
String result = value.convert(font);
assertEquals(expResult, result);
ParsedValue<String,String>[] values = new ParsedValue[] {
new ParsedValueImpl<String,String>("hello", StyleConverter.getStringConverter()),
new ParsedValueImpl<String,String>("world", StyleConverter.getStringConverter())
};
ParsedValue<ParsedValue<String,String>[], String[]> seq =
new ParsedValueImpl<ParsedValue<String,String>[], String[]>(values, StringConverter.SequenceConverter.getInstance());
String[] strings = seq.convert(font);
assertEquals("hello", strings[0]);
assertEquals("world", strings[1]);
}
}
