package test.javafx.scene.effect;
import static test.com.sun.javafx.test.TestHelper.box;
import java.util.ArrayList;
import java.util.Collection;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ImageInput;
import test.javafx.scene.image.TestImages;
import javafx.scene.shape.Rectangle;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.BBoxComparator;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class ImageInput_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
ArrayList array = new ArrayList();
final ImageInput testImageInput = new ImageInput();
array.add(config(testImageInput, "source",
null, TestImages.TEST_IMAGE_100x200));
array.add(config(testImageInput, "x", 0.0, 20.0));
array.add(config(testImageInput, "y", 0.0, 20.0));
Node testNode = createTestNode();
array.add(config(testNode.getEffect(),
"source", TestImages.TEST_IMAGE_32x32, TestImages.TEST_IMAGE_64x64,
testNode,
"boundsInLocal",
box(0.0, 0.0, 32.0, 32.0),
box(0.0, 0.0, 64.0, 64.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
((ImageInput) testNode.getEffect()).setSource(TestImages.TEST_IMAGE_32x32);
array.add(config(testNode.getEffect(),
"x", 0.0, 50.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 32.0, 32.0),
box(50.0, 0.0, 32.0, 32.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
((ImageInput) testNode.getEffect()).setSource(TestImages.TEST_IMAGE_32x32);
array.add(config(testNode.getEffect(),
"y", 0.0, 50.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 32.0, 32.0),
box(0.0, 50.0, 32.0, 32.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"source", TestImages.TEST_IMAGE_32x32, TestImages.TEST_IMAGE_64x64,
testNode,
"boundsInLocal",
box(0.0, 0.0, 32.0, 32.0),
box(0.0, 0.0, 64.0, 64.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
ImageInput imageInput = (ImageInput)((ColorAdjust)testNode.getEffect()).getInput();
imageInput.setSource(TestImages.TEST_IMAGE_32x32);
array.add(config(imageInput,
"x", 0.0, 50.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 32.0, 32.0),
box(50.0, 0.0, 32.0, 32.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
imageInput = (ImageInput)((ColorAdjust)testNode.getEffect()).getInput();
imageInput.setSource(TestImages.TEST_IMAGE_32x32);
array.add(config(imageInput,
"y", 0.0, 50.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 32.0, 32.0),
box(0.0, 50.0, 32.0, 32.0),
new BBoxComparator(0.01)));
return array;
}
public ImageInput_properties_Test(final Configuration configuration) {
super(configuration);
}
private static Rectangle createTestNode() {
Rectangle r = new Rectangle(100, 100);
r.setEffect(new ImageInput());
return r;
}
private static Rectangle createTestNodeWithChainedEffect() {
Rectangle r = new Rectangle(100, 100);
ColorAdjust c = new ColorAdjust();
c.setInput(new ImageInput());
r.setEffect(c);
return r;
}
}
