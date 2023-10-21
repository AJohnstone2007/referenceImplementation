package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.sun.javafx.scene.DirtyBits;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import test.com.sun.javafx.test.OnInvalidateMethodsTestBase;
@RunWith(Parameterized.class)
public class Circle_onInvalidate_Test extends OnInvalidateMethodsTestBase {
public Circle_onInvalidate_Test(Configuration config) {
super(config);
}
@Parameters
public static Collection<Object[]>data() {
Object[][] data = new Object[][] {
{new Configuration(Circle.class, "centerX", 255.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(Circle.class, "centerY", 552.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(Circle.class, "radius", 55.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(CubicCurve.class, "startX", 5.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})}
};
return Arrays.asList(data);
}
}
