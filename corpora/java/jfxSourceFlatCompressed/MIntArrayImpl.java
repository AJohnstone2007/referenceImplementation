package com.javafx.experiments.importers.maya.values.impl;
import java.util.Iterator;
import com.javafx.experiments.importers.maya.types.MIntArrayType;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.MIntArray;
public class MIntArrayImpl extends MDataImpl implements MIntArray {
private int[] data;
static class Parser {
private MIntArray array;
Parser(MIntArray array) {
this.array = array;
}
public void parse(Iterator<String> elements) {
int i = 0;
while (elements.hasNext()) {
array.set(
i++,
Integer.parseInt(elements.next()));
}
}
}
static class MIntArraySlice extends MDataImpl implements MIntArray {
private MIntArray array;
private int base;
private int length;
MIntArraySlice(
MIntArray array,
int base,
int length) {
super((MIntArrayType) array.getType());
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
public void set(int index, int x) {
if (index >= length) {
throw new ArrayIndexOutOfBoundsException(index);
}
array.set(base + index, x);
}
public int[] get() {
throw new RuntimeException("Probably shouldn't fetch the data behind a slice");
}
public int get(int index) {
return array.get(base + index);
}
public void parse(Iterator<String> elements) {
new Parser(this).parse(elements);
}
}
public MIntArrayImpl(MIntArrayType type) {
super(type);
}
public void setSize(int size) {
if (data == null || size > data.length) {
int[] newdata = new int[size];
if (data != null) {
System.arraycopy(data, 0, newdata, 0, data.length);
}
data = newdata;
}
}
public void set(int index, int x) {
setSize(index + 1);
data[index] = x;
}
public int[] get() {
return data;
}
public int get(int index) {
return data[index];
}
public MData getData(int index) {
return getData(index, index + 1);
}
public int getSize() {
return data == null ? 0 : data.length;
}
public MData getData(int start, int end) {
return new MIntArraySlice(this, start, end - start + 1);
}
public void parse(Iterator<String> elements) {
new Parser(this).parse(elements);
}
public String toString() {
String result = getType().getName();
String sep = " ";
if (data != null) {
for (int f : data) {
result += sep;
result += f;
}
}
return result;
}
}
