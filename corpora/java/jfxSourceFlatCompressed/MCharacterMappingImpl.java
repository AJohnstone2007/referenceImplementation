package com.javafx.experiments.importers.maya.values.impl;
import java.util.Iterator;
import com.javafx.experiments.importers.maya.types.MCharacterMappingType;
import com.javafx.experiments.importers.maya.values.MCharacterMapping;
public class MCharacterMappingImpl extends MDataImpl implements MCharacterMapping {
class EntryImpl implements Entry {
public String getKey() {
return key;
}
public int getSourceIndex() {
return sourceIndex;
}
public int getTargetIndex() {
return targetIndex;
}
String key;
int sourceIndex;
int targetIndex;
public EntryImpl(String key, int sourceIndex, int targetIndex) {
this.key = key; this.sourceIndex = sourceIndex; this.targetIndex = targetIndex;
}
}
Entry[] entries;
public MCharacterMappingImpl(MCharacterMappingType type) {
super(type);
}
public Entry[] getMapping() {
return entries;
}
public void parse(Iterator<String> values) {
int count = Integer.parseInt(values.next());
entries = new Entry[count];
for (int i = 0; i < count; i++) {
String k = values.next();
int i1 = Integer.parseInt(values.next());
int i2 = Integer.parseInt(values.next());
entries[i] = new EntryImpl(k, i1, i2);
}
}
}
