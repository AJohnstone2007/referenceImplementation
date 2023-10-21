package test.javafx.scene.effect;
import static test.com.sun.javafx.test.TestHelper.box;
import java.util.ArrayList;
import java.util.Collection;
import javafx.scene.Node;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Reflection;
import javafx.scene.shape.Rectangle;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.BBoxComparator;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class Reflection_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
ArrayList array = new ArrayList();
final Reflection testReflection = new Reflection();
array.add(config(testReflection, "input", null, new BoxBlur()));
array.add(config(testReflection, "topOffset", 0.0, 50.0));
array.add(config(testReflection, "topOpacity", 0.5, 0.0));
array.add(config(testReflection, "bottomOpacity", 1.0, 0.8));
array.add(config(testReflection, "fraction", 0.75, 0.5));
Node testNode = createTestNode();
array.add(config(testNode.getEffect(),
"topOffset", 10.0, 20.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 100.0, 185.0),
box(0.0, 0.0, 100.0, 195.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"fraction", 0.0, 1.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 100.0, 100.0),
box(0.0, 0.0, 100.0, 200.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"input", null, new BoxBlur(),
testNode,
"boundsInLocal",
box(0.0, 0.0, 100.0, 175.0),
box(-2.0, -2.0, 104.0, 182.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((Glow)testNode.getEffect()).getInput(),
"topOffset", 10.0, 20.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 100.0, 185.0),
box(0.0, 0.0, 100.0, 195.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((Glow)testNode.getEffect()).getInput(),
"fraction", 0.0, 1.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 100.0, 100.0),
box(0.0, 0.0, 100.0, 200.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((Glow)testNode.getEffect()).getInput(),
"input", null, new BoxBlur(),
testNode,
"boundsInLocal",
box(0.0, 0.0, 100.0, 175.0),
box(-2.0, -2.0, 104.0, 182.0),
new BBoxComparator(0.01)));
return array;
}
public Reflection_properties_Test(final Configuration configuration) {
super(configuration);
}
private static Rectangle createTestNode() {
Rectangle r = new Rectangle(100, 100);
r.setEffect(new Reflection());
return r;
}
private static Rectangle createTestNodeWithChainedEffect() {
Rectangle r = new Rectangle(100, 100);
Glow g = new Glow();
g.setInput(new Reflection());
r.setEffect(g);
return r;
}
}
