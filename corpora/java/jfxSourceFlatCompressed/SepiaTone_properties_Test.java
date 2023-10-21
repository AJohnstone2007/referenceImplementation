package test.javafx.scene.effect;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.SepiaTone;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
@RunWith(Parameterized.class)
public final class SepiaTone_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
final SepiaTone testSepiaTone = new SepiaTone();
return Arrays.asList(new Object[] {
config(testSepiaTone, "input", null, new BoxBlur()),
config(testSepiaTone, "level", 0.3, 0.6)
});
}
public SepiaTone_properties_Test(final Configuration configuration) {
super(configuration);
}
}
