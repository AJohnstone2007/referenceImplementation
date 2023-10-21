package com.sun.prism.impl.packrect;
import com.sun.javafx.geom.Rectangle;
class Level {
int length;
int size;
private int sizeOffset;
private int lengthOffset;
Level(int length, int size, int sizeOffset) {
this.length = length;
this.size = size;
this.sizeOffset = sizeOffset;
}
boolean add(Rectangle rect, int x, int y, int requestedLength, int requestedSize, boolean vertical) {
if (lengthOffset + requestedLength <= length && requestedSize <= size) {
if (vertical) {
rect.x = sizeOffset;
rect.y = lengthOffset;
} else {
rect.x = lengthOffset;
rect.y = sizeOffset;
}
lengthOffset += requestedLength;
rect.x += x;
rect.y += y;
return true;
}
return false;
}
}
