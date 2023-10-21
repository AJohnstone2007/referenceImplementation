package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.CssMethodsTestBase;
@RunWith(Parameterized.class)
public class Shape_cssMethods_Test extends CssMethodsTestBase {
private static final Rectangle TEST_SHAPE = new Rectangle(100, 100);
@Parameters
public static Collection data() {
return Arrays.asList(new Object[] {
config(TEST_SHAPE, "fill", null, "-fx-fill", Color.RED),
config(TEST_SHAPE, "fill", null, "-fx-fill", null),
config(TEST_SHAPE, "smooth", false, "-fx-smooth", true),
config(TEST_SHAPE, "stroke", null, "-fx-stroke", Color.BLUE),
config(TEST_SHAPE, "strokeDashOffset", 0.0,
"-fx-stroke-dash-offset", 2.0),
config(TEST_SHAPE, "strokeLineCap", StrokeLineCap.SQUARE,
"-fx-stroke-line-cap", StrokeLineCap.ROUND),
config(TEST_SHAPE, "strokeLineJoin", StrokeLineJoin.BEVEL,
"-fx-stroke-line-join", StrokeLineJoin.MITER),
config(TEST_SHAPE, "strokeType", StrokeType.CENTERED,
"-fx-stroke-type", StrokeType.INSIDE),
config(TEST_SHAPE, "strokeMiterLimit", 0.0,
"-fx-stroke-miter-limit", 20.0),
config(TEST_SHAPE, "strokeWidth", 1.0,
"-fx-stroke-width", 2.0),
config(TEST_SHAPE, "translateY", 0.0,
"-fx-translate-y", 10.0)
});
}
public Shape_cssMethods_Test(final Configuration configuration) {
super(configuration);
}
static {
}
}
