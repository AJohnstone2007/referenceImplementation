package com.sun.javafx.scene.control.inputmap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
public class InputMap<N extends Node> implements EventHandler<Event> {
private final N node;
private final ObservableList<InputMap<N>> childInputMaps;
private final ObservableList<Mapping<?>> mappings;
private final Map<EventType<?>, List<EventHandler<? super Event>>> installedEventHandlers;
private final Map<EventType, List<Mapping>> eventTypeMappings;
public InputMap(N node) {
if (node == null) {
throw new IllegalArgumentException("Node can not be null");
}
this.node = node;
this.eventTypeMappings = new HashMap<>();
this.installedEventHandlers = new HashMap<>();
this.mappings = FXCollections.observableArrayList();
mappings.addListener((ListChangeListener<Mapping<?>>) c -> {
while (c.next()) {
if (c.wasRemoved()) {
for (Mapping<?> mapping : c.getRemoved()) {
removeMapping(mapping);
}
}
if (c.wasAdded()) {
List<Mapping<?>> toRemove = new ArrayList<>();
for (Mapping<?> mapping : c.getAddedSubList()) {
if (mapping == null) {
toRemove.add(null);
} else {
addMapping(mapping);
}
}
if (!toRemove.isEmpty()) {
getMappings().removeAll(toRemove);
throw new IllegalArgumentException("Null mappings not permitted");
}
}
}
});
childInputMaps = FXCollections.observableArrayList();
childInputMaps.addListener((ListChangeListener<InputMap<N>>) c -> {
while (c.next()) {
if (c.wasRemoved()) {
for (InputMap<N> map : c.getRemoved()) {
map.setParentInputMap(null);
}
}
if (c.wasAdded()) {
List<InputMap<N>> toRemove = new ArrayList<>();
for (InputMap<N> map : c.getAddedSubList()) {
if (map.getNode() != getNode()) {
toRemove.add(map);
} else {
map.setParentInputMap(this);
}
}
if (!toRemove.isEmpty()) {
getChildInputMaps().removeAll(toRemove);
throw new IllegalArgumentException("Child InputMap intances need to share a common Node object");
}
}
}
});
}
private ReadOnlyObjectWrapper<InputMap<N>> parentInputMap = new ReadOnlyObjectWrapper<InputMap<N>>(this, "parentInputMap") {
@Override protected void invalidated() {
reprocessAllMappings();
}
};
private final void setParentInputMap(InputMap<N> value) { parentInputMap.set(value); }
private final InputMap<N> getParentInputMap() {return parentInputMap.get(); }
private final ReadOnlyObjectProperty<InputMap<N>> parentInputMapProperty() { return parentInputMap.getReadOnlyProperty(); }
private ObjectProperty<Predicate<? extends Event>> interceptor = new SimpleObjectProperty<>(this, "interceptor");
public final Predicate<? extends Event> getInterceptor() {
return interceptor.get();
}
public final void setInterceptor(Predicate<? extends Event> value) {
interceptor.set(value);
}
public final ObjectProperty<Predicate<? extends Event>> interceptorProperty() {
return interceptor;
}
public final N getNode() {
return node;
}
public ObservableList<Mapping<?>> getMappings() {
return mappings;
}
public ObservableList<InputMap<N>> getChildInputMaps() {
return childInputMaps;
}
public void dispose() {
for (InputMap<N> childInputMap : getChildInputMaps()) {
childInputMap.dispose();
}
removeAllEventHandlers();
getMappings().clear();
}
@Override public void handle(Event e) {
if (e == null || e.isConsumed()) return;
List<Mapping<?>> mappings = lookup(e, true);
for (Mapping<?> mapping : mappings) {
EventHandler eventHandler = mapping.getEventHandler();
if (eventHandler != null) {
eventHandler.handle(e);
}
if (mapping.isAutoConsume()) {
e.consume();
}
if (e.isConsumed()) {
break;
}
}
}
public Optional<Mapping<?>> lookupMapping(Object mappingKey) {
if (mappingKey == null) {
return Optional.empty();
}
List<Mapping<?>> mappings = lookupMappingKey(mappingKey);
for (int i = 0; i < getChildInputMaps().size(); i++) {
InputMap<N> childInputMap = getChildInputMaps().get(i);
List<Mapping<?>> childMappings = childInputMap.lookupMappingKey(mappingKey);
mappings.addAll(0, childMappings);
}
return mappings.size() > 0 ? Optional.of(mappings.get(0)) : Optional.empty();
}
private List<Mapping<?>> lookupMappingKey(Object mappingKey) {
return getMappings().stream()
.filter(mapping -> !mapping.isDisabled())
.filter(mapping -> mappingKey.equals(mapping.getMappingKey()))
.collect(Collectors.toList());
}
private List<Mapping<?>> lookup(Event event, boolean testInterceptors) {
if (testInterceptors) {
boolean interceptorsApplies = testInterceptor(event, getInterceptor());
if (interceptorsApplies) {
return Collections.emptyList();
}
}
List<Mapping<?>> mappings = new ArrayList<>();
int minSpecificity = 0;
List<Pair<Integer, Mapping<?>>> results = lookupMappingAndSpecificity(event, minSpecificity);
if (! results.isEmpty()) {
minSpecificity = results.get(0).getKey();
mappings.addAll(results.stream().map(pair -> pair.getValue()).collect(Collectors.toList()));
}
for (int i = 0; i < getChildInputMaps().size(); i++) {
InputMap childInputMap = getChildInputMaps().get(i);
minSpecificity = scanRecursively(childInputMap, event, testInterceptors, minSpecificity, mappings);
}
return mappings;
}
private int scanRecursively(InputMap<?> inputMap, Event event, boolean testInterceptors, int minSpecificity, List<Mapping<?>> mappings) {
if (testInterceptors) {
boolean interceptorsApplies = testInterceptor(event, inputMap.getInterceptor());
if (interceptorsApplies) {
return minSpecificity;
}
}
List<Pair<Integer, Mapping<?>>> childResults = inputMap.lookupMappingAndSpecificity(event, minSpecificity);
if (!childResults.isEmpty()) {
int specificity = childResults.get(0).getKey();
List<Mapping<?>> childMappings = childResults.stream()
.map(pair -> pair.getValue())
.collect(Collectors.toList());
if (specificity == minSpecificity) {
mappings.addAll(0, childMappings);
} else if (specificity > minSpecificity) {
mappings.clear();
minSpecificity = specificity;
mappings.addAll(childMappings);
}
}
for (int i = 0; i < inputMap.getChildInputMaps().size(); i++) {
minSpecificity = scanRecursively(inputMap.getChildInputMaps().get(i), event, testInterceptors, minSpecificity, mappings);
}
return minSpecificity;
}
private InputMap<N> getRootInputMap() {
InputMap<N> rootInputMap = this;
while (true) {
if (rootInputMap == null) break;
InputMap<N> parentInputMap = rootInputMap.getParentInputMap();
if (parentInputMap == null) break;
rootInputMap = parentInputMap;
}
return rootInputMap;
}
private void addMapping(Mapping<?> mapping) {
InputMap<N> rootInputMap = getRootInputMap();
rootInputMap.addEventHandler(mapping.eventType);
EventType<?> et = mapping.getEventType();
List<Mapping> _eventTypeMappings = this.eventTypeMappings.computeIfAbsent(et, f -> new ArrayList<>());
_eventTypeMappings.add(mapping);
}
private void removeMapping(Mapping<?> mapping) {
EventType<?> et = mapping.getEventType();
if (this.eventTypeMappings.containsKey(et)) {
List<?> _eventTypeMappings = this.eventTypeMappings.get(et);
_eventTypeMappings.remove(mapping);
}
}
private void addEventHandler(EventType et) {
List<EventHandler<? super Event>> eventHandlers =
installedEventHandlers.computeIfAbsent(et, f -> new ArrayList<>());
final EventHandler<? super Event> eventHandler = this::handle;
if (eventHandlers.isEmpty()) {
node.addEventHandler(et, eventHandler);
}
eventHandlers.add(eventHandler);
}
private void removeAllEventHandlers() {
for (EventType<?> et : installedEventHandlers.keySet()) {
List<EventHandler<? super Event>> handlers = installedEventHandlers.get(et);
for (EventHandler<? super Event> handler : handlers) {
node.removeEventHandler(et, handler);
}
}
}
private void reprocessAllMappings() {
removeAllEventHandlers();
this.mappings.stream().forEach(this::addMapping);
for (InputMap<N> child : getChildInputMaps()) {
child.reprocessAllMappings();
}
}
private List<Pair<Integer, Mapping<?>>> lookupMappingAndSpecificity(final Event event, final int minSpecificity) {
int _minSpecificity = minSpecificity;
List<Mapping> mappings = this.eventTypeMappings.getOrDefault(event.getEventType(), Collections.emptyList());
List<Pair<Integer, Mapping<?>>> result = new ArrayList<>();
for (Mapping mapping : mappings) {
if (mapping.isDisabled()) continue;
boolean interceptorsApplies = testInterceptor(event, mapping.getInterceptor());
if (interceptorsApplies) {
continue;
}
int specificity = mapping.getSpecificity(event);
if (specificity > 0 && specificity == _minSpecificity) {
result.add(new Pair<>(specificity, mapping));
} else if (specificity > _minSpecificity) {
result.clear();
result.add(new Pair<>(specificity, mapping));
_minSpecificity = specificity;
}
}
return result;
}
private boolean testInterceptor(Event e, Predicate interceptor) {
return interceptor != null && interceptor.test(e);
}
public static abstract class Mapping<T extends Event> {
private final EventType<T> eventType;
private final EventHandler<T> eventHandler;
public Mapping(final EventType<T> eventType, final EventHandler<T> eventHandler) {
this.eventType = eventType;
this.eventHandler = eventHandler;
}
public abstract int getSpecificity(Event event);
private BooleanProperty disabled = new SimpleBooleanProperty(this, "disabled", false);
public final void setDisabled(boolean value) { disabled.set(value); }
public final boolean isDisabled() {return disabled.get(); }
public final BooleanProperty disabledProperty() { return disabled; }
private BooleanProperty autoConsume = new SimpleBooleanProperty(this, "autoConsume", true);
public final void setAutoConsume(boolean value) { autoConsume.set(value); }
public final boolean isAutoConsume() {return autoConsume.get(); }
public final BooleanProperty autoConsumeProperty() { return autoConsume; }
public final EventType<T> getEventType() {
return eventType;
}
public final EventHandler<T> getEventHandler() {
return eventHandler;
}
private ObjectProperty<Predicate<? extends Event>> interceptor = new SimpleObjectProperty<>(this, "interceptor");
public final Predicate<? extends Event> getInterceptor() {
return interceptor.get();
}
public final void setInterceptor(Predicate<? extends Event> value) {
interceptor.set(value);
}
public final ObjectProperty<Predicate<? extends Event>> interceptorProperty() {
return interceptor;
}
public Object getMappingKey() {
return eventType;
}
@Override public boolean equals(Object o) {
if (this == o) return true;
if (!(o instanceof Mapping)) return false;
Mapping that = (Mapping) o;
if (eventType != null ? !eventType.equals(that.getEventType()) : that.getEventType() != null) return false;
return true;
}
@Override public int hashCode() {
return eventType != null ? eventType.hashCode() : 0;
}
}
public static class KeyMapping extends Mapping<KeyEvent> {
private final KeyBinding keyBinding;
public KeyMapping(final KeyCode keyCode, final EventHandler<KeyEvent> eventHandler) {
this(new KeyBinding(keyCode), eventHandler);
}
public KeyMapping(final KeyCode keyCode, final EventType<KeyEvent> eventType, final EventHandler<KeyEvent> eventHandler) {
this(new KeyBinding(keyCode, eventType), eventHandler);
}
public KeyMapping(KeyBinding keyBinding, final EventHandler<KeyEvent> eventHandler) {
this(keyBinding, eventHandler, null);
}
public KeyMapping(KeyBinding keyBinding, final EventHandler<KeyEvent> eventHandler, Predicate<KeyEvent> interceptor) {
super(keyBinding == null ? null : keyBinding.getType(), eventHandler);
if (keyBinding == null) {
throw new IllegalArgumentException("KeyMapping keyBinding constructor argument can not be null");
}
this.keyBinding = keyBinding;
setInterceptor(interceptor);
}
@Override public Object getMappingKey() {
return keyBinding;
}
@Override public int getSpecificity(Event e) {
if (isDisabled()) return 0;
if (!(e instanceof KeyEvent)) return 0;
return keyBinding.getSpecificity((KeyEvent)e);
}
@Override public boolean equals(Object o) {
if (this == o) return true;
if (!(o instanceof KeyMapping)) return false;
if (!super.equals(o)) return false;
KeyMapping that = (KeyMapping) o;
return keyBinding.equals(that.keyBinding);
}
@Override public int hashCode() {
return Objects.hash(keyBinding);
}
}
public static class MouseMapping extends Mapping<MouseEvent> {
public MouseMapping(final EventType<MouseEvent> eventType, final EventHandler<MouseEvent> eventHandler) {
super(eventType, eventHandler);
if (eventType == null) {
throw new IllegalArgumentException("MouseMapping eventType constructor argument can not be null");
}
}
@Override public int getSpecificity(Event e) {
if (isDisabled()) return 0;
if (!(e instanceof MouseEvent)) return 0;
EventType<MouseEvent> et = getEventType();
int s = 0;
if (e.getEventType() == MouseEvent.MOUSE_CLICKED && et != MouseEvent.MOUSE_CLICKED) return 0; else s++;
if (e.getEventType() == MouseEvent.MOUSE_DRAGGED && et != MouseEvent.MOUSE_DRAGGED) return 0; else s++;
if (e.getEventType() == MouseEvent.MOUSE_ENTERED && et != MouseEvent.MOUSE_ENTERED) return 0; else s++;
if (e.getEventType() == MouseEvent.MOUSE_ENTERED_TARGET && et != MouseEvent.MOUSE_ENTERED_TARGET) return 0; else s++;
if (e.getEventType() == MouseEvent.MOUSE_EXITED && et != MouseEvent.MOUSE_EXITED) return 0; else s++;
if (e.getEventType() == MouseEvent.MOUSE_EXITED_TARGET && et != MouseEvent.MOUSE_EXITED_TARGET) return 0; else s++;
if (e.getEventType() == MouseEvent.MOUSE_MOVED && et != MouseEvent.MOUSE_MOVED) return 0; else s++;
if (e.getEventType() == MouseEvent.MOUSE_PRESSED && et != MouseEvent.MOUSE_PRESSED) return 0; else s++;
if (e.getEventType() == MouseEvent.MOUSE_RELEASED && et != MouseEvent.MOUSE_RELEASED) return 0; else s++;
return s;
}
}
public static class KeyMappingInterceptor implements Predicate<Event> {
private final KeyBinding keyBinding;
public KeyMappingInterceptor(KeyBinding keyBinding) {
this.keyBinding = keyBinding;
}
public boolean test(Event event) {
if (!(event instanceof KeyEvent)) return false;
return KeyBinding.toKeyBinding((KeyEvent)event).equals(keyBinding);
}
}
public static class MouseMappingInterceptor implements Predicate<Event> {
private final EventType<MouseEvent> eventType;
public MouseMappingInterceptor(EventType<MouseEvent> eventType) {
this.eventType = eventType;
}
public boolean test(Event event) {
if (!(event instanceof MouseEvent)) return false;
return event.getEventType() == this.eventType;
}
}
}
