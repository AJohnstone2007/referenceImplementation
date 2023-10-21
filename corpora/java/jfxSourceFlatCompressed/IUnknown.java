package com.sun.javafx.font.directwrite;
class IUnknown {
long ptr;
IUnknown(long ptr) {
this.ptr = ptr;
}
int AddRef() {
return OS.AddRef(ptr);
}
int Release() {
int result = 0;
if (ptr != 0) {
result = OS.Release(ptr);
ptr = 0;
}
return result;
}
}
