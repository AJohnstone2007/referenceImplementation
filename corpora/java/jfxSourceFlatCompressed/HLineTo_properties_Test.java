package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.shape.HLineTo;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class HLineTo_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final HLineTo testHLineTo = new HLineTo();
return Arrays.asList(new Object[] {
config(testHLineTo, "x", 50.0, 100.0)
});
}
public HLineTo_properties_Test(final Configuration configuration) {
super(configuration);
}
}
