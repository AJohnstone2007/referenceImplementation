package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.sun.javafx.scene.DirtyBits;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;
import test.com.sun.javafx.test.OnInvalidateMethodsTestBase;
@RunWith(Parameterized.class)
public class SVGPath_onInvalidate_Test extends OnInvalidateMethodsTestBase {
public SVGPath_onInvalidate_Test(Configuration config) {
super(config);
}
@Parameters
public static Collection<Object[]> data() {
Object[][] data = new Object[][] {
{new Configuration(SVGPath.class, "fillRule", FillRule.EVEN_ODD, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.SHAPE_FILLRULE})},
{new Configuration(SVGPath.class, "content", "cool", new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_CONTENTS})}
};
return Arrays.asList(data);
}
}
