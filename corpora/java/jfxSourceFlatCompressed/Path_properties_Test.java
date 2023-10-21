package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Path;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class Path_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final Path testPath = new Path();
return Arrays.asList(new Object[] {
config(testPath, "fillRule", FillRule.NON_ZERO, FillRule.EVEN_ODD)
});
}
public Path_properties_Test(final Configuration configuration) {
super(configuration);
}
}
