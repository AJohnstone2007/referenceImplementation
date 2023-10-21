package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.shape.CubicCurve;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class CubicCurve_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final CubicCurve testCubicCurve = new CubicCurve();
return Arrays.asList(new Object[] {
config(testCubicCurve, "startX", 0.0, 100.0),
config(testCubicCurve, "startY", 0.0, 100.0),
config(testCubicCurve, "controlX1", 20.0, 120.0),
config(testCubicCurve, "controlY1", 40.0, 140.0),
config(testCubicCurve, "controlX2", 40.0, 140.0),
config(testCubicCurve, "controlY2", 20.0, 120.0),
config(testCubicCurve, "endX", 100.0, 200.0),
config(testCubicCurve, "endY", 100.0, 200.0)
});
}
public CubicCurve_properties_Test(final Configuration configuration) {
super(configuration);
}
}
