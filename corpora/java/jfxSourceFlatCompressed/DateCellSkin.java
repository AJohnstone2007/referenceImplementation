package javafx.scene.control.skin;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.DateCell;
import javafx.scene.text.Text;
import com.sun.javafx.scene.control.behavior.DateCellBehavior;
public class DateCellSkin extends CellSkinBase<DateCell> {
private final BehaviorBase<DateCell> behavior;
public DateCellSkin(DateCell control) {
super(control);
behavior = new DateCellBehavior(control);
control.setMaxWidth(Double.MAX_VALUE);
}
@Override public void dispose() {
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override protected void updateChildren() {
super.updateChildren();
Text secondaryText = (Text)getSkinnable().getProperties().get("DateCell.secondaryText");
if (secondaryText != null) {
secondaryText.setManaged(false);
getChildren().add(secondaryText);
}
}
@Override protected void layoutChildren(final double x, final double y,
final double w, final double h) {
super.layoutChildren(x, y, w, h);
Text secondaryText = (Text)getSkinnable().getProperties().get("DateCell.secondaryText");
if (secondaryText != null) {
double textX = x + w - rightLabelPadding() - secondaryText.getLayoutBounds().getWidth();
double textY = y + h - bottomLabelPadding() - secondaryText.getLayoutBounds().getHeight();
secondaryText.relocate(snapPositionX(textX), snapPositionY(textY));
}
}
@Override protected double computePrefWidth(double height,
double topInset, double rightInset,
double bottomInset, double leftInset) {
double pref = super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
return snapSizeX(Math.max(pref, cellSize()));
}
@Override protected double computePrefHeight(double width,
double topInset, double rightInset,
double bottomInset, double leftInset) {
double pref = super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
return snapSizeY(Math.max(pref, cellSize()));
}
private double cellSize() {
double cellSize = getCellSize();
Text secondaryText = (Text)getSkinnable().getProperties().get("DateCell.secondaryText");
if (secondaryText != null && cellSize == DEFAULT_CELL_SIZE) {
cellSize = 36;
}
return cellSize;
}
}
