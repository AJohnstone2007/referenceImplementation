package test.javafx.embed.swing;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import junit.framework.Assert;
public class FXImageConversionTest {
private static int width = 100;
private static int height = 100;
@Test public void testImageConversionRGBOpaque() {
WritableImage newimage = new WritableImage(width, height);
PixelWriter pw = newimage.getPixelWriter();
for (int x = 0; x < width; x++) {
for (int y = 0; y < height; y++) {
pw.setArgb(x, y, 0xff000000);
}
}
try {
BufferedImage b = new BufferedImage(width + 1, height + 1,
BufferedImage.TYPE_INT_RGB);
BufferedImage bf = SwingFXUtils.fromFXImage(newimage, b);
assertTrue(bf.getType() == BufferedImage.TYPE_INT_RGB);
} catch (ClassCastException cex) {
Assert.fail("FX image conversion wrong cast " + cex);
}
}
@Test public void testImageConversionRGBNotOpaque() {
WritableImage newimage = new WritableImage(width, height);
PixelWriter pw = newimage.getPixelWriter();
for (int x = 0; x < width/2; x++) {
for (int y = 0; y < height/2; y++) {
pw.setArgb(x, y, 0xff000000);
}
}
try {
BufferedImage b = new BufferedImage(width + 1, height + 1,
BufferedImage.TYPE_INT_RGB);
BufferedImage bf = SwingFXUtils.fromFXImage(newimage, b);
assertTrue(bf.getType() == BufferedImage.TYPE_INT_ARGB_PRE);
} catch (ClassCastException cex) {
Assert.fail("FX image conversion wrong cast " + cex);
}
}
@Test public void testImageConversionGray() {
WritableImage newimage = new WritableImage(width, height);
PixelWriter pw = newimage.getPixelWriter();
for (int x = 0; x < width; x++) {
for (int y = 0; y < height; y++) {
pw.setArgb(x, y, 0xff000000);
}
}
try {
BufferedImage b = new BufferedImage(width + 1, height + 1,
BufferedImage.TYPE_BYTE_GRAY);
BufferedImage bf = SwingFXUtils.fromFXImage(newimage, b);
assertTrue(bf.getType() == BufferedImage.TYPE_INT_ARGB_PRE);
} catch (ClassCastException cex) {
Assert.fail("FX image conversion wrong cast " + cex);
}
}
}
