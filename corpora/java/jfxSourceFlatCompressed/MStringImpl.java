package com.javafx.experiments.importers.maya.values.impl;
import java.util.Iterator;
import com.javafx.experiments.importers.maya.types.MStringType;
import com.javafx.experiments.importers.maya.values.MString;
public class MStringImpl extends MDataImpl implements MString {
private String value;
public MStringImpl(MStringType type) {
super(type);
}
public void set(String str) {
value = str;
}
public String get() {
return value;
}
public void parse(Iterator<String> values) {
value = values.next();
}
public String toString() {
String result = getType().getName();
result += " " + value;
return result;
}
}
