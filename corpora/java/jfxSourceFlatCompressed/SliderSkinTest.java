package test.javafx.scene.control.skin;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import static org.junit.Assert.assertEquals;
import javafx.scene.control.Slider;
import javafx.scene.control.skin.SliderSkin;
import org.junit.Before;
import org.junit.Test;
public class SliderSkinTest {
private Slider slider;
private SliderSkinMock skin;
@Before public void setup() {
slider = new Slider();
skin = new SliderSkinMock(slider);
slider.setSkin(skin);
}
@Test public void maxWidthTracksPreferred() {
slider.setOrientation(Orientation.VERTICAL);
slider.setPrefWidth(500);
assertEquals(500, slider.maxWidth(-1), 0);
}
@Test public void maxHeightTracksPreferred() {
slider.setOrientation(Orientation.HORIZONTAL);
slider.setPrefHeight(500);
assertEquals(500, slider.maxHeight(-1), 0);
}
public static final class SliderSkinMock extends SliderSkin {
boolean propertyChanged = false;
int propertyChangeCount = 0;
public SliderSkinMock(Slider slider) {
super(slider);
}
public void addWatchedProperty(ObservableValue<?> p) {
p.addListener(o -> {
propertyChanged = true;
propertyChangeCount++;
});
}
}
}
