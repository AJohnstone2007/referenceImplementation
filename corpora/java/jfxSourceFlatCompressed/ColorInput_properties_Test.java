package test.javafx.scene.effect;
import static test.com.sun.javafx.test.TestHelper.box;
import java.util.ArrayList;
import java.util.Collection;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.BBoxComparator;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class ColorInput_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
ArrayList array = new ArrayList();
final ColorInput testColorInput = new ColorInput();
array.add(config(testColorInput, "paint", Color.RED, Color.BLUE));
array.add(config(testColorInput, "x", 0.0, 100.0));
array.add(config(testColorInput, "y", 0.0, 100.0));
array.add(config(testColorInput, "width", 50.0, 150.0));
array.add(config(testColorInput, "height", 50.0, 150.0));
Node testNode = createTestNode();
array.add(config(testNode.getEffect(),
"width", 0.0, 50.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 0.0, 50.0),
box(0.0, 0.0, 50.0, 50.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"height", 0.0, 50.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 50.0, 0.0),
box(0.0, 0.0, 50.0, 50.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"x", 0.0, 50.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 50.0, 50.0),
box(50.0, 0.0, 50.0, 50.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"y", 0.0, 50.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 50.0, 50.0),
box(0.0, 50.0, 50.0, 50.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"width", 0.0, 50.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 0.0, 50.0),
box(0.0, 0.0, 50.0, 50.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"height", 0.0, 50.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 50.0, 0.0),
box(0.0, 0.0, 50.0, 50.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"x", 0.0, 50.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 50.0, 50.0),
box(50.0, 0.0, 50.0, 50.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"y", 0.0, 50.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 50.0, 50.0),
box(0.0, 50.0, 50.0, 50.0),
new BBoxComparator(0.01)));
return array;
}
public ColorInput_properties_Test(final Configuration configuration) {
super(configuration);
}
private static Rectangle createTestNode() {
Rectangle r = new Rectangle(100, 100);
ColorInput flood = new ColorInput();
flood.setHeight(50);
flood.setWidth(50);
r.setEffect(flood);
return r;
}
private static Rectangle createTestNodeWithChainedEffect() {
Rectangle r = new Rectangle(100, 100);
ColorInput flood = new ColorInput();
flood.setHeight(50);
flood.setWidth(50);
ColorAdjust c = new ColorAdjust();
c.setInput(flood);
r.setEffect(c);
return r;
}
}
