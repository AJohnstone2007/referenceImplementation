package test.com.sun.javafx.util;
import com.sun.javafx.util.DataURI;
import org.junit.Test;
import static org.junit.Assert.*;
public class DataURITest {
@Test
public void testMissingDataSeparatorIsInvalid() {
String data = "data:";
DataURI uri = DataURI.tryParse(data);
assertFalse(DataURI.matchScheme(data));
assertNull(uri);
}
@Test(expected = IllegalArgumentException.class)
public void testParametersListWithoutKeyValuePairsIsInvalid() {
String data = "data:foo;bar;baz,";
assertTrue(DataURI.matchScheme(data));
DataURI uri = DataURI.tryParse(data);
}
@Test
public void testEmptyDataBase64EncodedIsValid() {
String data = "data:base64,";
DataURI uri = DataURI.tryParse(data);
assertTrue(DataURI.matchScheme(data));
assertNotNull(uri);
assertTrue(uri.isBase64());
assertEquals(0, uri.getData().length);
}
@Test
public void testDataSchemeIsAcceptedCaseInvariant() {
String data = "DATA:,";
DataURI uri = DataURI.tryParse(data);
assertTrue(DataURI.matchScheme(data));
assertNotNull(uri);
assertEquals("text", uri.getMimeType());
assertEquals("plain", uri.getMimeSubtype());
assertEquals(0, uri.getParameters().size());
assertEquals(0, uri.getData().length);
}
@Test
public void testBase64TokenIsAcceptedCaseInvariant() {
String data = "data:BASE64,";
DataURI uri = DataURI.tryParse(data);
assertTrue(DataURI.matchScheme(data));
assertNotNull(uri);
assertTrue(uri.isBase64());
assertEquals(0, uri.getData().length);
}
@Test
public void testLeadingOrTrailingWhitespaceIsAcceptable() {
String data = "  data:,foo  ";
DataURI uri = DataURI.tryParse(data);
assertTrue(DataURI.matchScheme(data));
assertNotNull(uri);
assertEquals(3, uri.getData().length);
}
@Test
public void testParseTextPlain() {
String data = "data:,Hello%2C%20World!";
DataURI uri = DataURI.tryParse(data);
assertTrue(DataURI.matchScheme(data));
assertNotNull(uri);
assertFalse(uri.isBase64());
assertEquals("text", uri.getMimeType());
assertEquals("plain", uri.getMimeSubtype());
assertEquals(0, uri.getParameters().size());
assertEquals("Hello, World!", new String(uri.getData()));
}
@Test
public void testParseTextPlainBase64Encoded() {
String data = "data:text/plain;base64,SGVsbG8sIFdvcmxkIQ==";
DataURI uri = DataURI.tryParse(data);
assertTrue(DataURI.matchScheme(data));
assertNotNull(uri);
assertTrue(uri.isBase64());
assertEquals("text", uri.getMimeType());
assertEquals("plain", uri.getMimeSubtype());
assertEquals(0, uri.getParameters().size());
assertEquals("Hello, World!", new String(uri.getData()));
}
@Test
public void testParseTextHtmlWithParameter() {
String data = "data:text/html;foo=bar,%3Ch1%3EHello%2C%20World!%3C%2Fh1%3E";
DataURI uri = DataURI.tryParse(data);
assertTrue(DataURI.matchScheme(data));
assertNotNull(uri);
assertFalse(uri.isBase64());
assertEquals("text", uri.getMimeType());
assertEquals("html", uri.getMimeSubtype());
assertEquals(1, uri.getParameters().size());
assertEquals("bar", uri.getParameters().get("foo"));
assertEquals("<h1>Hello, World!</h1>", new String(uri.getData()));
}
@Test
public void testParseTextHtmlWithMultipleParameters() {
String data = "data:text/html;foo=bar;baz=qux,%3Ch1%3EHello%2C%20World!%3C%2Fh1%3E";
DataURI uri = DataURI.tryParse(data);
assertTrue(DataURI.matchScheme(data));
assertNotNull(uri);
assertFalse(uri.isBase64());
assertEquals("text", uri.getMimeType());
assertEquals("html", uri.getMimeSubtype());
assertEquals(2, uri.getParameters().size());
assertEquals("bar", uri.getParameters().get("foo"));
assertEquals("qux", uri.getParameters().get("baz"));
assertEquals("<h1>Hello, World!</h1>", new String(uri.getData()));
}
@Test
public void testParseTextPlainWithMultipleParametersBase64Encoded() {
String data = "data:text/plain;foo=bar;baz=qux;base64,SGVsbG8sIFdvcmxkIQ==";
DataURI uri = DataURI.tryParse(data);
assertTrue(DataURI.matchScheme(data));
assertNotNull(uri);
assertTrue(uri.isBase64());
assertEquals("text", uri.getMimeType());
assertEquals("plain", uri.getMimeSubtype());
assertEquals(2, uri.getParameters().size());
assertEquals("bar", uri.getParameters().get("foo"));
assertEquals("qux", uri.getParameters().get("baz"));
assertEquals("Hello, World!", new String(uri.getData()));
}
@Test
public void testShortUriToString() {
String data = "data:text/plain;charset=utf-8;base64,SGVsbG8sIFdvcmxkIQ==";
DataURI uri = DataURI.tryParse(data);
assertNotNull(uri);
assertEquals("data:text/plain;charset=utf-8;base64,SGVsbG8sIFdvcmxkIQ==", uri.toString());
}
@Test
public void testLongUriToString() {
String data = "data:text/plain;charset=utf-8;base64,SGVsbG9Xb3JsZEhlbGxvV29ybGRIZWxsb1dvcmxkSGVsbG9Xb3JsZEhlbGxvV29ybGRIZWxsb1dvcmxk";
DataURI uri = DataURI.tryParse(data);
assertNotNull(uri);
assertEquals("data:text/plain;charset=utf-8;base64,SGVsbG9Xb3JsZE...RIZWxsb1dvcmxk", uri.toString());
}
}
