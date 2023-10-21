package com.javafx.experiments.importers.maya.values.impl;
import java.util.Iterator;
import com.javafx.experiments.importers.maya.types.MBoolType;
import com.javafx.experiments.importers.maya.values.MBool;
public class MBoolImpl extends MDataImpl implements MBool {
private boolean value;
public MBoolImpl(MBoolType type) {
super(type);
}
public void set(boolean value) {
this.value = value;
}
public boolean get() {
return value;
}
public void parse(Iterator<String> values) {
String val = values.next();
if (val.equals("yes") ||
val.equals("true")) {
value = true;
} else {
value = false;
}
}
public String toString() {
String result = getType().getName();
result += " " + value;
return result;
}
}
