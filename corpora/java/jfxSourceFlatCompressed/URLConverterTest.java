package test.com.sun.javafx.css.converters;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;
import com.sun.javafx.css.ParsedValueImpl;
import javafx.css.converter.URLConverter;
import org.junit.Test;
import static org.junit.Assert.*;
public class URLConverterTest {
public URLConverterTest() {
}
@Test
public void testGetInstance() {
StyleConverter<ParsedValue[],String> result = URLConverter.getInstance();
assertNotNull(result);
}
@Test
public void testConvertWithNullBaseURL() {
ParsedValue[] values = new ParsedValue[] {
new ParsedValueImpl<String,String>("test/javafx/css/converter/some.txt", null),
new ParsedValueImpl<String,String>(null,null)
};
ParsedValueImpl<ParsedValue[], String> value =
new ParsedValueImpl<ParsedValue[], String>(values, URLConverter.getInstance());
Font font = null;
ClassLoader cl = Thread.currentThread().getContextClassLoader();
String expResult = cl.getResource("test/javafx/css/converter/some.txt").toExternalForm();
String result = value.convert(font);
assertEquals(expResult, result);
}
public void testConvertWithBaseURL() {
ClassLoader cl = Thread.currentThread().getContextClassLoader();
String base = cl.getResource("com/..").toExternalForm();
ParsedValue[] values = new ParsedValue[] {
new ParsedValueImpl<String,String>("test/javafx/css/converter/some.txt", null),
new ParsedValueImpl<String,String>(base,null)
};
ParsedValueImpl<ParsedValue[], String> value =
new ParsedValueImpl<ParsedValue[], String>(values, URLConverter.getInstance());
Font font = null;
String expResult = cl.getResource("test/javafx/css/converter/some.txt").toExternalForm();
String result = value.convert(font);
assertEquals(expResult, result);
}
@Test
public void testConvertWithAbsoluteURLAndNullBaseURL() {
ClassLoader cl = Thread.currentThread().getContextClassLoader();
String expResult = cl.getResource("test/javafx/css/converter/some.txt").toExternalForm();
ParsedValue[] values = new ParsedValue[] {
new ParsedValueImpl<String,String>(expResult, null),
new ParsedValueImpl<String,String>(null,null)
};
ParsedValueImpl<ParsedValue[], String> value =
new ParsedValueImpl<ParsedValue[], String>(values, URLConverter.getInstance());
Font font = null;
String result = value.convert(font);
assertEquals(expResult, result);
}
@Test
public void testConvertWithAbsoluteURLWithBaseURL() {
ClassLoader cl = Thread.currentThread().getContextClassLoader();
String baseURL = cl.getResource("com/..").toExternalForm();
String expResult = cl.getResource("test/javafx/css/converter/some.txt").toExternalForm();
ParsedValue[] values = new ParsedValue[] {
new ParsedValueImpl<String,String>(expResult, null),
new ParsedValueImpl<String,String>(baseURL,null)
};
ParsedValueImpl<ParsedValue[], String> value =
new ParsedValueImpl<ParsedValue[], String>(values, URLConverter.getInstance());
Font font = null;
String result = value.convert(font);
assertEquals(expResult, result);
}
@Test
public void testConvertWithDataURI() {
String dataUri = "data:text/plain;charset=utf-8;base64,SGVsbG8sIFdvcmxkIQ==";
ParsedValue[] values = new ParsedValue[] { new ParsedValueImpl<String,String>(dataUri, null) };
ParsedValueImpl<ParsedValue[], String> value = new ParsedValueImpl<>(values, URLConverter.getInstance());
String result = value.convert(null);
assertEquals(dataUri, result);
}
}
