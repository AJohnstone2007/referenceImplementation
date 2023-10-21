package com.sun.javafx.scene.control;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TablePositionBase;
import java.util.*;
public abstract class SelectedCellsMap<T extends TablePositionBase> {
private final ObservableList<T> selectedCells;
private final ObservableList<T> sortedSelectedCells;
private final Map<Integer, BitSet> selectedCellBitSetMap;
public SelectedCellsMap(final ListChangeListener<T> listener) {
selectedCells = FXCollections.<T>observableArrayList();
sortedSelectedCells = new SortedList<>(selectedCells, (T o1, T o2) -> {
int result = o1.getRow() - o2.getRow();
return result == 0 ? (o1.getColumn() - o2.getColumn()) : result;
});
sortedSelectedCells.addListener(listener);
selectedCellBitSetMap = new TreeMap<>((o1, o2) -> o1.compareTo(o2));
}
public abstract boolean isCellSelectionEnabled();
public int size() {
return selectedCells.size();
}
public T get(int i) {
if (i < 0) {
return null;
}
return sortedSelectedCells.get(i);
}
public void add(T tp) {
final int row = tp.getRow();
final int columnIndex = tp.getColumn();
boolean isNewBitSet = false;
BitSet bitset;
if (! selectedCellBitSetMap.containsKey(row)) {
bitset = new BitSet();
selectedCellBitSetMap.put(row, bitset);
isNewBitSet = true;
} else {
bitset = selectedCellBitSetMap.get(row);
}
final boolean cellSelectionModeEnabled = isCellSelectionEnabled();
if (cellSelectionModeEnabled) {
if (columnIndex >= 0) {
boolean isAlreadySet = bitset.get(columnIndex);
if (!isAlreadySet) {
bitset.set(columnIndex);
selectedCells.add(tp);
}
} else {
if (!selectedCells.contains(tp)) {
selectedCells.add(tp);
}
}
} else {
if (isNewBitSet) {
if (columnIndex >= 0) {
bitset.set(columnIndex);
}
selectedCells.add(tp);
}
}
}
public void addAll(Collection<T> cells) {
for (T tp : cells) {
final int row = tp.getRow();
final int columnIndex = tp.getColumn();
BitSet bitset;
if (! selectedCellBitSetMap.containsKey(row)) {
bitset = new BitSet();
selectedCellBitSetMap.put(row, bitset);
} else {
bitset = selectedCellBitSetMap.get(row);
}
if (columnIndex < 0) {
continue;
}
bitset.set(columnIndex);
}
selectedCells.addAll(cells);
}
public void setAll(Collection<T> cells) {
selectedCellBitSetMap.clear();
for (T tp : cells) {
final int row = tp.getRow();
final int columnIndex = tp.getColumn();
BitSet bitset;
if (! selectedCellBitSetMap.containsKey(row)) {
bitset = new BitSet();
selectedCellBitSetMap.put(row, bitset);
} else {
bitset = selectedCellBitSetMap.get(row);
}
if (columnIndex < 0) {
continue;
}
bitset.set(columnIndex);
}
selectedCells.setAll(cells);
}
public void remove(T tp) {
final int row = tp.getRow();
final int columnIndex = tp.getColumn();
if (selectedCellBitSetMap.containsKey(row)) {
BitSet bitset = selectedCellBitSetMap.get(row);
if (columnIndex >= 0) {
bitset.clear(columnIndex);
}
if (bitset.isEmpty()) {
selectedCellBitSetMap.remove(row);
}
}
selectedCells.remove(tp);
}
public void clear() {
selectedCellBitSetMap.clear();
selectedCells.clear();
}
public boolean isSelected(int row, int columnIndex) {
if (columnIndex < 0) {
return selectedCellBitSetMap.containsKey(row);
} else {
return selectedCellBitSetMap.containsKey(row) ? selectedCellBitSetMap.get(row).get(columnIndex) : false;
}
}
public int indexOf(T tp) {
return sortedSelectedCells.indexOf(tp);
}
public boolean isEmpty() {
return selectedCells.isEmpty();
}
public ObservableList<T> getSelectedCells() {
return selectedCells;
}
}