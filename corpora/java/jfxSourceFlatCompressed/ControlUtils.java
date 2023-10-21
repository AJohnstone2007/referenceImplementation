package javafx.scene.control;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
class ControlUtils {
private ControlUtils() { }
public static void scrollToIndex(final Control control, int index) {
Utils.executeOnceWhenPropertyIsNonNull(control.skinProperty(), (Skin<?> skin) -> {
Event.fireEvent(control, new ScrollToEvent<>(control, control, ScrollToEvent.scrollToTopIndex(), index));
});
}
public static void scrollToColumn(final Control control, final TableColumnBase<?, ?> column) {
Utils.executeOnceWhenPropertyIsNonNull(control.skinProperty(), (Skin<?> skin) -> {
control.fireEvent(new ScrollToEvent<TableColumnBase<?, ?>>(control, control, ScrollToEvent.scrollToColumn(), column));
});
}
static void requestFocusOnControlOnlyIfCurrentFocusOwnerIsChild(Control c) {
Scene scene = c.getScene();
final Node focusOwner = scene == null ? null : scene.getFocusOwner();
if (focusOwner == null) {
c.requestFocus();
} else if (! c.equals(focusOwner)) {
Parent p = focusOwner.getParent();
while (p != null) {
if (c.equals(p)) {
c.requestFocus();
break;
}
p = p.getParent();
}
}
}
static <T> ListChangeListener.Change<T> buildClearAndSelectChange(
ObservableList<T> list, List<T> removed, T retainedRow, Comparator<T> rowComparator) {
return new ListChangeListener.Change<T>(list) {
private final int[] EMPTY_PERM = new int[0];
private final int removedSize = removed.size();
private final List<T> firstRemovedRange;
private final List<T> secondRemovedRange;
private boolean invalid = true;
private boolean atFirstRange = true;
private int from = -1;
{
int insertionPoint = Collections.binarySearch(removed, retainedRow, rowComparator);
if (insertionPoint >= 0) {
firstRemovedRange = removed;
secondRemovedRange = Collections.emptyList();
} else {
int midIndex = -insertionPoint - 1;
firstRemovedRange = removed.subList(0, midIndex);
secondRemovedRange = removed.subList(midIndex, removedSize);
}
}
@Override public int getFrom() {
checkState();
return from;
}
@Override public int getTo() {
return getFrom();
}
@Override public List<T> getRemoved() {
checkState();
return atFirstRange ? firstRemovedRange : secondRemovedRange;
}
@Override public int getRemovedSize() {
checkState();
return atFirstRange ? firstRemovedRange.size() : secondRemovedRange.size();
}
@Override protected int[] getPermutation() {
checkState();
return EMPTY_PERM;
}
@Override public boolean next() {
if (invalid) {
invalid = false;
from = atFirstRange ? 0 : 1;
return true;
}
if (atFirstRange && !secondRemovedRange.isEmpty()) {
atFirstRange = false;
from = 1;
return true;
}
return false;
}
@Override public void reset() {
invalid = true;
atFirstRange = !firstRemovedRange.isEmpty();
}
private void checkState() {
if (invalid) {
throw new IllegalStateException("Invalid Change state: next() must be called before inspecting the Change.");
}
}
};
}
public static <S> void updateSelectedIndices(MultipleSelectionModelBase<S> sm, boolean isCellSelectionEnabled, ListChangeListener.Change<? extends TablePositionBase<?>> c, IntPredicate removeRowFilter) {
sm.selectedIndices._beginChange();
while (c.next()) {
sm.startAtomic();
final List<Integer> removed = c.getRemoved().stream()
.mapToInt(TablePositionBase::getRow)
.distinct()
.filter(removeRowFilter)
.boxed()
.peek(sm.selectedIndices::clear)
.collect(Collectors.toList());
final int addedSize = (int)c.getAddedSubList().stream()
.mapToInt(TablePositionBase::getRow)
.distinct()
.peek(sm.selectedIndices::set)
.count();
sm.stopAtomic();
int from = c.getFrom();
if (isCellSelectionEnabled && 0 < from && from < c.getList().size()) {
int tpRow = c.getList().get(from).getRow();
from = sm.selectedIndices.indexOf(tpRow);
}
final int to = from + addedSize;
if (c.wasReplaced()) {
sm.selectedIndices._nextReplace(from, to, removed);
} else if (c.wasRemoved()) {
sm.selectedIndices._nextRemove(from, removed);
} else if (c.wasAdded()) {
sm.selectedIndices._nextAdd(from, to);
}
}
c.reset();
sm.selectedIndices.reset();
if (sm.isAtomic()) {
return;
}
if (sm.getSelectedItems().isEmpty() && sm.getSelectedItem() != null) {
sm.setSelectedItem(null);
}
sm.selectedIndices._endChange();
}
public static <S> int getIndexOfChildWithDescendant(TreeItem<S> parent, TreeItem<S> item) {
if (item == null || parent == null) {
return -1;
}
TreeItem<S> child = item, ancestor = item.getParent();
while (ancestor != null) {
if (ancestor == parent) {
return parent.getChildren().indexOf(child);
}
child = ancestor;
ancestor = child.getParent();
}
return -1;
}
public static <S> boolean isTreeItemIncludingAncestorsExpanded(TreeItem<S> item) {
if (item == null || !item.isExpanded()) {
return false;
}
TreeItem<S> ancestor = item.getParent();
while (ancestor != null) {
if (!ancestor.isExpanded()) {
return false;
}
ancestor = ancestor.getParent();
}
return true;
}
}
