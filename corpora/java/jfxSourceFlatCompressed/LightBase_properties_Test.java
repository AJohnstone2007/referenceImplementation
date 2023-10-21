package test.javafx.scene;
import test.com.sun.javafx.test.PropertiesTestBase;
import java.util.ArrayList;
import java.util.Collection;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class)
public final class LightBase_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
ArrayList array = new ArrayList();
PointLight testLight = createPointLight();
array.add(config(testLight, "lightOn", true, false));
array.add(config(testLight, "color", Color.WHITE, null));
return array;
}
public LightBase_properties_Test(final Configuration configuration) {
super(configuration);
}
private static PointLight createPointLight() {
return new PointLight();
}
}
