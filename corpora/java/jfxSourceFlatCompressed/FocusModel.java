package javafx.scene.control;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
public abstract class FocusModel<T> {
public FocusModel() {
focusedIndexProperty().addListener(valueModel -> {
setFocusedItem(getModelItem(getFocusedIndex()));
});
}
private ReadOnlyIntegerWrapper focusedIndex = new ReadOnlyIntegerWrapper(this, "focusedIndex", -1);
public final ReadOnlyIntegerProperty focusedIndexProperty() { return focusedIndex.getReadOnlyProperty(); }
public final int getFocusedIndex() { return focusedIndex.get(); }
final void setFocusedIndex(int value) { focusedIndex.set(value); }
private ReadOnlyObjectWrapper<T> focusedItem = new ReadOnlyObjectWrapper<T>(this, "focusedItem");
public final ReadOnlyObjectProperty<T> focusedItemProperty() { return focusedItem.getReadOnlyProperty(); }
public final T getFocusedItem() { return focusedItemProperty().get(); }
final void setFocusedItem(T value) { focusedItem.set(value); }
protected abstract int getItemCount();
protected abstract T getModelItem(int index);
public boolean isFocused(int index) {
if (index < 0 || index >= getItemCount()) return false;
return getFocusedIndex() == index;
}
public void focus(int index) {
if (index < 0 || index >= getItemCount()) {
setFocusedIndex(-1);
} else {
int oldFocusIndex = getFocusedIndex();
setFocusedIndex(index);
if (oldFocusIndex == index) {
setFocusedItem(getModelItem(index));
}
}
}
public void focusPrevious() {
if (getFocusedIndex() == -1) {
focus(0);
} else if (getFocusedIndex() > 0) {
focus(getFocusedIndex() - 1);
}
}
public void focusNext() {
if (getFocusedIndex() == -1) {
focus(0);
} else if (getFocusedIndex() != getItemCount() -1) {
focus(getFocusedIndex() + 1);
}
}
}
