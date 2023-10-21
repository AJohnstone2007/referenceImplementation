package javafx.scene.control;
import java.util.HashMap;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import com.sun.javafx.collections.VetoableListDecorator;
import com.sun.javafx.collections.TrackableObservableList;
public class ToggleGroup {
public ToggleGroup() {
}
public final ObservableList<Toggle> getToggles() {
return toggles;
}
private final ObservableList<Toggle> toggles = new VetoableListDecorator<Toggle>(new TrackableObservableList<Toggle>() {
@Override protected void onChanged(Change<Toggle> c) {
while (c.next()) {
final List<Toggle> addedToggles = c.getAddedSubList();
for (Toggle t : c.getRemoved()) {
if (t.isSelected()) {
selectToggle(null);
}
if (!addedToggles.contains(t)) {
t.setToggleGroup(null);
}
}
for (Toggle t: addedToggles) {
if (!ToggleGroup.this.equals(t.getToggleGroup())) {
if (t.getToggleGroup() != null) {
t.getToggleGroup().getToggles().remove(t);
}
t.setToggleGroup(ToggleGroup.this);
}
}
for (Toggle t : addedToggles) {
if (t.isSelected()) {
selectToggle(t);
break;
}
}
}
}
}) {
@Override protected void onProposedChange(List<Toggle> toBeAdded, int... indexes) {
for (Toggle t: toBeAdded) {
if (indexes[0] == 0 && indexes[1] == size()) {
break;
}
if (toggles.contains(t)) {
throw new IllegalArgumentException("Duplicate toggles are not allow in a ToggleGroup.");
}
}
}
};
private final ReadOnlyObjectWrapper<Toggle> selectedToggle = new ReadOnlyObjectWrapper<Toggle>() {
@Override public void set(final Toggle newSelectedToggle) {
if (isBound()) {
throw new java.lang.RuntimeException("A bound value cannot be set.");
}
final Toggle old = get();
if (old == newSelectedToggle) {
return;
}
if (setSelected(newSelectedToggle, true) ||
(newSelectedToggle != null && newSelectedToggle.getToggleGroup() == ToggleGroup.this) ||
(newSelectedToggle == null)) {
if (old == null || old.getToggleGroup() == ToggleGroup.this || !old.isSelected()) {
setSelected(old, false);
}
super.set(newSelectedToggle);
}
}
};
public final void selectToggle(Toggle value) { selectedToggle.set(value); }
public final Toggle getSelectedToggle() { return selectedToggle.get(); }
public final ReadOnlyObjectProperty<Toggle> selectedToggleProperty() { return selectedToggle.getReadOnlyProperty(); }
private boolean setSelected(Toggle toggle, boolean selected) {
if (toggle != null &&
toggle.getToggleGroup() == this &&
!toggle.selectedProperty().isBound()) {
toggle.setSelected(selected);
return true;
}
return false;
}
final void clearSelectedToggle() {
if (!selectedToggle.getValue().isSelected()) {
for (Toggle toggle: getToggles()) {
if (toggle.isSelected()) {
return;
}
}
}
selectedToggle.set(null);
}
private static final Object USER_DATA_KEY = new Object();
private ObservableMap<Object, Object> properties;
public final ObservableMap<Object, Object> getProperties() {
if (properties == null) {
properties = FXCollections.observableMap(new HashMap<Object, Object>());
}
return properties;
}
public boolean hasProperties() {
return properties != null && !properties.isEmpty();
}
public void setUserData(Object value) {
getProperties().put(USER_DATA_KEY, value);
}
public Object getUserData() {
return getProperties().get(USER_DATA_KEY);
}
}
