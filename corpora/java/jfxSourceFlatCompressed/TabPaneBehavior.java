package com.sun.javafx.scene.control.behavior;
import com.sun.javafx.scene.control.inputmap.InputMap;
import javafx.event.Event;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.*;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import java.util.List;
import static javafx.scene.input.KeyCode.*;
import static com.sun.javafx.scene.control.inputmap.InputMap.KeyMapping;
import static com.sun.javafx.scene.control.inputmap.InputMap.MouseMapping;
public class TabPaneBehavior extends BehaviorBase<TabPane> {
private final InputMap<TabPane> tabPaneInputMap;
public TabPaneBehavior(TabPane tabPane) {
super(tabPane);
tabPaneInputMap = createInputMap();
addDefaultMapping(tabPaneInputMap,
new KeyMapping(UP, e -> selectPreviousTab()),
new KeyMapping(DOWN, e -> selectNextTab()),
new KeyMapping(LEFT, e -> rtl(tabPane, this::selectNextTab, this::selectPreviousTab)),
new KeyMapping(RIGHT, e -> rtl(tabPane, this::selectPreviousTab, this::selectNextTab)),
new KeyMapping(HOME, e -> {
if (getNode().isFocused()) {
moveSelection(-1, 1);
}
}),
new KeyMapping(END, e -> {
if (getNode().isFocused()) {
moveSelection(getNode().getTabs().size(), -1);
}
}),
new KeyMapping(new KeyBinding(PAGE_UP).ctrl(), e -> selectPreviousTab()),
new KeyMapping(new KeyBinding(PAGE_DOWN).ctrl(), e -> selectNextTab()),
new KeyMapping(new KeyBinding(TAB).ctrl(), e -> selectNextTab()),
new KeyMapping(new KeyBinding(TAB).ctrl().shift(), e -> selectPreviousTab()),
new MouseMapping(MouseEvent.MOUSE_PRESSED, e -> getNode().requestFocus())
);
}
@Override public InputMap<TabPane> getInputMap() {
return tabPaneInputMap;
}
public void selectTab(Tab tab) {
getNode().getSelectionModel().select(tab);
}
public boolean canCloseTab(Tab tab) {
Event event = new Event(tab,tab,Tab.TAB_CLOSE_REQUEST_EVENT);
Event.fireEvent(tab, event);
return ! event.isConsumed();
}
public void closeTab(Tab tab) {
TabPane tabPane = getNode();
int index = tabPane.getTabs().indexOf(tab);
if (index != -1) {
tabPane.getTabs().remove(index);
}
if (tab.getOnClosed() != null) {
Event.fireEvent(tab, new Event(Tab.CLOSED_EVENT));
}
}
public void selectNextTab() {
moveSelection(1);
}
public void selectPreviousTab() {
moveSelection(-1);
}
private void moveSelection(int delta) {
moveSelection(getNode().getSelectionModel().getSelectedIndex(), delta);
}
private void moveSelection(int startIndex, int delta) {
final TabPane tabPane = getNode();
if (tabPane.getTabs().isEmpty()) return;
int tabIndex = findValidTab(startIndex, delta);
if (tabIndex > -1) {
final SelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
selectionModel.select(tabIndex);
}
tabPane.requestFocus();
}
private int findValidTab(int startIndex, int delta) {
final TabPane tabPane = getNode();
final List<Tab> tabs = tabPane.getTabs();
final int max = tabs.size();
int index = startIndex;
do {
index = nextIndex(index + delta, max);
Tab tab = tabs.get(index);
if (tab != null && !tab.isDisable()) {
return index;
}
} while (index != startIndex);
return -1;
}
private int nextIndex(int value, int max) {
final int min = 0;
int r = value % max;
if (r > min && max < min) {
r = r + max - min;
} else if (r < min && max > min) {
r = r + max - min;
}
return r;
}
}
