package test.com.sun.glass.ui.mac;
import com.sun.glass.ui.Clipboard;
import com.sun.glass.ui.Pixels;
import com.sun.glass.ui.mac.MacPasteboardShim;
import com.sun.javafx.PlatformUtil;
import javafx.application.Platform;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import static test.util.Util.runAndWait;
public class MacPasteboardTest {
private static final CountDownLatch startupLatch = new CountDownLatch(1);
private static MacPasteboardShim macPasteboardShim;
@BeforeClass
public static void setup() throws Exception {
if (PlatformUtil.isMac()) {
Platform.startup(() -> {
macPasteboardShim = new MacPasteboardShim();
startupLatch.countDown();
});
}
}
@AfterClass
public static void teardown() {
if (PlatformUtil.isMac()) {
Platform.exit();
}
}
@Test
public void testValidLocalImageURLMacPasteboard() throws Exception {
assumeTrue(PlatformUtil.isMac());
final String localImage = getClass().getResource("blue.png").toURI().toURL().toString();
runAndWait(() -> {
macPasteboardShim.pushMacPasteboard(new HashMap<>(Map.of(Clipboard.URI_TYPE, localImage)));
Object content = macPasteboardShim.popMacPasteboard(Clipboard.RAW_IMAGE_TYPE);
assertTrue("The content was not a raw image", content instanceof Pixels);
Pixels pixels = (Pixels) content;
assertEquals("The raw image width", 64, pixels.getWidth());
assertEquals("The raw image height", 64, pixels.getHeight());
});
}
@Test
public void testDataBase64ImageMacPasteboard() {
assumeTrue(PlatformUtil.isMac());
final String encodedImage = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAKCAIAAAA7N+mxAAAAAXNSR0IArs4c6QAAAAR"
+ "nQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAAcSURBVChTY/jPwADBZACyNMHAqGYSwZDU/P8/ABieT81GAGKoAAAAAElFTkSuQmCC";
runAndWait(() -> {
macPasteboardShim.pushMacPasteboard(new HashMap<>(Map.of(Clipboard.URI_TYPE, encodedImage)));
Object content = macPasteboardShim.popMacPasteboard(Clipboard.RAW_IMAGE_TYPE);
assertNull("The content was not null", content);
});
}
@Test
public void testNotAnImageURLMacPasteboard() {
assumeTrue(PlatformUtil.isMac());
final String invalidImage = "not.an.image.url";
runAndWait(() -> {
macPasteboardShim.pushMacPasteboard(new HashMap<>(Map.of(Clipboard.URI_TYPE, invalidImage)));
Object content = macPasteboardShim.popMacPasteboard(Clipboard.RAW_IMAGE_TYPE);
assertNull("The content was not null", content);
});
}
}
