package javafx.scene.control;
public abstract class TableFocusModel<T, TC extends TableColumnBase<T,?>> extends FocusModel<T> {
public TableFocusModel() {
}
public abstract void focus(int row, TC column);
public abstract boolean isFocused(int row, TC column);
public abstract void focusAboveCell();
public abstract void focusBelowCell();
public abstract void focusLeftCell();
public abstract void focusRightCell();
}
