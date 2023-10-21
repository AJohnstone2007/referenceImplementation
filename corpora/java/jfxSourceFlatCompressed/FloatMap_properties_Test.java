package test.javafx.scene.effect;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.effect.FloatMap;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class FloatMap_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final FloatMap testFloatMap = new FloatMap();
return Arrays.asList(new Object[] {
config(testFloatMap, "width", 64, 128),
config(testFloatMap, "height", 64, 128)
});
}
public FloatMap_properties_Test(final Configuration configuration) {
super(configuration);
}
}
