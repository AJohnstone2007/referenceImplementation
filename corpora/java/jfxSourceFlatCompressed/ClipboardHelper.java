package com.sun.javafx.scene.input;
import com.sun.javafx.util.Utils;
import javafx.scene.input.Clipboard;
public class ClipboardHelper {
private static ClipboardAccessor clipboardAccessor;
static {
Utils.forceInit(Clipboard.class);
}
private ClipboardHelper() {
}
public static boolean contentPut(Clipboard clipboard) {
return clipboardAccessor.contentPut(clipboard);
}
public static void setClipboardAccessor(final ClipboardAccessor newAccessor) {
if (clipboardAccessor != null) {
throw new IllegalStateException();
}
clipboardAccessor = newAccessor;
}
public interface ClipboardAccessor {
boolean contentPut(Clipboard clipboard);
}
}
