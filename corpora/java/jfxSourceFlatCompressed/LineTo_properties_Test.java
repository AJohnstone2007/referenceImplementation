package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.shape.LineTo;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class LineTo_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final LineTo testLineTo = new LineTo();
return Arrays.asList(new Object[] {
config(testLineTo, "x", 50.0, 100.0),
config(testLineTo, "y", 50.0, 100.0)
});
}
public LineTo_properties_Test(final Configuration configuration) {
super(configuration);
}
}
