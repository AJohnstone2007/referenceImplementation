package test.com.sun.javafx.util;
import test.com.sun.javafx.pgstub.StubToolkit;
import test.com.sun.javafx.pgstub.StubToolkit.ScreenConfiguration;
import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.util.Utils;
import java.util.Arrays;
import java.util.Collection;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class)
public final class Utils_getScreenForRectangle_Test {
private final Rectangle2D rectangle;
private final int expectedScreenIndex;
@Parameters
public static Collection data() {
return Arrays.asList(
new Object[] {
config(100, 100, 100, 100, 0),
config(2020, 200, 100, 100, 1),
config(1920 - 75, 200, 100, 100, 0),
config(1920 - 25, 200, 100, 100, 1),
config(0, 0, 3360, 1200, 0),
config(2020, 50, 100, 100, 1),
config(2020, 70, 100, 100, 1),
config(1970, -50, 100, 100, 0),
config(2170, -50, 100, 100, 1),
config(2020, 1150, 100, 100, 1),
config(2020, 1170, 100, 100, 0),
config(1970, 1250, 100, 100, 0),
config(2170, 1250, 100, 100, 1)
});
}
public Utils_getScreenForRectangle_Test(
final Rectangle2D rectangle, final int expectedScreenIndex) {
this.rectangle = rectangle;
this.expectedScreenIndex = expectedScreenIndex;
}
@Before
public void setUp() {
((StubToolkit) Toolkit.getToolkit()).setScreens(
new ScreenConfiguration(0, 0, 1920, 1200, 0, 0, 1920, 1172, 96),
new ScreenConfiguration(1920, 160, 1440, 900,
1920, 160, 1440, 900, 96));
}
@After
public void tearDown() {
((StubToolkit) Toolkit.getToolkit()).resetScreens();
}
@Test
public void test() {
final Screen selectedScreen = Utils.getScreenForRectangle(rectangle);
Assert.assertEquals(expectedScreenIndex,
Screen.getScreens().indexOf(selectedScreen));
}
private static Object[] config(final double x, final double y,
final double width, final double height,
final int expectedScreenIndex) {
return new Object[] {
new Rectangle2D(x, y, width, height),
expectedScreenIndex
};
}
}
