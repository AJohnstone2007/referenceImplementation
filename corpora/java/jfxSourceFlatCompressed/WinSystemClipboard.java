package com.sun.glass.ui.win;
import com.sun.glass.ui.Application;
import com.sun.glass.ui.Pixels;
import com.sun.glass.ui.SystemClipboard;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
class WinSystemClipboard extends SystemClipboard {
private static native void initIDs();
static {
initIDs();
}
private long ptr = 0L;
protected WinSystemClipboard(String name) {
super(name);
create();
}
protected final long getPtr() {
return ptr;
}
protected native boolean isOwner();
protected native void create();
protected native void dispose();
protected native void push(Object[] keys, int supportedActions);
protected native boolean pop();
static final byte[] terminator = new byte[] { 0, 0 };
static final String defaultCharset = "UTF-16LE";
static final String RTFCharset = "US-ASCII";
private byte[] fosSerialize(String mime, long index) {
Object data = getLocalData(mime);
if (data instanceof ByteBuffer) {
byte[] b = ((ByteBuffer)data).array();
if (HTML_TYPE.equals(mime)) {
b = WinHTMLCodec.encode(b);
}
return b;
} else if (data instanceof String) {
String st = ((String) data).replaceAll("(\r\n|\r|\n)", "\r\n");
if (HTML_TYPE.equals(mime)) {
try {
byte[] bytes = st.getBytes(WinHTMLCodec.defaultCharset);
ByteBuffer ba = ByteBuffer.allocate(bytes.length + 1);
ba.put(bytes);
ba.put((byte)0);
return WinHTMLCodec.encode(ba.array());
} catch (UnsupportedEncodingException ex) {
return null;
}
} else if (RTF_TYPE.equals(mime)) {
try {
byte[] bytes = st.getBytes(RTFCharset);
ByteBuffer ba = ByteBuffer.allocate(bytes.length + 1);
ba.put(bytes);
ba.put((byte)0);
return ba.array();
} catch (UnsupportedEncodingException ex) {
return null;
}
} else {
ByteBuffer ba = ByteBuffer.allocate((st.length() + 1) * 2);
try {
ba.put(st.getBytes(defaultCharset));
} catch (UnsupportedEncodingException ex) {
}
ba.put(terminator);
return ba.array();
}
} else if (FILE_LIST_TYPE.equals(mime)) {
String[] ast = ((String[]) data);
if (ast != null && ast.length > 0) {
int size = 0;
for (String st : ast) {
size += (st.length() + 1) * 2;
}
size += 2;
try {
ByteBuffer ba = ByteBuffer.allocate(size);
for (String st : ast) {
ba.put(st.getBytes(defaultCharset));
ba.put(terminator);
}
ba.put(terminator);
return ba.array();
} catch (UnsupportedEncodingException ex) {
}
}
} else if (RAW_IMAGE_TYPE.equals(mime)) {
Pixels pxls = (Pixels)data;
if (pxls != null) {
ByteBuffer ba = ByteBuffer.allocate(
pxls.getWidth() * pxls.getHeight() * 4 + 8);
ba.putInt(pxls.getWidth());
ba.putInt(pxls.getHeight());
ba.put(pxls.asByteBuffer());
return ba.array();
}
}
return null;
}
private static final class MimeTypeParser {
protected static final String externalBodyMime = "message/external-body";
protected String mime;
protected boolean bInMemoryFile;
protected int index;
public MimeTypeParser() {
parse("");
}
public MimeTypeParser(String mimeFull) {
parse(mimeFull);
}
public void parse(String mimeFull) {
mime = mimeFull;
bInMemoryFile = false;
index = -1;
if (mimeFull.startsWith(externalBodyMime)) {
String mimeParts[] = mimeFull.split(";");
String accessType = "";
int indexValue = -1;
for (int i = 1; i < mimeParts.length; ++i) {
String params[] = mimeParts[i].split("=");
if (params.length == 2) {
if( params[0].trim().equalsIgnoreCase("index") ) {
indexValue = Integer.parseInt(params[1].trim());
} else if( params[0].trim().equalsIgnoreCase("access-type") ) {
accessType = params[1].trim();
}
}
if (indexValue != -1 && !accessType.isEmpty()) {
break;
}
}
if (accessType.equalsIgnoreCase("clipboard")) {
bInMemoryFile = true;
mime = mimeParts[0];
index = indexValue;
}
}
}
public String getMime() {
return mime;
}
public int getIndex() {
return index;
}
public boolean isInMemoryFile() {
return bInMemoryFile;
}
}
protected final void pushToSystem(HashMap<String, Object> cacheData, int supportedActions) {
Set<String> mimes = cacheData.keySet();
Set<String> mimesForSystem = new HashSet<String>();
MimeTypeParser parser = new MimeTypeParser();
for (String mime : mimes) {
parser.parse(mime);
if ( !parser.isInMemoryFile() ) {
mimesForSystem.add(mime);
}
}
push(mimesForSystem.toArray(), supportedActions);
}
private native byte[] popBytes(String mime, long index);
protected final Object popFromSystem(String mimeFull) {
if ( !pop() ) {
return null;
}
MimeTypeParser parser = new MimeTypeParser(mimeFull);
String mime = parser.getMime();
byte[] data = popBytes(mime, parser.getIndex());
if (data != null) {
if (TEXT_TYPE.equals(mime) || URI_TYPE.equals(mime)) {
try {
return new String(data, 0, data.length - 2, defaultCharset);
} catch (UnsupportedEncodingException ex) {
}
} else if (HTML_TYPE.equals(mime)) {
try {
data = WinHTMLCodec.decode(data);
return new String(data, 0, data.length, WinHTMLCodec.defaultCharset);
} catch (UnsupportedEncodingException ex) {
}
} else if (RTF_TYPE.equals(mime)) {
try {
return new String(data, 0, data.length, RTFCharset);
} catch (UnsupportedEncodingException ex) {
}
} else if (FILE_LIST_TYPE.equals(mime)) {
try {
String st = new String(data, 0, data.length, defaultCharset);
return st.split("\0");
} catch (UnsupportedEncodingException ex) {
}
} else if (RAW_IMAGE_TYPE.equals(mime)) {
ByteBuffer size = ByteBuffer.wrap(data, 0, 8);
return Application.GetApplication().createPixels(size.getInt(), size.getInt(), ByteBuffer.wrap(data, 8, data.length - 8) );
} else {
return ByteBuffer.wrap(data);
}
} else {
if (URI_TYPE.equals(mime) || TEXT_TYPE.equals(mime)) {
data = popBytes(mime + ";locale", parser.getIndex());
if (data != null) {
try {
return new String(data, 0, data.length - 1, "UTF-8");
} catch (UnsupportedEncodingException ex) {
}
}
}
if (URI_TYPE.equals(mime)) {
String[] ret = (String[])popFromSystem(FILE_LIST_TYPE);
if (ret != null) {
StringBuilder out = new StringBuilder();
for (int i = 0; i < ret.length; i++) {
String fileName = ret[i];
fileName = fileName.replace("\\", "/");
if (out.length() > 0) {
out.append("\r\n");
}
out.append("file:/").append(fileName);
}
return out.toString();
}
}
}
return null;
}
private native String[] popMimesFromSystem();
protected final String[] mimesFromSystem() {
if (!pop()) {
return null;
}
return popMimesFromSystem();
}
@Override public String toString() {
return "Windows System Clipboard";
}
@Override protected final void close() {
dispose();
ptr = 0L;
}
@Override protected native void pushTargetActionToSystem(int actionDone);
private native int popSupportedSourceActions();
@Override protected int supportedSourceActionsFromSystem() {
if (!pop()) {
return ACTION_NONE;
}
return popSupportedSourceActions();
}
}
