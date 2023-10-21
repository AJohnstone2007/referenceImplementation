package com.javafx.experiments.importers.maya.values.impl;
import java.util.Iterator;
import com.javafx.experiments.importers.maya.types.MIntType;
import com.javafx.experiments.importers.maya.values.MInt;
public class MIntImpl extends MDataImpl implements MInt {
private int value;
public MIntImpl(MIntType type) {
super(type);
}
public void set(int value) {
this.value = value;
}
public int get() {
return value;
}
public void parse(Iterator<String> values) {
String val = values.next().toLowerCase();
value = Integer.parseInt(val);
}
public String toString() {
String result = getType().getName();
result += " " + value;
return result;
}
}
