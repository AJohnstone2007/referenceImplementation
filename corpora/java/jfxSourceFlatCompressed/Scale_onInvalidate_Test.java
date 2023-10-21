package test.javafx.scene.transform;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.sun.javafx.scene.DirtyBits;
import javafx.scene.transform.Scale;
import test.com.sun.javafx.test.OnInvalidateMethodsTestBase;
@RunWith(Parameterized.class)
public class Scale_onInvalidate_Test extends OnInvalidateMethodsTestBase {
public Scale_onInvalidate_Test(Configuration config) {
super(config);
}
@Parameters
public static Collection<Object[]> data() {
Object[][] data = new Object[][] {
{new Configuration(Scale.class, "pivotX", 2.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })},
{new Configuration(Scale.class, "pivotY", 2.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })},
{new Configuration(Scale.class, "pivotZ", 2.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })},
{new Configuration(Scale.class, "x", 20.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })},
{new Configuration(Scale.class, "y", 20.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })},
{new Configuration(Scale.class, "z", 20.0, new DirtyBits[] {DirtyBits.NODE_TRANSFORM, DirtyBits.NODE_TRANSFORMED_BOUNDS })}
};
return Arrays.asList(data);
}
}
