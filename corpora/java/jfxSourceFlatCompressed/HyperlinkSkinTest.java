package test.javafx.scene.control.skin;
import static org.junit.Assert.assertEquals;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.skin.HyperlinkSkin;
import org.junit.Before;
import org.junit.Test;
public class HyperlinkSkinTest {
private Hyperlink hyperlink;
private HyperlinkSkinMock skin;
@Before public void setup() {
hyperlink = new Hyperlink("Test");
skin = new HyperlinkSkinMock(hyperlink);
hyperlink.setPadding(new Insets(10, 10, 10, 10));
hyperlink.setSkin(skin);
}
@Test public void maxWidthTracksPreferred() {
hyperlink.setPrefWidth(500);
assertEquals(500, hyperlink.maxWidth(-1), 0);
}
@Test public void maxHeightTracksPreferred() {
hyperlink.setPrefHeight(500);
assertEquals(500, hyperlink.maxHeight(-1), 0);
}
public static final class HyperlinkSkinMock extends HyperlinkSkin {
boolean propertyChanged = false;
int propertyChangeCount = 0;
public HyperlinkSkinMock(Hyperlink hyperlink) {
super(hyperlink);
}
public void addWatchedProperty(ObservableValue<?> p) {
p.addListener(o -> {
propertyChanged = true;
propertyChangeCount++;
});
}
}
}
