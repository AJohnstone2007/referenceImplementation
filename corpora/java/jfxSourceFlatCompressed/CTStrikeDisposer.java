package com.sun.javafx.font.coretext;
import java.lang.ref.WeakReference;
import com.sun.javafx.font.DisposerRecord;
import com.sun.javafx.font.FontResource;
import com.sun.javafx.font.FontStrike;
import com.sun.javafx.font.FontStrikeDesc;
class CTStrikeDisposer implements DisposerRecord {
private FontResource fontResource;
private FontStrikeDesc desc;
private long fontRef = 0L;
private boolean disposed = false;
public CTStrikeDisposer(FontResource font,
FontStrikeDesc desc,
long fontRef) {
this.fontResource = font;
this.desc = desc;
this.fontRef = fontRef;
}
public synchronized void dispose() {
if (!disposed) {
WeakReference<FontStrike> ref = fontResource.getStrikeMap().get(desc);
if (ref != null) {
Object o = ref.get();
if (o == null) {
fontResource.getStrikeMap().remove(desc);
}
}
if (fontRef != 0) {
OS.CFRelease(fontRef);
fontRef = 0;
}
disposed = true;
}
}
}
