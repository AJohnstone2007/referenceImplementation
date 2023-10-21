package javafx.scene.control;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
public abstract class MultipleSelectionModel<T> extends SelectionModel<T> {
private ObjectProperty<SelectionMode> selectionMode;
public final void setSelectionMode(SelectionMode value) {
selectionModeProperty().set(value);
}
public final SelectionMode getSelectionMode() {
return selectionMode == null ? SelectionMode.SINGLE : selectionMode.get();
}
public final ObjectProperty<SelectionMode> selectionModeProperty() {
if (selectionMode == null) {
selectionMode = new ObjectPropertyBase<SelectionMode>(SelectionMode.SINGLE) {
@Override protected void invalidated() {
if (getSelectionMode() == SelectionMode.SINGLE) {
if (! isEmpty()) {
int lastIndex = getSelectedIndex();
clearSelection();
select(lastIndex);
}
}
}
@Override
public Object getBean() {
return MultipleSelectionModel.this;
}
@Override
public String getName() {
return "selectionMode";
}
};
}
return selectionMode;
}
public MultipleSelectionModel() { }
public abstract ObservableList<Integer> getSelectedIndices();
public abstract ObservableList<T> getSelectedItems();
public abstract void selectIndices(int index, int... indices);
public void selectRange(final int start, final int end) {
if (start == end) return;
final boolean asc = start < end;
final int low = asc ? start : end;
final int high = asc ? end : start;
final int arrayLength = high - low - 1;
int[] indices = new int[arrayLength];
int startValue = asc ? low : high;
int firstVal = asc ? startValue++ : startValue--;
for (int i = 0; i < arrayLength; i++) {
indices[i] = asc ? startValue++ : startValue--;
}
selectIndices(firstVal, indices);
}
public abstract void selectAll();
@Override public abstract void selectFirst();
@Override public abstract void selectLast();
}
