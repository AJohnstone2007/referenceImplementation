package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.sun.javafx.scene.DirtyBits;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Path;
import test.com.sun.javafx.test.OnInvalidateMethodsTestBase;
@RunWith(Parameterized.class)
public class Path_onInvalidate_Test extends OnInvalidateMethodsTestBase {
public Path_onInvalidate_Test(Configuration config) {
super(config);
}
@Parameters
public static Collection<Object[]>data() {
Object[][] data = new Object[][] {
{new Configuration(Path.class, "fillRule", FillRule.EVEN_ODD, new DirtyBits[]{DirtyBits.NODE_CONTENTS, DirtyBits.NODE_BOUNDS})}
};
return Arrays.asList(data);
}
}
