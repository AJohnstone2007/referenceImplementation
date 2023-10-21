package test.javafx.scene.effect;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.effect.Light;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class SpotLight_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final Light.Spot testSpotLight = new Light.Spot();
return Arrays.asList(new Object[] {
config(testSpotLight, "pointsAtX", 0.0, 100.0),
config(testSpotLight, "pointsAtY", 0.0, 100.0),
config(testSpotLight, "pointsAtZ", 0.0, 100.0),
config(testSpotLight, "specularExponent", 1.0, 3.0)
});
}
public SpotLight_properties_Test(final Configuration configuration) {
super(configuration);
}
}
