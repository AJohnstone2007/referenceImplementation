package com.sun.javafx.scene.control;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
public abstract class SelectedItemsReadOnlyObservableList<E> extends ObservableListBase<E> {
private final ObservableList<Integer> selectedIndices;
private final Supplier<Integer> modelSizeSupplier;
private final List<WeakReference<E>> itemsRefList;
public SelectedItemsReadOnlyObservableList(ObservableList<Integer> selectedIndices, Supplier<Integer> modelSizeSupplier) {
this.modelSizeSupplier = modelSizeSupplier;
this.selectedIndices = selectedIndices;
this.itemsRefList = new ArrayList<>();
selectedIndices.addListener((ListChangeListener<Integer>)c -> {
int totalRemovedSize = 0;
beginChange();
while (c.next()) {
if (c.wasReplaced()) {
List<E> removed = getRemovedElements(c, totalRemovedSize);
List<E> added = getAddedElements(c);
if (!removed.equals(added)) {
nextReplace(c.getFrom(), c.getTo(), removed);
}
} else if (c.wasAdded()) {
nextAdd(c.getFrom(), c.getTo());
} else if (c.wasRemoved()) {
int removedSize = c.getRemovedSize();
if (removedSize == 1) {
nextRemove(c.getFrom(), getRemovedModelItem(totalRemovedSize + c.getFrom()));
} else {
nextRemove(c.getFrom(), getRemovedElements(c, totalRemovedSize));
}
totalRemovedSize += removedSize;
} else if (c.wasPermutated()) {
int[] permutation = new int[size()];
for (int i = 0; i < size(); i++) {
permutation[i] = c.getPermutation(i);
}
nextPermutation(c.getFrom(), c.getTo(), permutation);
} else if (c.wasUpdated()) {
for (int i = c.getFrom(); i < c.getTo(); i++) {
nextUpdate(i);
}
}
}
itemsRefList.clear();
for (int selectedIndex : selectedIndices) {
itemsRefList.add(new WeakReference<>(getModelItem(selectedIndex)));
}
endChange();
});
}
protected abstract E getModelItem(int index);
@Override
public E get(int index) {
int pos = selectedIndices.get(index);
return getModelItem(pos);
}
@Override
public int size() {
return selectedIndices.size();
}
private E _getModelItem(int index) {
if (index >= modelSizeSupplier.get()) {
return getRemovedModelItem(index);
} else {
return getModelItem(index);
}
}
private E getRemovedModelItem(int index) {
return index < 0 || index >= itemsRefList.size() ? null : itemsRefList.get(index).get();
}
private List<E> getRemovedElements(ListChangeListener.Change<? extends Integer> c, int totalRemovedSize) {
List<E> removed = new ArrayList<>(c.getRemovedSize());
final int startPos = c.getFrom();
for (int i = startPos, max = startPos + c.getRemovedSize(); i < max; i++) {
removed.add(getRemovedModelItem(i + totalRemovedSize));
}
return removed;
}
private List<E> getAddedElements(ListChangeListener.Change<? extends Integer> c) {
List<E> added = new ArrayList<>(c.getAddedSize());
for (int index : c.getAddedSubList()) {
added.add(_getModelItem(index));
}
return added;
}
}
