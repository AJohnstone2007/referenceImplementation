package test.javafx.scene.effect;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DisplacementMap;
import javafx.scene.effect.FloatMap;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class DisplacementMap_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final DisplacementMap testDisplacementMap = new DisplacementMap();
return Arrays.asList(new Object[] {
config(testDisplacementMap, "input", null, new BoxBlur()),
config(testDisplacementMap, "mapData", null, new FloatMap()),
config(testDisplacementMap, "scaleX", 1.0, 0.5),
config(testDisplacementMap, "scaleY", 1.0, 0.5),
config(testDisplacementMap, "offsetX", 0.0, 10.0),
config(testDisplacementMap, "offsetY", 0.0, 10.0),
config(testDisplacementMap, "wrap", false, true)
});
}
public DisplacementMap_properties_Test(final Configuration configuration) {
super(configuration);
}
}
