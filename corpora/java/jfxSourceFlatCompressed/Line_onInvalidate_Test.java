package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.sun.javafx.scene.DirtyBits;
import javafx.scene.shape.Line;
import test.com.sun.javafx.test.OnInvalidateMethodsTestBase;
@RunWith(Parameterized.class)
public class Line_onInvalidate_Test extends OnInvalidateMethodsTestBase {
public Line_onInvalidate_Test(Configuration config) {
super(config);
}
@Parameters
public static Collection<Object[]>data() {
Object[][] data = new Object[][] {
{new Configuration(Line.class, "startX", 10.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(Line.class, "startY", 11.0, new DirtyBits[]{DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(Line.class, "endX", 100.0, new DirtyBits[]{DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(Line.class, "endY", 10.0, new DirtyBits[]{DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})}
};
return Arrays.asList(data);
}
}
