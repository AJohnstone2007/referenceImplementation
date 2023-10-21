package com.sun.javafx.font.directwrite;
class IWICImagingFactory extends IUnknown {
IWICImagingFactory(long ptr) {
super(ptr);
}
IWICBitmap CreateBitmap(int uiWidth, int uiHeight, int pixelFormat, int options) {
long result = OS.CreateBitmap(ptr, uiWidth, uiHeight, pixelFormat, options);
return result != 0 ? new IWICBitmap(result) : null;
}
}
