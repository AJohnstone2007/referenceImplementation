package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.shape.MoveTo;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class MoveTo_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final MoveTo testMoveTo = new MoveTo();
return Arrays.asList(new Object[] {
config(testMoveTo, "x", 50.0, 100.0),
config(testMoveTo, "y", 50.0, 100.0)
});
}
public MoveTo_properties_Test(final Configuration configuration) {
super(configuration);
}
}
