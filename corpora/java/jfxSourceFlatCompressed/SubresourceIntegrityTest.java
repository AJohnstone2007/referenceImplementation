package test.javafx.scene.web;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
@RunWith(Parameterized.class)
public final class SubresourceIntegrityTest extends TestBase {
private final String hashValue;
private final String expected;
private File htmlFile;
private final static String LOADED = "hello";
private final static String NOT_LOADED = "not loaded";
@Parameters
public static Collection<String[]> data() {
return Arrays.asList(new String[][] {
{"sha1-/kpzvnGzRkcE9OFn5j8qRE61nZY=", LOADED},
{"sha224-zgiBbbuKJixMVEkaOXnvpSYZGsx7SbSZ0QOckg==", LOADED},
{"sha256-vcl3cFaIDAtcQBkUZFdY+tW/bjrg6vX1R+hQ8uB5tHc=", LOADED},
{"sha384-+GrI+cacF05VlQitRghQhs1by9CSIyc8XgZTbymUg2oA0EYdLiPMtilnFP3LDbkY", LOADED},
{"sha512-V8m3j61x5soaVcO83NuHavY7Yn4MQYoUgrqJe38f6QYG9QzzgWbVDB1SrZsZ2CVR1IsOnV2MLhnDaZhWOwHDsw==", LOADED},
{"sha1-0000000000000000000000000000", LOADED},
{"sha224-0000000000000000000000000000000000000000", LOADED},
{"sha256-Vcl3cFaIDAtcQBkUZFdY+tW/bjrg6vX1R+hQ8uB5tHc=", NOT_LOADED},
{"sha384-+grI+cacF05VlQitRghQhs1by9CSIyc8XgZTbymUg2oA0EYdLiPMtilnFP3LDbkY", NOT_LOADED},
{"sha512-v8m3j61x5soaVcO83NuHavY7Yn4MQYoUgrqJe38f6QYG9QzzgWbVDB1SrZsZ2CVR1IsOnV2MLhnDaZhWOwHDsw==", NOT_LOADED},
{"unknown-0000", LOADED},
{"", LOADED},
});
}
public SubresourceIntegrityTest(final String hashValue, final String expected) {
this.hashValue = hashValue;
this.expected = expected;
}
@Before
public void setup() throws Exception {
htmlFile = new File("subresource-integrity-test.html");
final FileOutputStream out = new FileOutputStream(htmlFile);
final String scriptUrl =
new File("src/test/resources/test/html/subresource-integrity-test.js").toURI().toASCIIString();
final String html =
String.format("<html>\n" +
"<head><script src='%s' integrity='%s' crossorigin='anonymous'></script></head>\n" +
"<body>%s</body>\n" +
"</html>", scriptUrl, hashValue, NOT_LOADED);
out.write(html.getBytes());
out.close();
}
@Test
public void testScriptTagWithCorrectHashValue() {
load(htmlFile);
final String bodyText = (String) executeScript("document.body.innerText");
assertNotNull("document.body.innerText must be non null for " + hashValue, bodyText);
assertEquals(hashValue, expected, bodyText);
}
@After
public void tearDown() {
if (!htmlFile.delete()) {
htmlFile.deleteOnExit();
}
}
}
