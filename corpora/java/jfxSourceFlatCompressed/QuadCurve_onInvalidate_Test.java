package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.sun.javafx.scene.DirtyBits;
import javafx.scene.shape.QuadCurve;
import test.com.sun.javafx.test.OnInvalidateMethodsTestBase;
@RunWith(Parameterized.class)
public class QuadCurve_onInvalidate_Test extends OnInvalidateMethodsTestBase {
public QuadCurve_onInvalidate_Test(Configuration config) {
super(config);
}
@Parameters
public static Collection<Object[]>data() {
Object[][] data = new Object[][] {
{new Configuration(QuadCurve.class, "startX", 111.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(QuadCurve.class, "startY", 101.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(QuadCurve.class, "controlX", 121.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(QuadCurve.class, "controlY", 131.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(QuadCurve.class, "endX", 231.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(QuadCurve.class, "endY", 134.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
};
return Arrays.asList(data);
}
}
