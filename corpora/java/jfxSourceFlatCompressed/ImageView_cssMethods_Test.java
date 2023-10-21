package test.javafx.scene.image;
import java.util.Arrays;
import java.util.Collection;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.pgstub.StubImageLoaderFactory;
import test.com.sun.javafx.pgstub.StubPlatformImageInfo;
import test.com.sun.javafx.pgstub.StubToolkit;
import test.com.sun.javafx.test.CssMethodsTestBase;
import test.com.sun.javafx.test.ValueComparator;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
@RunWith(Parameterized.class)
public final class ImageView_cssMethods_Test extends CssMethodsTestBase {
private static final ImageView TEST_IMAGE_VIEW = new ImageView();
private static final String TEST_IMAGE_URL1 = "file:test_image_1.png";
private static final String TEST_IMAGE_URL2 = "file:test_image_2.png";
private static final ValueComparator IMAGE_COMPARATOR =
new ValueComparator() {
@Override
public boolean equals(final Object expected,
final Object actual) {
return ((actual instanceof Image)
&& ((Image) actual).getUrl().equals(expected));
}
};
@BeforeClass
public static void configureImageLoaderFactory() {
final StubImageLoaderFactory imageLoaderFactory =
((StubToolkit) Toolkit.getToolkit()).getImageLoaderFactory();
imageLoaderFactory.reset();
imageLoaderFactory.registerImage(
TEST_IMAGE_URL1,
new StubPlatformImageInfo(32, 32));
imageLoaderFactory.registerImage(
TEST_IMAGE_URL2,
new StubPlatformImageInfo(48, 48));
}
@Parameters
public static Collection data() {
return Arrays.asList(new Object[] {
config(TEST_IMAGE_VIEW, "image", null,
"-fx-image", TEST_IMAGE_URL1, IMAGE_COMPARATOR),
config(TEST_IMAGE_VIEW, "image",
TestImages.TEST_IMAGE_32x32,
"-fx-image", TEST_IMAGE_URL2, IMAGE_COMPARATOR),
config(TEST_IMAGE_VIEW, "translateX", 0.0,
"-fx-translate-x", 10.0)
});
}
public ImageView_cssMethods_Test(final Configuration configuration) {
super(configuration);
}
}
