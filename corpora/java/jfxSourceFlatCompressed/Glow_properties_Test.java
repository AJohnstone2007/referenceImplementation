package test.javafx.scene.effect;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Glow;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class Glow_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final Glow testGlow = new Glow();
return Arrays.asList(new Object[] {
config(testGlow, "input", null, new BoxBlur()),
config(testGlow, "level", 0.3, 0.6)
});
}
public Glow_properties_Test(final Configuration configuration) {
super(configuration);
}
}
