package com.sun.prism.impl.packrect;
import com.sun.javafx.geom.Rectangle;
import com.sun.prism.Texture;
import java.util.ArrayList;
import java.util.List;
public class RectanglePacker {
private Texture backingStore;
private List<Level> levels = new ArrayList<Level>(150);
private static final int MIN_SIZE = 8;
private static final int ROUND_UP = 4;
private int recentUsedLevelIndex = 0;
private int length;
private int size;
private int sizeOffset;
private int x;
private int y;
private boolean vertical;
public RectanglePacker(Texture backingStore, int x, int y,
int width, int height, boolean vertical) {
this.backingStore = backingStore;
if (vertical) {
this.length = height;
this.size = width;
} else {
this.length = width;
this.size = height;
}
this.x = x;
this.y = y;
this.vertical = vertical;
}
public RectanglePacker(Texture backingStore, int width, int height) {
this(backingStore, 0, 0, width, height, false);
}
public final Texture getBackingStore() {
return backingStore;
}
public final boolean add(Rectangle rect) {
final int requestedLength = vertical ? rect.height : rect.width;
final int requestedSize = vertical ? rect.width : rect.height;
if (requestedLength > length) return false;
if (requestedSize > size) return false;
int newSize = MIN_SIZE > requestedSize ? MIN_SIZE : requestedSize;
newSize = (newSize + ROUND_UP - 1) - (newSize - 1) % ROUND_UP;
int newIndex;
if (recentUsedLevelIndex < levels.size() &&
levels.get(recentUsedLevelIndex).size != newSize) {
newIndex = binarySearch(levels, newSize);
} else {
newIndex = recentUsedLevelIndex;
}
final boolean newLevelFlag = sizeOffset + newSize <= size;
for (int i = newIndex, max = levels.size(); i < max; i++) {
Level level = levels.get(i);
if (level.size > (newSize + ROUND_UP * 2) && newLevelFlag) {
break;
} else if (level.add(rect, x, y, requestedLength, requestedSize, vertical)) {
recentUsedLevelIndex = i;
return true;
}
}
if (!newLevelFlag) {
return false;
}
Level newLevel = new Level(length, newSize, sizeOffset);
sizeOffset += newSize;
if (newIndex < levels.size() && levels.get(newIndex).size <= newSize) {
levels.add(newIndex + 1, newLevel);
recentUsedLevelIndex = newIndex + 1;
} else {
levels.add(newIndex, newLevel);
recentUsedLevelIndex = newIndex;
}
return newLevel.add(rect, x, y, requestedLength, requestedSize, vertical);
}
public void clear() {
levels.clear();
sizeOffset = 0;
recentUsedLevelIndex = 0;
}
public void dispose() {
if (backingStore != null) {
backingStore.dispose();
}
backingStore = null;
levels = null;
}
private static int binarySearch(List<Level> levels, int k) {
int key = k + 1;
int from = 0, to = levels.size() - 1;
int mid = 0;
int midSize = 0;
if (to < 0) {
return 0;
}
while (from <= to) {
mid = (from + to) / 2;
midSize = levels.get(mid).size;
if (key < midSize) {
to = mid - 1;
} else {
from = mid + 1;
}
}
if (midSize < k) {
return mid + 1;
} else if (midSize > k) {
return mid > 0 ? mid - 1 : 0;
} else {
return mid;
}
}
}
