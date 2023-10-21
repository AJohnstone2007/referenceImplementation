package test.com.sun.javafx.tk.quantum;
import com.sun.javafx.tk.quantum.WindowStageShim;
import com.sun.prism.Image;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
public class WindowStageTest {
private void addImage(List<Object> images, int size) {
byte[] pixels = new byte[size * size * 3];
Image image = Image.fromByteRgbData(pixels, size, size);
images.add(image);
}
@Test
public void bestIconSizeTest() {
List<Object> images = new ArrayList();
addImage(images, 16);
addImage(images, 32);
addImage(images, 48);
Image image = WindowStageShim.findBestImage(images, 16, 16);
Assert.assertEquals(16, image.getWidth());
image = WindowStageShim.findBestImage(images, 48, 48);
Assert.assertEquals(48, image.getWidth());
image = WindowStageShim.findBestImage(images, 32, 32);
Assert.assertEquals(32, image.getWidth());
image = WindowStageShim.findBestImage(images, 128, 128);
Assert.assertEquals(32, image.getWidth());
}
}
