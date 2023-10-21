package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.shape.Circle;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class Circle_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final Circle testCircle = new Circle();
return Arrays.asList(new Object[] {
config(testCircle, "centerX", 0.0, 100.0),
config(testCircle, "centerY", 0.0, 100.0),
config(testCircle, "radius", 50.0, 150.0)
});
}
public Circle_properties_Test(final Configuration configuration) {
super(configuration);
}
}
