package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class SVGPath_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final SVGPath testSvgPath = new SVGPath();
return Arrays.asList(new Object[] {
config(testSvgPath, "fillRule",
FillRule.NON_ZERO, FillRule.EVEN_ODD),
config(testSvgPath, "content", "", "M40,60 C42,48 44,30 25,32")
});
}
public SVGPath_properties_Test(final Configuration configuration) {
super(configuration);
}
}
