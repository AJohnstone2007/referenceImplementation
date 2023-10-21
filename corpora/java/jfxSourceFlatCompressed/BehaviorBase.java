package com.sun.javafx.scene.control.behavior;
import javafx.scene.Node;
import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.inputmap.InputMap.Mapping;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
public abstract class BehaviorBase<N extends Node> {
private final N node;
private final List<Mapping<?>> installedDefaultMappings;
private final List<Runnable> childInputMapDisposalHandlers;
public BehaviorBase(N node) {
this.node = node;
this.installedDefaultMappings = new ArrayList<>();
this.childInputMapDisposalHandlers = new ArrayList<>();
}
public abstract InputMap<N> getInputMap();
public final N getNode() {
return node;
}
public void dispose() {
for (Mapping<?> mapping : installedDefaultMappings) {
getInputMap().getMappings().remove(mapping);
}
for (Runnable r : childInputMapDisposalHandlers) {
r.run();
}
}
protected void addDefaultMapping(List<Mapping<?>> newMapping) {
addDefaultMapping(getInputMap(), newMapping.toArray(new Mapping[newMapping.size()]));
}
protected void addDefaultMapping(Mapping<?>... newMapping) {
addDefaultMapping(getInputMap(), newMapping);
}
protected void addDefaultMapping(InputMap<N> inputMap, Mapping<?>... newMapping) {
List<Mapping<?>> existingMappings = new ArrayList<>(inputMap.getMappings());
for (Mapping<?> mapping : newMapping) {
if (existingMappings.contains(mapping)) continue;
inputMap.getMappings().add(mapping);
installedDefaultMappings.add(mapping);
}
}
protected <T extends Node> void addDefaultChildMap(InputMap<T> parentInputMap, InputMap<T> newChildInputMap) {
parentInputMap.getChildInputMaps().add(newChildInputMap);
childInputMapDisposalHandlers.add(() -> parentInputMap.getChildInputMaps().remove(newChildInputMap));
}
protected InputMap<N> createInputMap() {
return new InputMap<>(node);
}
protected void removeMapping(Object key) {
InputMap<?> inputMap = getInputMap();
inputMap.lookupMapping(key).ifPresent(mapping -> {
inputMap.getMappings().remove(mapping);
installedDefaultMappings.remove(mapping);
});
}
void rtl(Node node, Runnable rtlMethod, Runnable nonRtlMethod) {
switch(node.getEffectiveNodeOrientation()) {
case RIGHT_TO_LEFT: rtlMethod.run(); break;
default: nonRtlMethod.run(); break;
}
}
<T> void rtl(Node node, T object, Consumer<T> rtlMethod, Consumer<T> nonRtlMethod) {
switch(node.getEffectiveNodeOrientation()) {
case RIGHT_TO_LEFT: rtlMethod.accept(object); break;
default: nonRtlMethod.accept(object); break;
}
}
boolean isRTL(Node n) {
switch(n.getEffectiveNodeOrientation()) {
case RIGHT_TO_LEFT: return true;
default: return false;
}
}
}
