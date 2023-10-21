package test.javafx.scene.effect;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class ColorAdjust_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final ColorAdjust testColorAdjust = new ColorAdjust();
return Arrays.asList(new Object[] {
config(testColorAdjust, "input", null, new BoxBlur()),
config(testColorAdjust, "hue", 0.0, 0.5),
config(testColorAdjust, "saturation", 0.0, 0.5),
config(testColorAdjust, "brightness", 0.0, 0.5),
config(testColorAdjust, "contrast", 0.0, 0.5)
});
}
public ColorAdjust_properties_Test(final Configuration configuration) {
super(configuration);
}
}
