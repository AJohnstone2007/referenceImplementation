package test.javafx.scene.effect;
import static test.com.sun.javafx.test.TestHelper.box;
import java.util.ArrayList;
import java.util.Collection;
import javafx.scene.Node;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.MotionBlur;
import javafx.scene.shape.Rectangle;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.BBoxComparator;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class MotionBlur_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
ArrayList array = new ArrayList();
final MotionBlur testMotionBlur = new MotionBlur();
array.add(config(testMotionBlur, "input", null, new BoxBlur()));
array.add(config(testMotionBlur, "radius", 20.0, 40.0));
array.add(config(testMotionBlur, "angle", 0.0, 45.0));
Node testNode = createTestNode();
array.add(config(testNode.getEffect(),
"radius", 10.0, 20.0,
testNode,
"boundsInLocal",
box(-10.0, 0.0, 120.0, 100.0),
box(-20.0, 0.0, 140.0, 100.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"angle", 0.0, 45.0,
testNode,
"boundsInLocal",
box(-10.0, 0.0, 120.0, 100.0),
box(-8.0, -8.0, 116.0, 116.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"input", null, new BoxBlur(),
testNode,
"boundsInLocal",
box(-10.0, -0.0, 120.0, 100.0),
box(-12.0, -2.0, 124.0, 104.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"radius", 10.0, 20.0,
testNode,
"boundsInLocal",
box(-10.0, 0.0, 120.0, 100.0),
box(-20.0, 0.0, 140.0, 100.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"angle", 0.0, 45.0,
testNode,
"boundsInLocal",
box(-10.0, 0.0, 120.0, 100.0),
box(-8.0, -8.0, 116.0, 116.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"input", null, new BoxBlur(),
testNode,
"boundsInLocal",
box(-10.0, -0.0, 120.0, 100.0),
box(-12.0, -2.0, 124.0, 104.0),
new BBoxComparator(0.01)));
return array;
}
public MotionBlur_properties_Test(final Configuration configuration) {
super(configuration);
}
private static Rectangle createTestNode() {
Rectangle r = new Rectangle(100, 100);
r.setEffect(new MotionBlur());
return r;
}
private static Rectangle createTestNodeWithChainedEffect() {
Rectangle r = new Rectangle(100, 100);
ColorAdjust c = new ColorAdjust();
c.setInput(new MotionBlur());
r.setEffect(c);
return r;
}
}
