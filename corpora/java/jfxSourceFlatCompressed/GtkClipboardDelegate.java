package com.sun.glass.ui.gtk;
import com.sun.glass.ui.Clipboard;
import com.sun.glass.ui.delegate.ClipboardDelegate;
class GtkClipboardDelegate implements ClipboardDelegate {
public GtkClipboardDelegate() {
}
public Clipboard createClipboard(String clipboardName) {
if (Clipboard.SYSTEM.equals(clipboardName)) {
return new GtkSystemClipboard();
}
if (Clipboard.DND.equals(clipboardName)) {
return new GtkDnDClipboard();
}
return null;
}
}
