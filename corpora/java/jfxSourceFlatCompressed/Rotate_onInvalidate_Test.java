package test.javafx.scene.transform;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.sun.javafx.scene.DirtyBits;
import javafx.scene.transform.Rotate;
import test.com.sun.javafx.test.OnInvalidateMethodsTestBase;
@RunWith(Parameterized.class)
public class Rotate_onInvalidate_Test extends OnInvalidateMethodsTestBase {
public Rotate_onInvalidate_Test(Configuration config) {
super(config);
}
@Parameters
public static Collection<Object[]> data() {
Object[][] data = new Object[][] {
{new Configuration(Rotate.class, "pivotX", 2.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })},
{new Configuration(Rotate.class, "pivotY", 2.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })},
{new Configuration(Rotate.class, "pivotZ", 2.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })},
{new Configuration(Rotate.class, "angle", 20.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })}
};
return Arrays.asList(data);
}
}
