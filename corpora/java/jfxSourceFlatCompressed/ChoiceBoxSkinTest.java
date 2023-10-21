package test.javafx.scene.control.skin;
import static org.junit.Assert.assertEquals;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.skin.ChoiceBoxSkin;
import org.junit.Before;
import org.junit.Test;
public class ChoiceBoxSkinTest {
private ChoiceBox choicebox;
private ChoiceBoxSkinMock skin;
@Before public void setup() {
choicebox = new ChoiceBox();
skin = new ChoiceBoxSkinMock(choicebox);
choicebox.setPadding(new Insets(10, 10, 10, 10));
choicebox.setSkin(skin);
}
@Test public void maxWidthTracksPreferred() {
choicebox.setPrefWidth(500);
assertEquals(500, choicebox.maxWidth(-1), 0);
}
@Test public void maxHeightTracksPreferred() {
choicebox.setPrefHeight(500);
assertEquals(500, choicebox.maxHeight(-1), 0);
}
public static final class ChoiceBoxSkinMock extends ChoiceBoxSkin {
boolean propertyChanged = false;
int propertyChangeCount = 0;
public ChoiceBoxSkinMock(ChoiceBox choicebox) {
super(choicebox);
}
public void addWatchedProperty(ObservableValue<?> p) {
p.addListener(o -> {
propertyChanged = true;
propertyChangeCount++;
});
}
}
}
