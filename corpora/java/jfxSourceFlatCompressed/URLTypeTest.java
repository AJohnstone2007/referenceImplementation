package test.com.sun.javafx.css;
import com.sun.javafx.css.ParsedValueImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import javafx.css.ParsedValue;
import javafx.scene.text.Font;
import org.junit.Test;
import javafx.css.converter.StringConverter;
import javafx.css.converter.URLConverter;
public class URLTypeTest {
public URLTypeTest() {
}
static final String absClassName = "/test/com/sun/javafx/css/URLTypeTest.class";
static final String classURL = URLTypeTest.class.getResource("URLTypeTest.class").toExternalForm();
final String baseURL = "http://a/b/c/d;p?q";
final String[][] testPairs = new String[][] {
{"file:h" , "file:h"},
{"g" , "http://a/b/c/g"},
{"./g" , "http://a/b/c/g"},
{"g/" , "http://a/b/c/g/"},
{"/g" , null},
{absClassName , classURL},
{"//g" , "http://g"},
{"g?y" , "http://a/b/c/g?y"},
{"#s" , "http://a/b/c/d;p?q#s"},
{"g#s" , "http://a/b/c/g#s"},
{"g?y#s" , "http://a/b/c/g?y#s"},
{";x" , "http://a/b/c/;x"},
{"g;x" , "http://a/b/c/g;x"},
{"g;x?y#s" , "http://a/b/c/g;x?y#s"},
{"", null},
{"." , "http://a/b/c/"},
{"./" , "http://a/b/c/"},
{".." , "http://a/b/"},
{"../" , "http://a/b/"},
{"../g" , "http://a/b/g"},
{"../.." , "http://a/"},
{"../../" , "http://a/"},
{"../../g" , "http://a/g"}
};
@Test
public void testConvert() {
ParsedValue<ParsedValue[],String>[] urls = new ParsedValue[testPairs.length];
for(int n=0; n<testPairs.length; n++) {
ParsedValue[] values = new ParsedValue[] {
new ParsedValueImpl<String,String>(testPairs[n][0], StringConverter.getInstance()),
new ParsedValueImpl<String, String>(baseURL, null)
};
urls[n] = new ParsedValueImpl<ParsedValue[],String>(values, URLConverter.getInstance());
};
ParsedValue<ParsedValue<ParsedValue[],String>[],String[]> value =
new ParsedValueImpl<ParsedValue<ParsedValue[],String>[],String[]>(urls, URLConverter.SequenceConverter.getInstance());
Font font = null;
String[] result = value.convert(font);
assertEquals(testPairs.length, result.length);
for(int n=0; n<result.length; n++) {
String msg = "[" + n + "]" + "resolve \'" + testPairs[n][0] + "\'";
assertEquals(msg, testPairs[n][1], result[n]);
}
}
}
