package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.shape.Ellipse;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class Ellipse_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final Ellipse testEllipse = new Ellipse();
return Arrays.asList(new Object[] {
config(testEllipse, "centerX", 0.0, 100.0),
config(testEllipse, "centerY", 0.0, 100.0),
config(testEllipse, "radiusX", 50.0, 150.0),
config(testEllipse, "radiusY", 50.0, 150.0)
});
}
public Ellipse_properties_Test(final Configuration configuration) {
super(configuration);
}
}
