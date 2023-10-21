package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.sun.javafx.scene.DirtyBits;
import javafx.scene.shape.ArcTo;
import test.com.sun.javafx.test.OnInvalidateMethodsTestBase;
@RunWith(Parameterized.class)
public class ArcTo_onInvalidate_Test extends OnInvalidateMethodsTestBase {
public ArcTo_onInvalidate_Test(Configuration config) {
super(config);
}
@Parameters
public static Collection<Object[]> data() {
Object[][] data = new Object[][] {
{new Configuration(ArcTo.class, "x", 100.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_CONTENTS})},
{new Configuration(ArcTo.class, "y", 100.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_CONTENTS})},
{new Configuration(ArcTo.class, "radiusX", 10.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_CONTENTS})},
{new Configuration(ArcTo.class, "radiusY", 122.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_CONTENTS})},
{new Configuration(ArcTo.class, "xAxisRotation", 45.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_CONTENTS})},
{new Configuration(ArcTo.class, "largeArcFlag", true, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_CONTENTS})},
{new Configuration(ArcTo.class, "sweepFlag", true, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_CONTENTS})}
};
return Arrays.asList(data);
}
}
