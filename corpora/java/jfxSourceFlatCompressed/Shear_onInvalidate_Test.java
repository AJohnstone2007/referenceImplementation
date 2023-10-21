package test.javafx.scene.transform;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.sun.javafx.scene.DirtyBits;
import javafx.scene.transform.Shear;
import test.com.sun.javafx.test.OnInvalidateMethodsTestBase;
@RunWith(Parameterized.class)
public class Shear_onInvalidate_Test extends OnInvalidateMethodsTestBase {
public Shear_onInvalidate_Test(Configuration config) {
super(config);
}
@Parameters
public static Collection<Object[]> data() {
Object[][] data = new Object[][] {
{new Configuration(Shear.class, "pivotX", 2.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })},
{new Configuration(Shear.class, "pivotY", 2.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })},
{new Configuration(Shear.class, "x", 20.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })},
{new Configuration(Shear.class, "y", 20.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })}
};
return Arrays.asList(data);
}
}
