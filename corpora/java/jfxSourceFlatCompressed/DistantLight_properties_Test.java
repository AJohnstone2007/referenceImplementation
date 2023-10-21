package test.javafx.scene.effect;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.effect.Light;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class DistantLight_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final Light.Distant testDistantLight = new Light.Distant();
return Arrays.asList(new Object[] {
config(testDistantLight, "azimuth", 45.0, -135.0),
config(testDistantLight, "elevation", 45.0, 30.0)
});
}
public DistantLight_properties_Test(final Configuration configuration) {
super(configuration);
}
}
