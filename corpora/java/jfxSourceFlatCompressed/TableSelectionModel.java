package javafx.scene.control;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
public abstract class TableSelectionModel<T> extends MultipleSelectionModelBase<T> {
public TableSelectionModel() {
}
public abstract boolean isSelected(int row, TableColumnBase<T,?> column);
public abstract void select(int row, TableColumnBase<T,?> column);
public abstract void clearAndSelect(int row, TableColumnBase<T,?> column);
public abstract void clearSelection(int row, TableColumnBase<T,?> column);
public abstract void selectLeftCell();
public abstract void selectRightCell();
public abstract void selectAboveCell();
public abstract void selectBelowCell();
public abstract void selectRange(int minRow, TableColumnBase<T,?> minColumn,
int maxRow, TableColumnBase<T,?> maxColumn);
private BooleanProperty cellSelectionEnabled =
new SimpleBooleanProperty(this, "cellSelectionEnabled");
public final BooleanProperty cellSelectionEnabledProperty() {
return cellSelectionEnabled;
}
public final void setCellSelectionEnabled(boolean value) {
cellSelectionEnabledProperty().set(value);
}
public final boolean isCellSelectionEnabled() {
return cellSelectionEnabled == null ? false : cellSelectionEnabled.get();
}
}
