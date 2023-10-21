package test.javafx.scene;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.effect.BlendMode;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.CssMethodsTestBase;
@RunWith(Parameterized.class)
public class Parent_cssMethods_Test extends CssMethodsTestBase {
private static final Parent TEST_PARENT = new Group();
@Parameters
public static Collection data() {
return Arrays.asList(new Object[] {
config(TEST_PARENT, "translateX", 0.0, "-fx-translate-x", 10.0)
});
}
public Parent_cssMethods_Test(final Configuration configuration) {
super(configuration);
}
}
