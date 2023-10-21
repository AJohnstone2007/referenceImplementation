package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.shape.CubicCurveTo;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class CubicCurveTo_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final CubicCurveTo testCubicCurveTo = new CubicCurveTo();
return Arrays.asList(new Object[] {
config(testCubicCurveTo, "controlX1", 20.0, 120.0),
config(testCubicCurveTo, "controlY1", 40.0, 140.0),
config(testCubicCurveTo, "controlX2", 40.0, 140.0),
config(testCubicCurveTo, "controlY2", 20.0, 120.0),
config(testCubicCurveTo, "x", 0.0, 100.0),
config(testCubicCurveTo, "y", 0.0, 100.0)
});
}
public CubicCurveTo_properties_Test(final Configuration configuration) {
super(configuration);
}
}
