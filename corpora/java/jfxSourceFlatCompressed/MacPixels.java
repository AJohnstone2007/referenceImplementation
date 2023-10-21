package com.sun.glass.ui.mac;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import com.sun.glass.ui.Application;
import com.sun.glass.ui.Pixels;
import java.security.AccessController;
import java.security.PrivilegedAction;
final class MacPixels extends Pixels {
private native static int _initIDs();
static {
nativeFormat = _initIDs();
}
private static final int nativeFormat;
static int getNativeFormat_impl() {
return nativeFormat;
}
protected MacPixels(int width, int height, ByteBuffer data) {
super(width, height, data);
}
protected MacPixels(int width, int height, IntBuffer data) {
super(width, height, data);
}
protected MacPixels(int width, int height, IntBuffer data, float scalex, float scaley) {
super(width, height, data, scalex, scaley);
}
@Override
protected void _fillDirectByteBuffer(ByteBuffer bb) {
if (this.bytes != null) {
this.bytes.rewind();
if (this.bytes.isDirect() == true) {
_copyPixels(bb, this.bytes, getWidth()*getHeight());
} else {
bb.put(this.bytes);
}
this.bytes.rewind();
} else {
this.ints.rewind();
if (this.ints.isDirect() == true) {
_copyPixels(bb, this.ints, getWidth()*getHeight());
} else {
for (int i=0; i<this.ints.capacity(); i++) {
int data = this.ints.get();
bb.put((byte)((data>>0)&0xff));
bb.put((byte)((data>>8)&0xff));
bb.put((byte)((data>>16)&0xff));
bb.put((byte)((data>>24)&0xff));
}
}
this.ints.rewind();
}
}
native protected void _copyPixels(Buffer src, Buffer dst, int size);
@Override native protected void _attachInt(long ptr, int w, int h, IntBuffer ints, int[] array, int offset);
@Override native protected void _attachByte(long ptr, int w, int h, ByteBuffer bytes, byte[] array, int offset);
@Override
public String toString() {
return "MacPixels ["+getWidth()+"x"+getHeight()+"]: "+super.toString();
}
}
