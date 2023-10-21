package com.sun.glass.ui.monocle;
import java.util.Arrays;
class IntSet {
private int[] elements = new int[4];
private int size = 0;
void addInt(int value) {
int i = getIndex(value);
if (i < 0) {
int insertionPoint = -1 - i;
if (size == elements.length) {
elements = Arrays.copyOf(elements, size * 2);
}
if (insertionPoint != size) {
System.arraycopy(elements, insertionPoint,
elements, insertionPoint + 1,
size - insertionPoint);
}
elements[insertionPoint] = value;
size ++;
}
}
void removeInt(int value) {
int i = getIndex(value);
if (i >= 0) {
if (i < size - 1) {
System.arraycopy(elements, i + 1, elements, i, size - i - 1);
}
size --;
}
}
boolean containsInt(int value) {
return getIndex(value) >= 0;
}
private int getIndex(int value) {
int i;
for (i = 0; i < size; i++) {
if (elements[i] == value) {
return i;
} else if (elements[i] > value) {
return -i - 1;
}
}
return -i - 1;
}
int get(int index) {
return elements[index];
}
void difference(IntSet dest, IntSet compared) {
int i = 0;
int j = 0;
while (i < size && j < compared.size) {
int a = elements[i];
int b = compared.elements[j];
if (a < b) {
dest.addInt(a);
i ++;
} else if (a > b) {
j ++;
} else {
i ++;
j ++;
}
}
while (i < size) {
dest.addInt(elements[i]);
i ++;
}
}
void clear() {
size = 0;
}
int size() {
return size;
}
boolean isEmpty() {
return size == 0;
}
void copyTo(IntSet target) {
if (target.elements.length < size) {
target.elements = Arrays.copyOf(elements, elements.length);
} else {
System.arraycopy(elements, 0, target.elements, 0, size);
}
target.size = size;
}
public boolean equals(IntSet set) {
if (set.size == size) {
for (int i = 0; i < size; i++) {
if (set.elements[i] != elements[i]) {
return false;
}
}
return true;
} else {
return false;
}
}
public boolean equals(Object o) {
if (o instanceof IntSet) {
return equals((IntSet) o);
} else {
return false;
}
}
public String toString() {
StringBuffer sb = new StringBuffer("IntSet[");
for (int i = 0; i < size; i++) {
sb.append(elements[i]);
if (i < size - 1) {
sb.append(",");
}
}
sb.append("]");
return sb.toString();
}
}
