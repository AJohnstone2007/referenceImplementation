package com.sun.glass.ui.monocle;
import com.sun.glass.ui.Size;
abstract class NativeCursor {
protected boolean isVisible = false;
abstract Size getBestSize();
abstract void setVisibility(boolean visibility);
boolean getVisiblity() {
return isVisible;
}
abstract void setImage(byte[] cursorImage);
abstract void setLocation(int x, int y);
abstract void setHotSpot(int hotspotX, int hotspotY);
abstract void shutdown();
}
