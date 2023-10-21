package com.sun.glass.ui.monocle;
import com.sun.glass.ui.Clipboard;
import com.sun.glass.ui.delegate.ClipboardDelegate;
final class MonocleClipboardDelegate implements ClipboardDelegate {
@Override
public Clipboard createClipboard(String clipboardName) {
if (Clipboard.DND.equals(clipboardName)) {
return new MonocleDnDClipboard();
} else if (Clipboard.SYSTEM.equals(clipboardName)) {
return new MonocleSystemClipboard();
} else {
return null;
}
}
}
