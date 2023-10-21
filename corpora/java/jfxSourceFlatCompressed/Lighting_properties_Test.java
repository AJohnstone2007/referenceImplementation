package test.javafx.scene.effect;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class Lighting_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final Lighting testLighting = new Lighting();
return Arrays.asList(new Object[] {
config(testLighting, "light",
new Light.Distant(),
new Light.Point()),
config(testLighting, "bumpInput", null, new BoxBlur()),
config(testLighting, "contentInput", null, new BoxBlur()),
config(testLighting, "diffuseConstant", 1.0, 1.5),
config(testLighting, "specularConstant", 0.3, 0.6),
config(testLighting, "specularExponent", 20.0, 30.0),
config(testLighting, "surfaceScale", 1.5, 0.5)
});
}
public Lighting_properties_Test(final Configuration configuration) {
super(configuration);
}
}
