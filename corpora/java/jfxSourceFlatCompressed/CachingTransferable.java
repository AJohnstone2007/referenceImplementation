package com.sun.javafx.embed.swing;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
public class CachingTransferable implements Transferable {
@Override
public Object getTransferData(final DataFlavor flavor) throws UnsupportedEncodingException
{
String mimeType = DataFlavorUtils.getFxMimeType(flavor);
return DataFlavorUtils.adjustFxData(
flavor, getData(mimeType));
}
@Override
public DataFlavor[] getTransferDataFlavors() {
final String mimeTypes[] = getMimeTypes();
return DataFlavorUtils.getDataFlavors(mimeTypes);
}
@Override
public boolean isDataFlavorSupported(final DataFlavor flavor) {
return isMimeTypeAvailable(
DataFlavorUtils.getFxMimeType(flavor));
}
private Map<String, Object> mimeType2Data = Collections.EMPTY_MAP;
public void updateData(Transferable t, boolean fetchData) {
final Map<String, DataFlavor> mimeType2DataFlavor =
DataFlavorUtils.adjustSwingDataFlavors(
t.getTransferDataFlavors());
try {
mimeType2Data = DataFlavorUtils.readAllData(t, mimeType2DataFlavor,
fetchData);
} catch (Exception e) {
mimeType2Data = Collections.EMPTY_MAP;
}
}
public void updateData(Clipboard cb, boolean fetchData) {
mimeType2Data = new HashMap<>();
for (DataFormat f : cb.getContentTypes()) {
mimeType2Data.put(DataFlavorUtils.getMimeType(f),
fetchData ? cb.getContent(f) : null);
}
}
public Object getData(final String mimeType) {
return mimeType2Data.get(mimeType);
}
public String[] getMimeTypes() {
return mimeType2Data.keySet().toArray(new String[0]);
}
public boolean isMimeTypeAvailable(final String mimeType) {
return Arrays.asList(getMimeTypes()).contains(mimeType);
}
}
