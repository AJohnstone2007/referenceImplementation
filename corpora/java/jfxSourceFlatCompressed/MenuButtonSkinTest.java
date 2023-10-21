package test.javafx.scene.control.skin;
import static org.junit.Assert.assertEquals;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.skin.MenuButtonSkin;
import org.junit.Before;
import org.junit.Test;
public class MenuButtonSkinTest {
private MenuButton menubutton;
private MenuButtonSkinMock skin;
@Before public void setup() {
menubutton = new MenuButton();
menubutton.getItems().addAll(new MenuItem("Vanilla"), new MenuItem("Chocolate"));
skin = new MenuButtonSkinMock(menubutton);
menubutton.setPadding(new Insets(10, 10, 10, 10));
menubutton.setSkin(skin);
}
@Test public void maxWidthTracksPreferred() {
menubutton.setPrefWidth(500);
assertEquals(500, menubutton.maxWidth(-1), 0);
}
@Test public void maxHeightTracksPreferred() {
menubutton.setPrefHeight(100);
assertEquals(100, menubutton.maxHeight(-1), 0);
}
public static final class MenuButtonSkinMock extends MenuButtonSkin {
boolean propertyChanged = false;
int propertyChangeCount = 0;
public MenuButtonSkinMock(MenuButton menubutton) {
super(menubutton);
}
public void addWatchedProperty(ObservableValue<?> p) {
p.addListener(o -> {
propertyChanged = true;
propertyChangeCount++;
});
}
}
}
