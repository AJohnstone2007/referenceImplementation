package test.javafx.scene.effect;
import static test.com.sun.javafx.test.TestHelper.box;
import java.util.ArrayList;
import java.util.Collection;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.BBoxComparator;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class DropShadow_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
ArrayList array = new ArrayList();
final DropShadow testDropShadow = new DropShadow();
array.add(config(testDropShadow, "input", null, new BoxBlur()));
array.add(config(testDropShadow, "radius", 50.0, 100.0));
array.add(config(testDropShadow, "width", 100.0, 200.0));
array.add(config(testDropShadow, "height", 100.0, 200.0));
array.add(config(testDropShadow, "blurType",
BlurType.GAUSSIAN, BlurType.THREE_PASS_BOX));
array.add(config(testDropShadow, "spread", 0.0, 0.5));
array.add(config(testDropShadow, "color", Color.BLACK, Color.RED));
array.add(config(testDropShadow, "offsetX", 0.0, 50.0));
array.add(config(testDropShadow, "offsetY", 0.0, 50.0));
Node testNode = createTestNode();
array.add(config(testNode.getEffect(),
"radius", 10.0, 21.0,
testNode,
"boundsInLocal",
box(-9.0, -9.0, 118.0, 118.0),
box(-20.0, -20.0, 140.0, 140.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"width", 10.0, 21.0,
testNode,
"boundsInLocal",
box(-3.0, -9.0, 106.0, 118.0),
box(-9.0, -9.0, 118.0, 118.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"height", 10.0, 21.0,
testNode,
"boundsInLocal",
box(-9.0, -3.0, 118.0, 106.0),
box(-9.0, -9.0, 118.0, 118.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"offsetX", 10.0, 21.0,
testNode,
"boundsInLocal",
box(0.0, -9.0, 119.0, 118.0),
box(0.0, -9.0, 130.0, 118.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"offsetY", 10.0, 21.0,
testNode,
"boundsInLocal",
box(-9.0, 0.0, 118.0, 119.0),
box(-9.0, 0.0, 118.0, 130.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"blurType", BlurType.ONE_PASS_BOX, BlurType.THREE_PASS_BOX,
testNode,
"boundsInLocal",
box(-3.0, -3.0, 106.0, 106.0),
box(-9.0, -9.0, 118.0, 118.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"blurType", BlurType.TWO_PASS_BOX, BlurType.GAUSSIAN,
testNode,
"boundsInLocal",
box(-6.0, -6.0, 112.0, 112.0),
box(-10.0, -10.0, 120.0, 120.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"input", null, new BoxBlur(),
testNode,
"boundsInLocal",
box(-9.0, -9.0, 118.0, 118.0),
box(-11.0, -11.0, 122.0, 122.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"radius", 10.0, 21.0,
testNode,
"boundsInLocal",
box(-9.0, -9.0, 118.0, 118.0),
box(-20.0, -20.0, 140.0, 140.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"width", 10.0, 21.0,
testNode,
"boundsInLocal",
box(-3.0, -9.0, 106.0, 118.0),
box(-9.0, -9.0, 118.0, 118.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"height", 10.0, 21.0,
testNode,
"boundsInLocal",
box(-9.0, -3.0, 118.0, 106.0),
box(-9.0, -9.0, 118.0, 118.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"offsetX", 10.0, 21.0,
testNode,
"boundsInLocal",
box(0.0, -9.0, 119.0, 118.0),
box(0.0, -9.0, 130.0, 118.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"offsetY", 10.0, 21.0,
testNode,
"boundsInLocal",
box(-9.0, 0.0, 118.0, 119.0),
box(-9.0, 0.0, 118.0, 130.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"blurType", BlurType.ONE_PASS_BOX, BlurType.THREE_PASS_BOX,
testNode,
"boundsInLocal",
box(-3.0, -3.0, 106.0, 106.0),
box(-9.0, -9.0, 118.0, 118.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"blurType", BlurType.TWO_PASS_BOX, BlurType.GAUSSIAN,
testNode,
"boundsInLocal",
box(-6.0, -6.0, 112.0, 112.0),
box(-10.0, -10.0, 120.0, 120.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"input", null, new BoxBlur(),
testNode,
"boundsInLocal",
box(-9.0, -9.0, 118.0, 118.0),
box(-11.0, -11.0, 122.0, 122.0),
new BBoxComparator(0.01)));
return array;
}
public DropShadow_properties_Test(final Configuration configuration) {
super(configuration);
}
private static Rectangle createTestNode() {
Rectangle r = new Rectangle(100, 100);
r.setEffect(new DropShadow());
return r;
}
private static Rectangle createTestNodeWithChainedEffect() {
Rectangle r = new Rectangle(100, 100);
ColorAdjust c = new ColorAdjust();
c.setInput(new DropShadow());
r.setEffect(c);
return r;
}
}
