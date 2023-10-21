package test.javafx.scene.effect;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class Blend_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final Blend testBlend = new Blend();
return Arrays.asList(new Object[] {
config(testBlend, "mode", BlendMode.SRC_OVER, BlendMode.SRC_ATOP),
config(testBlend, "opacity", 1.0, 0.5),
config(testBlend, "bottomInput", null, new BoxBlur()),
config(testBlend, "topInput", null, new BoxBlur())
});
}
public Blend_properties_Test(final Configuration configuration) {
super(configuration);
}
}
