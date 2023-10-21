package com.sun.javafx.embed.swing;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.scene.input.DataFormat;
final class DataFlavorUtils {
static String getFxMimeType(final DataFlavor flavor) {
return flavor.getPrimaryType() + "/" + flavor.getSubType();
}
static DataFlavor[] getDataFlavors(String[] mimeTypes) {
final ArrayList<DataFlavor> flavors =
new ArrayList<DataFlavor>(mimeTypes.length);
for (String mime : mimeTypes) {
DataFlavor flavor = null;
try {
flavor = new DataFlavor(mime);
} catch (ClassNotFoundException | IllegalArgumentException e) {
continue;
}
flavors.add(flavor);
}
return flavors.toArray(new DataFlavor[0]);
}
static DataFlavor getDataFlavor(final DataFormat format) {
DataFlavor[] flavors = getDataFlavors(format.getIdentifiers().toArray(new String[1]));
return flavors.length == 0 ? null : flavors[0];
}
static String getMimeType(final DataFormat format) {
for (String id : format.getIdentifiers()) return id;
return null;
}
static DataFormat getDataFormat(final DataFlavor flavor) {
String mimeType = getFxMimeType(flavor);
DataFormat dataFormat = DataFormat.lookupMimeType(mimeType);
if (dataFormat == null) {
dataFormat = new DataFormat(mimeType);
}
return dataFormat;
}
private static class ByteBufferInputStream extends InputStream {
private final ByteBuffer bb;
private ByteBufferInputStream(ByteBuffer bb) { this.bb = bb; }
@Override public int available() { return bb.remaining(); }
@Override public int read() throws IOException {
if (!bb.hasRemaining()) return -1;
return bb.get() & 0xFF;
}
@Override public int read(byte[] bytes, int off, int len) throws IOException {
if (!bb.hasRemaining()) return -1;
len = Math.min(len, bb.remaining());
bb.get(bytes, off, len);
return len;
}
}
static Object adjustFxData(final DataFlavor flavor, final Object fxData)
throws UnsupportedEncodingException
{
if (fxData instanceof String) {
if (flavor.isRepresentationClassInputStream()) {
final String encoding = flavor.getParameter("charset");
return new ByteArrayInputStream(encoding != null
? ((String) fxData).getBytes(encoding)
: ((String) fxData).getBytes());
}
if (flavor.isRepresentationClassByteBuffer()) {
}
}
if (fxData instanceof ByteBuffer) {
if (flavor.isRepresentationClassInputStream()) {
return new ByteBufferInputStream((ByteBuffer)fxData);
}
}
return fxData;
}
static Object adjustSwingData(final DataFlavor flavor,
final String mimeType,
final Object swingData)
{
if (swingData == null) {
return swingData;
}
if (flavor.isFlavorJavaFileListType()) {
final List<File> fileList = (List<File>)swingData;
final String[] paths = new String[fileList.size()];
int i = 0;
for (File f : fileList) {
paths[i++] = f.getPath();
}
return paths;
}
DataFormat dataFormat = DataFormat.lookupMimeType(mimeType);
if (DataFormat.PLAIN_TEXT.equals(dataFormat)) {
if (flavor.isFlavorTextType()) {
if (swingData instanceof InputStream) {
InputStream in = (InputStream)swingData;
ByteArrayOutputStream out = new ByteArrayOutputStream();
byte[] bb = new byte[64];
try {
int len = in.read(bb);
while (len != -1) {
out.write(bb, 0, len);
len = in.read(bb);
}
out.close();
return new String(out.toByteArray());
} catch (Exception z) {
}
}
} else if (swingData != null) {
return swingData.toString();
}
}
return swingData;
}
static Map<String, DataFlavor> adjustSwingDataFlavors(final DataFlavor[] flavors) {
final Map<String, Set<DataFlavor>> mimeType2Flavors =
new HashMap<>(flavors.length);
for (DataFlavor flavor : flavors) {
final String mimeType = getFxMimeType(flavor);
if (mimeType2Flavors.containsKey(mimeType)) {
final Set<DataFlavor> mimeTypeFlavors = mimeType2Flavors.get(
mimeType);
try {
mimeTypeFlavors.add(flavor);
} catch (UnsupportedOperationException e) {
}
} else {
Set<DataFlavor> mimeTypeFlavors = new HashSet<DataFlavor>();
if (flavor.isFlavorTextType()) {
mimeTypeFlavors.add(DataFlavor.stringFlavor);
mimeTypeFlavors = Collections.unmodifiableSet(
mimeTypeFlavors);
} else {
mimeTypeFlavors.add(flavor);
}
mimeType2Flavors.put(mimeType, mimeTypeFlavors);
}
}
final Map<String, DataFlavor> mimeType2Flavor = new HashMap<>();
for (String mimeType : mimeType2Flavors.keySet()) {
final DataFlavor[] mimeTypeFlavors = mimeType2Flavors.get(mimeType).
toArray(new DataFlavor[0]);
if (mimeTypeFlavors.length == 1) {
mimeType2Flavor.put(mimeType, mimeTypeFlavors[0]);
} else {
mimeType2Flavor.put(mimeType, mimeTypeFlavors[0]);
}
}
return mimeType2Flavor;
}
private static Object readData(final Transferable t, final DataFlavor flavor) {
Object obj = null;
try {
obj = t.getTransferData(flavor);
} catch (UnsupportedFlavorException ex) {
ex.printStackTrace(System.err);
} catch (IOException ex) {
ex.printStackTrace(System.err);
}
return obj;
}
static Map<String, Object> readAllData(final Transferable t,
final Map<String, DataFlavor> fxMimeType2DataFlavor,
final boolean fetchData)
{
final Map<String, Object> fxMimeType2Data = new HashMap<>();
for (DataFlavor flavor : t.getTransferDataFlavors()) {
Object obj = fetchData ? readData(t, flavor) : null;
if (obj != null || !fetchData) {
String mimeType = getFxMimeType(flavor);
obj = adjustSwingData(flavor, mimeType, obj);
fxMimeType2Data.put(mimeType, obj);
}
}
for (Map.Entry<String, DataFlavor> e: fxMimeType2DataFlavor.entrySet()) {
String mimeType = e.getKey();
DataFlavor flavor = e.getValue();
Object obj = fetchData ? readData(t, flavor) : null;
if (obj != null || !fetchData) {
obj = adjustSwingData(flavor, mimeType, obj);
fxMimeType2Data.put(e.getKey(), obj);
}
}
return fxMimeType2Data;
}
}
