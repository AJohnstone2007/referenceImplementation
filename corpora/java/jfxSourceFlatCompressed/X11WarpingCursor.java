package com.sun.glass.ui.monocle;
public class X11WarpingCursor extends X11Cursor {
private int nextX, nextY;
private static X xLib = X.getX();
@Override
void setLocation(int x, int y) {
if (x != nextX || y != nextY) {
nextX = x;
nextY = y;
MonocleWindowManager.getInstance().repaintAll();
}
}
void warp() {
if (isVisible) {
int[] position = new int[2];
xLib.XQueryPointer(xdisplay, xwindow, position);
if (position[0] != nextX || position[1] != nextY) {
xLib.XWarpPointer(xdisplay, 0l, 0l, 0, 0, 0, 0,
nextX - position[0],
nextY - position[1]);
}
}
}
}
