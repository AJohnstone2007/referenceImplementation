package test.javafx.scene.image;
import static test.javafx.scene.image.ImageViewConfig.config;
import static test.javafx.scene.image.TestImages.TEST_IMAGE_100x200;
import static test.javafx.scene.image.TestImages.TEST_IMAGE_200x100;
import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.image.ImageView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class)
public class ImageView_verifyContains_Test {
private final ImageViewConfig imageViewConfig;
private final float x;
private final float y;
private final boolean expectedContainsResult;
private ImageView imageView;
@Parameters
public static Collection data() {
return Arrays.asList(new Object[][] {
{ config(null, 0, 0), 0, 0, false },
{ config(TEST_IMAGE_100x200, 0, 0), 25, 50, true },
{ config(TEST_IMAGE_100x200, 25, 50), 25 + 75, 50 + 50, false },
{ config(TEST_IMAGE_100x200, 25, 0), 25 + 25, 150, false },
{ config(TEST_IMAGE_100x200, 0, 50), 75, 50 + 150, true },
{ config(TEST_IMAGE_200x100, 0, 0), 250, 75, false },
{ config(TEST_IMAGE_200x100, 50, 25), 50 + 150, 25 + 125, false },
{ config(TEST_IMAGE_200x100, 50, 0), 0, 25, false },
{ config(TEST_IMAGE_200x100, 0, 25), 50, 0, false },
{
config(null, 0, 0, 400, 400, false),
200, 200, false
},
{
config(TEST_IMAGE_100x200, 50, 0, 0, 400, false),
50 + 25, 100, true
},
{
config(TEST_IMAGE_200x100, 0, 50, 400, 0, false),
300, 50 + 75, true
},
{
config(TEST_IMAGE_100x200, 0, 200, 400, 400, true),
150, 200 + 300, true
},
{
config(TEST_IMAGE_200x100, 200, 0, 400, 400, true),
200 + 100, 50, true
},
{
config(TEST_IMAGE_100x200, 0, 0,
50, 0, 100, 200,
0, 400, true),
50, 300, true
},
{
config(TEST_IMAGE_100x200, 0, 0,
-50, 0, 100, 200,
400, 400, true),
50, 100, false
},
{
config(TEST_IMAGE_100x200, 0, 0,
50, 50, 50, 150,
400, 400, false),
395, 395, true
},
{
config(TEST_IMAGE_200x100, 20, 10,
0, 50, 200, 100,
400, 0, true),
300, 50, true
},
{
config(TEST_IMAGE_200x100, 20, 10,
0, -50, 200, 100,
400, 0, true),
100, 50, false
},
});
}
public ImageView_verifyContains_Test(final ImageViewConfig imageViewConfig,
final float x,
final float y,
final boolean expectedContainsResult) {
this.imageViewConfig = imageViewConfig;
this.x = x;
this.y = y;
this.expectedContainsResult = expectedContainsResult;
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
public void verifyContains() {
assertEquals(expectedContainsResult,
imageView.contains(x, y));
}
}
