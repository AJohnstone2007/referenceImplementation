package test.javafx.scene.effect;
import static test.com.sun.javafx.test.TestHelper.box;
import java.util.ArrayList;
import java.util.Collection;
import javafx.scene.Node;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.shape.Rectangle;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.BBoxComparator;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class GaussianBlur_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
ArrayList array = new ArrayList();
final GaussianBlur testGaussianBlur = new GaussianBlur();
array.add(config(testGaussianBlur, "input", null, new BoxBlur()));
array.add(config(testGaussianBlur, "radius", 20.0, 40.0));
Node testNode = createTestNode();
array.add(config(testNode.getEffect(),
"radius", 0.0, 10.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 100.0, 100.0),
box(-10.0, -10.0, 120.0, 120.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"input", null, new BoxBlur(),
testNode,
"boundsInLocal",
box(-10.0, -10.0, 120.0, 120.0),
box(-12.0, -12.0, 124.0, 124.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((Glow)testNode.getEffect()).getInput(),
"radius", 0.0, 10.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 100.0, 100.0),
box(-10.0, -10.0, 120.0, 120.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((Glow)testNode.getEffect()).getInput(),
"input", null, new BoxBlur(),
testNode,
"boundsInLocal",
box(-10.0, -10.0, 120.0, 120.0),
box(-12.0, -12.0, 124.0, 124.0),
new BBoxComparator(0.01)));
return array;
}
public GaussianBlur_properties_Test(final Configuration configuration) {
super(configuration);
}
private static Rectangle createTestNode() {
Rectangle r = new Rectangle(100, 100);
r.setEffect(new GaussianBlur());
return r;
}
private static Rectangle createTestNodeWithChainedEffect() {
Rectangle r = new Rectangle(100, 100);
Glow g = new Glow();
g.setInput(new GaussianBlur());
r.setEffect(g);
return r;
}
}
