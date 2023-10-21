package com.sun.glass.ui.monocle;
import com.sun.glass.ui.Size;
class NullCursor extends NativeCursor {
@Override
Size getBestSize() {
return new Size(16, 16);
}
@Override
void setVisibility(boolean visibility) {
}
@Override
void setImage(byte[] cursorImage) {
}
@Override
void setLocation(int x, int y) {
}
@Override
void setHotSpot(int hotspotX, int hotspotY) {
}
@Override
void shutdown() {
}
}
