package test.javafx.scene.effect;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BoxBlur;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class Bloom_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final Bloom testBloom = new Bloom();
return Arrays.asList(new Object[] {
config(testBloom, "input", null, new BoxBlur()),
config(testBloom, "threshold", 0.3, 0.6)
});
}
public Bloom_properties_Test(final Configuration configuration) {
super(configuration);
}
}
