package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.shape.Line;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class Line_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final Line testLine = new Line();
return Arrays.asList(new Object[] {
config(testLine, "startX", 0.0, 100.0),
config(testLine, "startY", 0.0, 100.0),
config(testLine, "endX", 100.0, 0.0),
config(testLine, "endY", 100.0, 0.0),
});
}
public Line_properties_Test(final Configuration configuration) {
super(configuration);
}
}
