package com.sun.glass.ui.monocle;
import com.sun.glass.ui.Size;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
class SoftwareCursor extends NativeCursor {
private ByteBuffer cursorBuffer;
private int renderX;
private int renderY;
private int hotspotX;
private int hotspotY;
@Override
Size getBestSize() {
return new Size(16, 16);
}
@Override
void setVisibility(boolean visibility) {
if (visibility != isVisible) {
isVisible = visibility;
MonocleWindowManager.getInstance().repaintAll();
}
}
@Override
void setImage(byte[] cursorImage) {
cursorBuffer = ByteBuffer.allocate(cursorImage.length);
NativeCursors.colorKeyCursor(cursorImage, cursorBuffer.asIntBuffer(), 32, 0);
cursorBuffer = cursorBuffer.order(ByteOrder.nativeOrder());
}
@Override
void setLocation(int x, int y) {
int renderX = x - hotspotX;
int renderY = y - hotspotY;
if (renderX != this.renderX || renderY != this.renderY) {
this.renderX = renderX;
this.renderY = renderY;
MonocleWindowManager.getInstance().repaintAll();
}
}
@Override
void setHotSpot(int hotspotX, int hotspotY) {
this.hotspotX = hotspotX;
this.hotspotY = hotspotY;
}
@Override
void shutdown() {
}
int getRenderX() {
return renderX;
}
int getRenderY() {
return renderY;
}
Buffer getCursorBuffer() {
cursorBuffer.clear();
return cursorBuffer;
}
}
