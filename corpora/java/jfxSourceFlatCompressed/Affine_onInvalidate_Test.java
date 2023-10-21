package test.javafx.scene.transform;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.sun.javafx.scene.DirtyBits;
import javafx.scene.transform.Affine;
import test.com.sun.javafx.test.OnInvalidateMethodsTestBase;
@RunWith(Parameterized.class)
public class Affine_onInvalidate_Test extends OnInvalidateMethodsTestBase {
public Affine_onInvalidate_Test(Configuration config) {
super(config);
}
@Parameters
public static Collection<Object[]> data() {
Object[][] data = new Object[][] {
{new Configuration(Affine.class, "mxx", 2.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })},
{new Configuration(Affine.class, "mxy", 2.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })},
{new Configuration(Affine.class, "mxz", 2.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })},
{new Configuration(Affine.class, "myx", 2.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })},
{new Configuration(Affine.class, "myz", 2.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })},
{new Configuration(Affine.class, "mzx", 2.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })},
{new Configuration(Affine.class, "mzy", 2.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })},
{new Configuration(Affine.class, "tx", 2.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })},
{new Configuration(Affine.class, "ty", 2.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })},
{new Configuration(Affine.class, "tz", 2.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })}
};
return Arrays.asList(data);
}
}
