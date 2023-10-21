package test.javafx.scene.control.skin;
import static org.junit.Assert.assertEquals;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.skin.ToolBarSkin;
import org.junit.Before;
import org.junit.Test;
public class ToolBarSkinTest {
private ToolBar toolbar;
private ToolBarSkinMock skin;
@Before public void setup() {
toolbar = new ToolBar();
toolbar.getItems().addAll(new Button("Cut"), new Button("Copy"));
skin = new ToolBarSkinMock(toolbar);
toolbar.setPadding(new Insets(10, 10, 10, 10));
toolbar.setSkin(skin);
}
@Test public void horizontalMaxHeightTracksPreferred() {
toolbar.setOrientation(Orientation.HORIZONTAL);
toolbar.setPrefHeight(100);
assertEquals(100, toolbar.maxHeight(-1), 0);
}
@Test public void verticalMaxWidthTracksPreferred() {
toolbar.setOrientation(Orientation.VERTICAL);
toolbar.setPrefWidth(100);
assertEquals(100, toolbar.maxWidth(-1), 0);
}
public static final class ToolBarSkinMock extends ToolBarSkin {
boolean propertyChanged = false;
int propertyChangeCount = 0;
public ToolBarSkinMock(ToolBar toolbar) {
super(toolbar);
}
public void addWatchedProperty(ObservableValue<?> p) {
p.addListener(o -> {
propertyChanged = true;
propertyChangeCount++;
});
}
}
}
