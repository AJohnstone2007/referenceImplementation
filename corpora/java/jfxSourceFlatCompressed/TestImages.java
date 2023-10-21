package test.javafx.scene.image;
import test.com.sun.javafx.pgstub.StubImageLoaderFactory;
import test.com.sun.javafx.pgstub.StubPlatformImageInfo;
import test.com.sun.javafx.pgstub.StubToolkit;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.image.Image;
import javafx.scene.image.ImageShim;
public final class TestImages {
public static final Image TEST_IMAGE_0x100;
public static final Image TEST_IMAGE_100x0;
public static final Image TEST_IMAGE_100x200;
public static final Image TEST_IMAGE_200x100;
public static final Image TEST_IMAGE_32x32;
public static final Image TEST_IMAGE_32x64;
public static final Image TEST_IMAGE_64x32;
public static final Image TEST_IMAGE_64x64;
public static final Image TEST_ERROR_IMAGE;
private static final StubImageLoaderFactory imageLoaderFactory;
private TestImages() {
}
static {
imageLoaderFactory =
((StubToolkit) Toolkit.getToolkit()).getImageLoaderFactory();
TEST_IMAGE_0x100 = createTestImage(0, 100);
TEST_IMAGE_100x0 = createTestImage(100, 0);
TEST_IMAGE_100x200 = createTestImage(100, 200);
TEST_IMAGE_200x100 = createTestImage(200, 100);
TEST_IMAGE_32x32 = createTestImage(32, 32);
TEST_IMAGE_32x64 = createTestImage(32, 64);
TEST_IMAGE_64x32 = createTestImage(64, 32);
TEST_IMAGE_64x64 = createTestImage(64, 64);
TEST_ERROR_IMAGE = new Image("file:error.png");
}
public static Image createTestImage(
final int width,
final int height) {
final String url = "file:testImg_" + width + "x" + height + ".png";
imageLoaderFactory.registerImage(
url, new StubPlatformImageInfo(width, height));
return new Image(url);
}
public static Image createAnimatedTestImage(
final int width,
final int height,
final int loopCount, final int... frameDelays) {
final String url = "file:testAnimImg_" + width + "x" + height + ".png";
final StubPlatformImageInfo spii =
new StubPlatformImageInfo(width, height, frameDelays, loopCount);
imageLoaderFactory.registerImage(url, spii);
return new Image(url);
}
public static void disposeAnimatedImage(final Image image) {
ImageShim.dispose(image);
}
}
