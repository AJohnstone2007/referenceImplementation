package javafx.scene.control.skin;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.ListCellBehavior;
import javafx.geometry.Orientation;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
public class ListCellSkin<T> extends CellSkinBase<ListCell<T>> {
private final BehaviorBase<ListCell<T>> behavior;
public ListCellSkin(ListCell<T> control) {
super(control);
behavior = new ListCellBehavior<>(control);
}
@Override public void dispose() {
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
double pref = super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
ListView<T> listView = getSkinnable().getListView();
return listView == null ? 0 :
listView.getOrientation() == Orientation.VERTICAL ? pref : Math.max(pref, getCellSize());
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
double fixedCellSize = getFixedCellSize();
if (fixedCellSize > 0) {
return fixedCellSize;
}
final double cellSize = getCellSize();
final double prefHeight = cellSize == DEFAULT_CELL_SIZE ? super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset) : cellSize;
return prefHeight;
}
@Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
double fixedCellSize = getFixedCellSize();
if (fixedCellSize > 0) {
return fixedCellSize;
}
return super.computeMinHeight(width, topInset, rightInset, bottomInset, leftInset);
}
@Override protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
double fixedCellSize = getFixedCellSize();
if (fixedCellSize > 0) {
return fixedCellSize;
}
return super.computeMaxHeight(width, topInset, rightInset, bottomInset, leftInset);
}
private double getFixedCellSize() {
ListView<?> listView = getSkinnable().getListView();
return listView != null ? listView.getFixedCellSize() : Region.USE_COMPUTED_SIZE;
}
}
