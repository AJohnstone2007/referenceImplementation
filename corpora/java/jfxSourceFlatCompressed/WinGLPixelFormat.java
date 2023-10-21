package com.sun.prism.es2;
class WinGLPixelFormat extends GLPixelFormat {
private static native long nCreatePixelFormat(long nativeScreen, int[] attrArr);
WinGLPixelFormat(long nativeScreen, Attributes attrs) {
super(nativeScreen, attrs);
int attrArr[] = new int[GLPixelFormat.Attributes.NUM_ITEMS];
attrArr[GLPixelFormat.Attributes.RED_SIZE] = attrs.getRedSize();
attrArr[GLPixelFormat.Attributes.GREEN_SIZE] = attrs.getGreenSize();
attrArr[GLPixelFormat.Attributes.BLUE_SIZE] = attrs.getBlueSize();
attrArr[GLPixelFormat.Attributes.ALPHA_SIZE] = attrs.getAlphaSize();
attrArr[GLPixelFormat.Attributes.DEPTH_SIZE] = attrs.getDepthSize();
attrArr[GLPixelFormat.Attributes.DOUBLEBUFFER] = attrs.isDoubleBuffer() ? 1 : 0;
attrArr[GLPixelFormat.Attributes.ONSCREEN] = attrs.isOnScreen() ? 1 : 0;
long nativePF = nCreatePixelFormat(nativeScreen, attrArr);
setNativePFInfo(nativePF);
}
}
