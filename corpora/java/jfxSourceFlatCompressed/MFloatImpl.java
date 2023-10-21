package com.javafx.experiments.importers.maya.values.impl;
import java.util.Iterator;
import com.javafx.experiments.importers.maya.types.MFloatType;
import com.javafx.experiments.importers.maya.values.MFloat;
public class MFloatImpl extends MDataImpl implements MFloat {
float value;
public MFloatImpl(MFloatType type) {
super(type);
}
public void set(float value) {
this.value = value;
}
public float get() {
return value;
}
public void parse(Iterator<String> values) {
String val = values.next().toLowerCase();
value = Float.parseFloat(val);
}
public String toString() {
String result = getType().getName();
result += " " + value;
return result;
}
}
