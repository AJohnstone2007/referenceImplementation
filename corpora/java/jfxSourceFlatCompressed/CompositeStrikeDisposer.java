package com.sun.javafx.font;
import java.lang.ref.WeakReference;
import com.sun.javafx.font.DisposerRecord;
import com.sun.javafx.font.FontResource;
import com.sun.javafx.font.FontStrikeDesc;
class CompositeStrikeDisposer implements DisposerRecord {
FontResource fontResource;
FontStrikeDesc desc;
boolean disposed = false;
public CompositeStrikeDisposer(FontResource font, FontStrikeDesc desc) {
this.fontResource = font;
this.desc = desc;
}
public synchronized void dispose() {
if (!disposed) {
WeakReference ref = fontResource.getStrikeMap().get(desc);
if (ref != null) {
Object o = ref.get();
if (o == null) {
fontResource.getStrikeMap().remove(desc);
}
}
disposed = true;
}
}
}
