package test.javafx.scene.effect;
import static test.com.sun.javafx.test.TestHelper.box;
import java.util.ArrayList;
import java.util.Collection;
import javafx.scene.Node;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.shape.Rectangle;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.BBoxComparator;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class PerspectiveTransform_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
ArrayList array = new ArrayList();
final PerspectiveTransform testPerspectiveTransform =
new PerspectiveTransform();
array.add(config(testPerspectiveTransform, "input",
null, new BoxBlur()));
array.add(config(testPerspectiveTransform, "ulx", 0.0, 10.0));
array.add(config(testPerspectiveTransform, "uly", 0.0, 10.0));
array.add(config(testPerspectiveTransform, "urx", 0.0, 310.0));
array.add(config(testPerspectiveTransform, "ury", 0.0, 40.0));
array.add(config(testPerspectiveTransform, "lrx", 0.0, 310.0));
array.add(config(testPerspectiveTransform, "lry", 0.0, 60.0));
array.add(config(testPerspectiveTransform, "llx", 0.0, 10.0));
array.add(config(testPerspectiveTransform, "lly", 0.0, 90.0));
Node testNode = createTestNode();
array.add(config(testNode.getEffect(),
"llx", 10.0, 20.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 10.0, 0.0),
box(0.0, 0.0, 20.0, 0.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"lly", 10.0, 20.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 0.0, 10.0),
box(0.0, 0.0, 0.0, 20.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"lrx", 10.0, 20.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 10.0, 0.0),
box(0.0, 0.0, 20.0, 0.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"lry", 10.0, 20.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 0.0, 10.0),
box(0.0, 0.0, 0.0, 20.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"ulx", 10.0, 20.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 10.0, 0.0),
box(0.0, 0.0, 20.0, 0.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"uly", 10.0, 20.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 0.0, 10.0),
box(0.0, 0.0, 0.0, 20.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"urx", 10.0, 20.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 10.0, 0.0),
box(0.0, 0.0, 20.0, 0.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
array.add(config(testNode.getEffect(),
"ury", 10.0, 20.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 0.0, 10.0),
box(0.0, 0.0, 0.0, 20.0),
new BBoxComparator(0.01)));
testNode = createTestNode();
PerspectiveTransform pt = (PerspectiveTransform)testNode.getEffect();
pt.setLlx(0); pt.setLly(0);
pt.setLrx(100); pt.setLly(0);
pt.setUlx(0); pt.setUly(100);
pt.setUrx(100); pt.setUly(100);
array.add(config(testNode.getEffect(),
"input", null, new BoxBlur(),
testNode,
"boundsInLocal",
box(0.0, 0.0, 100.0, 100.0),
box(0.0, 0.0, 100.0, 100.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"llx", 10.0, 20.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 10.0, 0.0),
box(0.0, 0.0, 20.0, 0.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"lly", 10.0, 20.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 0.0, 10.0),
box(0.0, 0.0, 0.0, 20.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"lrx", 10.0, 20.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 10.0, 0.0),
box(0.0, 0.0, 20.0, 0.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"lry", 10.0, 20.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 0.0, 10.0),
box(0.0, 0.0, 0.0, 20.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"ulx", 10.0, 20.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 10.0, 0.0),
box(0.0, 0.0, 20.0, 0.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"uly", 10.0, 20.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 0.0, 10.0),
box(0.0, 0.0, 0.0, 20.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"urx", 10.0, 20.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 10.0, 0.0),
box(0.0, 0.0, 20.0, 0.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
array.add(config(((ColorAdjust)testNode.getEffect()).getInput(),
"ury", 10.0, 20.0,
testNode,
"boundsInLocal",
box(0.0, 0.0, 0.0, 10.0),
box(0.0, 0.0, 0.0, 20.0),
new BBoxComparator(0.01)));
testNode = createTestNodeWithChainedEffect();
pt = (PerspectiveTransform)(((ColorAdjust)testNode.getEffect()).getInput());
pt.setLlx(0); pt.setLly(0);
pt.setLrx(100); pt.setLly(0);
pt.setUlx(0); pt.setUly(100);
pt.setUrx(100); pt.setUly(100);
array.add(config(pt,
"input", null, new BoxBlur(),
testNode,
"boundsInLocal",
box(0.0, 0.0, 100.0, 100.0),
box(0.0, 0.0, 100.0, 100.0),
new BBoxComparator(0.01)));
return array;
}
public PerspectiveTransform_properties_Test(final Configuration configuration) {
super(configuration);
}
private static Rectangle createTestNode() {
Rectangle r = new Rectangle(100, 100);
r.setEffect(new PerspectiveTransform());
return r;
}
private static Rectangle createTestNodeWithChainedEffect() {
Rectangle r = new Rectangle(100, 100);
ColorAdjust c = new ColorAdjust();
c.setInput(new PerspectiveTransform());
r.setEffect(c);
return r;
}
}
