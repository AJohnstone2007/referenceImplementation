package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.sun.javafx.scene.DirtyBits;
import javafx.scene.shape.Rectangle;
import test.com.sun.javafx.test.OnInvalidateMethodsTestBase;
@RunWith(Parameterized.class)
public class Rectangle_onInvalidate_Test extends OnInvalidateMethodsTestBase {
public Rectangle_onInvalidate_Test(Configuration configuration) {
super(configuration);
}
@Parameters
public static Collection<Object[]>data() {
Object[][] data = new Object[][] {
{new Configuration(Rectangle.class, "x", 100.0, new DirtyBits[] {DirtyBits.NODE_GEOMETRY, DirtyBits.NODE_BOUNDS})},
{new Configuration(Rectangle.class, "y", 100.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(Rectangle.class, "width", 123.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(Rectangle.class, "height", 123.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(Rectangle.class, "arcWidth", 11.0, new DirtyBits[] {DirtyBits.NODE_GEOMETRY})},
{new Configuration(Rectangle.class, "arcHeight", 22.0, new DirtyBits[] {DirtyBits.NODE_GEOMETRY})}
};
return Arrays.asList(data);
}
}
