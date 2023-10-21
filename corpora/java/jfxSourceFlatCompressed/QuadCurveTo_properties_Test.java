package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.shape.QuadCurveTo;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class QuadCurveTo_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final QuadCurveTo testQuadCurveTo = new QuadCurveTo();
return Arrays.asList(new Object[] {
config(testQuadCurveTo, "controlX", 80.0, 180.0),
config(testQuadCurveTo, "controlY", 20.0, 120.0),
config(testQuadCurveTo, "x", 100.0, 200.0),
config(testQuadCurveTo, "y", 100.0, 200.0)
});
}
public QuadCurveTo_properties_Test(final Configuration configuration) {
super(configuration);
}
}
