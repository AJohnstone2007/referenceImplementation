package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class Arc_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final Arc testArc = new Arc();
return Arrays.asList(new Object[] {
config(testArc, "centerX", 0.0, 100.0),
config(testArc, "centerY", 0.0, 100.0),
config(testArc, "radiusX", 50.0, 150.0),
config(testArc, "radiusY", 50.0, 150.0),
config(testArc, "startAngle", 0.0, 50.0),
config(testArc, "length", 40.0, 80.0),
config(testArc, "type", ArcType.OPEN, ArcType.ROUND)
});
}
public Arc_properties_Test(final Configuration configuration) {
super(configuration);
}
}
