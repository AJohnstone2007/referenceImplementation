package test.javafx.scene.shape;
import static test.com.sun.javafx.test.TestHelper.box;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.shape.Rectangle;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class Rectangle_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final Rectangle testRectangle = createTestRectangle();
return Arrays.asList(new Object[] {
config(testRectangle, "x", 0.0, 100.0),
config(testRectangle, "y", 0.0, 200.0),
config(testRectangle, "width", 50.0, 200.0),
config(testRectangle, "height", 50.0, 200.0),
config(testRectangle, "arcWidth", 10.0, 20.0),
config(testRectangle, "arcHeight", 10.0, 20.0),
config(createTestRectangle(),
"x", 0.0, 100.0,
"boundsInLocal",
box(0, 0, 100, 100), box(100, 0, 100, 100)),
config(createTestRectangle(),
"y", 0.0, 100.0,
"boundsInLocal",
box(0, 0, 100, 100), box(0, 100, 100, 100)),
config(createTestRectangle(),
"width", 50.0, 200.0,
"boundsInLocal",
box(0, 0, 50, 100), box(0, 0, 200, 100)),
config(createTestRectangle(),
"height", 50.0, 200.0,
"boundsInLocal",
box(0, 0, 100, 50), box(0, 0, 100, 200)),
config(createTestRectangle(),
"x", 0.0, 100.0,
"layoutBounds",
box(0, 0, 100, 100), box(100, 0, 100, 100)),
config(createTestRectangle(),
"y", 0.0, 100.0,
"layoutBounds",
box(0, 0, 100, 100), box(0, 100, 100, 100)),
config(createTestRectangle(),
"width", 50.0, 200.0,
"layoutBounds",
box(0, 0, 50, 100), box(0, 0, 200, 100)),
config(createTestRectangle(),
"height", 50.0, 200.0,
"layoutBounds",
box(0, 0, 100, 50), box(0, 0, 100, 200)),
config(createTestRectangle(),
"translateX", 0.0, 100.0,
"boundsInParent",
box(0, 0, 100, 100), box(100, 0, 100, 100)),
config(createTestRectangle(),
"translateY", 0.0, 100.0,
"boundsInParent",
box(0, 0, 100, 100), box(0, 100, 100, 100))
});
}
public Rectangle_properties_Test(final Configuration configuration) {
super(configuration);
}
private static Rectangle createTestRectangle() {
return new Rectangle(100, 100);
}
}
