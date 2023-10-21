package javafx.scene.control;
import com.sun.javafx.collections.NonIterableChange;
import static javafx.scene.control.SelectionMode.SINGLE;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.sun.javafx.scene.control.MultipleAdditionAndRemovedChange;
import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;
import com.sun.javafx.scene.control.SelectedItemsReadOnlyObservableList;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.util.Callback;
import javafx.util.Pair;
abstract class MultipleSelectionModelBase<T> extends MultipleSelectionModel<T> {
public MultipleSelectionModelBase() {
selectedIndexProperty().addListener(valueModel -> {
setSelectedItem(getModelItem(getSelectedIndex()));
});
selectedIndices = new SelectedIndicesList();
selectedItems = new SelectedItemsReadOnlyObservableList<T>(selectedIndices, () -> getItemCount()) {
@Override protected T getModelItem(int index) {
return MultipleSelectionModelBase.this.getModelItem(index);
}
};
}
final SelectedIndicesList selectedIndices;
@Override public ObservableList<Integer> getSelectedIndices() {
return selectedIndices;
}
private final ObservableListBase<T> selectedItems;
@Override public ObservableList<T> getSelectedItems() {
return selectedItems;
}
ListChangeListener.Change selectedItemChange;
protected abstract int getItemCount();
protected abstract T getModelItem(int index);
protected abstract void focus(int index);
protected abstract int getFocusedIndex();
static class ShiftParams {
private final int clearIndex;
private final int setIndex;
private final boolean selected;
ShiftParams(int clearIndex, int setIndex, boolean selected) {
this.clearIndex = clearIndex;
this.setIndex = setIndex;
this.selected = selected;
}
public final int getClearIndex() {
return clearIndex;
}
public final int getSetIndex() {
return setIndex;
}
public final boolean isSelected() {
return selected;
}
}
void shiftSelection(int position, int shift, final Callback<ShiftParams, Void> callback) {
shiftSelection(Arrays.asList(new Pair<>(position, shift)), callback);
}
void shiftSelection(List<Pair<Integer, Integer>> shifts, final Callback<ShiftParams, Void> callback) {
int selectedIndicesCardinality = selectedIndices.size();
if (selectedIndicesCardinality == 0) return;
int selectedIndicesSize = selectedIndices.bitsetSize();
int[] perm = new int[selectedIndicesSize];
Arrays.fill(perm, -1);
Collections.sort(shifts, (s1, s2) -> Integer.compare(s2.getKey(), s1.getKey()));
final int lowestShiftPosition = shifts.get(shifts.size() - 1).getKey();
BitSet selectedIndicesCopy = (BitSet) selectedIndices.bitset.clone();
startAtomic();
for (Pair<Integer, Integer> shift : shifts) {
doShift(shift, callback, perm);
}
stopAtomic();
final int[] prunedPerm = Arrays.stream(perm).filter(value -> value > -1).toArray();
final boolean hasSelectionChanged = prunedPerm.length > 0;
final int selectedIndex = getSelectedIndex();
if (selectedIndex >= lowestShiftPosition && selectedIndex > -1) {
int totalShift = shifts.stream()
.filter(shift -> shift.getKey() <= selectedIndex)
.mapToInt(shift -> shift.getValue())
.sum();
final int newSelectionLead = Math.max(0, selectedIndex + totalShift);
setSelectedIndex(newSelectionLead);
if (hasSelectionChanged) {
selectedIndices.set(newSelectionLead, true);
} else {
select(newSelectionLead);
}
}
if (hasSelectionChanged) {
BitSet removed = (BitSet) selectedIndicesCopy.clone();
removed.andNot(selectedIndices.bitset);
BitSet added = (BitSet) selectedIndices.bitset.clone();
added.andNot(selectedIndicesCopy);
selectedIndices.reset();
selectedIndices.callObservers(new MultipleAdditionAndRemovedChange<>(
added.stream().boxed().collect(Collectors.toList()),
removed.stream().boxed().collect(Collectors.toList()),
selectedIndices
));
}
}
private void doShift(Pair<Integer, Integer> shiftPair, final Callback<ShiftParams, Void> callback, int[] perm) {
final int position = shiftPair.getKey();
final int shift = shiftPair.getValue();
if (position < 0) return;
if (shift == 0) return;
int idx = (int) Arrays.stream(perm).filter(value -> value > -1).count();
int selectedIndicesSize = selectedIndices.bitsetSize() - idx;
if (shift > 0) {
for (int i = selectedIndicesSize - 1; i >= position && i >= 0; i--) {
boolean selected = selectedIndices.isSelected(i);
if (callback == null) {
selectedIndices.clear(i);
selectedIndices.set(i + shift, selected);
} else {
callback.call(new ShiftParams(i, i + shift, selected));
}
if (selected) {
perm[idx++] = i + 1;
}
}
selectedIndices.clear(position);
} else if (shift < 0) {
for (int i = position; i < selectedIndicesSize; i++) {
if ((i + shift) < 0) continue;
if ((i + 1 + shift) < position) continue;
boolean selected = selectedIndices.isSelected(i + 1);
if (callback == null) {
selectedIndices.clear(i + 1);
selectedIndices.set(i + 1 + shift, selected);
} else {
callback.call(new ShiftParams(i + 1, i + 1 + shift, selected));
}
if (selected) {
perm[idx++] = i;
}
}
}
}
void startAtomic() {
selectedIndices.startAtomic();
}
void stopAtomic() {
selectedIndices.stopAtomic();
}
boolean isAtomic() {
return selectedIndices.isAtomic();
}
@Override public void clearAndSelect(int row) {
if (row < 0 || row >= getItemCount()) {
clearSelection();
return;
}
final boolean wasSelected = isSelected(row);
if (wasSelected && getSelectedIndices().size() == 1) {
if (getSelectedItem() == getModelItem(row)) {
return;
}
}
BitSet selectedIndicesCopy = new BitSet();
selectedIndicesCopy.or(selectedIndices.bitset);
selectedIndicesCopy.clear(row);
List<Integer> previousSelectedIndices = new SelectedIndicesList(selectedIndicesCopy);
startAtomic();
clearSelection();
select(row);
stopAtomic();
ListChangeListener.Change<Integer> change;
if (wasSelected) {
change = ControlUtils.buildClearAndSelectChange(
selectedIndices, previousSelectedIndices, row, Comparator.naturalOrder());
} else {
int changeIndex = Math.max(0, selectedIndices.indexOf(row));
change = new NonIterableChange.GenericAddRemoveChange<>(
changeIndex, changeIndex+1, previousSelectedIndices, selectedIndices);
}
selectedIndices.callObservers(change);
}
@Override public void select(int row) {
if (row == -1) {
clearSelection();
return;
}
if (row < 0 || row >= getItemCount()) {
return;
}
boolean isSameRow = row == getSelectedIndex();
T currentItem = getSelectedItem();
T newItem = getModelItem(row);
boolean isSameItem = newItem != null && newItem.equals(currentItem);
boolean fireUpdatedItemEvent = isSameRow && ! isSameItem;
focus(row);
if (! selectedIndices.isSelected(row)) {
if (getSelectionMode() == SINGLE) {
startAtomic();
quietClearSelection();
stopAtomic();
}
selectedIndices.set(row);
}
setSelectedIndex(row);
if (fireUpdatedItemEvent) {
setSelectedItem(newItem);
}
}
@Override public void select(T obj) {
if (obj == null && getSelectionMode() == SelectionMode.SINGLE) {
clearSelection();
return;
}
Object rowObj = null;
for (int i = 0, max = getItemCount(); i < max; i++) {
rowObj = getModelItem(i);
if (rowObj == null) continue;
if (rowObj.equals(obj)) {
if (isSelected(i)) {
return;
}
if (getSelectionMode() == SINGLE) {
quietClearSelection();
}
select(i);
return;
}
}
setSelectedIndex(-1);
setSelectedItem(obj);
}
@Override public void selectIndices(int row, int... rows) {
if (rows == null || rows.length == 0) {
select(row);
return;
}
int rowCount = getItemCount();
if (getSelectionMode() == SINGLE) {
quietClearSelection();
for (int i = rows.length - 1; i >= 0; i--) {
int index = rows[i];
if (index >= 0 && index < rowCount) {
selectedIndices.set(index);
select(index);
break;
}
}
if (selectedIndices.isEmpty()) {
if (row > 0 && row < rowCount) {
selectedIndices.set(row);
select(row);
}
}
} else {
selectedIndices.set(row, rows);
IntStream.concat(IntStream.of(row), IntStream.of(rows))
.filter(index -> index >= 0 && index < rowCount)
.reduce((first, second) -> second)
.ifPresent(lastIndex -> {
setSelectedIndex(lastIndex);
focus(lastIndex);
setSelectedItem(getModelItem(lastIndex));
});
}
}
@Override public void selectAll() {
if (getSelectionMode() == SINGLE) return;
if (getItemCount() <= 0) return;
final int rowCount = getItemCount();
final int focusedIndex = getFocusedIndex();
clearSelection();
selectedIndices.set(0, rowCount, true);
if (focusedIndex == -1) {
setSelectedIndex(rowCount - 1);
focus(rowCount - 1);
} else {
setSelectedIndex(focusedIndex);
focus(focusedIndex);
}
}
@Override public void selectFirst() {
if (getSelectionMode() == SINGLE) {
quietClearSelection();
}
if (getItemCount() > 0) {
select(0);
}
}
@Override public void selectLast() {
if (getSelectionMode() == SINGLE) {
quietClearSelection();
}
int numItems = getItemCount();
if (numItems > 0 && getSelectedIndex() < numItems - 1) {
select(numItems - 1);
}
}
@Override public void clearSelection(int index) {
if (index < 0) return;
boolean wasEmpty = selectedIndices.isEmpty();
selectedIndices.clear(index);
if (! wasEmpty && selectedIndices.isEmpty()) {
clearSelection();
}
}
@Override public void clearSelection() {
quietClearSelection();
if (! isAtomic()) {
setSelectedIndex(-1);
focus(-1);
}
}
private void quietClearSelection() {
selectedIndices.clear();
}
@Override public boolean isSelected(int index) {
if (index >= 0 && index < selectedIndices.bitsetSize()) {
return selectedIndices.isSelected(index);
}
return false;
}
@Override public boolean isEmpty() {
return selectedIndices.isEmpty();
}
@Override public void selectPrevious() {
int focusIndex = getFocusedIndex();
if (getSelectionMode() == SINGLE) {
quietClearSelection();
}
if (focusIndex == -1) {
select(getItemCount() - 1);
} else if (focusIndex > 0) {
select(focusIndex - 1);
}
}
@Override public void selectNext() {
int focusIndex = getFocusedIndex();
if (getSelectionMode() == SINGLE) {
quietClearSelection();
}
if (focusIndex == -1) {
select(0);
} else if (focusIndex != getItemCount() -1) {
select(focusIndex + 1);
}
}
class SelectedIndicesList extends ReadOnlyUnbackedObservableList<Integer> {
private final BitSet bitset;
private int size = -1;
private int lastGetIndex = -1;
private int lastGetValue = -1;
private int atomicityCount = 0;
public SelectedIndicesList() {
this(new BitSet());
}
public SelectedIndicesList(BitSet bitset) {
this.bitset = bitset;
}
boolean isAtomic() {
return atomicityCount > 0;
}
void startAtomic() {
atomicityCount++;
}
void stopAtomic() {
atomicityCount = Math.max(0, atomicityCount - 1);
}
@Override public Integer get(int index) {
final int itemCount = size();
if (index < 0 || index >= itemCount) {
throw new IndexOutOfBoundsException(index + " >= " + itemCount);
}
if (index == (lastGetIndex + 1) && lastGetValue < itemCount) {
lastGetIndex++;
lastGetValue = bitset.nextSetBit(lastGetValue + 1);
return lastGetValue;
} else if (index == (lastGetIndex - 1) && lastGetValue > 0) {
lastGetIndex--;
lastGetValue = bitset.previousSetBit(lastGetValue - 1);
return lastGetValue;
} else {
for (lastGetIndex = 0, lastGetValue = bitset.nextSetBit(0);
lastGetValue >= 0 || lastGetIndex == index;
lastGetIndex++, lastGetValue = bitset.nextSetBit(lastGetValue + 1)) {
if (lastGetIndex == index) {
return lastGetValue;
}
}
}
return -1;
}
public void set(int index) {
if (!isValidIndex(index) || isSelected(index)) {
return;
}
_beginChange();
size = -1;
bitset.set(index);
int indicesIndex = indexOf(index);
_nextAdd(indicesIndex, indicesIndex + 1);
_endChange();
}
private boolean isValidIndex(int index) {
return index >= 0 && index < getItemCount();
}
public void set(int index, boolean isSet) {
if (isSet) {
set(index);
} else {
clear(index);
}
}
public void set(int index, int end, boolean isSet) {
_beginChange();
size = -1;
if (isSet) {
bitset.set(index, end, isSet);
int indicesIndex = indexOf(index);
int span = end - index;
_nextAdd(indicesIndex, indicesIndex + span);
} else {
bitset.set(index, end, isSet);
}
_endChange();
}
public void set(int index, int... indices) {
if (indices == null || indices.length == 0) {
set(index);
} else {
startAtomic();
List<Integer> sortedNewIndices =
IntStream.concat(IntStream.of(index), IntStream.of(indices))
.distinct()
.filter(this::isValidIndex)
.filter(this::isNotSelected)
.sorted()
.boxed()
.peek(this::set)
.collect(Collectors.toList());
stopAtomic();
final int size = sortedNewIndices.size();
if (size == 0) {
} else if (size == 1) {
_beginChange();
int _index = sortedNewIndices.get(0);
int indicesIndex = indexOf(_index);
_nextAdd(indicesIndex, indicesIndex + 1);
_endChange();
} else {
_beginChange();
int pos = 0;
int start = 0;
int end = 0;
int startValue = sortedNewIndices.get(pos++);
start = indexOf(startValue);
end = start + 1;
int endValue = startValue;
while (pos < size) {
int previousEndValue = endValue;
endValue = sortedNewIndices.get(pos++);
++end;
if (previousEndValue != (endValue - 1)) {
_nextAdd(start, end);
start = end;
continue;
}
if (pos == size) {
_nextAdd(start, start + pos);
}
}
_endChange();
}
}
}
public void clear() {
_beginChange();
List<Integer> removed = bitset.stream().boxed().collect(Collectors.toList());
size = 0;
bitset.clear();
_nextRemove(0, removed);
_endChange();
}
public void clear(int index) {
if (!bitset.get(index)) return;
int indicesIndex = indexOf(index);
_beginChange();
size = -1;
bitset.clear(index);
_nextRemove(indicesIndex, index);
_endChange();
}
public boolean isSelected(int index) {
return bitset.get(index);
}
public boolean isNotSelected(int index) {
return !isSelected(index);
}
@Override public int size() {
if (size >= 0) {
return size;
}
size = bitset.cardinality();
return size;
}
public int bitsetSize() {
return bitset.size();
}
@Override public int indexOf(Object obj) {
if (!(obj instanceof Number)) {
return -1;
}
Number n = (Number) obj;
int index = n.intValue();
if (!bitset.get(index)) {
return -1;
}
if (index == 0) {
return 0;
}
if (index == bitset.length() - 1) {
return size() - 1;
}
if (index > bitset.length() / 2) {
int count = 1;
for (int i = bitset.nextSetBit(index+1); i >= 0; i = bitset.nextSetBit(i+1)) {
count++;
}
return size() - count;
}
int count = 0;
for (int i = bitset.previousSetBit(index-1); i >= 0; i = bitset.previousSetBit(i-1)) {
count++;
}
return count;
}
@Override public boolean contains(Object o) {
if (o instanceof Number) {
Number n = (Number) o;
int index = n.intValue();
return index >= 0 && index < bitset.length() &&
bitset.get(index);
}
return false;
}
public void reset() {
this.lastGetIndex = -1;
this.lastGetValue = -1;
}
@Override public void _beginChange() {
if (!isAtomic()) {
super._beginChange();
}
}
@Override public void _endChange() {
if (!isAtomic()) {
super._endChange();
}
}
@Override public final void _nextUpdate(int pos) {
if (!isAtomic()) {
nextUpdate(pos);
}
}
@Override public final void _nextSet(int idx, Integer old) {
if (!isAtomic()) {
nextSet(idx, old);
}
}
@Override public final void _nextReplace(int from, int to, List<? extends Integer> removed) {
if (!isAtomic()) {
nextReplace(from, to, removed);
}
}
@Override public final void _nextRemove(int idx, List<? extends Integer> removed) {
if (!isAtomic()) {
nextRemove(idx, removed);
}
}
@Override public final void _nextRemove(int idx, Integer removed) {
if (!isAtomic()) {
nextRemove(idx, removed);
}
}
@Override public final void _nextPermutation(int from, int to, int[] perm) {
if (!isAtomic()) {
nextPermutation(from, to, perm);
}
}
@Override public final void _nextAdd(int from, int to) {
if (!isAtomic()) {
nextAdd(from, to);
}
}
}
}
