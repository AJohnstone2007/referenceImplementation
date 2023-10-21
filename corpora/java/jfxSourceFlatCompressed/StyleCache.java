package com.sun.javafx.css;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
public final class StyleCache {
public StyleCache() {
}
public void clear() {
if (entries == null) return;
Thread.dumpStack();
entries.clear();
}
public StyleCacheEntry getStyleCacheEntry(StyleCacheEntry.Key key) {
StyleCacheEntry entry = null;
if (entries != null) {
entry = entries.get(key);
}
return entry;
}
public void addStyleCacheEntry(StyleCacheEntry.Key key, StyleCacheEntry entry) {
if (entries == null) {
entries = new HashMap<>(5);
}
entries.put(key, entry);
}
public static final class Key {
public Key(int[] styleMapIds, int count) {
this.styleMapIds = new int[count];
System.arraycopy(styleMapIds, 0, this.styleMapIds, 0, count);
}
public Key(Key other) {
this(other.styleMapIds, other.styleMapIds.length);
}
public int[] getStyleMapIds() {
return styleMapIds;
}
@Override public String toString() {
return Arrays.toString(styleMapIds);
}
@Override
public int hashCode() {
if (hash == Integer.MIN_VALUE) {
hash = 3;
if (styleMapIds != null) {
for (int i=0; i<styleMapIds.length; i++) {
final int id = styleMapIds[i];
hash = 17 * (hash + id);
}
}
}
return hash;
}
@Override
public boolean equals(Object obj) {
if (obj == this) return true;
if (obj == null || obj.getClass() != this.getClass()) {
return false;
}
final Key other = (Key) obj;
if (this.hash != other.hash) return false;
if ((this.styleMapIds == null) ^ (other.styleMapIds == null)) {
return false;
}
if (this.styleMapIds == null) {
return true;
}
if (this.styleMapIds.length != other.styleMapIds.length) {
return false;
}
for (int i=0; i<styleMapIds.length; i++) {
if (styleMapIds[i] != other.styleMapIds[i]) {
return false;
}
}
return true;
}
final int[] styleMapIds;
private int hash = Integer.MIN_VALUE;
}
private Map<StyleCacheEntry.Key,StyleCacheEntry> entries;
}
