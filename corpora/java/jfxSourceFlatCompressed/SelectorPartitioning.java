package com.sun.javafx.css;
import javafx.css.CompoundSelector;
import javafx.css.Selector;
import javafx.css.SimpleSelector;
import javafx.css.StyleClass;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
public final class SelectorPartitioning {
public SelectorPartitioning() {}
private final static class PartitionKey<K> {
private final K key;
private PartitionKey(K key) {
this.key = key;
}
@Override
public boolean equals(Object obj) {
if (obj == null) {
return false;
}
if (getClass() != obj.getClass()) {
return false;
}
final PartitionKey<K> other = (PartitionKey<K>) obj;
if (this.key != other.key && (this.key == null || !this.key.equals(other.key))) {
return false;
}
return true;
}
@Override
public int hashCode() {
int hash = 7;
hash = 71 * hash + (this.key != null ? this.key.hashCode() : 0);
return hash;
}
}
private static final class Partition {
private final PartitionKey key;
private final Map<PartitionKey, Slot> slots;
private List<Selector> selectors;
private Partition(PartitionKey key) {
this.key = key;
slots = new HashMap<PartitionKey,Slot>();
}
private void addSelector(Selector pair) {
if (selectors == null) {
selectors = new ArrayList<Selector>();
}
selectors.add(pair);
}
private Slot partition(PartitionKey id, Map<PartitionKey, Partition> map) {
Slot slot = slots.get(id);
if (slot == null) {
Partition partition = getPartition(id,map);
slot = new Slot(partition);
slots.put(id, slot);
}
return slot;
}
}
private static final class Slot {
private final Partition partition;
private final Map<PartitionKey, Slot> referents;
private List<Selector> selectors;
private Slot(Partition partition) {
this.partition = partition;
this.referents = new HashMap<PartitionKey, Slot>();
}
private void addSelector(Selector pair) {
if (selectors == null) {
selectors = new ArrayList<Selector>();
}
selectors.add(pair);
}
private Slot partition(PartitionKey id, Map<PartitionKey, Partition> map) {
Slot slot = referents.get(id);
if (slot == null) {
Partition p = getPartition(id, map);
slot = new Slot(p);
referents.put(id, slot);
}
return slot;
}
}
private final Map<PartitionKey, Partition> idMap = new HashMap<PartitionKey,Partition>();
private final Map<PartitionKey, Partition> typeMap = new HashMap<PartitionKey,Partition>();
private final Map<PartitionKey, Partition> styleClassMap = new HashMap<PartitionKey,Partition>();
private int ordinal;
public void reset() {
idMap.clear();
typeMap.clear();
styleClassMap.clear();
ordinal = 0;
}
private static Partition getPartition(PartitionKey id, Map<PartitionKey,Partition> map) {
Partition treeNode = map.get(id);
if (treeNode == null) {
treeNode = new Partition(id);
map.put(id, treeNode);
}
return treeNode;
}
private static final int ID_BIT = 4;
private static final int TYPE_BIT = 2;
private static final int STYLECLASS_BIT = 1;
private static final PartitionKey WILDCARD = new PartitionKey<String>("*");
public void partition(Selector selector) {
SimpleSelector simpleSelector = null;
if (selector instanceof CompoundSelector) {
final List<SimpleSelector> selectors = ((CompoundSelector)selector).getSelectors();
final int last = selectors.size()-1;
simpleSelector = selectors.get(last);
} else {
simpleSelector = (SimpleSelector)selector;
}
final String selectorId = simpleSelector.getId();
final boolean hasId =
(selectorId != null && selectorId.isEmpty() == false);
final PartitionKey idKey = hasId
? new PartitionKey(selectorId)
: null;
final String selectorType = simpleSelector.getName();
final boolean hasType =
(selectorType != null && selectorType.isEmpty() == false);
final PartitionKey typeKey = hasType
? new PartitionKey(selectorType)
: null;
final Set<StyleClass> selectorStyleClass = simpleSelector.getStyleClassSet();
final boolean hasStyleClass =
(selectorStyleClass != null && selectorStyleClass.size() > 0);
final PartitionKey styleClassKey = hasStyleClass
? new PartitionKey<Set<StyleClass>>(selectorStyleClass)
: null;
final int c =
(hasId ? ID_BIT : 0) | (hasType ? TYPE_BIT : 0) | (hasStyleClass ? STYLECLASS_BIT : 0);
Partition partition = null;
Slot slot = null;
selector.setOrdinal(ordinal++);
switch(c) {
case ID_BIT | TYPE_BIT | STYLECLASS_BIT:
case ID_BIT | TYPE_BIT:
partition = getPartition(idKey, idMap);
slot = partition.partition(typeKey, typeMap);
if ((c & STYLECLASS_BIT) == STYLECLASS_BIT) {
slot = slot.partition(styleClassKey, styleClassMap);
}
slot.addSelector(selector);
break;
case TYPE_BIT | STYLECLASS_BIT:
case TYPE_BIT:
partition = getPartition(typeKey, typeMap);
if ((c & STYLECLASS_BIT) == STYLECLASS_BIT) {
slot = partition.partition(styleClassKey, styleClassMap);
slot.addSelector(selector);
} else {
partition.addSelector(selector);
}
break;
case ID_BIT | STYLECLASS_BIT:
case ID_BIT:
case STYLECLASS_BIT:
default:
assert(false);
}
}
public List<Selector> match(String selectorId, String selectorType, Set<StyleClass> selectorStyleClass) {
final boolean hasId =
(selectorId != null && selectorId.isEmpty() == false);
final PartitionKey idKey = hasId
? new PartitionKey(selectorId)
: null;
final boolean hasType =
(selectorType != null && selectorType.isEmpty() == false);
final PartitionKey typeKey = hasType
? new PartitionKey(selectorType)
: null;
final boolean hasStyleClass =
(selectorStyleClass != null && selectorStyleClass.size() > 0);
final PartitionKey styleClassKey = hasStyleClass
? new PartitionKey<Set<StyleClass>>(selectorStyleClass)
: null;
int c =
(hasId ? ID_BIT : 0) | (hasType ? TYPE_BIT : 0) | (hasStyleClass ? STYLECLASS_BIT : 0);
Partition partition = null;
Slot slot = null;
List<Selector> selectors = new ArrayList<Selector>();
while (c != 0) {
switch(c) {
case ID_BIT | TYPE_BIT | STYLECLASS_BIT:
case ID_BIT | TYPE_BIT:
{
partition = idMap.get(idKey);
if (partition != null) {
if (partition.selectors != null) {
selectors.addAll(partition.selectors);
}
PartitionKey typePK = typeKey;
do {
slot = partition.slots.get(typePK);
if (slot != null) {
if (slot.selectors != null) {
selectors.addAll(slot.selectors);
}
if ((c & STYLECLASS_BIT) == STYLECLASS_BIT) {
Set<StyleClass> key = (Set<StyleClass>)styleClassKey.key;
for (Slot s : slot.referents.values()) {
if (s.selectors == null || s.selectors.isEmpty()) continue;
Set<StyleClass> other = (Set<StyleClass>)s.partition.key.key;
if (key.containsAll(other)) {
selectors.addAll(s.selectors);
}
}
}
}
typePK=WILDCARD.equals(typePK) == false ? WILDCARD : null;
} while(typePK != null);
}
c -= ID_BIT;
continue;
}
case ID_BIT | STYLECLASS_BIT:
case ID_BIT:
c -= ID_BIT;
break;
case TYPE_BIT | STYLECLASS_BIT:
case TYPE_BIT:
{
PartitionKey typePK = typeKey;
do {
partition = typeMap.get(typePK);
if (partition != null) {
if (partition.selectors != null) {
selectors.addAll(partition.selectors);
}
if ((c & STYLECLASS_BIT) == STYLECLASS_BIT) {
Set<StyleClass> key = (Set<StyleClass>)styleClassKey.key;
for (Slot s : partition.slots.values()) {
if (s.selectors == null || s.selectors.isEmpty()) continue;
Set<StyleClass> other = (Set<StyleClass>)s.partition.key.key;
if (key.containsAll(other)) {
selectors.addAll(s.selectors);
}
}
}
}
typePK=WILDCARD.equals(typePK) == false ? WILDCARD : null;
} while(typePK != null);
c -= TYPE_BIT;
continue;
}
case STYLECLASS_BIT:
c -= STYLECLASS_BIT;
break;
default:
assert(false);
}
}
Collections.sort(selectors, COMPARATOR);
return selectors;
}
private static final Comparator<Selector> COMPARATOR =
(o1, o2) -> o1.getOrdinal() - o2.getOrdinal();
}
