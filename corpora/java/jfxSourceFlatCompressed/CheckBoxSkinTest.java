package test.javafx.scene.control.skin;
import static org.junit.Assert.assertEquals;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.skin.CheckBoxSkin;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;
public class CheckBoxSkinTest {
private CheckBox checkbox;
private CheckBoxSkinMock skin;
private static Toolkit tk;
private Scene scene;
private Stage stage;
@BeforeClass public static void initToolKit() {
tk = Toolkit.getToolkit();
}
@Before public void setup() {
checkbox = new CheckBox("Test");
skin = new CheckBoxSkinMock(checkbox);
checkbox.setPadding(new Insets(10, 10, 10, 10));
checkbox.setSkin(skin);
scene = new Scene(new Group(checkbox));
stage = new Stage();
stage.setScene(scene);
stage.show();
tk.firePulse();
}
@Test public void maxWidthTracksPreferred() {
checkbox.setPrefWidth(500);
assertEquals(500, checkbox.maxWidth(-1), 0);
}
@Test public void maxHeightTracksPreferred() {
checkbox.setPrefHeight(500);
assertEquals(500, checkbox.maxHeight(-1), 0);
}
@Test public void testPadding() {
checkbox.setPadding(new Insets(10, 20, 30, 40));
tk.firePulse();
double expectedArea = checkbox.getHeight() * checkbox.getWidth();
double actualArea = checkbox.getSkin().getNode().computeAreaInScreen();
assertEquals(expectedArea, actualArea, 0.001);
}
public static final class CheckBoxSkinMock extends CheckBoxSkin {
boolean propertyChanged = false;
int propertyChangeCount = 0;
public CheckBoxSkinMock(CheckBox checkbox) {
super(checkbox);
}
public void addWatchedProperty(ObservableValue<?> p) {
p.addListener(o -> {
propertyChanged = true;
propertyChangeCount++;
});
}
}
}
