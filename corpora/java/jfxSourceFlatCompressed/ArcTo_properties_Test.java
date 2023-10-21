package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.shape.ArcTo;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class ArcTo_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final ArcTo testArcTo = new ArcTo();
return Arrays.asList(new Object[] {
config(testArcTo, "radiusX", 50.0, 150.0),
config(testArcTo, "radiusY", 50.0, 150.0),
config(testArcTo, "largeArcFlag", false, true),
config(testArcTo, "sweepFlag", false, true),
config(testArcTo, "x", 0.0, 100.0),
config(testArcTo, "y", 0.0, 100.0)
});
}
public ArcTo_properties_Test(final Configuration configuration) {
super(configuration);
}
}
