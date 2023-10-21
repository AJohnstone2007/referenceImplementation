package com.sun.glass.ui.ios;
import com.sun.glass.ui.Clipboard;
import com.sun.glass.ui.delegate.ClipboardDelegate;
final class IosClipboardDelegate implements ClipboardDelegate {
@Override
public Clipboard createClipboard(String clipboardName) {
if (Clipboard.SYSTEM.equals(clipboardName)) {
return new IosSystemClipboard(clipboardName);
} else if (Clipboard.DND.equals(clipboardName)) {
return new IosDnDClipboard(clipboardName);
}
return null;
}
}
