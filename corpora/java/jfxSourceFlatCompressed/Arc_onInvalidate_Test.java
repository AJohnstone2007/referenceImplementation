package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.sun.javafx.scene.DirtyBits;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import test.com.sun.javafx.test.OnInvalidateMethodsTestBase;
@RunWith(Parameterized.class)
public class Arc_onInvalidate_Test extends OnInvalidateMethodsTestBase {
public Arc_onInvalidate_Test(Configuration configuration) {
super(configuration);
}
@Parameters
public static Collection<Object[]>data() {
Object[][] data = new Object[][] {
{new Configuration(Arc.class, "centerX", 222.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(Arc.class, "centerY", 111.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(Arc.class, "startAngle", 10.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(Arc.class, "length", 180.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(Arc.class, "radiusX", 123.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(Arc.class, "radiusY", 321.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(Arc.class, "type", ArcType.ROUND, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})}
};
return Arrays.asList(data);
}
}
