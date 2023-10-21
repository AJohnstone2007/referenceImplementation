package test.javafx.scene.effect;
import static test.com.sun.javafx.test.TestHelper.box;
import java.util.ArrayList;
import java.util.Collection;
import javafx.scene.Node;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.shape.Rectangle;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.BBoxComparator;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class BoxBlur_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
ArrayList array = new ArrayList();
final BoxBlur testBoxBlur = new BoxBlur();
array.add(config(testBoxBlur, "input", null, new BoxBlur()));
array.add(config(testBoxBlur, "width", 100.0, 200.0));
array.add(config(testBoxBlur, "height", 100.0, 200.0));
array.add(config(testBoxBlur, "iterations", 1, 3));
Node testNode = createTestNode();
array.add(config(testNode.getEffect(),
"iterations", 1, 2,
testNode,
"boundsInLocal",
box(-2.0, -2.0, 104.0, 104.0),
box(-4.0, -4.0, 108.0, 108.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"width", 5.0, 100.0,
testNode,
"boundsInLocal",
box(-2.0, -2.0, 104.0, 104.0),
box(-50.0, -2.0, 200.0, 104.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"height", 5.0, 100.0,
testNode,
"boundsInLocal",
box(-2.0, -2.0, 104.0, 104.0),
box(-2.0, -50.0, 104.0, 200.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"input", null, new BoxBlur(),
testNode,
"boundsInLocal",
box(-2.0, -2.0, 104.0, 104.0),
box(-4.0, -4.0, 108.0, 108.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"iterations", 1, 2,
testNode,
"boundsInLocal",
box(-2.0, -2.0, 104.0, 104.0),
box(-4.0, -4.0, 108.0, 108.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"width", 5.0, 100.0,
testNode,
"boundsInLocal",
box(-2.0, -2.0, 104.0, 104.0),
box(-50.0, -2.0, 200.0, 104.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"height", 5.0, 100.0,
testNode,
"boundsInLocal",
box(-2.0, -2.0, 104.0, 104.0),
box(-2.0, -50.0, 104.0, 200.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"input", null, new BoxBlur(),
testNode,
"boundsInLocal",
box(-2.0, -2.0, 104.0, 104.0),
box(-4.0, -4.0, 108.0, 108.0),
new BBoxComparator(0.01)));
return array;
}
public BoxBlur_properties_Test(final Configuration configuration) {
super(configuration);
}
private static Rectangle createTestNode() {
Rectangle r = new Rectangle(100, 100);
r.setEffect(new BoxBlur());
return r;
}
private static Rectangle createTestNodeWithChainedEffect() {
Rectangle r = new Rectangle(100, 100);
ColorAdjust ca = new ColorAdjust();
ca.setInput(new BoxBlur());
r.setEffect(ca);
return r;
}
}
