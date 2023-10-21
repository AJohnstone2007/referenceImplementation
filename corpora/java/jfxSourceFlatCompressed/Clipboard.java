package javafx.scene.input;
import com.sun.javafx.scene.input.ClipboardHelper;
import java.io.File;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.scene.image.Image;
import javafx.util.Pair;
import com.sun.javafx.tk.PermissionHelper;
import com.sun.javafx.tk.TKClipboard;
import com.sun.javafx.tk.Toolkit;
public class Clipboard {
static {
ClipboardHelper.setClipboardAccessor(new ClipboardHelper.ClipboardAccessor() {
@Override
public boolean contentPut(Clipboard clipboard) {
return clipboard.contentPut();
}
});
}
private boolean contentPut = false;
@SuppressWarnings("removal")
private final AccessControlContext acc = AccessController.getContext();
public static Clipboard getSystemClipboard() {
try {
PermissionHelper.checkClipboardPermission();
return getSystemClipboardImpl();
} catch (final SecurityException e) {
return getLocalClipboardImpl();
}
}
TKClipboard peer;
Clipboard(TKClipboard peer) {
Toolkit.getToolkit().checkFxUserThread();
if (peer == null) {
throw new NullPointerException();
}
peer.setSecurityContext(acc);
this.peer = peer;
}
public final void clear() {
setContent(null);
}
public final Set<DataFormat> getContentTypes() {
return peer.getContentTypes();
}
public final boolean setContent(Map<DataFormat, Object> content) {
Toolkit.getToolkit().checkFxUserThread();
if (content == null) {
contentPut = false;
peer.putContent(new Pair[0]);
return true;
} else {
Pair<DataFormat, Object>[] data = new Pair[content.size()];
int index = 0;
for (Map.Entry<DataFormat, Object> entry : content.entrySet()) {
data[index++] = new Pair<DataFormat, Object>(entry.getKey(), entry.getValue());
}
contentPut = peer.putContent(data);
return contentPut;
}
}
public final Object getContent(DataFormat dataFormat) {
Toolkit.getToolkit().checkFxUserThread();
return getContentImpl(dataFormat);
}
Object getContentImpl(DataFormat dataFormat) {
return peer.getContent(dataFormat);
}
public final boolean hasContent(DataFormat dataFormat) {
Toolkit.getToolkit().checkFxUserThread();
return peer.hasContent(dataFormat);
}
public final boolean hasString() {
return hasContent(DataFormat.PLAIN_TEXT);
}
public final String getString() {
return (String) getContent(DataFormat.PLAIN_TEXT);
}
public final boolean hasUrl() {
return hasContent(DataFormat.URL);
}
public final String getUrl() {
return (String) getContent(DataFormat.URL);
}
public final boolean hasHtml() {
return hasContent(DataFormat.HTML);
}
public final String getHtml() {
return (String) getContent(DataFormat.HTML);
}
public final boolean hasRtf() {
return hasContent(DataFormat.RTF);
}
public final String getRtf() {
return (String) getContent(DataFormat.RTF);
}
public final boolean hasImage() {
return hasContent(DataFormat.IMAGE);
};
public final Image getImage() {
return (Image) getContent(DataFormat.IMAGE);
}
public final boolean hasFiles() {
return hasContent(DataFormat.FILES);
}
public final List<File> getFiles() {
return (List<File>) getContent(DataFormat.FILES);
}
boolean contentPut() {
return contentPut;
}
private static Clipboard systemClipboard;
private static synchronized Clipboard getSystemClipboardImpl() {
if (systemClipboard == null) {
systemClipboard =
new Clipboard(Toolkit.getToolkit().getSystemClipboard());
}
return systemClipboard;
}
private static Clipboard localClipboard;
private static synchronized Clipboard getLocalClipboardImpl() {
if (localClipboard == null) {
localClipboard =
new Clipboard(Toolkit.getToolkit().createLocalClipboard());
}
return localClipboard;
}
}
