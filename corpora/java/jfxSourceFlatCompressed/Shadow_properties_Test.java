package test.javafx.scene.effect;
import static test.com.sun.javafx.test.TestHelper.box;
import java.util.ArrayList;
import java.util.Collection;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Shadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.BBoxComparator;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class Shadow_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
ArrayList array = new ArrayList();
final Shadow testShadow = new Shadow();
array.add(config(testShadow, "input", null, new BoxBlur()));
array.add(config(testShadow, "radius", 20.0, 40.0));
array.add(config(testShadow, "width", 100.0, 200.0));
array.add(config(testShadow, "height", 100.0, 200.0));
array.add(config(testShadow, "blurType",
BlurType.GAUSSIAN, BlurType.THREE_PASS_BOX));
array.add(config(testShadow, "color", Color.BLACK, Color.RED));
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
array.add(config(((Glow)testNode.getEffect()).getInput(),
"radius", 10.0, 21.0,
testNode,
"boundsInLocal",
box(-9.0, -9.0, 118.0, 118.0),
box(-20.0, -20.0, 140.0, 140.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((Glow)testNode.getEffect()).getInput(),
"width", 10.0, 21.0,
testNode,
"boundsInLocal",
box(-3.0, -9.0, 106.0, 118.0),
box(-9.0, -9.0, 118.0, 118.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((Glow)testNode.getEffect()).getInput(),
"height", 10.0, 21.0,
testNode,
"boundsInLocal",
box(-9.0, -3.0, 118.0, 106.0),
box(-9.0, -9.0, 118.0, 118.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((Glow)testNode.getEffect()).getInput(),
"blurType", BlurType.ONE_PASS_BOX, BlurType.THREE_PASS_BOX,
testNode,
"boundsInLocal",
box(-3.0, -3.0, 106.0, 106.0),
box(-9.0, -9.0, 118.0, 118.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((Glow)testNode.getEffect()).getInput(),
"blurType", BlurType.TWO_PASS_BOX, BlurType.GAUSSIAN,
testNode,
"boundsInLocal",
box(-6.0, -6.0, 112.0, 112.0),
box(-10.0, -10.0, 120.0, 120.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((Glow)testNode.getEffect()).getInput(),
"input", null, new BoxBlur(),
testNode,
"boundsInLocal",
box(-9.0, -9.0, 118.0, 118.0),
box(-11.0, -11.0, 122.0, 122.0),
new BBoxComparator(0.01)));
return array;
}
public Shadow_properties_Test(final Configuration configuration) {
super(configuration);
}
private static Rectangle createTestNode() {
Rectangle r = new Rectangle(100, 100);
r.setEffect(new Shadow());
return r;
}
private static Rectangle createTestNodeWithChainedEffect() {
Rectangle r = new Rectangle(100, 100);
Glow g = new Glow();
g.setInput(new Shadow());
r.setEffect(g);
return r;
}
}
