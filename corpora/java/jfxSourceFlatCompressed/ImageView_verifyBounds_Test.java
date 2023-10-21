package test.javafx.scene.image;
import static test.com.sun.javafx.test.TestHelper.assertBoundsEqual;
import static test.com.sun.javafx.test.TestHelper.box;
import static test.javafx.scene.image.ImageViewConfig.config;
import static test.javafx.scene.image.TestImages.TEST_IMAGE_100x200;
import static test.javafx.scene.image.TestImages.TEST_IMAGE_200x100;
import java.util.Arrays;
import java.util.Collection;
import javafx.geometry.BoundingBox;
import javafx.scene.image.ImageView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class)
public final class ImageView_verifyBounds_Test {
private final ImageViewConfig imageViewConfig;
private final BoundingBox expectedBounds;
private ImageView imageView;
@Parameters
public static Collection data() {
return Arrays.asList(new Object[][] {
{ config(TEST_IMAGE_100x200, 0, 0), box(0, 0, 100, 200) },
{ config(TEST_IMAGE_200x100, 20, 10), box(20, 10, 200, 100) },
{
config(null, 0, 0, 400, 400, false),
box(0, 0, 400, 400)
},
{
config(TEST_IMAGE_100x200, 10, 20, 0, 400, false),
box(10, 20, 100, 400)
},
{
config(TEST_IMAGE_200x100, 20, 10, 400, 0, false),
box(20, 10, 400, 100)
},
{
config(null, 0, 0, 400, 400, true),
box(0, 0, 400, 400)
},
{
config(TEST_IMAGE_100x200, 10, 20, 400, 400, true),
box(10, 20, 200, 400)
},
{
config(TEST_IMAGE_200x100, 20, 10, 400, 400, true),
box(20, 10, 400, 200)
},
{
config(TEST_IMAGE_100x200, 10, 20,
-50, 100, 200, 100,
400, 0, true),
box(10, 20, 400, 200)
},
{
config(TEST_IMAGE_200x100, 20, 10,
100, -50, 100, 200,
0, 400, true),
box(20, 10, 200, 400)
},
{
config(TEST_IMAGE_200x100, 0, 0,
0, 0, 0, 100,
400, 400, true),
box(0, 0, 400, 200)
},
{
config(TEST_IMAGE_100x200, 0, 0,
0, 0, 100, 0,
400, 400, true),
box(0, 0, 200, 400)
}
});
}
public ImageView_verifyBounds_Test(final ImageViewConfig imageViewConfig,
final BoundingBox expectedBounds) {
this.imageViewConfig = imageViewConfig;
this.expectedBounds = expectedBounds;
}
@Before
public void setUp() {
imageView = new ImageView();
imageViewConfig.applyTo(imageView);
}
@After
public void tearDown() {
imageView = null;
}
@Test
public void verifyBounds() {
assertBoundsEqual(expectedBounds, imageView.getBoundsInLocal());
}
}
