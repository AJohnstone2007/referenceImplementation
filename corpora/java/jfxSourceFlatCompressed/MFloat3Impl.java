package com.javafx.experiments.importers.maya.values.impl;
import java.util.Iterator;
import com.javafx.experiments.importers.maya.types.MFloat3Type;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.MFloat;
import com.javafx.experiments.importers.maya.values.MFloat3;
public class MFloat3Impl extends MDataImpl implements MFloat3 {
private float[] data = new float[3];
class MFloat3Component extends MDataImpl implements MFloat {
private int index;
MFloat3Component(int index) {
super(MFloat3Impl.this.getEnv().findDataType("float"));
this.index = index;
}
public void set(float value) {
data[index] = value;
}
public float get() {
return data[index];
}
public void parse(Iterator<String> elements) {
data[index] = Float.parseFloat(elements.next());
}
}
public MFloat3Impl(MFloat3Type type) {
super(type);
}
public void set(float x, float y, float z) {
data[0] = x; data[1] = y; data[2] = z;
}
public float[] get() {
return data;
}
public float getX() {
return data[0];
}
public float getY() {
return data[1];
}
public float getZ() {
return data[2];
}
public float get(int index) {
return data[index];
}
public void parse(Iterator<String> elements) {
for (int i = 0; i < 3; i++) {
data[i] = Float.parseFloat(elements.next());
}
}
public MData getData(int index) {
return new MFloat3Component(index);
}
public MData getData(String name) {
if (name.equals("x")) {
return getData(0);
} else if (name.equals("y")) {
return getData(1);
} else if (name.equals("z")) {
return getData(2);
}
return super.getData(name);
}
}
