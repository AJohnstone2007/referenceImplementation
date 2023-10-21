package test.javafx.scene.control.skin;
import static org.junit.Assert.assertEquals;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.RadioButton;
import javafx.scene.control.skin.RadioButtonSkin;
import org.junit.Before;
import org.junit.Test;
public class RadioButtonSkinTest {
private RadioButton radiobutton;
private RadioButtonSkinMock skin;
@Before public void setup() {
radiobutton = new RadioButton("Test");
skin = new RadioButtonSkinMock(radiobutton);
radiobutton.setPadding(new Insets(10, 10, 10, 10));
radiobutton.setSkin(skin);
}
@Test public void maxWidthTracksPreferred() {
radiobutton.setPrefWidth(500);
assertEquals(500, radiobutton.maxWidth(-1), 0);
}
@Test public void maxHeightTracksPreferred() {
radiobutton.setPrefHeight(500);
assertEquals(500, radiobutton.maxHeight(-1), 0);
}
public static final class RadioButtonSkinMock extends RadioButtonSkin {
boolean propertyChanged = false;
int propertyChangeCount = 0;
public RadioButtonSkinMock(RadioButton radiobutton) {
super(radiobutton);
}
public void addWatchedProperty(ObservableValue<?> p) {
p.addListener(o -> {
propertyChanged = true;
propertyChangeCount++;
});
}
}
}
