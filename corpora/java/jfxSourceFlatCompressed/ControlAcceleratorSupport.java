package com.sun.javafx.scene.control;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Control;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.KeyCombination;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
public class ControlAcceleratorSupport {
public static void addAcceleratorsIntoScene(ObservableList<MenuItem> items, Tab anchor) {
addAcceleratorsIntoScene(items, (Object)anchor);
}
public static void addAcceleratorsIntoScene(ObservableList<MenuItem> items, TableColumnBase<?,?> anchor) {
addAcceleratorsIntoScene(items, (Object)anchor);
}
public static void addAcceleratorsIntoScene(ObservableList<MenuItem> items, Node anchor) {
if (items == null ) {
return;
}
if (anchor == null) {
throw new IllegalArgumentException("Anchor cannot be null");
}
final Scene scene = anchor.getScene();
if (scene != null) {
doAcceleratorInstall(items, scene);
}
WeakReference<ChangeListener<Scene>> listenerW = sceneChangeListenerMap.get(anchor);
if (listenerW != null) {
ChangeListener<Scene> listener = listenerW.get();
if (listener != null) {
anchor.sceneProperty().removeListener(listener);
}
sceneChangeListenerMap.remove(anchor);
}
anchor.sceneProperty().addListener(getSceneChangeListener(anchor, items));
}
private static void addAcceleratorsIntoScene(ObservableList<MenuItem> items, Object anchor) {
if (anchor == null) {
throw new IllegalArgumentException("Anchor cannot be null");
}
final ReadOnlyObjectProperty<? extends Control> controlProperty = getControlProperty(anchor);
final Control control = controlProperty.get();
if (control == null) {
controlProperty.addListener(new InvalidationListener() {
@Override public void invalidated(Observable observable) {
final Control control = controlProperty.get();
if (control != null) {
controlProperty.removeListener(this);
addAcceleratorsIntoScene(items, control);
}
}
});
} else {
addAcceleratorsIntoScene(items, control);
}
}
private static Map<Object, WeakReference<ChangeListener<Scene>>> sceneChangeListenerMap = new WeakHashMap<>();
private static ChangeListener<Scene> getSceneChangeListener(Object anchor, ObservableList<MenuItem> items) {
WeakReference<ChangeListener<Scene>> sceneChangeListenerW = sceneChangeListenerMap.get(anchor);
ChangeListener<Scene> sceneChangeListener = sceneChangeListenerW == null ? null : sceneChangeListenerW.get();
if (sceneChangeListener == null) {
sceneChangeListener = (ov, oldScene, newScene) -> {
if (oldScene != null) {
removeAcceleratorsFromScene(items, oldScene);
}
if (newScene != null) {
doAcceleratorInstall(items, newScene);
}
};
sceneChangeListenerMap.put(anchor, new WeakReference<>(sceneChangeListener));
}
return sceneChangeListener;
}
private static void doAcceleratorInstall(final ObservableList<MenuItem> items, final Scene scene) {
items.addListener((ListChangeListener<MenuItem>) c -> {
while (c.next()) {
if (c.wasRemoved()) {
removeAcceleratorsFromScene(c.getRemoved(), scene);
}
if (c.wasAdded()) {
ControlAcceleratorSupport.doAcceleratorInstall(c.getAddedSubList(), scene);
}
}
});
doAcceleratorInstall((List<MenuItem>)items, scene);
}
private static void doAcceleratorInstall(final List<? extends MenuItem> items, final Scene scene) {
for (final MenuItem menuitem : items) {
if (menuitem instanceof Menu) {
doAcceleratorInstall(((Menu) menuitem).getItems(), scene);
} else {
if (menuitem.getAccelerator() != null) {
final Map<KeyCombination, Runnable> accelerators = scene.getAccelerators();
Runnable acceleratorRunnable = () -> {
if (menuitem.getOnMenuValidation() != null) {
Event.fireEvent(menuitem, new Event(MenuItem.MENU_VALIDATION_EVENT));
}
Menu target = menuitem.getParentMenu();
if(target!= null && target.getOnMenuValidation() != null) {
Event.fireEvent(target, new Event(MenuItem.MENU_VALIDATION_EVENT));
}
if (!menuitem.isDisable()) {
if (menuitem instanceof RadioMenuItem) {
((RadioMenuItem)menuitem).setSelected(!((RadioMenuItem)menuitem).isSelected());
}
else if (menuitem instanceof CheckMenuItem) {
((CheckMenuItem)menuitem).setSelected(!((CheckMenuItem)menuitem).isSelected());
}
menuitem.fire();
}
};
accelerators.put(menuitem.getAccelerator(), acceleratorRunnable);
}
menuitem.acceleratorProperty().addListener(getListener(scene, menuitem));
}
}
}
private static Map<MenuItem, WeakReference<ChangeListener<KeyCombination>>> changeListenerMap = new WeakHashMap<>();
private static ChangeListener<KeyCombination> getListener(final Scene scene, MenuItem menuItem) {
WeakReference<ChangeListener<KeyCombination>> listenerW = changeListenerMap.get(menuItem);
ChangeListener<KeyCombination> listener = listenerW == null ? null : listenerW.get();
if (listener == null) {
listener = (observable, oldValue, newValue) -> {
final Map<KeyCombination, Runnable> accelerators = scene.getAccelerators();
Runnable _acceleratorRunnable = accelerators.remove(oldValue);
if (newValue != null) {
accelerators.put(newValue, _acceleratorRunnable);
}
};
changeListenerMap.put(menuItem, new WeakReference<>(listener));
}
return listener;
}
public static void removeAcceleratorsFromScene(List<? extends MenuItem> items, Tab anchor) {
TabPane tabPane = anchor.getTabPane();
if (tabPane == null) return;
Scene scene = tabPane.getScene();
removeAcceleratorsFromScene(items, scene);
}
public static void removeAcceleratorsFromScene(List<? extends MenuItem> items, TableColumnBase<?,?> anchor) {
ReadOnlyObjectProperty<? extends Control> controlProperty = getControlProperty(anchor);
if (controlProperty == null) return;
Control control = controlProperty.get();
if (control == null) return;
Scene scene = control.getScene();
removeAcceleratorsFromScene(items, scene);
}
public static void removeAcceleratorsFromScene(List<? extends MenuItem> items, Node anchor) {
Scene scene = anchor.getScene();
if (scene == null) {
WeakReference<ChangeListener<Scene>> listenerW = sceneChangeListenerMap.get(anchor);
if (listenerW != null) {
ChangeListener<Scene> listener = listenerW.get();
if (listener != null) {
anchor.sceneProperty().removeListener(listener);
}
sceneChangeListenerMap.remove(anchor);
}
}
removeAcceleratorsFromScene(items, scene);
}
public static void removeAcceleratorsFromScene(List<? extends MenuItem> items, Scene scene) {
if (scene == null) {
return;
}
for (final MenuItem menuitem : items) {
if (menuitem instanceof Menu) {
removeAcceleratorsFromScene(((Menu)menuitem).getItems(), scene);
} else {
final Map<KeyCombination, Runnable> accelerators = scene.getAccelerators();
accelerators.remove(menuitem.getAccelerator());
WeakReference<ChangeListener<KeyCombination>> listenerW = changeListenerMap.get(menuitem);
if (listenerW != null) {
ChangeListener<KeyCombination> listener = listenerW.get();
if (listener != null) {
menuitem.acceleratorProperty().removeListener(listener);
}
changeListenerMap.remove(menuitem);
}
}
}
}
private static ReadOnlyObjectProperty<? extends Control> getControlProperty(Object obj) {
if (obj instanceof TableColumn) {
return ((TableColumn)obj).tableViewProperty();
} else if (obj instanceof TreeTableColumn) {
return ((TreeTableColumn)obj).treeTableViewProperty();
} else if (obj instanceof Tab) {
return ((Tab)obj).tabPaneProperty();
}
return null;
}
}
