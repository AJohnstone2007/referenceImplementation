package com.sun.javafx.scene.control;
import com.sun.javafx.collections.NonIterableChange;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.scene.control.Tab;
import java.util.List;
import java.util.ListIterator;
public class TabObservableList<E> extends ObservableListWrapper<E> {
private final List<E> tabList;
public TabObservableList(List<E> list) {
super(list);
tabList = list;
}
public void reorder(Tab fromTab, Tab toTab) {
if (!tabList.contains(fromTab) || !tabList.contains(toTab) || fromTab == toTab) {
return;
}
Object[] a = tabList.toArray();
int fromIndex = tabList.indexOf(fromTab);
int toIndex = tabList.indexOf(toTab);
if (fromIndex == -1 || toIndex == -1) {
return;
}
int direction = (toIndex - fromIndex) / Math.abs(toIndex - fromIndex);
for (int j = fromIndex; j != toIndex; j += direction) {
a[j] = a[j + direction];
}
a[toIndex] = fromTab;
ListIterator iter = tabList.listIterator();
for (int j = 0; j < tabList.size(); j++) {
iter.next();
iter.set(a[j]);
}
fromTab.getTabPane().getSelectionModel().select(fromTab);
int permSize = Math.abs(toIndex - fromIndex) + 1;
int[] perm = new int[permSize];
int from = direction > 0 ? fromIndex : toIndex;
int to = direction < 0 ? fromIndex : toIndex;
for (int i = 0; i < permSize; ++i) {
perm[i] = i + from;
}
fireChange(new NonIterableChange.SimplePermutationChange<E>(from, to + 1, perm, this));
}
}
