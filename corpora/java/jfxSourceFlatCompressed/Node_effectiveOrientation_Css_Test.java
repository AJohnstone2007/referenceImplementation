package test.javafx.scene;
import com.sun.javafx.css.StyleManager;
import javafx.css.Stylesheet;
import javafx.css.CssParser;
import com.sun.javafx.tk.Toolkit;
import static javafx.geometry.NodeOrientation.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
@Ignore("JDK-8234152")
public class Node_effectiveOrientation_Css_Test {
private Group root;
private Scene scene;
private Stage stage;
@Before
public void setUp() {
root = new Group();
scene = new Scene(root);
stage = new Stage();
stage.setScene(scene);
stage.show();
stage.requestFocus();
}
@After
public void tearDown() {
resetStyleManager();
stage.hide();
}
private static void resetStyleManager() {
StyleManager sm = StyleManager.getInstance();
sm.userAgentStylesheetContainers.clear();
sm.platformUserAgentStylesheetContainers.clear();
sm.stylesheetContainerMap.clear();
sm.cacheContainerMap.clear();
sm.hasDefaultUserAgentStylesheet = false;
}
public Node_effectiveOrientation_Css_Test() {}
@Test
public void test_SimpleSelector_dir_pseudoClass_with_scene_effective_orientation_ltr() {
Stylesheet stylesheet = new CssParser().parse(
".rect:dir(rtl) { -fx-fill: #ff0000; }" +
".rect:dir(ltr) { -fx-fill: #00ff00; }" +
".rect { -fx-fill: #0000ff; }"
);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
Rectangle rect = new Rectangle();
rect.getStyleClass().add("rect");
root.getChildren().add(rect);
root.applyCss();
assertEquals(scene.getEffectiveNodeOrientation(), LEFT_TO_RIGHT);
assertEquals(Color.web("#00ff00"), rect.getFill());
}
@Test
public void test_SimpleSelector_dir_pseudoClass_with_scene_effective_orientation_rtl() {
Stylesheet stylesheet = new CssParser().parse(
".rect:dir(rtl) { -fx-fill: #ff0000; }" +
".rect:dir(ltr) { -fx-fill: #00ff00; }" +
".rect { -fx-fill: #0000ff; }"
);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
Rectangle rect = new Rectangle();
rect.getStyleClass().add("rect");
root.getChildren().add(rect);
scene.setNodeOrientation(RIGHT_TO_LEFT);
root.applyCss();
assertEquals(scene.getEffectiveNodeOrientation(), RIGHT_TO_LEFT);
assertEquals(Color.web("#ff0000"), rect.getFill());
}
@Test
public void test_CompounSelector_dir_pseudoClass_on_parent_with_scene_effective_orientation_ltr() {
Stylesheet stylesheet = new CssParser().parse(
".root:dir(rtl) .rect { -fx-fill: #ff0000; }" +
".root:dir(ltr) .rect { -fx-fill: #00ff00; }" +
".root .rect { -fx-fill: #0000ff; }"
);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
Rectangle rect = new Rectangle();
rect.getStyleClass().add("rect");
root.getChildren().add(rect);
root.applyCss();
assertEquals(scene.getEffectiveNodeOrientation(), LEFT_TO_RIGHT);
assertEquals(Color.web("#00ff00"), rect.getFill());
}
@Test
public void test_CompoundSelector_dir_pseudoClass_on_parent_with_scene_effective_orientation_rtl() {
Stylesheet stylesheet = new CssParser().parse(
".root:dir(rtl) .rect { -fx-fill: #ff0000; }" +
".root:dir(ltr) .rect { -fx-fill: #00ff00; }" +
".root .rect { -fx-fill: #0000ff; }"
);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
Rectangle rect = new Rectangle();
rect.getStyleClass().add("rect");
root.getChildren().add(rect);
scene.setNodeOrientation(RIGHT_TO_LEFT);
root.applyCss();
assertEquals(scene.getEffectiveNodeOrientation(), RIGHT_TO_LEFT);
assertEquals(Color.web("#ff0000"), rect.getFill());
}
@Test
public void test_CompounSelector_dir_pseudoClass_on_child_with_scene_effective_orientation_ltr() {
Stylesheet stylesheet = new CssParser().parse(
".root .rect:dir(rtl) { -fx-fill: #ff0000; }" +
".root .rect:dir(ltr) { -fx-fill: #00ff00; }" +
".root .rect { -fx-fill: #0000ff; }"
);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
Rectangle rect = new Rectangle();
rect.getStyleClass().add("rect");
root.getChildren().add(rect);
root.applyCss();
assertEquals(scene.getEffectiveNodeOrientation(), LEFT_TO_RIGHT);
assertEquals(Color.web("#00ff00"), rect.getFill());
}
@Test
public void test_CompoundSelector_dir_pseudoClass_on_child_with_scene_effective_orientation_rtl() {
Stylesheet stylesheet = new CssParser().parse(
".root .rect:dir(rtl) { -fx-fill: #ff0000; }" +
".root .rect:dir(ltr) { -fx-fill: #00ff00; }" +
".root .rect { -fx-fill: #0000ff; }"
);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
Rectangle rect = new Rectangle();
rect.getStyleClass().add("rect");
root.getChildren().add(rect);
scene.setNodeOrientation(RIGHT_TO_LEFT);
root.applyCss();
assertEquals(scene.getEffectiveNodeOrientation(), RIGHT_TO_LEFT);
assertEquals(Color.web("#ff0000"), rect.getFill());
}
@Test
public void test_dir_pseudoClass_functions_on_scene_effective_orientation_not_node() {
Stylesheet stylesheet = new CssParser().parse(
".rect:dir(rtl) { -fx-fill: #ff0000; }" +
".rect:dir(ltr) { -fx-fill: #00ff00; }" +
".rect { -fx-fill: #0000ff; }"
);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
Rectangle rect = new Rectangle();
rect.getStyleClass().add("rect");
root.getChildren().add(rect);
rect.setNodeOrientation(RIGHT_TO_LEFT);
root.applyCss();
assertEquals(scene.getEffectiveNodeOrientation(), LEFT_TO_RIGHT);
assertEquals(rect.getEffectiveNodeOrientation(), RIGHT_TO_LEFT);
assertEquals(Color.web("#00ff00"), rect.getFill());
scene.setNodeOrientation(RIGHT_TO_LEFT);
rect.setNodeOrientation(LEFT_TO_RIGHT);
root.applyCss();
assertEquals(scene.getEffectiveNodeOrientation(), RIGHT_TO_LEFT);
assertEquals(rect.getEffectiveNodeOrientation(), LEFT_TO_RIGHT);
assertEquals(Color.web("#ff0000"), rect.getFill());
}
}
