package com.javafx.experiments.importers.maya.values.impl;
import java.util.Iterator;
import com.javafx.experiments.importers.maya.types.MFloat3ArrayType;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.MFloat3Array;
public class MFloat3ArrayImpl extends MDataImpl implements MFloat3Array {
private float[] data;
static class Parser {
private MFloat3Array array;
Parser(MFloat3Array array) {
this.array = array;
}
public void parse(Iterator<String> elements) {
int i = 0;
while (elements.hasNext()) {
array.set(
i++,
Float.parseFloat(elements.next()),
Float.parseFloat(elements.next()),
Float.parseFloat(elements.next()));
}
}
}
static class MFloat3ArraySlice extends MDataImpl implements MFloat3Array {
private MFloat3Array array;
private int base;
private int length;
MFloat3ArraySlice(
MFloat3Array array,
int base,
int length) {
super((MFloat3ArrayType) array.getType());
this.array = array;
this.base = base;
this.length = length;
}
public void setSize(int size) {
array.setSize(base + size);
}
public int getSize() {
return length;
}
public void set(int index, float x, float y, float z) {
if (index >= length) {
throw new ArrayIndexOutOfBoundsException(index);
}
array.set(base + index, x, y, z);
}
public float[] get() {
throw new RuntimeException("Probably shouldn't fetch the data behind a slice");
}
public void parse(Iterator<String> elements) {
new Parser(this).parse(elements);
}
}
public MFloat3ArrayImpl(MFloat3ArrayType type) {
super(type);
}
public void setSize(int size) {
if (data == null || 3 * size > data.length) {
float[] newdata = new float[3 * size];
if (data != null) {
System.arraycopy(data, 0, newdata, 0, data.length);
}
data = newdata;
}
}
public void set(int index, float x, float y, float z) {
data[3 * index + 0] = x;
data[3 * index + 1] = y;
data[3 * index + 2] = z;
}
public int getSize() {
return data == null ? 0 : data.length / 3;
}
public float[] get() {
return data;
}
public MData getData(int index) {
return getData(index, index + 1);
}
public MData getData(int start, int end) {
return new MFloat3ArraySlice(this, start, end - start + 1);
}
public void parse(Iterator<String> elements) {
new Parser(this).parse(elements);
}
public String toString() {
String result = getType().getName();
String sep = " ";
if (data != null) {
for (float f : data) {
result += sep;
result += f;
}
}
return result;
}
}
