package test.com.sun.javafx.iio.png;
import com.sun.javafx.iio.png.PNGImageLoader2;
import test.com.sun.javafx.iio.ImageTestHelper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
public class PNGImageLoaderTest {
private void testImage(InputStream stream) throws IOException {
PNGImageLoader2 loader = new PNGImageLoader2(stream);
loader.load(0, 0, 0, true, true);
}
@Test
public void testRT35133() throws IOException {
InputStream stream = ImageTestHelper.createTestImageStream("png");
InputStream testStream = ImageTestHelper.createStutteringInputStream(stream);
testImage(testStream);
}
@Test(timeout = 1000, expected = IOException.class)
public void testRT27010() throws IOException {
int[] corruptedIDATLength = {
137, 80, 78, 71, 13, 10, 26, 10,
0, 0, 0, 13, 0x49, 0x48, 0x44, 0x52,
0, 0, 4, 0, 0, 0, 4, 0, 8, 6, 0, 0, 0,
0x7f, 0x1d, 0x2b, 0x83,
0x80, 0, 0x80, 0, 0x49, 0x44, 0x41, 0x54
};
ByteArrayInputStream stream = ImageTestHelper.constructStreamFromInts(corruptedIDATLength);
testImage(stream);
}
@Test(timeout = 1000, expected = IOException.class)
public void testRT27010MultipleIDAT() throws IOException {
int[] corruptedIDATLength = {
137, 80, 78, 71, 13, 10, 26, 10,
0, 0, 0, 13, 0x49, 0x48, 0x44, 0x52,
0, 0, 4, 0, 0, 0, 4, 0, 8, 6, 0, 0, 0,
0x7f, 0x1d, 0x2b, 0x83,
0, 0, 0, 1, 0x49, 0x44, 0x41, 0x54,
0,
0, 0, 0, 0,
0x80, 0, 0, 0, 0x49, 0x44, 0x41, 0x54,
};
ByteArrayInputStream stream = ImageTestHelper.constructStreamFromInts(corruptedIDATLength);
testImage(stream);
}
}
