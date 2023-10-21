package test.javafx.scene.effect;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class InnerShadow_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final InnerShadow testInnerShadow = new InnerShadow();
return Arrays.asList(new Object[] {
config(testInnerShadow, "input", null, new BoxBlur()),
config(testInnerShadow, "radius", 20.0, 40.0),
config(testInnerShadow, "width", 100.0, 200.0),
config(testInnerShadow, "height", 100.0, 200.0),
config(testInnerShadow, "blurType",
BlurType.GAUSSIAN, BlurType.THREE_PASS_BOX),
config(testInnerShadow, "choke", 0.0, 0.5),
config(testInnerShadow, "color", Color.BLACK, Color.RED),
config(testInnerShadow, "offsetX", 0.0, 50.0),
config(testInnerShadow, "offsetY", 0.0, 50.0)
});
}
public InnerShadow_properties_Test(final Configuration configuration) {
super(configuration);
}
}
