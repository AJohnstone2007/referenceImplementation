package javafx.scene.control;
public abstract class SingleSelectionModel<T> extends SelectionModel<T> {
public SingleSelectionModel() { }
@Override public void clearSelection() {
updateSelectedIndex(-1);
}
@Override public void clearSelection(int index) {
if (getSelectedIndex() == index) {
clearSelection();
}
}
@Override public boolean isEmpty() {
return getItemCount() == 0 || getSelectedIndex() == -1;
}
@Override public boolean isSelected(int index) {
return getSelectedIndex() == index;
}
@Override public void clearAndSelect(int index) {
select(index);
}
@Override public void select(T obj) {
if (obj == null) {
setSelectedIndex(-1);
setSelectedItem(null);
return;
}
final int itemCount = getItemCount();
for (int i = 0; i < itemCount; i++) {
final T value = getModelItem(i);
if (value != null && value.equals(obj)) {
select(i);
return;
}
}
setSelectedItem(obj);
}
@Override public void select(int index) {
if (index == -1) {
clearSelection();
return;
}
final int itemCount = getItemCount();
if (itemCount == 0 || index < 0 || index >= itemCount) return;
updateSelectedIndex(index);
}
@Override public void selectPrevious() {
if (getSelectedIndex() == 0) return;
select(getSelectedIndex() - 1);
}
@Override public void selectNext() {
select(getSelectedIndex() + 1);
}
@Override public void selectFirst() {
if (getItemCount() > 0) {
select(0);
}
}
@Override public void selectLast() {
int numItems = getItemCount();
if (numItems > 0 && getSelectedIndex() < numItems - 1) {
select(numItems - 1);
}
}
protected abstract T getModelItem(int index);
protected abstract int getItemCount();
private void updateSelectedIndex(int newIndex) {
int currentIndex = getSelectedIndex();
T currentItem = getSelectedItem();
setSelectedIndex(newIndex);
if (currentIndex == -1 && currentItem != null && newIndex == -1) {
} else {
setSelectedItem(getModelItem(getSelectedIndex()));
}
}
}
