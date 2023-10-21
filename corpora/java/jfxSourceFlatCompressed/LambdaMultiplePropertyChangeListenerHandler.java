package com.sun.javafx.scene.control;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
public final class LambdaMultiplePropertyChangeListenerHandler {
@SuppressWarnings("rawtypes")
private static final Consumer EMPTY_CONSUMER = e -> {};
private final Map<ObservableValue<?>, Consumer<ObservableValue<?>>> propertyReferenceMap;
private final ChangeListener<Object> propertyChangedListener;
private final WeakChangeListener<Object> weakPropertyChangedListener;
private final Map<Observable, Consumer<Observable>> observableReferenceMap;
private final InvalidationListener invalidationListener;
private final WeakInvalidationListener weakInvalidationListener;
private final Map<ObservableList<?>, Consumer<Change<?>>> observableListReferenceMap;
private final ListChangeListener<Object> listChangeListener;
private final WeakListChangeListener<Object> weakListChangeListener;
public LambdaMultiplePropertyChangeListenerHandler() {
this.propertyReferenceMap = new HashMap<>();
this.propertyChangedListener = (observable, oldValue, newValue) -> {
propertyReferenceMap.getOrDefault(observable, EMPTY_CONSUMER).accept(observable);
};
this.weakPropertyChangedListener = new WeakChangeListener<>(propertyChangedListener);
this.observableReferenceMap = new HashMap<>();
this.invalidationListener = obs -> {
observableReferenceMap.getOrDefault(obs, EMPTY_CONSUMER).accept(obs);
};
this.weakInvalidationListener = new WeakInvalidationListener(invalidationListener);
this.observableListReferenceMap = new IdentityHashMap<>();
this.listChangeListener = change -> {
observableListReferenceMap.getOrDefault(change.getList(), EMPTY_CONSUMER).accept(change);
};
this.weakListChangeListener = new WeakListChangeListener<>(listChangeListener);
}
public final void registerChangeListener(ObservableValue<?> property, Consumer<ObservableValue<?>> consumer) {
if (property == null || consumer == null) return;
if (!propertyReferenceMap.containsKey(property)) {
property.addListener(weakPropertyChangedListener);
}
propertyReferenceMap.merge(property, consumer, Consumer::andThen);
}
public final Consumer<ObservableValue<?>> unregisterChangeListeners(ObservableValue<?> property) {
if (property == null) return null;
property.removeListener(weakPropertyChangedListener);
return propertyReferenceMap.remove(property);
}
public final void registerInvalidationListener(Observable observable, Consumer<Observable> consumer) {
if (observable == null || consumer == null) return;
if (!observableReferenceMap.containsKey(observable)) {
observable.addListener(weakInvalidationListener);
}
observableReferenceMap.merge(observable, consumer, Consumer::andThen);
}
public final Consumer<Observable> unregisterInvalidationListeners(Observable observable) {
if (observable == null) return null;
observable.removeListener(weakInvalidationListener);
return observableReferenceMap.remove(observable);
}
public final void registerListChangeListener(ObservableList<?> list, Consumer<Change<?>> consumer) {
if (list == null || consumer == null) return;
if (!observableListReferenceMap.containsKey(list)) {
list.addListener(weakListChangeListener);
}
observableListReferenceMap.merge(list, consumer, Consumer::andThen);
}
public final Consumer<Change<?>> unregisterListChangeListeners(ObservableList<?> list) {
if (list == null) return null;
list.removeListener(weakListChangeListener);
return observableListReferenceMap.remove(list);
}
public void dispose() {
for (ObservableValue<?> value : propertyReferenceMap.keySet()) {
value.removeListener(weakPropertyChangedListener);
}
propertyReferenceMap.clear();
for (Observable value : observableReferenceMap.keySet()) {
value.removeListener(weakInvalidationListener);
}
observableReferenceMap.clear();
for (ObservableList<?> list : observableListReferenceMap.keySet()) {
list.removeListener(weakListChangeListener);
}
observableListReferenceMap.clear();
}
}
