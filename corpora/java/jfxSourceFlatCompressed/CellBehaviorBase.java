package com.sun.javafx.scene.control.behavior;
import javafx.scene.control.Cell;
import javafx.scene.control.Control;
import javafx.scene.control.FocusModel;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import com.sun.javafx.scene.control.inputmap.InputMap;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import java.util.ArrayList;
import java.util.List;
public abstract class CellBehaviorBase<T extends Cell> extends BehaviorBase<T> {
private static final String ANCHOR_PROPERTY_KEY = "anchor";
private static final String IS_DEFAULT_ANCHOR_KEY = "isDefaultAnchor";
public static <T> T getAnchor(Control control, T defaultResponse) {
return hasNonDefaultAnchor(control) ?
(T) control.getProperties().get(ANCHOR_PROPERTY_KEY) :
defaultResponse;
}
public static <T> void setAnchor(Control control, T anchor, boolean isDefaultAnchor) {
if (control == null) return;
if (anchor == null) {
removeAnchor(control);
} else {
control.getProperties().put(ANCHOR_PROPERTY_KEY, anchor);
control.getProperties().put(IS_DEFAULT_ANCHOR_KEY, isDefaultAnchor);
}
}
public static boolean hasNonDefaultAnchor(Control control) {
Boolean isDefaultAnchor = (Boolean) control.getProperties().remove(IS_DEFAULT_ANCHOR_KEY);
return (isDefaultAnchor == null || isDefaultAnchor == false) && hasAnchor(control);
}
public static boolean hasDefaultAnchor(Control control) {
Boolean isDefaultAnchor = (Boolean) control.getProperties().remove(IS_DEFAULT_ANCHOR_KEY);
return isDefaultAnchor != null && isDefaultAnchor == true && hasAnchor(control);
}
private static boolean hasAnchor(Control control) {
return control.getProperties().get(ANCHOR_PROPERTY_KEY) != null;
}
public static void removeAnchor(Control control) {
control.getProperties().remove(ANCHOR_PROPERTY_KEY);
control.getProperties().remove(IS_DEFAULT_ANCHOR_KEY);
}
private final InputMap<T> cellInputMap;
private boolean latePress = false;
public CellBehaviorBase(T control) {
super(control);
cellInputMap = createInputMap();
InputMap.MouseMapping pressedMapping, releasedMapping, mouseDragged;
addDefaultMapping(
pressedMapping = new InputMap.MouseMapping(MouseEvent.MOUSE_PRESSED, this::mousePressed),
releasedMapping = new InputMap.MouseMapping(MouseEvent.MOUSE_RELEASED, this::mouseReleased),
mouseDragged = new InputMap.MouseMapping(MouseEvent.MOUSE_DRAGGED, this::mouseDragged)
);
pressedMapping.setAutoConsume(false);
releasedMapping.setAutoConsume(false);
mouseDragged.setAutoConsume(false);
}
protected abstract Control getCellContainer();
protected abstract MultipleSelectionModel<?> getSelectionModel();
protected abstract FocusModel<?> getFocusModel();
protected abstract void edit(T cell);
protected boolean handleDisclosureNode(double x, double y) {
return false;
}
protected boolean isClickPositionValid(final double x, final double y) {
return true;
}
@Override public InputMap<T> getInputMap() {
return cellInputMap;
}
protected int getIndex() {
return getNode() instanceof IndexedCell ? ((IndexedCell<?>)getNode()).getIndex() : -1;
}
public void mousePressed(MouseEvent e) {
if (e.isSynthesized()) {
latePress = true;
} else {
latePress = isSelected();
if (!latePress) {
doSelect(e.getX(), e.getY(), e.getButton(), e.getClickCount(),
e.isShiftDown(), e.isShortcutDown());
}
}
}
public void mouseReleased(MouseEvent e) {
if (latePress) {
latePress = false;
doSelect(e.getX(), e.getY(), e.getButton(), e.getClickCount(),
e.isShiftDown(), e.isShortcutDown());
}
}
public void mouseDragged(MouseEvent e) {
latePress = false;
}
protected void doSelect(final double x, final double y, final MouseButton button,
final int clickCount, final boolean shiftDown, final boolean shortcutDown) {
final T cell = getNode();
final Control cellContainer = getCellContainer();
if (cell.isEmpty() || ! cell.contains(x, y)) {
return;
}
final int index = getIndex();
boolean selected = cell.isSelected();
MultipleSelectionModel<?> sm = getSelectionModel();
if (sm == null) return;
FocusModel<?> fm = getFocusModel();
if (fm == null) return;
if (handleDisclosureNode(x,y)) {
return;
}
if (! isClickPositionValid(x, y)) return;
if (shiftDown) {
if (! hasNonDefaultAnchor(cellContainer)) {
setAnchor(cellContainer, fm.getFocusedIndex(), false);
}
} else {
removeAnchor(cellContainer);
}
if (button == MouseButton.PRIMARY || (button == MouseButton.SECONDARY && !selected)) {
if (sm.getSelectionMode() == SelectionMode.SINGLE) {
simpleSelect(button, clickCount, shortcutDown);
} else {
if (shortcutDown) {
if (selected) {
sm.clearSelection(index);
fm.focus(index);
} else {
sm.select(index);
}
} else if (shiftDown && clickCount == 1) {
final int focusedIndex = getAnchor(cellContainer, fm.getFocusedIndex());
selectRows(focusedIndex, index);
fm.focus(index);
} else {
simpleSelect(button, clickCount, shortcutDown);
}
}
}
}
protected void simpleSelect(MouseButton button, int clickCount, boolean shortcutDown) {
final int index = getIndex();
MultipleSelectionModel<?> sm = getSelectionModel();
boolean isAlreadySelected = sm.isSelected(index);
if (isAlreadySelected && shortcutDown) {
sm.clearSelection(index);
getFocusModel().focus(index);
isAlreadySelected = false;
} else {
sm.clearAndSelect(index);
}
handleClicks(button, clickCount, isAlreadySelected);
}
protected void handleClicks(MouseButton button, int clickCount, boolean isAlreadySelected) {
if (button == MouseButton.PRIMARY) {
if (clickCount == 1 && isAlreadySelected) {
edit(getNode());
} else if (clickCount == 1) {
edit(null);
} else if (clickCount == 2 && getNode().isEditable()) {
edit(getNode());
}
}
}
void selectRows(int focusedIndex, int index) {
final boolean asc = focusedIndex < index;
int minRow = Math.min(focusedIndex, index);
int maxRow = Math.max(focusedIndex, index);
List<Integer> selectedIndices = new ArrayList<>(getSelectionModel().getSelectedIndices());
for (int i = 0, max = selectedIndices.size(); i < max; i++) {
int selectedIndex = selectedIndices.get(i);
if (selectedIndex < minRow || selectedIndex > maxRow) {
getSelectionModel().clearSelection(selectedIndex);
}
}
if (minRow == maxRow) {
getSelectionModel().select(minRow);
} else {
if (asc) {
getSelectionModel().selectRange(minRow, maxRow + 1);
} else {
getSelectionModel().selectRange(maxRow, minRow - 1);
}
}
}
protected boolean isSelected() {
return getNode().isSelected();
}
}
