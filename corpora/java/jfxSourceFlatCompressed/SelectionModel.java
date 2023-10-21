package javafx.scene.control;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
public abstract class SelectionModel<T> {
public final ReadOnlyIntegerProperty selectedIndexProperty() { return selectedIndex.getReadOnlyProperty(); }
private ReadOnlyIntegerWrapper selectedIndex = new ReadOnlyIntegerWrapper(this, "selectedIndex", -1);
protected final void setSelectedIndex(int value) { selectedIndex.set(value); }
public final int getSelectedIndex() { return selectedIndexProperty().get(); }
public final ReadOnlyObjectProperty<T> selectedItemProperty() { return selectedItem.getReadOnlyProperty(); }
private ReadOnlyObjectWrapper<T> selectedItem = new ReadOnlyObjectWrapper<T>(this, "selectedItem");
protected final void setSelectedItem(T value) { selectedItem.set(value); }
public final T getSelectedItem() { return selectedItemProperty().get(); }
public SelectionModel() { }
public abstract void clearAndSelect(int index);
public abstract void select(int index);
public abstract void select(T obj);
public abstract void clearSelection(int index);
public abstract void clearSelection();
public abstract boolean isSelected(int index);
public abstract boolean isEmpty();
public abstract void selectPrevious();
public abstract void selectNext();
public abstract void selectFirst();
public abstract void selectLast();
}
