package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.sun.javafx.scene.DirtyBits;
import javafx.scene.shape.VLineTo;
import test.com.sun.javafx.test.OnInvalidateMethodsTestBase;
@RunWith(Parameterized.class)
public class VLineTo_onInvalidate_Test extends OnInvalidateMethodsTestBase {
public VLineTo_onInvalidate_Test(Configuration config) {
super(config);
}
@Parameters
public static Collection<Object[]> data() {
Object[][] data = new Object[][] {
{new Configuration(VLineTo.class, "y", 100.0, new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_CONTENTS})}
};
return Arrays.asList(data);
}
}
