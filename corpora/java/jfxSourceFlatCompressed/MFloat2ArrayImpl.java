package com.javafx.experiments.importers.maya.values.impl;
import java.util.Iterator;
import com.javafx.experiments.importers.maya.types.MFloat2ArrayType;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.MFloat2Array;
public class MFloat2ArrayImpl extends MDataImpl implements MFloat2Array {
private float[] data;
static class Parser {
private MFloat2Array array;
Parser(MFloat2Array array) {
this.array = array;
}
public void parse(Iterator<String> elements) {
int i = 0;
while (elements.hasNext()) {
array.set(
i++,
Float.parseFloat(elements.next()),
Float.parseFloat(elements.next()));
}
}
}
static class MFloat2ArraySlice extends MDataImpl implements MFloat2Array {
private MFloat2Array array;
private int base;
private int length;
MFloat2ArraySlice(
MFloat2Array array,
int base,
int length) {
super((MFloat2ArrayType) array.getType());
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
public void set(int index, float x, float y) {
if (index >= length) {
throw new ArrayIndexOutOfBoundsException(index);
}
array.set(base + index, x, y);
}
public float[] get() {
throw new RuntimeException("Probably shouldn't fetch the data behind a slice");
}
public void parse(Iterator<String> elements) {
new Parser(this).parse(elements);
}
}
public MFloat2ArrayImpl(MFloat2ArrayType type) {
super(type);
}
public void setSize(int size) {
if (data == null || 2 * size > data.length) {
float[] newdata = new float[2 * size];
if (data != null) {
System.arraycopy(data, 0, newdata, 0, data.length);
}
data = newdata;
}
}
public void set(int index, float x, float y) {
data[2 * index + 0] = x;
data[2 * index + 1] = y;
}
public int getSize() {
return data == null ? 0 : data.length / 2;
}
public float[] get() {
return data;
}
public MData getData(int index) {
return getData(index, index + 1);
}
public MData getData(int start, int end) {
return new MFloat2ArraySlice(this, start, end - start + 1);
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
