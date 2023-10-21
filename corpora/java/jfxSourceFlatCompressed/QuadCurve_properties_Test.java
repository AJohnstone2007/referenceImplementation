package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.shape.QuadCurve;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class QuadCurve_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final QuadCurve testQuadCurve = new QuadCurve();
return Arrays.asList(new Object[] {
config(testQuadCurve, "startX", 0.0, 100.0),
config(testQuadCurve, "startY", 0.0, 100.0),
config(testQuadCurve, "controlX", 80.0, 180.0),
config(testQuadCurve, "controlY", 20.0, 120.0),
config(testQuadCurve, "endX", 100.0, 200.0),
config(testQuadCurve, "endY", 100.0, 200.0)
});
}
public QuadCurve_properties_Test(final Configuration configuration) {
super(configuration);
}
}
