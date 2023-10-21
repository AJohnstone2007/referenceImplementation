package test.javafx.scene.effect;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.effect.Light;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class PointLight_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final Light.Point testPointLight = new Light.Point();
return Arrays.asList(new Object[] {
config(testPointLight, "x", 0.0, -100.0),
config(testPointLight, "y", 0.0, -100.0),
config(testPointLight, "z", 0.0, 50.0)
});
}
public PointLight_properties_Test(final Configuration configuration) {
super(configuration);
}
}
