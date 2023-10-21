package test.javafx.scene.paint;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import org.junit.Test;
import test.com.sun.javafx.pgstub.StubImageLoaderFactory;
import test.com.sun.javafx.pgstub.StubPlatformImageInfo;
import test.com.sun.javafx.pgstub.StubToolkit;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.paint.ImagePattern;
public class ImagePatternTest {
private Image createImage() {
final String url = "file:test.png";
StubToolkit toolkit = (StubToolkit) Toolkit.getToolkit();
StubImageLoaderFactory imageLoaderFactory =
toolkit.getImageLoaderFactory();
imageLoaderFactory.registerImage(
url, new StubPlatformImageInfo(100, 200));
return new Image(url);
}
@Test
public void testImagePatternShort() {
Image image = createImage();
ImagePattern pattern = new ImagePattern(image);
assertEquals(image, pattern.getImage());
assertEquals(0f, pattern.getX(), 0.0001);
assertEquals(0f, pattern.getY(), 0.0001);
assertEquals(1f, pattern.getWidth(), 0.0001);
assertEquals(1f, pattern.getHeight(), 0.0001);
assertTrue(pattern.isProportional());
}
@Test
public void testImagePatternLong() {
Image image = createImage();
ImagePattern pattern = new ImagePattern(image, 1, 2, 3, 4, false);
assertEquals(image, pattern.getImage());
assertEquals(1f, pattern.getX(), 0.0001);
assertEquals(2f, pattern.getY(), 0.0001);
assertEquals(3f, pattern.getWidth(), 0.0001);
assertEquals(4f, pattern.getHeight(), 0.0001);
assertFalse(pattern.isProportional());
}
@Test
public void testImpl_getPlatformPaint() {
ImagePattern pattern = new ImagePattern(createImage());
Object paint = Toolkit.getPaintAccessor().getPlatformPaint(pattern);
assertNotNull(paint);
assertSame(paint, Toolkit.getPaintAccessor().getPlatformPaint(pattern));
}
}
