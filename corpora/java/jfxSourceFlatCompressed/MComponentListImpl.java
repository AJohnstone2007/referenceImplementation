package com.javafx.experiments.importers.maya.values.impl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.javafx.experiments.importers.maya.types.MComponentListType;
import com.javafx.experiments.importers.maya.values.MComponentList;
public class MComponentListImpl extends MDataImpl implements MComponentList {
private List<Component> components = new ArrayList<Component>();
public MComponentListImpl(MComponentListType type) {
super(type);
}
public void set(List<Component> value) {
components = value;
}
public List<Component> get() {
return components;
}
public void parse(Iterator<String> values) {
try {
int num = Integer.parseInt(values.next());
for (int i = 0; i < num; i++) {
components.add(Component.parse(values.next()));
}
} catch (Exception e) {
e.printStackTrace();
}
}
public String toString() {
StringBuffer res = new StringBuffer();
res.append(getType().getName());
for (Component c : components) {
res.append(" ");
res.append(c);
}
return res.toString();
}
}
