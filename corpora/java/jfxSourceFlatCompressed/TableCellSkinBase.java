package javafx.scene.control.skin;
import com.sun.javafx.scene.control.Properties;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Control;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.TableColumnBase;
import javafx.scene.shape.Rectangle;
public abstract class TableCellSkinBase<S, T, C extends IndexedCell<T>> extends CellSkinBase<C> {
boolean isDeferToParentForPrefWidth = false;
public TableCellSkinBase(final C control) {
super(control);
Rectangle clip = new Rectangle();
clip.widthProperty().bind(control.widthProperty());
clip.heightProperty().bind(control.heightProperty());
getSkinnable().setClip(clip);
TableColumnBase<?,?> tableColumn = getTableColumn();
if (tableColumn != null) {
tableColumn.widthProperty().addListener(weakColumnWidthListener);
}
if (control.getProperties().containsKey(Properties.DEFER_TO_PARENT_PREF_WIDTH)) {
isDeferToParentForPrefWidth = true;
}
}
private InvalidationListener columnWidthListener = valueModel -> getSkinnable().requestLayout();
private WeakInvalidationListener weakColumnWidthListener =
new WeakInvalidationListener(columnWidthListener);
public abstract ReadOnlyObjectProperty<? extends TableColumnBase<S,T>> tableColumnProperty();
public final TableColumnBase<S,T> getTableColumn() {
return tableColumnProperty().get();
}
@Override public void dispose() {
if (getSkinnable() == null) return;
TableColumnBase<?,T> tableColumn = getTableColumn();
if (tableColumn != null) {
tableColumn.widthProperty().removeListener(weakColumnWidthListener);
}
super.dispose();
}
@Override protected void layoutChildren(final double x, final double y,
final double w, final double h) {
layoutLabelInArea(x, y, w, h - getSkinnable().getPadding().getBottom());
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
if (isDeferToParentForPrefWidth) {
return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
}
TableColumnBase<?,?> tableColumn = getTableColumn();
return tableColumn == null ? 0 : snapSizeX(tableColumn.getWidth());
}
}
