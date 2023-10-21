package test.javafx.scene.control.skin;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.Separator;
import javafx.scene.control.SkinBaseShim;
import javafx.scene.control.skin.SeparatorSkin;
import javafx.scene.layout.Region;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
public class SeparatorSkinTest {
private Separator separator;
private SeparatorSkinMock skin;
private Region line;
@Before public void setup() {
separator = new Separator();
skin = new SeparatorSkinMock(separator);
separator.setPadding(new Insets(10, 8, 6, 4));
separator.setSkin(skin);
line = (Region) SkinBaseShim.getChildren(skin).get(0);
line.setPadding((new Insets(4, 3, 2, 1)));
separator.layout();
separator.layout();
}
@Test public void orientationChangesOnSeparatorShouldInvoke_handleControlPropertyChanged() {
skin.addWatchedProperty(separator.orientationProperty());
assertFalse(skin.propertyChanged);
separator.setOrientation(Orientation.VERTICAL);
assertTrue(skin.propertyChanged);
assertEquals(1, skin.propertyChangeCount);
}
@Test public void halignmentChangesOnSeparatorShouldInvoke_handleControlPropertyChanged() {
skin.addWatchedProperty(separator.halignmentProperty());
assertFalse(skin.propertyChanged);
separator.setHalignment(HPos.RIGHT);
assertTrue(skin.propertyChanged);
assertEquals(1, skin.propertyChangeCount);
}
@Test public void valignmentChangesOnSeparatorShouldInvoke_handleControlPropertyChanged() {
skin.addWatchedProperty(separator.valignmentProperty());
assertFalse(skin.propertyChanged);
separator.setValignment(VPos.BASELINE);
assertTrue(skin.propertyChanged);
assertEquals(1, skin.propertyChangeCount);
}
@Test public void orientationChangeShouldInvalidateLayout() {
assertFalse(separator.isNeedsLayout());
separator.setOrientation(Orientation.VERTICAL);
assertTrue(separator.isNeedsLayout());
}
@Test public void halignmentChangeShouldInvalidateLayout() {
assertFalse(separator.isNeedsLayout());
separator.setHalignment(HPos.RIGHT);
assertTrue(separator.isNeedsLayout());
}
@Test public void valignmentChangeShouldInvalidateLayout() {
assertFalse(separator.isNeedsLayout());
separator.setValignment(VPos.BASELINE);
assertTrue(separator.isNeedsLayout());
}
@Test public void minWidthWhenVerticalShouldBePaddingOfLinePlusPaddingOfSeparator() {
separator.setOrientation(Orientation.VERTICAL);
final Insets linePadding = line.getInsets();
final Insets skinPadding = separator.getInsets();
assertEquals(linePadding.getLeft() + linePadding.getRight() +
skinPadding.getLeft() + skinPadding.getRight(),
separator.minWidth(-1), 0);
}
@Ignore
@Test public void minWidthWhenHorizontalShouldBePositiveNonZeroPlusPadding() {
separator.setOrientation(Orientation.HORIZONTAL);
final Insets linePadding = line.getInsets();
final Insets skinPadding = separator.getInsets();
assertTrue(separator.minWidth(-1) > 0);
assertEquals(linePadding.getLeft() + linePadding.getRight() +
skinPadding.getLeft() + skinPadding.getRight(),
separator.minWidth(-1), 0);
}
@Ignore
@Test public void minHeightWhenVerticalShouldBePositiveNonZeroPlusPadding() {
separator.setOrientation(Orientation.VERTICAL);
final Insets linePadding = line.getInsets();
final Insets skinPadding = separator.getInsets();
assertTrue(separator.minHeight(-1) > 0);
assertEquals(linePadding.getTop() + linePadding.getBottom() +
skinPadding.getTop() + skinPadding.getBottom(),
separator.minHeight(-1), 0);
}
@Test public void minHeightWhenHorizontalShouldBePaddingOfLinePlusPaddingOfSeparator() {
separator.setOrientation(Orientation.HORIZONTAL);
final Insets linePadding = line.getInsets();
final Insets skinPadding = separator.getInsets();
assertEquals(linePadding.getTop() + linePadding.getBottom() +
skinPadding.getTop() + skinPadding.getBottom(),
separator.minHeight(-1), 0);
}
@Test public void maxWidthWhenVerticalShouldBePaddingOfLinePlusPaddingOfSeparator() {
separator.setOrientation(Orientation.VERTICAL);
final Insets linePadding = line.getInsets();
final Insets skinPadding = separator.getInsets();
assertEquals(linePadding.getLeft() + linePadding.getRight() +
skinPadding.getLeft() + skinPadding.getRight(),
separator.maxWidth(-1), 0);
}
@Test public void maxWidthWhenHorizontalShouldBeMAX_VALUE() {
separator.setOrientation(Orientation.HORIZONTAL);
assertEquals(Double.MAX_VALUE, separator.maxWidth(-1), 0);
}
@Test public void maxHeightWhenVerticalShouldBeMAX_VALUE() {
separator.setOrientation(Orientation.VERTICAL);
assertEquals(Double.MAX_VALUE, separator.maxHeight(-1), 0);
}
@Test public void maxHeightWhenHorizontalShouldBePaddingOfLinePlusPaddingOfSeparator() {
separator.setOrientation(Orientation.HORIZONTAL);
final Insets linePadding = line.getInsets();
final Insets skinPadding = separator.getInsets();
assertEquals(linePadding.getTop() + linePadding.getBottom() +
skinPadding.getTop() + skinPadding.getBottom(),
separator.maxHeight(-1), 0);
}
@Test public void onVerticalMaxWidthTracksPreferred() {
separator.setOrientation(Orientation.VERTICAL);
separator.setPrefWidth(100);
assertEquals(100, separator.maxWidth(-1), 0);
}
@Test public void onHorizontalMaxHeightTracksPreferred() {
separator.setOrientation(Orientation.HORIZONTAL);
separator.setPrefHeight(100);
assertEquals(100, separator.maxHeight(-1), 0);
}
public static final class SeparatorSkinMock extends SeparatorSkin {
boolean propertyChanged = false;
int propertyChangeCount = 0;
public SeparatorSkinMock(Separator sep) {
super(sep);
}
public void addWatchedProperty(ObservableValue<?> p) {
p.addListener(o -> {
propertyChanged = true;
propertyChangeCount++;
});
}
}
}
