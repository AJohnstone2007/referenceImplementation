package test.javafx.scene.input;
import java.util.Collection;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import javafx.scene.input.DataFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class DataFormatTest {
static DataFormat customFormat = new DataFormat("Custom1", "Custom2");
static DataFormat uniqueFormat = new DataFormat("Unique");
@Parameters
public static Collection getParams() {
return Arrays.asList(new Object[][] {
{ DataFormat.PLAIN_TEXT, "text/plain", null },
{ DataFormat.HTML, "text/html", null },
{ DataFormat.RTF, "text/rtf", null },
{ DataFormat.URL, "text/uri-list", null },
{ DataFormat.IMAGE, "application/x-java-rawimage", null },
{ DataFormat.FILES, "application/x-java-file-list", "java.file-list" },
{ customFormat, "Custom1", "Custom2" }
});
}
private DataFormat format;
private String mime1;
private String mime2;
public DataFormatTest(DataFormat format, String mime1, String mime2) {
this.format = format;
this.mime1 = mime1;
this.mime2 = mime2;
}
@Test
public void testMimeTypes() {
assertEquals(mime2 != null ? 2 : 1, format.getIdentifiers().size());
assertTrue(format.getIdentifiers().contains(mime1));
if (mime2 != null) {
assertTrue(format.getIdentifiers().contains(mime2));
}
}
@Test
public void dataFormatsShouldBeFound() {
assertSame(format, DataFormat.lookupMimeType(mime1));
if (mime2 != null) {
assertSame(format, DataFormat.lookupMimeType(mime2));
}
}
@Test
public void testToString() {
assertNotNull(customFormat.toString());
assertFalse("".equals(customFormat.toString()));
}
@Test(expected=IllegalArgumentException.class)
public void shouldNotBePossibleToReuseMimeTypes() {
DataFormat customEqual = new DataFormat(format.getIdentifiers().toArray(
new String[format.getIdentifiers().size()]));
}
@Test
public void testEqualsAndHashCode() {
assertEquals(format, format);
assertEquals(format.hashCode(), format.hashCode());
assertFalse(uniqueFormat.equals(format));
assertFalse(uniqueFormat.hashCode() == format.hashCode());
}
}
