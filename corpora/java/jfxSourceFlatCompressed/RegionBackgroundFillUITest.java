package test.robot.javafx.scene.layout;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assume.assumeTrue;
public class RegionBackgroundFillUITest extends RegionUITestBase {
final String EXPECTED_WARNING = "EXPECTED WARNING: This is a negative test"
+ " to verify that negative value is not accepted for -fx-background-radius."
+ " A 'No radii value may be < 0' warning message is expected.";
@Test(timeout = 20000)
public void basicFill() {
setStyle("-fx-background-color: red;");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void translucentFill() {
setStyle("-fx-background-color: rgba(255, 0, 0, .2);");
checkRegionCornersAndBoundariesOfBackgroundFill(
region.getBackground().getFills().get(0), Color.rgb(255, 204, 204), SCENE_FILL);
}
@Test(timeout = 20000)
public void basicFill_Insets1() {
setStyle(
"-fx-background-color: red;" +
"-fx-background-insets: 5");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void basicFill_Insets2() {
setStyle(
"-fx-background-color: red;" +
"-fx-background-insets: 5 10");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void basicFill_Insets3() {
setStyle(
"-fx-background-color: red;" +
"-fx-background-insets: 5 10 15");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void basicFill_Insets4() {
setStyle(
"-fx-background-color: red;" +
"-fx-background-insets: 5 10 15 20");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void basicFill_NegativeInsets1() {
setStyle(
"-fx-background-color: red;" +
"-fx-background-insets: -5");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void basicFill_NegativeInsets2() {
setStyle(
"-fx-background-color: red;" +
"-fx-background-insets: -5 -10");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void basicFill_NegativeInsets3() {
setStyle(
"-fx-background-color: red;" +
"-fx-background-insets: -5 -10 -15");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void basicFill_NegativeInsets4() {
setStyle(
"-fx-background-color: red;" +
"-fx-background-insets: -5 -10 -15 -20");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void basicFill_MixedInsets() {
setStyle(
"-fx-background-color: red;" +
"-fx-background-insets: 10 10 -10 10");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void basicFill_Radius1() {
assumeTrue(checkIntegralUIScale());
setStyle(
"-fx-background-color: red;" +
"-fx-background-radius: 10");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void basicFill_Radius2() {
assumeTrue(checkIntegralUIScale());
setStyle(
"-fx-background-color: red;" +
"-fx-background-radius: 10 20");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void basicFill_Radius3() {
assumeTrue(checkIntegralUIScale());
setStyle(
"-fx-background-color: red;" +
"-fx-background-radius: 10 20 30");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void basicFill_Radius4() {
assumeTrue(checkIntegralUIScale());
setStyle(
"-fx-background-color: red;" +
"-fx-background-radius: 10 20 30 40");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void basicFill_RadiusAndInsets() {
assumeTrue(checkIntegralUIScale());
setStyle(
"-fx-background-color: red;" +
"-fx-background-radius: 10 20 30 40;" +
"-fx-background-insets: 5 10 15 20");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void basicFill_NegativeRadius1() {
System.err.println(EXPECTED_WARNING);
setStyle(
"-fx-background-color: red;" +
"-fx-background-radius: -10");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void basicFill_NegativeRadius2() {
System.err.println(EXPECTED_WARNING);
setStyle(
"-fx-background-color: red;" +
"-fx-background-radius: -10 -20");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void basicFill_NegativeRadius3() {
System.err.println(EXPECTED_WARNING);
setStyle(
"-fx-background-color: red;" +
"-fx-background-radius: -10 -20 -30");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void basicFill_NegativeRadius4() {
System.err.println(EXPECTED_WARNING);
setStyle(
"-fx-background-color: red;" +
"-fx-background-radius: -10 -20 -30 -40");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void imageFill() {
setStyle("-fx-background-color: repeating-image-pattern('test/robot/javafx/scene/layout/test20x20.png');");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void imageFill_MixedInsets() {
setStyle(
"-fx-background-color: repeating-image-pattern('test/robot/javafx/scene/layout/test20x20.png');" +
"-fx-background-insets: 5 10 -15 20");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void imageFill_Radius4() {
setStyle(
"-fx-background-color: repeating-image-pattern('test/robot/javafx/scene/layout/test20x20.png');" +
"-fx-background-radius: 10 20 30 40");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void imageFill_MissingImage() {
setStyle(
"-fx-background-color: repeating-image-pattern('test/robot/javafx/scene/layout/missing.png');" +
"-fx-background-radius: 10 20 30 40");
assertColorEquals(SCENE_FILL, WIDTH / 2, HEIGHT / 2, TOLERANCE);
}
@Test(timeout = 20000)
public void imageFill_Stretched() {
setStyle("-fx-background-color: image-pattern('test/robot/javafx/scene/layout/test20x20.png');");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void imageFill_Stretched2() {
setStyle("-fx-background-color: image-pattern('test/robot/javafx/scene/layout/test20x20.png', 0, 0, 1, 1);");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void imageFill_Stretched3() {
setStyle("-fx-background-color: image-pattern('test/robot/javafx/scene/layout/test20x20.png', 0, 0, 1, 1, true);");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void imageFill_Tiled() {
setStyle("-fx-background-color: image-pattern('test/robot/javafx/scene/layout/test20x20.png', 0, 0, 40, 40, false);");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void linearFill() {
setStyle("-fx-background-color: linear-gradient(to bottom, red 0%, blue 100%);");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void linearFill2() {
setStyle("-fx-background-color: linear-gradient(to right, red 0%, blue 100%);");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void linearFill_MixedInsets() {
setStyle(
"-fx-background-color: linear-gradient(to bottom, red 0%, blue 100%);" +
"-fx-background-insets: 5 10 -15 20");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void linearFill_Radius4() {
setStyle(
"-fx-background-color: linear-gradient(to bottom, red 0%, blue 100%);" +
"-fx-background-radius: 10 20 30 40");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void testScenario1() {
assumeTrue(checkIntegralUIScale());
setStyle(
"-fx-background-color: red;" +
"-fx-background-insets: 0 0 -10 0, 0, 10, 20;" +
"-fx-background-radius: 10 20 30 40;" +
"-fx-padding: 10 20 30 40;");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void testScenario2() {
assumeTrue(checkIntegralUIScale());
setStyle(
"-fx-background-color: red, green, blue, grey;" +
"-fx-background-insets: 0 0 -10 0, 0, 10, 20;" +
"-fx-background-radius: 5 10 15 20, 25, 30 35 40 45;" +
"-fx-padding: 10 20 30 40;");
BackgroundFill red = region.getBackground().getFills().get(0);
BackgroundFill green = region.getBackground().getFills().get(1);
BackgroundFill blue = region.getBackground().getFills().get(2);
BackgroundFill grey = region.getBackground().getFills().get(3);
checkRegionLeftBoundary(red, Color.GREEN, SCENE_FILL);
checkRegionTopLeftCorner(red, SCENE_FILL);
checkRegionTopBoundary(red, Color.GREEN, SCENE_FILL);
checkRegionTopRightCorner(red, SCENE_FILL);
checkRegionRightBoundary(red, Color.GREEN, SCENE_FILL);
checkRegionBottomRightCorner(red, SCENE_FILL);
checkRegionBottomBoundary(red, SCENE_FILL);
checkRegionBottomLeftCorner(red, SCENE_FILL);
checkRegionLeftBoundary(green, SCENE_FILL);
checkRegionTopLeftCorner(green, Color.RED);
checkRegionTopBoundary(green, SCENE_FILL);
checkRegionTopRightCorner(green, Color.RED);
checkRegionRightBoundary(green, SCENE_FILL);
checkRegionBottomRightCorner(green, Color.RED);
checkRegionBottomBoundary(green, Color.RED);
checkRegionBottomLeftCorner(green, Color.RED);
checkRegionCornersAndBoundariesOfBackgroundFill(blue, Color.GREEN);
checkRegionCornersAndBoundariesOfBackgroundFill(grey, Color.BLUE);
}
@Test(timeout = 20000)
public void testScenario3() {
assumeTrue(checkIntegralUIScale());
setStyle(
"-fx-background-color: red, green, blue, grey;" +
"-fx-background-insets: 0 0 -10 0, 0, 10, 20;" +
"-fx-background-radius: 10 20 30 40;" +
"-fx-padding: 10 20 30 40;");
BackgroundFill red = region.getBackground().getFills().get(0);
BackgroundFill green = region.getBackground().getFills().get(1);
BackgroundFill blue = region.getBackground().getFills().get(2);
BackgroundFill grey = region.getBackground().getFills().get(3);
checkRegionLeftBoundary(red, Color.GREEN, SCENE_FILL);
checkRegionTopLeftCorner(red, Color.GREEN, SCENE_FILL);
checkRegionTopBoundary(red, Color.GREEN, SCENE_FILL);
checkRegionTopRightCorner(red, Color.GREEN, SCENE_FILL);
checkRegionRightBoundary(red, Color.GREEN, SCENE_FILL);
checkRegionBottomRightCorner(red, SCENE_FILL);
checkRegionBottomBoundary(red, SCENE_FILL);
checkRegionBottomLeftCorner(red, SCENE_FILL);
checkRegionLeftBoundary(green, SCENE_FILL);
checkRegionTopLeftCorner(green, SCENE_FILL);
checkRegionTopBoundary(green, SCENE_FILL);
checkRegionTopRightCorner(green, SCENE_FILL);
checkRegionRightBoundary(green, SCENE_FILL);
checkRegionBottomRightCorner(green, Color.RED);
checkRegionBottomBoundary(green, Color.RED);
checkRegionBottomLeftCorner(green, Color.RED);
checkRegionCornersAndBoundariesOfBackgroundFill(blue, Color.GREEN);
checkRegionCornersAndBoundariesOfBackgroundFill(grey, Color.BLUE);
}
@Test(timeout = 20000)
public void testScenario4() {
assumeTrue(checkIntegralUIScale());
setStyle(
"-fx-background-color: red, green, blue, repeating-image-pattern('test/robot/javafx/scene/layout/test20x20.png');" +
"-fx-background-insets: 0 0 -10 0, 0, 10, 20;" +
"-fx-background-radius: 10 20 30 40;" +
"-fx-padding: 10 20 30 40;");
BackgroundFill red = region.getBackground().getFills().get(0);
BackgroundFill green = region.getBackground().getFills().get(1);
BackgroundFill blue = region.getBackground().getFills().get(2);
BackgroundFill image = region.getBackground().getFills().get(3);
checkRegionLeftBoundary(red, Color.GREEN, SCENE_FILL);
checkRegionTopLeftCorner(red, Color.GREEN, SCENE_FILL);
checkRegionTopBoundary(red, Color.GREEN, SCENE_FILL);
checkRegionTopRightCorner(red, Color.GREEN, SCENE_FILL);
checkRegionRightBoundary(red, Color.GREEN, SCENE_FILL);
checkRegionBottomRightCorner(red, SCENE_FILL);
checkRegionBottomBoundary(red, SCENE_FILL);
checkRegionBottomLeftCorner(red, SCENE_FILL);
checkRegionLeftBoundary(green, SCENE_FILL);
checkRegionTopLeftCorner(green, SCENE_FILL);
checkRegionTopBoundary(green, SCENE_FILL);
checkRegionTopRightCorner(green, SCENE_FILL);
checkRegionRightBoundary(green, SCENE_FILL);
checkRegionBottomRightCorner(green, Color.RED);
checkRegionBottomBoundary(green, Color.RED);
checkRegionBottomLeftCorner(green, Color.RED);
checkRegionCornersAndBoundariesOfBackgroundFill(blue, Color.GREEN);
}
@Test(timeout = 20000)
public void testScenario5() {
assumeTrue(checkIntegralUIScale());
setStyle(
"-fx-background-color: red, green, repeating-image-pattern('test/robot/javafx/scene/layout/test20x20.png'), blue;" +
"-fx-background-insets: 0 0 -10 0, 0, 10, 20;" +
"-fx-background-radius: 10 20 30 40;" +
"-fx-padding: 10 20 30 40;");
BackgroundFill red = region.getBackground().getFills().get(0);
BackgroundFill green = region.getBackground().getFills().get(1);
BackgroundFill image = region.getBackground().getFills().get(2);
BackgroundFill blue = region.getBackground().getFills().get(3);
checkRegionLeftBoundary(red, Color.GREEN, SCENE_FILL);
checkRegionTopLeftCorner(red, Color.GREEN, SCENE_FILL);
checkRegionTopBoundary(red, Color.GREEN, SCENE_FILL);
checkRegionTopRightCorner(red, Color.GREEN, SCENE_FILL);
checkRegionRightBoundary(red, Color.GREEN, SCENE_FILL);
checkRegionBottomRightCorner(red, SCENE_FILL);
checkRegionBottomBoundary(red, SCENE_FILL);
checkRegionBottomLeftCorner(red, SCENE_FILL);
checkRegionLeftBoundary(green, SCENE_FILL);
checkRegionTopLeftCorner(green, SCENE_FILL);
checkRegionTopBoundary(green, SCENE_FILL);
checkRegionTopRightCorner(green, SCENE_FILL);
checkRegionRightBoundary(green, SCENE_FILL);
checkRegionBottomRightCorner(green, Color.RED);
checkRegionBottomBoundary(green, Color.RED);
checkRegionBottomLeftCorner(green, Color.RED);
checkRegionCornersAndBoundariesOfBackgroundFill(image, Color.GREEN);
}
@Test(timeout = 20000)
public void testExample1() {
setStyle(
"-fx-background-color: red, green, blue;" +
"-fx-background-insets: 4, 8, 12, 16;" +
"-fx-background-radius: 14;");
checkRegionCornersAndBoundariesForFills();
}
@Test(timeout = 20000)
public void testOnePixelTopInset() {
assumeTrue(checkIntegralUIScale());
setStyle(
"-fx-background-color: red, yellow;" +
"-fx-background-insets: 0, 1 0 0 0;");
BackgroundFill red = region.getBackground().getFills().get(0);
BackgroundFill yellow = region.getBackground().getFills().get(1);
checkRegionLeftBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopLeftCorner(red, Color.RED, SCENE_FILL, 0, .2);
checkRegionTopBoundary(red, Color.RED, SCENE_FILL, 0, .2);
checkRegionTopRightCorner(red, Color.RED, SCENE_FILL, 0, .2);
checkRegionRightBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomRightCorner(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomLeftCorner(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionLeftBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopLeftCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopBoundary(yellow, Color.YELLOW, Color.RED, 0, .2);
checkRegionTopRightCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionRightBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomRightCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomLeftCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
}
@Test(timeout = 20000)
public void testOnePixelRightInset() {
assumeTrue(checkIntegralUIScale());
setStyle(
"-fx-background-color: red, yellow;" +
"-fx-background-insets: 0, 0 1 0 0;");
BackgroundFill red = region.getBackground().getFills().get(0);
BackgroundFill yellow = region.getBackground().getFills().get(1);
checkRegionLeftBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopLeftCorner(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopRightCorner(red, Color.RED, SCENE_FILL, 0, .2);
checkRegionRightBoundary(red, Color.RED, SCENE_FILL, 0, .2);
checkRegionBottomRightCorner(red, Color.RED, SCENE_FILL, 0, .2);
checkRegionBottomBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomLeftCorner(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionLeftBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopLeftCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopRightCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionRightBoundary(yellow, Color.YELLOW, Color.RED, 0, .2);
checkRegionBottomRightCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomLeftCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
}
@Test(timeout = 20000)
public void testOnePixelBottomInset() {
assumeTrue(checkIntegralUIScale());
setStyle(
"-fx-background-color: red, yellow;" +
"-fx-background-insets: 0, 0 0 1 0;");
BackgroundFill red = region.getBackground().getFills().get(0);
BackgroundFill yellow = region.getBackground().getFills().get(1);
checkRegionLeftBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopLeftCorner(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopRightCorner(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionRightBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomRightCorner(red, Color.RED, SCENE_FILL, 0, .2);
checkRegionBottomBoundary(red, Color.RED, SCENE_FILL, 0, .2);
checkRegionBottomLeftCorner(red, Color.RED, SCENE_FILL, 0, .2);
checkRegionLeftBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopLeftCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopRightCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionRightBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomRightCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomBoundary(yellow, Color.YELLOW, Color.RED, 0, .2);
checkRegionBottomLeftCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
}
@Test(timeout = 20000)
public void testOnePixelLeftInset() {
assumeTrue(checkIntegralUIScale());
setStyle(
"-fx-background-color: red, yellow;" +
"-fx-background-insets: 0, 0 0 0 1;");
BackgroundFill red = region.getBackground().getFills().get(0);
BackgroundFill yellow = region.getBackground().getFills().get(1);
checkRegionLeftBoundary(red, Color.RED, SCENE_FILL, 0, .2);
checkRegionTopLeftCorner(red, Color.RED, SCENE_FILL, 0, .2);
checkRegionTopBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopRightCorner(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionRightBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomRightCorner(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomLeftCorner(red, Color.RED, SCENE_FILL, 0, .2);
checkRegionLeftBoundary(yellow, Color.YELLOW, Color.RED, 0, .2);
checkRegionTopLeftCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopRightCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionRightBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomRightCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomLeftCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
}
@Ignore("RT-33446")
@Test(timeout = 20000)
public void testHalfPixelTopInset() {
setStyle(
"-fx-background-color: red, yellow;" +
"-fx-background-insets: 0, .5 0 0 0;");
BackgroundFill red = region.getBackground().getFills().get(0);
BackgroundFill yellow = region.getBackground().getFills().get(1);
Color blended = Color.rgb(254, 127, 27);
checkRegionLeftBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopLeftCorner(red, blended, SCENE_FILL, 0, .2);
checkRegionTopBoundary(red, blended, SCENE_FILL, 0, .2);
checkRegionTopRightCorner(red, blended, SCENE_FILL, 0, .2);
checkRegionRightBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomRightCorner(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomLeftCorner(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionLeftBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopLeftCorner(yellow, blended, SCENE_FILL, 0, .2);
checkRegionTopBoundary(yellow, blended, SCENE_FILL, 0, .2);
checkRegionTopRightCorner(yellow, blended, SCENE_FILL, 0, .2);
checkRegionRightBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomRightCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomLeftCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
}
@Ignore("RT-33446")
@Test(timeout = 20000)
public void testHalfPixelRightInset() {
setStyle(
"-fx-background-color: red, yellow;" +
"-fx-background-insets: 0, 0 .5 0 0;");
BackgroundFill red = region.getBackground().getFills().get(0);
BackgroundFill yellow = region.getBackground().getFills().get(1);
Color blended = Color.rgb(254, 127, 27);
checkRegionLeftBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopLeftCorner(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopRightCorner(red, blended, SCENE_FILL, 0, .2);
checkRegionRightBoundary(red, blended, SCENE_FILL, 0, .2);
checkRegionBottomRightCorner(red, blended, SCENE_FILL, 0, .2);
checkRegionBottomBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomLeftCorner(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionLeftBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopLeftCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopRightCorner(yellow, blended, SCENE_FILL, 0, .2);
checkRegionRightBoundary(yellow, blended, SCENE_FILL, 0, .2);
checkRegionBottomRightCorner(yellow, blended, SCENE_FILL, 0, .2);
checkRegionBottomBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomLeftCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
}
@Ignore("RT-33446")
@Test(timeout = 20000)
public void testHalfPixelBottomInset() {
setStyle(
"-fx-background-color: red, yellow;" +
"-fx-background-insets: 0, 0 0 .5 0;");
BackgroundFill red = region.getBackground().getFills().get(0);
BackgroundFill yellow = region.getBackground().getFills().get(1);
Color blended = Color.rgb(254, 127, 27);
checkRegionLeftBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopLeftCorner(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopRightCorner(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionRightBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomRightCorner(red, blended, SCENE_FILL, 0, .2);
checkRegionBottomBoundary(red, blended, SCENE_FILL, 0, .2);
checkRegionBottomLeftCorner(red, blended, SCENE_FILL, 0, .2);
checkRegionLeftBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopLeftCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopRightCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionRightBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomRightCorner(yellow, blended, SCENE_FILL, 0, .2);
checkRegionBottomBoundary(yellow, blended, SCENE_FILL, 0, .2);
checkRegionBottomLeftCorner(yellow, blended, SCENE_FILL, 0, .2);
}
@Ignore("RT-33446")
@Test(timeout = 20000)
public void testHalfPixelLeftInset() {
setStyle(
"-fx-background-color: red, yellow;" +
"-fx-background-insets: 0, 0 0 0 .5;");
BackgroundFill red = region.getBackground().getFills().get(0);
BackgroundFill yellow = region.getBackground().getFills().get(1);
Color blended = Color.rgb(254, 127, 27);
checkRegionLeftBoundary(red, blended, SCENE_FILL, 0, .2);
checkRegionTopLeftCorner(red, blended, SCENE_FILL, 0, .2);
checkRegionTopBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopRightCorner(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionRightBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomRightCorner(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomLeftCorner(red, blended, SCENE_FILL, 0, .2);
checkRegionLeftBoundary(yellow, blended, SCENE_FILL, 0, .2);
checkRegionTopLeftCorner(yellow, blended, SCENE_FILL, 0, .2);
checkRegionTopBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionTopRightCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionRightBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomRightCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomLeftCorner(yellow, blended, SCENE_FILL, 0, .2);
}
@Ignore("RT-33446")
@Test(timeout = 20000)
public void testHalfPixelTopLeftInset() {
setStyle(
"-fx-background-color: red, yellow;" +
"-fx-background-insets: 0, .5 0 0 .5;");
BackgroundFill red = region.getBackground().getFills().get(0);
BackgroundFill yellow = region.getBackground().getFills().get(1);
Color blended = Color.rgb(254, 127, 27);
checkRegionLeftBoundary(red, blended, SCENE_FILL, 0, .2);
checkRegionTopBoundary(red, blended, SCENE_FILL, 0, .2);
checkRegionTopRightCorner(red, blended, SCENE_FILL, 0, .2);
checkRegionRightBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomRightCorner(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomBoundary(red, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomLeftCorner(red, blended, SCENE_FILL, 0, .2);
checkRegionLeftBoundary(yellow, blended, SCENE_FILL, 0, .2);
checkRegionTopBoundary(yellow, blended, SCENE_FILL, 0, .2);
checkRegionTopRightCorner(yellow, blended, SCENE_FILL, 0, .2);
checkRegionRightBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomRightCorner(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomBoundary(yellow, Color.YELLOW, SCENE_FILL, 0, .2);
checkRegionBottomLeftCorner(yellow, blended, SCENE_FILL, 0, .2);
}
@Test(timeout = 20000)
public void testNoInsets() {
setStyle(
"-fx-background-color: red, yellow;" +
"-fx-background-insets: 0, 0;");
BackgroundFill red = region.getBackground().getFills().get(0);
BackgroundFill yellow = region.getBackground().getFills().get(1);
checkRegionCornersAndBoundariesOfBackgroundFill(red, Color.YELLOW, SCENE_FILL);
checkRegionCornersAndBoundariesOfBackgroundFill(yellow, SCENE_FILL);
}
@Test(timeout = 20000)
public void testYellowOnRed() {
setStyle(
"-fx-background-color: red, yellow;" +
"-fx-background-insets: 0, 40;");
checkRegionCornersAndBoundariesForFills();
}
}
