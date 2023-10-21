package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.shape.VLineTo;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class VLineTo_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final VLineTo testVLineTo = new VLineTo();
return Arrays.asList(new Object[] {
config(testVLineTo, "y", 50.0, 100.0)
});
}
public VLineTo_properties_Test(final Configuration configuration) {
super(configuration);
}
}
