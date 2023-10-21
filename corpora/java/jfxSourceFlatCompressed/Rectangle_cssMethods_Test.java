package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.shape.Rectangle;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.CssMethodsTestBase;
@RunWith(Parameterized.class)
public class Rectangle_cssMethods_Test extends CssMethodsTestBase {
private static final Rectangle TEST_RECTANGLE = new Rectangle(100, 100);
@Parameters
public static Collection data() {
return Arrays.asList(new Object[] {
config(TEST_RECTANGLE, "arcWidth", 0.0,
"-fx-arc-width", 10.0),
config(TEST_RECTANGLE, "arcHeight", 0.0,
"-fx-arc-height", 20.0),
config(TEST_RECTANGLE, "translateX", 0.0,
"-fx-translate-x", 10.0)
});
}
public Rectangle_cssMethods_Test(final Configuration configuration) {
super(configuration);
}
}
