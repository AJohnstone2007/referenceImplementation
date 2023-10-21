package com.sun.glass.ui.ios;
import java.util.HashMap;
final class IosPasteboard {
final static public int General = 1;
final static public int UtfIndex = 0;
final static public int ObjectIndex = 1;
final static public String UtfString = "public.utf8-plain-text";
final static public String UtfPdf = "com.adobe.pdf";
final static public String UtfTiff = "public.tiff";
final static public String UtfPng = "public.png";
final static public String UtfRtf = "public.rtf";
final static public String UtfRtfd = "com.apple.flat-rtfd";
final static public String UtfHtml = "public.html";
final static public String UtfTabularText = "public.utf8-tab-separated-values-text";
final static public String UtfFont = "com.apple.cocoa.pasteboard.character-formatting";
final static public String UtfColor = "com.apple.cocoa.pasteboard.color";
final static public String UtfSound = "com.apple.cocoa.pasteboard.sound";
final static public String UtfMultipleTextSelection = "com.apple.cocoa.pasteboard.multiple-text-selection";
final static public String UtfFindPanelSearchOptions = "com.apple.cocoa.pasteboard.find-panel-search-options";
final static public String UtfUrl = "public.url";
final static public String UtfFileUrl = "public.file-url";
private long ptr = 0L;
private boolean user;
private native long _createSystemPasteboard(int type);
public IosPasteboard(int type) {
this.user = false;
this.ptr = _createSystemPasteboard(type);
}
private native long _createUserPasteboard(String name);
public IosPasteboard(String name) {
this.user = true;
this.ptr = _createUserPasteboard(name);
}
public long getNativePasteboard() {
assertValid();
return this.ptr;
}
private native String _getName(long ptr);
public String getName() {
assertValid();
return _getName(this.ptr);
}
private native String[][] _getUTFs(long ptr);
public String[][] getUTFs() {
assertValid();
return _getUTFs(this.ptr);
}
private native byte[] _getItemAsRawImage(long ptr, int index);
public byte[] getItemAsRawImage(int index) {
assertValid();
return _getItemAsRawImage(this.ptr, index);
}
private native String _getItemAsString(long ptr, int index);
public String getItemAsString(int index) {
assertValid();
return _getItemAsString(this.ptr, index);
}
private native String _getItemStringForUTF(long ptr, int index, String utf);
public String getItemStringForUTF(int index, String utf) {
assertValid();
return _getItemStringForUTF(this.ptr, index, utf);
}
private native byte[] _getItemBytesForUTF(long ptr, int index, String utf);
public byte[] getItemBytesForUTF(int index, String utf) {
assertValid();
return _getItemBytesForUTF(this.ptr, index, utf);
}
private native long _getItemForUTF(long ptr, int index, String utf);
public long getItemForUTF(int index, String utf) {
assertValid();
return _getItemForUTF(this.ptr, index, utf);
}
private native long _putItemsFromArray(long ptr, Object[] items, int supportedActions);
public long putItemsFromArray(Object[] items, int supportedActions) {
return _putItemsFromArray(this.ptr, items, supportedActions);
}
private Object[] hashMapToArray(HashMap hashmap) {
Object[] array = null;
if ((hashmap != null) && (hashmap.size() > 0)) {
array = new Object[hashmap.size()];
java.util.Set keys = hashmap.keySet();
java.util.Iterator iterator = keys.iterator();
int index = 0;
while (iterator.hasNext() == true) {
Object item[] = new Object[2];
String utf = (String)iterator.next();
item[IosPasteboard.UtfIndex] = utf;
item[IosPasteboard.ObjectIndex] = hashmap.get(utf);
array[index++] = item;
}
}
return array;
}
public long putItems(HashMap<String,Object>[] items, int supportedActions) {
assertValid();
Object array[] = null;
if (items.length > 0) {
array = new Object[items.length];
for (int i=0; i<items.length; i++) {
array[i] = hashMapToArray(items[i]);
}
}
return putItemsFromArray(array, supportedActions);
}
private native long _clear(long ptr);
public long clear() {
assertValid();
return _clear(this.ptr);
}
private native long _getSeed(long ptr);
public long getSeed() {
assertValid();
return _getSeed(this.ptr);
}
private native int _getAllowedOperation(long ptr);
public int getAllowedOperation() {
assertValid();
return _getAllowedOperation(this.ptr);
}
private native void _release(long ptr);
public void release() {
assertValid();
if ((this.ptr != 0L) && (this.user == true)) {
_release(ptr);
}
this.ptr = 0L;
}
private void assertValid() {
if (this.ptr == 0L) {
throw new IllegalStateException("The IosPasteboard is not valid");
}
}
}
