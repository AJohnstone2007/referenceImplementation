package com.sun.glass.ui.mac;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.sun.glass.ui.Application;
import com.sun.glass.ui.Clipboard;
import com.sun.glass.ui.Pixels;
import com.sun.glass.ui.SystemClipboard;
class MacSystemClipboard extends SystemClipboard {
static final String FILE_SCHEME = "file";
static final private String BAD_URI_MSG = "bad URI in com.sun.glass.ui.mac.MacSystemClipboard for file: ";
static final private String BAD_URL_MSG = "bad URL in com.sun.glass.ui.mac.MacSystemClipboard for file: ";
static final boolean SUPPORT_10_5_API = true;
static final boolean SUPPORT_10_5_API_FORCE = false;
static final boolean SUPPORT_10_6_API = false;
long seed = 0;
final MacPasteboard pasteboard;
public MacSystemClipboard(String name) {
super(name);
switch (name) {
case Clipboard.DND:
this.pasteboard = new MacPasteboard(MacPasteboard.DragAndDrop);
break;
case Clipboard.SYSTEM:
this.pasteboard = new MacPasteboard(MacPasteboard.General);
break;
default:
this.pasteboard = new MacPasteboard(name);
break;
}
}
@Override
protected boolean isOwner() {
return (this.seed == this.pasteboard.getSeed());
}
@Override
protected int supportedSourceActionsFromSystem() {
return this.pasteboard.getAllowedOperation();
}
@Override
protected void pushTargetActionToSystem(int actionDone) {
}
@Override
protected void pushToSystem(HashMap<String, Object> data, int supportedActions) {
HashMap<String,Object> itemFirst = null;
HashMap<String,Object> itemList[] = null;
for (String mime : data.keySet()) {
Object object = data.get(mime);
if (object != null) {
switch (mime) {
case URI_TYPE:
{
List<HashMap<String, Object>> items = putToItemList(((String) object).split("\n"), true);
if (!items.isEmpty()) {
itemList = new HashMap[items.size()];
items.toArray(itemList);
}
break;
}
case RAW_IMAGE_TYPE:
case DRAG_IMAGE:
{
Pixels pixels = null;
if (object instanceof Pixels) {
pixels = (Pixels) object;
} else if (object instanceof ByteBuffer) {
try {
ByteBuffer bb = (ByteBuffer) object;
bb.rewind();
pixels = Application.GetApplication().createPixels(bb.getInt(), bb.getInt(), bb.slice());
} catch (Exception ex) {
}
} else if (object instanceof IntBuffer) {
try {
IntBuffer ib = (IntBuffer) object;
ib.rewind();
pixels = Application.GetApplication().createPixels(ib.get(), ib.get(), ib.slice());
} catch (Exception ex) {
}
} else {
throw new RuntimeException(object.getClass().getName() + " cannot be converted to Pixels");
}
if (pixels != null) {
if (itemFirst == null) {
itemFirst = new HashMap<>();
}
itemFirst.put(FormatEncoder.mimeToUtf(mime), pixels);
}
break;
}
case TEXT_TYPE:
case HTML_TYPE:
case RTF_TYPE:
{
if (object instanceof String) {
String string = (String)object;
if (itemFirst == null) {
itemFirst = new HashMap<>();
}
itemFirst.put(FormatEncoder.mimeToUtf(mime), string);
} else {
System.err.println("DelayedCallback not implemented yet: RT-14593");
Thread.dumpStack();
}
break;
}
case FILE_LIST_TYPE:
{
String files[] = (String[]) object;
if (data.get(URI_TYPE) == null) {
List<HashMap<String, Object>> items = putToItemList(files, true);
if (!items.isEmpty()) {
itemList = new HashMap[items.size()];
items.toArray(itemList);
}
} else if (MacSystemClipboard.SUPPORT_10_5_API) {
if (itemFirst == null) {
itemFirst = new HashMap<>();
}
StringBuilder string = null;
for (int i = 0; i < files.length; i++) {
String file = files[i];
String path = FileSystems.getDefault().getPath(file).toUri().toASCIIString();
if (string == null) {
string = new StringBuilder();
}
string.append(path);
if (i < (files.length - 1)) {
string.append("\n");
}
}
if (string != null) {
if ((itemFirst.get(MacPasteboard.UtfString) == null) || MacSystemClipboard.SUPPORT_10_5_API_FORCE) {
itemFirst.remove(MacPasteboard.UtfString);
itemFirst.put(MacPasteboard.UtfString, string.toString());
}
}
}
break;
}
default:
{
if (itemFirst == null) {
itemFirst = new HashMap<>();
}
itemFirst.put(FormatEncoder.mimeToUtf(mime), serialize(object));
break;
}
}
}
}
if (itemFirst != null) {
if (itemList == null || itemList.length == 0) {
itemList = new HashMap[1];
itemList[0] = itemFirst;
} else {
itemList[0].putAll(itemFirst);
}
}
if (itemList != null) {
this.seed = this.pasteboard.putItems(itemList, supportedActions);
}
}
@Override
protected Object popFromSystem(String mime) {
String[][] utfs = this.pasteboard.getUTFs();
if (utfs == null) {
return null;
}
switch (mime) {
case RAW_IMAGE_TYPE:
{
List<Pixels> list = new ArrayList<>();
for (int i = 0; i < utfs.length; i++) {
Object data = this.pasteboard.getItemAsRawImage(i);
if (data != null) {
Pixels pixels = getPixelsForRawImage((byte[]) data);
list.add(pixels);
if (SUPPORT_10_6_API == false) {
break;
}
}
}
return getObjectFromList(list);
}
case TEXT_TYPE:
case HTML_TYPE:
case RTF_TYPE:
case URI_TYPE:
{
List<String> list = new ArrayList<>();
for (int i = 0; i < utfs.length; i++) {
String item = this.pasteboard.getItemStringForUTF(i, FormatEncoder.mimeToUtf(mime));
if (item != null) {
list.add(item);
if (SUPPORT_10_6_API == false) {
break;
}
}
}
return getObjectFromList(list);
}
case FILE_LIST_TYPE:
{
List<String> list = new ArrayList<>();
for (int i = 0; i < utfs.length; i++) {
String file = this.pasteboard.getItemStringForUTF(i, MacPasteboard.UtfFileUrl);
if (file != null) {
list.add(_convertFileReferencePath(file));
}
}
String[] object = null;
if (list.size() > 0) {
object = new String[list.size()];
list.toArray(object);
}
return object;
}
default:
{
List<ByteBuffer> list = new ArrayList<>();
for (int i = 0; i < utfs.length; i++) {
byte data[] = this.pasteboard.getItemBytesForUTF(i, FormatEncoder.mimeToUtf(mime));
if (data != null) {
ByteBuffer bb = ByteBuffer.wrap(data);
list.add(bb);
if (SUPPORT_10_6_API == false) {
break;
}
}
}
return getObjectFromList(list);
}
}
}
private Object getObjectFromList(List<?> list) {
if (list.size() > 0) {
if (SUPPORT_10_6_API == false) {
return list.get(0);
} else {
return list;
}
}
return null;
}
@Override
protected String[] mimesFromSystem() {
String[][] all = this.pasteboard.getUTFs();
List<String> mimes = new ArrayList<>();
if (all != null) {
for (String[] utfs : all) {
if (utfs != null) {
for (String utf : utfs) {
String mime = FormatEncoder.utfToMime(utf);
if ((mime != null) && (!mimes.contains(mime))) {
mimes.add(mime);
}
}
}
}
}
String[] strings = new String[mimes.size()];
mimes.toArray(strings);
return strings;
}
@Override public String toString() {
return "Mac OS X "+this.pasteboard.getName()+" Clipboard";
}
private static class FormatEncoder {
private static final String DYNAMIC_UTI_PREFIX = "dyn.";
private static final Map<String, String> utm = new HashMap<>();
private static final Map<String, String> mtu = new HashMap<>();
static {
utm.put(MacPasteboard.UtfString, TEXT_TYPE);
utm.put(MacPasteboard.UtfHtml, HTML_TYPE);
utm.put(MacPasteboard.UtfRtf, RTF_TYPE);
utm.put(MacPasteboard.UtfUrl, URI_TYPE);
utm.put(MacPasteboard.UtfFileUrl, FILE_LIST_TYPE);
utm.put(MacPasteboard.UtfTiff, RAW_IMAGE_TYPE);
utm.put(MacPasteboard.UtfPng, RAW_IMAGE_TYPE);
utm.put(MacPasteboard.UtfRawImageType, RAW_IMAGE_TYPE);
utm.put(MacPasteboard.UtfDragImageType, DRAG_IMAGE);
utm.put(MacPasteboard.UtfDragImageOffset, DRAG_IMAGE_OFFSET);
mtu.put(TEXT_TYPE, MacPasteboard.UtfString);
mtu.put(HTML_TYPE, MacPasteboard.UtfHtml);
mtu.put(RTF_TYPE, MacPasteboard.UtfRtf);
mtu.put(URI_TYPE, MacPasteboard.UtfUrl);
mtu.put(FILE_LIST_TYPE, MacPasteboard.UtfFileUrl);
mtu.put(RAW_IMAGE_TYPE, MacPasteboard.UtfRawImageType);
mtu.put(DRAG_IMAGE, MacPasteboard.UtfDragImageType);
mtu.put(DRAG_IMAGE_OFFSET, MacPasteboard.UtfDragImageOffset);
}
public static synchronized String mimeToUtf(String mime) {
if (mtu.containsKey(mime)) {
return mtu.get(mime);
}
String encodedUTI = _convertMIMEtoUTI(mime);
mtu.put(mime, encodedUTI);
utm.put(encodedUTI, mime);
return encodedUTI;
}
public static synchronized String utfToMime(String uti) {
if (utm.containsKey(uti)) {
return utm.get(uti);
}
if (uti.startsWith(DYNAMIC_UTI_PREFIX)) {
String decodedMIME = _convertUTItoMIME(uti);
mtu.put(decodedMIME, uti);
utm.put(uti, decodedMIME);
return decodedMIME;
}
return null;
}
private static native String _convertMIMEtoUTI(String mime);
private static native String _convertUTItoMIME(String uti);
}
private URI createUri(String path, String message) {
URI uri = null;
try {
uri = new URI(path);
} catch (URISyntaxException ex) {
System.err.println(message+path);
Thread.dumpStack();
}
return uri;
}
private HashMap<String, Object> getItemFromURIString(String string) {
String utf;
String path = null;
if (string.indexOf(':') == -1) {
utf = MacPasteboard.UtfFileUrl;
path = FileSystems.getDefault().getPath(string).toUri().toASCIIString();
} else {
utf = MacPasteboard.UtfUrl;
URI uri = createUri(string, MacSystemClipboard.BAD_URI_MSG);
if (uri != null) {
path = uri.toASCIIString();
}
}
if (path != null) {
HashMap<String, Object> item = new HashMap<>();
item.put(utf, path);
return item;
} else {
return null;
}
}
private List<HashMap<String, Object>> putToItemList(String[] items, boolean excludeComments) {
List<HashMap<String, Object>> uriList = new ArrayList<>();
for (String file : items) {
if (!(excludeComments && file.startsWith("#"))) {
HashMap<String, Object> entry = getItemFromURIString(file);
if (entry != null) {
uriList.add(entry);
}
}
}
return uriList;
}
private static native String _convertFileReferencePath(String path);
private byte[] serialize(Object object) {
if (object instanceof String) {
String string = (String)object;
return string.getBytes();
} else if (object instanceof ByteBuffer) {
ByteBuffer buffer = (ByteBuffer)object;
return buffer.array();
} else {
throw new RuntimeException("can not handle "+object);
}
}
}
