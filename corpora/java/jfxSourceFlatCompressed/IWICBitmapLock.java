package com.sun.javafx.font.directwrite;
class IWICBitmapLock extends IUnknown {
IWICBitmapLock(long ptr) {
super(ptr);
}
byte[] GetDataPointer() {
return OS.GetDataPointer(ptr);
}
int GetStride() {
return OS.GetStride(ptr);
}
}
