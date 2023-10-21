package com.sun.glass.ui.win;
import com.sun.glass.ui.Clipboard;
import com.sun.glass.ui.delegate.ClipboardDelegate;
final class WinClipboardDelegate implements ClipboardDelegate {
public Clipboard createClipboard(String clipboardName) {
if (Clipboard.SYSTEM.equals(clipboardName)) {
return new WinSystemClipboard(clipboardName);
} else if (Clipboard.DND.equals(clipboardName)) {
return new WinDnDClipboard(clipboardName);
}
return null;
}
}
