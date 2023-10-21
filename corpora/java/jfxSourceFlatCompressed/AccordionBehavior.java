package com.sun.javafx.scene.control.behavior;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Accordion;
import javafx.scene.control.FocusModel;
import javafx.scene.control.TitledPane;
import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import static javafx.scene.input.KeyCode.*;
public class AccordionBehavior extends BehaviorBase<Accordion> {
private final InputMap<Accordion> inputMap;
private AccordionFocusModel focusModel;
public AccordionBehavior(Accordion accordion) {
super(accordion);
focusModel = new AccordionFocusModel(accordion);
inputMap = createInputMap();
addDefaultMapping(inputMap,
new InputMap.KeyMapping(UP, e -> pageUp(false)),
new InputMap.KeyMapping(DOWN, e -> pageDown(false)),
new InputMap.KeyMapping(LEFT, e -> {
if (isRTL(accordion)) pageDown(false);
else pageUp(false);
}),
new InputMap.KeyMapping(RIGHT, e -> {
if (isRTL(accordion)) pageUp(false);
else pageDown(false);
}),
new InputMap.KeyMapping(HOME, this::home),
new InputMap.KeyMapping(END, this::end),
new InputMap.KeyMapping(PAGE_UP, e -> pageUp(true)),
new InputMap.KeyMapping(PAGE_DOWN, e -> pageDown(true)),
new InputMap.KeyMapping(new KeyBinding(PAGE_UP).ctrl(), this::moveBackward),
new InputMap.KeyMapping(new KeyBinding(PAGE_DOWN).ctrl(), this::moveForward),
new InputMap.KeyMapping(new KeyBinding(TAB).ctrl(), this::moveForward),
new InputMap.KeyMapping(new KeyBinding(TAB).ctrl().shift(), this::moveBackward),
new InputMap.MouseMapping(MouseEvent.MOUSE_PRESSED, this::mousePressed)
);
}
@Override public void dispose() {
focusModel.dispose();
super.dispose();
}
@Override public InputMap<Accordion> getInputMap() {
return inputMap;
}
private void pageUp(boolean doExpand) {
Accordion accordion = getNode();
if (focusModel.getFocusedIndex() != -1 && accordion.getPanes().get(focusModel.getFocusedIndex()).isFocused()) {
focusModel.focusPrevious();
int next = focusModel.getFocusedIndex();
accordion.getPanes().get(next).requestFocus();
if (doExpand) {
accordion.getPanes().get(next).setExpanded(true);
}
}
}
private void pageDown(boolean doExpand) {
Accordion accordion = getNode();
if (focusModel.getFocusedIndex() != -1 && accordion.getPanes().get(focusModel.getFocusedIndex()).isFocused()) {
focusModel.focusNext();
int next = focusModel.getFocusedIndex();
accordion.getPanes().get(next).requestFocus();
if (doExpand) {
accordion.getPanes().get(next).setExpanded(true);
}
}
}
private void moveBackward(KeyEvent e) {
Accordion accordion = getNode();
focusModel.focusPrevious();
if (focusModel.getFocusedIndex() != -1) {
int next = focusModel.getFocusedIndex();
accordion.getPanes().get(next).requestFocus();
accordion.getPanes().get(next).setExpanded(true);
}
}
private void moveForward(KeyEvent e) {
Accordion accordion = getNode();
focusModel.focusNext();
if (focusModel.getFocusedIndex() != -1) {
int next = focusModel.getFocusedIndex();
accordion.getPanes().get(next).requestFocus();
accordion.getPanes().get(next).setExpanded(true);
}
}
private void home(KeyEvent e) {
Accordion accordion = getNode();
if (focusModel.getFocusedIndex() != -1 && accordion.getPanes().get(focusModel.getFocusedIndex()).isFocused()) {
TitledPane tp = accordion.getPanes().get(0);
tp.requestFocus();
tp.setExpanded(!tp.isExpanded());
}
}
private void end(KeyEvent e) {
Accordion accordion = getNode();
if (focusModel.getFocusedIndex() != -1 && accordion.getPanes().get(focusModel.getFocusedIndex()).isFocused()) {
TitledPane tp = accordion.getPanes().get(accordion.getPanes().size() - 1);
tp.requestFocus();
tp.setExpanded(!tp.isExpanded());
}
}
public void mousePressed(MouseEvent e) {
Accordion accordion = getNode();
if (accordion.getPanes().size() > 0) {
TitledPane lastTitledPane = accordion.getPanes().get(accordion.getPanes().size() - 1);
lastTitledPane.requestFocus();
}
else {
accordion.requestFocus();
}
}
static class AccordionFocusModel extends FocusModel<TitledPane> {
private final Accordion accordion;
private final ChangeListener<Boolean> focusListener = new ChangeListener<Boolean>() {
@Override
public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
if (newValue) {
if (accordion.getExpandedPane() != null) {
accordion.getExpandedPane().requestFocus();
} else {
if (! accordion.getPanes().isEmpty()) {
accordion.getPanes().get(0).requestFocus();
}
}
}
}
};
private final ChangeListener<Boolean> paneFocusListener = new ChangeListener<Boolean>() {
@Override public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
if (newValue) {
final ReadOnlyBooleanProperty focusedProperty = (ReadOnlyBooleanProperty) observable;
final TitledPane tp = (TitledPane) focusedProperty.getBean();
focus(accordion.getPanes().indexOf(tp));
}
}
};
private final ListChangeListener<TitledPane> panesListener = c -> {
while (c.next()) {
if (c.wasAdded()) {
for (final TitledPane tp: c.getAddedSubList()) {
tp.focusedProperty().addListener(paneFocusListener);
}
} else if (c.wasRemoved()) {
for (final TitledPane tp: c.getAddedSubList()) {
tp.focusedProperty().removeListener(paneFocusListener);
}
}
}
};
public AccordionFocusModel(final Accordion accordion) {
if (accordion == null) {
throw new IllegalArgumentException("Accordion can not be null");
}
this.accordion = accordion;
this.accordion.focusedProperty().addListener(focusListener);
this.accordion.getPanes().addListener(panesListener);
for (final TitledPane tp: this.accordion.getPanes()) {
tp.focusedProperty().addListener(paneFocusListener);
}
}
void dispose() {
accordion.focusedProperty().removeListener(focusListener);
accordion.getPanes().removeListener(panesListener);
for (final TitledPane tp: this.accordion.getPanes()) {
tp.focusedProperty().removeListener(paneFocusListener);
}
}
@Override
protected int getItemCount() {
final ObservableList<TitledPane> panes = accordion.getPanes();
return panes == null ? 0 : panes.size();
}
@Override
protected TitledPane getModelItem(int row) {
final ObservableList<TitledPane> panes = accordion.getPanes();
if (panes == null) return null;
if (row < 0) return null;
return panes.get(row%panes.size());
}
@Override public void focusPrevious() {
if (getFocusedIndex() <= 0) {
focus(accordion.getPanes().size() - 1);
} else {
focus((getFocusedIndex() - 1)%accordion.getPanes().size());
}
}
@Override public void focusNext() {
if (getFocusedIndex() == -1) {
focus(0);
} else {
focus((getFocusedIndex() + 1)%accordion.getPanes().size());
}
}
}
}
