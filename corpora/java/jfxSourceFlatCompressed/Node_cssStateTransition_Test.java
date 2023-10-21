package test.javafx.css;
import com.sun.javafx.css.StyleManager;
import java.io.IOException;
import javafx.css.CssMetaData;
import javafx.css.CssParser;
import javafx.css.PseudoClass;
import javafx.css.StyleableProperty;
import javafx.css.Stylesheet;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
public class Node_cssStateTransition_Test {
public Node_cssStateTransition_Test() {
}
private static void resetStyleManager() {
StyleManager sm = StyleManager.getInstance();
sm.userAgentStylesheetContainers.clear();
sm.platformUserAgentStylesheetContainers.clear();
sm.stylesheetContainerMap.clear();
sm.cacheContainerMap.clear();
sm.hasDefaultUserAgentStylesheet = false;
}
@Before
public void setUp() {
resetStyleManager();
}
@AfterClass
public static void cleanupOnce() {
resetStyleManager();
}
@Test
public void testPropertiesResetOnStyleclassChange() {
Rectangle rect = new Rectangle(50,50);
Paint defaultFill = rect.getFill();
Paint defaultStroke = rect.getStroke();
Double defaultStrokeWidth = Double.valueOf(rect.getStrokeWidth());
CssMetaData metaData = ((StyleableProperty)rect.fillProperty()).getCssMetaData();
assertEquals(defaultFill, metaData.getInitialValue(rect));
metaData = ((StyleableProperty)rect.strokeProperty()).getCssMetaData();
assertEquals(defaultStroke, metaData.getInitialValue(rect));
metaData = ((StyleableProperty)rect.strokeWidthProperty()).getCssMetaData();
assertEquals(defaultStrokeWidth, metaData.getInitialValue(rect));
Stylesheet stylesheet = null;
try {
stylesheet = new CssParser().parse(
"testPropertiesResetOnStyleclassChange",
".rect { -fx-fill: red; -fx-stroke: yellow; -fx-stroke-width: 3px; }" +
".rect.green { -fx-fill: green; }" +
".green { -fx-stroke: green; }"
);
} catch(IOException ioe) {
fail();
}
rect.getStyleClass().add("rect");
Group root = new Group();
root.getChildren().add(rect);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
Scene scene = new Scene(root);
root.applyCss();
assertEquals(Color.RED, rect.getFill());
assertEquals(Color.YELLOW, rect.getStroke());
assertEquals(3d, rect.getStrokeWidth(), 1e-6);
rect.getStyleClass().add("green");
root.applyCss();
assertEquals(Color.GREEN, rect.getFill());
assertEquals(Color.GREEN, rect.getStroke());
assertEquals(3d, rect.getStrokeWidth(), 1e-6);
rect.getStyleClass().remove("rect");
root.applyCss();
assertEquals(defaultFill, rect.getFill());
assertEquals(Color.GREEN, rect.getStroke());
assertEquals(defaultStrokeWidth.doubleValue(), rect.getStrokeWidth(), 1e-6);
rect.getStyleClass().remove("green");
root.applyCss();
assertEquals(defaultFill, rect.getFill());
assertEquals(defaultStroke, rect.getStroke());
assertEquals(defaultStrokeWidth.doubleValue(), rect.getStrokeWidth(), 1e-6);
}
@Test
public void testPropertiesResetOnPsedudoClassStateChange() {
Rectangle rect = new Rectangle(50,50);
Paint defaultFill = rect.getFill();
Paint defaultStroke = rect.getStroke();
Double defaultStrokeWidth = Double.valueOf(rect.getStrokeWidth());
CssMetaData metaData = ((StyleableProperty)rect.fillProperty()).getCssMetaData();
assertEquals(defaultFill, metaData.getInitialValue(rect));
metaData = ((StyleableProperty)rect.strokeProperty()).getCssMetaData();
assertEquals(defaultStroke, metaData.getInitialValue(rect));
metaData = ((StyleableProperty)rect.strokeWidthProperty()).getCssMetaData();
assertEquals(defaultStrokeWidth, metaData.getInitialValue(rect));
Stylesheet stylesheet = null;
try {
stylesheet = new CssParser().parse(
"testPropertiesResetOnPsedudoClassStateChange",
".rect:hover { -fx-fill: red; -fx-stroke: yellow; -fx-stroke-width: 3px; }" +
".rect:hover:focused { -fx-fill: green; }" +
".rect:focused { -fx-stroke: green; }"
);
} catch(IOException ioe) {
fail();
}
rect.getStyleClass().add("rect");
Group root = new Group();
root.getChildren().add(rect);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
Scene scene = new Scene(root);
root.applyCss();
assertEquals(defaultFill, rect.getFill());
assertEquals(defaultStroke, rect.getStroke());
assertEquals(defaultStrokeWidth, rect.getStrokeWidth(), 1e-6);
rect.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true);
root.applyCss();
assertEquals(Color.RED, rect.getFill());
assertEquals(Color.YELLOW, rect.getStroke());
assertEquals(3d, rect.getStrokeWidth(), 1e-6);
rect.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true);
root.applyCss();
assertEquals(Color.GREEN, rect.getFill());
assertEquals(Color.GREEN, rect.getStroke());
assertEquals(3d, rect.getStrokeWidth(), 1e-6);
rect.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), false);
root.applyCss();
assertEquals(defaultFill, rect.getFill());
assertEquals(Color.GREEN, rect.getStroke());
assertEquals(defaultStrokeWidth.doubleValue(), rect.getStrokeWidth(), 1e-6);
rect.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), false);
root.applyCss();
assertEquals(defaultFill, rect.getFill());
assertEquals(defaultStroke, rect.getStroke());
assertEquals(defaultStrokeWidth.doubleValue(), rect.getStrokeWidth(), 1e-6);
}
}
