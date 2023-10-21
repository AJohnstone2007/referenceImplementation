package com.sun.glass.ui.mac;
import com.sun.glass.ui.Clipboard;
import java.util.HashMap;
public class MacPasteboardShim {
private static MacSystemClipboard dndClipboard;
public MacPasteboardShim() {
dndClipboard = new MacSystemClipboard(Clipboard.DND);
}
public void pushMacPasteboard(HashMap<String, Object> data) {
dndClipboard.pushToSystem(data, Clipboard.ACTION_ANY);
}
public Object popMacPasteboard(String mime) {
return dndClipboard.popFromSystem(mime);
}
}
