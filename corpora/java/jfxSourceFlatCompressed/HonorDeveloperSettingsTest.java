package test.javafx.css;
import static org.junit.Assert.*;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.PlatformUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import com.sun.javafx.util.Logging;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.sun.javafx.logging.PlatformLogger;
import static org.junit.Assume.assumeTrue;
public class HonorDeveloperSettingsTest {
private Scene scene;
private Rectangle rect;
private Text text;
private void resetStyleManager() {
StyleManager sm = StyleManager.getInstance();
sm.forget(scene);
sm.userAgentStylesheetContainers.clear();
sm.platformUserAgentStylesheetContainers.clear();
sm.stylesheetContainerMap.clear();
sm.cacheContainerMap.clear();
sm.hasDefaultUserAgentStylesheet = false;
}
@BeforeClass
public static void setUpClass() {
if (PlatformUtil.isUnix()) {
assumeTrue(Boolean.getBoolean("unstable.test"));
}
}
@After
public void cleanup() {
resetStyleManager();
}
@Before
public void setUp() {
rect = new Rectangle();
rect.setId("rectangle");
text = new Text();
text.setId("text");
Group group = new Group();
group.getChildren().addAll(rect, text);
scene = new Scene(group);
System.setProperty("binary.css", "false");
String url = getClass().getResource("HonorDeveloperSettingsTest_UA.css").toExternalForm();
StyleManager.getInstance().setDefaultUserAgentStylesheet(url);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
}
@Test
public void testOpacityIsSetByCSSByDefault() {
rect.applyCss();
assertEquals(.76, rect.getOpacity(), 0.01);
}
@Test
public void testOpacityWithInitializedValueSameAsDefaultValueIsIgnoredByCSS() {
rect.setOpacity(1.0);
rect.applyCss();
assertEquals(1.0, rect.getOpacity(), 0.01);
}
@Test
public void testOpacityWithInitializedValueIsIgnoredByCSS() {
rect.setOpacity(0.535);
rect.applyCss();
assertEquals(0.535, rect.getOpacity(), 0.01);
}
@Test
public void testOpacityWithManuallyChangedValueIsIgnoredByCSS() {
rect.applyCss();
assertEquals(.76, rect.getOpacity(), 0.01);
rect.setOpacity(.873);
rect.applyCss();
assertEquals(.873, rect.getOpacity(), 0.01);
}
@Test
public void testOpacityWithManuallyChangedValueAndInlineStyleIsSetToInlineStyle() {
rect.applyCss();
assertEquals(.76, rect.getOpacity(), 0.01);
rect.setStyle("-fx-opacity: 42%;");
rect.setOpacity(.873);
rect.applyCss();
assertEquals(.42, rect.getOpacity(), 0.01);
}
@Test
public void testCursorIsSetByCSSByDefault() {
rect.applyCss();
assertEquals(Cursor.HAND, rect.getCursor());
}
@Test
public void testCursorWithInitializedValueSameAsDefaultValueIsIgnoredByCSS() {
rect.setCursor(null);
rect.applyCss();
assertEquals(null, rect.getCursor());
}
@Test
public void testCursorWithInitializedValueIsIgnoredByCSS() {
rect.setCursor(Cursor.WAIT);
rect.applyCss();
assertEquals(Cursor.WAIT, rect.getCursor());
}
@Test
public void testCursorWithManuallyChangedValueIsIgnoredByCSS() {
rect.applyCss();
assertEquals(Cursor.HAND, rect.getCursor());
rect.setCursor(Cursor.WAIT);
rect.applyCss();
assertEquals(Cursor.WAIT, rect.getCursor());
}
@Test
public void testFontIsSetByCSSByDefault() {
text.applyCss();
assertNotSame(Font.getDefault(), text.getFont());
}
@Test
public void testFontWithInitializedValueSameAsDefaultValueIsIgnoredByCSS() {
text.setFont(Font.getDefault());
text.applyCss();
assertSame(Font.getDefault(), text.getFont());
}
@Test
public void testFontWithInitializedValueIsIgnoredByCSS() {
Font f = Font.font(Font.getDefault().getFamily(), 54.0);
text.setFont(f);
text.applyCss();
assertSame(f, text.getFont());
}
@Test
public void testFontWithManuallyChangedValueIsIgnoredByCSS() {
Font f = Font.font(Font.getDefault().getFamily(), 54.0);
text.applyCss();
assertNotSame(f, text.getFont());
text.setFont(f);
text.applyCss();
assertSame(f, text.getFont());
}
@Test
public void testUseInheritedFontSizeFromStylesheetForEmSize() {
String url = getClass().getResource("HonorDeveloperSettingsTest_AUTHOR.css").toExternalForm();
scene.getStylesheets().add(url);
scene.getRoot().applyCss();
assertEquals(20, rect.getStrokeWidth(), 0.00001);
}
@Test
public void testInhertWithNoStyleDoesNotOverrideUserSetValue() {
Font font = Font.font("Amble", 14);
text.setFont(font);
String url = getClass().getResource("HonorDeveloperSettingsTest_AUTHOR.css").toExternalForm();
scene.getStylesheets().add(url);
scene.getRoot().applyCss();
assertEquals(14, text.getStrokeWidth(), 0.00001);
}
@Test
public void testInlineStyleInheritedFromParentApplies() {
text.setId(null);
text.setStyle("-fx-stroke-width: 1em; -fx-stroke: red;");
scene.getRoot().setStyle("-fx-font: 18 Amble;");
scene.getRoot().applyCss();
assertEquals(18, text.getStrokeWidth(), 0.00001);
}
@Test
public void testInlineStyleNotInheritedFromParentWhenUserSetsFont() {
text.setStyle("-fx-stroke-width: 1em;");
Font font = Font.font("Amble", 14);
text.setFont(font);
scene.getRoot().setStyle("-fx-font: 18 Amble;");
scene.getRoot().applyCss();
assertEquals(14, text.getStrokeWidth(), 0.00001);
}
@Test public void test_RT_20686_UAStyleDoesNotOverrideSetFontSmoothingType() {
text.setId("rt-20686-ua");
text.setFontSmoothingType(FontSmoothingType.LCD);
scene.getRoot().applyCss();
assertEquals(FontSmoothingType.LCD, text.getFontSmoothingType());
}
@Test public void test_RT_20686_AuthorStyleOverridesSetFontSmoothingType() {
String url = getClass().getResource("HonorDeveloperSettingsTest_AUTHOR.css").toExternalForm();
scene.getStylesheets().add(url);
text.setId("rt-20686-author");
text.setFontSmoothingType(FontSmoothingType.GRAY);
scene.getRoot().applyCss();
assertEquals(FontSmoothingType.LCD, text.getFontSmoothingType());
}
@Test public void test_InlineFontStyleApplies() {
text.setStyle("-fx-font-size: 24;");
scene.getRoot().applyCss();
double size = text.getFont().getSize();
assertEquals(24, size, .0001);
}
@Test public void test_FontInheritsFromDotRootStyle() {
String url = getClass().getResource("HonorDeveloperSettingsTest_AUTHOR.css").toExternalForm();
scene.getStylesheets().add(url);
text.setId(null);
scene.getRoot().applyCss();
double size = text.getFont().getSize();
assertEquals(20, size, .0001);
}
@Test public void test_InlineFontStyleOverridesStylesheetStyles() {
String url = getClass().getResource("HonorDeveloperSettingsTest_AUTHOR.css").toExternalForm();
scene.getStylesheets().add(url);
text.setId(null);
text.setStyle("-fx-font-size: 24;");
scene.getRoot().applyCss();
double size = text.getFont().getSize();
assertEquals(24, size, .0001);
}
@Test public void test_InlineFontStyleFromParentOverridesStylesheetStyles() {
String url = getClass().getResource("HonorDeveloperSettingsTest_AUTHOR.css").toExternalForm();
scene.getStylesheets().add(url);
text.setId(null);
Group g = (Group)scene.getRoot();
g.setStyle("-fx-font-size: 32;");
g.applyCss();
double size = text.getFont().getSize();
assertEquals(32, size, .0001);
}
}
