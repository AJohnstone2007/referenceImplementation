package com.sun.javafx.font.directwrite;
class IWICBitmap extends IUnknown {
IWICBitmap(long ptr) {
super(ptr);
}
IWICBitmapLock Lock(int x, int y, int width, int height, int flags) {
long result = OS.Lock(ptr, x, y, width, height, flags);
return result != 0 ? new IWICBitmapLock(result) : null;
}
}
