package javafx.scene.control;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
public class CheckBoxTreeItem<T> extends TreeItem<T> {
@SuppressWarnings("unchecked")
public static <T> EventType<TreeModificationEvent<T>> checkBoxSelectionChangedEvent() {
return (EventType<TreeModificationEvent<T>>) CHECK_BOX_SELECTION_CHANGED_EVENT;
}
private static final EventType<? extends Event> CHECK_BOX_SELECTION_CHANGED_EVENT
= new EventType<Event>(TreeModificationEvent.ANY, "checkBoxSelectionChangedEvent");
public CheckBoxTreeItem() {
this(null);
}
public CheckBoxTreeItem(T value) {
this(value, null, false);
}
public CheckBoxTreeItem(T value, Node graphic) {
this(value, graphic, false);
}
public CheckBoxTreeItem(T value, Node graphic, boolean selected) {
this(value, graphic, selected, false);
}
public CheckBoxTreeItem(T value, Node graphic, boolean selected, boolean independent) {
super(value, graphic);
setSelected(selected);
setIndependent(independent);
selectedProperty().addListener(stateChangeListener);
indeterminateProperty().addListener(stateChangeListener);
}
private final ChangeListener<Boolean> stateChangeListener = (ov, oldVal, newVal) -> {
updateState();
};
private final BooleanProperty selected = new SimpleBooleanProperty(this, "selected", false) {
@Override protected void invalidated() {
super.invalidated();
fireEvent(CheckBoxTreeItem.this, true);
}
};
public final void setSelected(boolean value) { selectedProperty().setValue(value); }
public final boolean isSelected() { return selected.getValue(); }
public final BooleanProperty selectedProperty() { return selected; }
private final BooleanProperty indeterminate = new SimpleBooleanProperty(this, "indeterminate", false) {
@Override protected void invalidated() {
super.invalidated();
fireEvent(CheckBoxTreeItem.this, false);
}
};
public final void setIndeterminate(boolean value) { indeterminateProperty().setValue(value); }
public final boolean isIndeterminate() { return indeterminate.getValue(); }
public final BooleanProperty indeterminateProperty() { return indeterminate; }
public final BooleanProperty independentProperty() { return independent; }
private final BooleanProperty independent = new SimpleBooleanProperty(this, "independent", false);
public final void setIndependent(boolean value) { independentProperty().setValue(value); }
public final boolean isIndependent() { return independent.getValue(); }
private static boolean updateLock = false;
private void updateState() {
if (isIndependent()) return;
boolean firstLock = ! updateLock;
updateLock = true;
updateUpwards();
if (firstLock) updateLock = false;
if (updateLock) return;
updateDownwards();
}
private void updateUpwards() {
if (! (getParent() instanceof CheckBoxTreeItem)) return;
CheckBoxTreeItem<?> parent = (CheckBoxTreeItem<?>) getParent();
int selectCount = 0;
int indeterminateCount = 0;
for (TreeItem<?> child : parent.getChildren()) {
if (! (child instanceof CheckBoxTreeItem)) continue;
CheckBoxTreeItem<?> cbti = (CheckBoxTreeItem<?>) child;
selectCount += cbti.isSelected() && ! cbti.isIndeterminate() ? 1 : 0;
indeterminateCount += cbti.isIndeterminate() ? 1 : 0;
}
if (selectCount == parent.getChildren().size()) {
parent.setSelected(true);
parent.setIndeterminate(false);
} else if (selectCount == 0 && indeterminateCount == 0) {
parent.setSelected(false);
parent.setIndeterminate(false);
} else {
parent.setIndeterminate(true);
}
}
private void updateDownwards() {
if (! isLeaf()) {
for (TreeItem<T> child : getChildren()) {
if (child instanceof CheckBoxTreeItem) {
CheckBoxTreeItem<T> cbti = ((CheckBoxTreeItem<T>) child);
cbti.setSelected(isSelected());
}
}
}
}
private void fireEvent(CheckBoxTreeItem<T> item, boolean selectionChanged) {
Event evt = new CheckBoxTreeItem.TreeModificationEvent<T>(CHECK_BOX_SELECTION_CHANGED_EVENT, item, selectionChanged);
Event.fireEvent(this, evt);
}
public static class TreeModificationEvent<T> extends Event {
private static final long serialVersionUID = -8445355590698862999L;
private transient final CheckBoxTreeItem<T> treeItem;
private final boolean selectionChanged;
public static final EventType<Event> ANY =
new EventType<Event> (Event.ANY, "TREE_MODIFICATION");
public TreeModificationEvent(EventType<? extends Event> eventType, CheckBoxTreeItem<T> treeItem, boolean selectionChanged) {
super(eventType);
this.treeItem = treeItem;
this.selectionChanged = selectionChanged;
}
public CheckBoxTreeItem<T> getTreeItem() {
return treeItem;
}
public boolean wasSelectionChanged() {
return selectionChanged;
}
public boolean wasIndeterminateChanged() {
return ! selectionChanged;
}
}
}
