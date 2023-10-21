package test.javafx.scene.control.skin;
import static org.junit.Assert.assertEquals;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.Separator;
import javafx.scene.control.SkinBaseShim;
import javafx.scene.control.skin.SeparatorSkin;
import javafx.scene.layout.Region;
import org.junit.Before;
import org.junit.Test;
public class SeparatorSkinLayoutTest {
private Separator separator;
private SeparatorSkin skin;
private Region line;
@Before public void setup() {
separator = new Separator();
skin = new SeparatorSkin(separator);
separator.setPadding(new Insets(10, 8, 6, 4));
separator.setSkin(skin);
line = (Region) SkinBaseShim.getChildren(skin).get(0);
line.setPadding((new Insets(4, 3, 2, 1)));
separator.resize(100, 100);
separator.layout();
}
private void assertLineWidthMatchesSkinWidth() {
Bounds lineBounds = line.getBoundsInParent();
assertEquals(separator.getWidth() - separator.getInsets().getRight(), lineBounds.getMaxX(), .1);
assertEquals(separator.getInsets().getLeft(), lineBounds.getMinX(), .1);
}
private void assertLineHeightMatchesSkinHeight() {
Bounds lineBounds = line.getBoundsInParent();
assertEquals(separator.getInsets().getTop(), lineBounds.getMinY(), .1);
assertEquals(separator.getHeight() - separator.getInsets().getBottom(), lineBounds.getMaxY(), .1);
}
@Test public void separatorWith_TOP_PositionsLineAtTopOfArea() {
separator.setValignment(VPos.TOP);
separator.layout();
Bounds lineBounds = line.getBoundsInParent();
assertEquals(separator.getInsets().getTop(), lineBounds.getMinY(), .1);
assertEquals(line.prefHeight(-1), lineBounds.getHeight(), .1);
assertLineWidthMatchesSkinWidth();
}
@Test public void separatorWith_CENTER_PositionsLineAtCenterOfArea() {
separator.setValignment(VPos.CENTER);
separator.layout();
Bounds lineBounds = line.getBoundsInParent();
final double ch = separator.getHeight() - (separator.getInsets().getTop() + separator.getInsets().getBottom());
final double centerLine = separator.getInsets().getTop() + (ch / 2.0);
assertEquals(centerLine - (lineBounds.getHeight() / 2.0), lineBounds.getMinY(), .1);
assertEquals(line.prefHeight(-1), lineBounds.getHeight(), .1);
assertLineWidthMatchesSkinWidth();
}
@Test public void separatorWith_BOTTOM_PositionsLineAtBottomOfArea() {
separator.setValignment(VPos.BOTTOM);
separator.layout();
Bounds lineBounds = line.getBoundsInParent();
final double y = separator.getHeight() - (separator.getInsets().getBottom() + line.prefHeight(-1));
assertEquals(y, lineBounds.getMinY(), .1);
assertEquals(line.prefHeight(-1), lineBounds.getHeight(), .1);
assertLineWidthMatchesSkinWidth();
}
@Test public void separatorWith_LEFT_PositionsLineAtLeftOfArea_vertical() {
separator.setHalignment(HPos.LEFT);
separator.setOrientation(Orientation.VERTICAL);
separator.layout();
Bounds lineBounds = line.getBoundsInParent();
assertEquals(separator.getInsets().getLeft(), lineBounds.getMinX(), .1);
assertEquals(line.prefWidth(-1), lineBounds.getWidth(), .1);
assertLineHeightMatchesSkinHeight();
}
@Test public void separatorWith_CENTER_PositionsLineAtCenterOfArea_vertical() {
separator.setHalignment(HPos.CENTER);
separator.setOrientation(Orientation.VERTICAL);
separator.layout();
Bounds lineBounds = line.getBoundsInParent();
final double cw = separator.getWidth() - (separator.getInsets().getLeft() + separator.getInsets().getRight());
final double centerLine = separator.getInsets().getLeft() + (cw / 2.0);
assertEquals(centerLine - (lineBounds.getWidth() / 2.0), lineBounds.getMinX(), .1);
assertEquals(line.prefWidth(-1), lineBounds.getWidth(), .1);
assertLineHeightMatchesSkinHeight();
}
@Test public void separatorWith_RIGHT_PositionsLineAtRightOfArea_vertical() {
separator.setHalignment(HPos.RIGHT);
separator.setOrientation(Orientation.VERTICAL);
separator.layout();
Bounds lineBounds = line.getBoundsInParent();
final double x = separator.getWidth() - (separator.getInsets().getRight() + line.prefWidth(-1));
assertEquals(x, lineBounds.getMinX(), .1);
assertEquals(line.prefWidth(-1), lineBounds.getWidth(), .1);
assertLineHeightMatchesSkinHeight();
}
}
