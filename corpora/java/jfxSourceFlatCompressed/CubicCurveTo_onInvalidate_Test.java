package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.sun.javafx.scene.DirtyBits;
import javafx.scene.shape.CubicCurveTo;
import test.com.sun.javafx.test.OnInvalidateMethodsTestBase;
@RunWith(Parameterized.class)
public class CubicCurveTo_onInvalidate_Test extends OnInvalidateMethodsTestBase {
public CubicCurveTo_onInvalidate_Test(Configuration config) {
super(config);
}
@Parameters
public static Collection<Object[]> data() {
Object[][] data = new Object[][] {
{new Configuration(CubicCurveTo.class, "x", 100.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_CONTENTS})},
{new Configuration(CubicCurveTo.class, "y", 100.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_CONTENTS})},
{new Configuration(CubicCurveTo.class, "controlX1", 10.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_CONTENTS})},
{new Configuration(CubicCurveTo.class, "controlY1", 122.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_CONTENTS})},
{new Configuration(CubicCurveTo.class, "controlX2", 45.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_CONTENTS})},
{new Configuration(CubicCurveTo.class, "controlY2", 55.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_CONTENTS})}
};
return Arrays.asList(data);
}
}
