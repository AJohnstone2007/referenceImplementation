package javafx.scene.image;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
public abstract class WritablePixelFormat<T extends Buffer>
extends PixelFormat<T>
{
WritablePixelFormat(Type type) {
super(type);
}
@Override
public boolean isWritable() {
return true;
}
public abstract void setArgb(T buf, int x, int y, int scanlineStride,
int argb);
static class IntArgb extends WritablePixelFormat<IntBuffer> {
static final IntArgb INSTANCE = new IntArgb();
private IntArgb() {
super(Type.INT_ARGB);
}
@Override
public boolean isPremultiplied() {
return false;
}
@Override
public int getArgb(IntBuffer buf, int x, int y, int scanlineStride) {
return buf.get(y * scanlineStride + x);
}
@Override
public void setArgb(IntBuffer buf, int x, int y, int scanlineStride,
int argb)
{
buf.put(y * scanlineStride + x, argb);
}
}
static class IntArgbPre extends WritablePixelFormat<IntBuffer> {
static final IntArgbPre INSTANCE = new IntArgbPre();
private IntArgbPre() {
super(Type.INT_ARGB_PRE);
}
@Override
public boolean isPremultiplied() {
return true;
}
@Override
public int getArgb(IntBuffer buf, int x, int y, int scanlineStride) {
return PretoNonPre(buf.get(y * scanlineStride + x));
}
@Override
public void setArgb(IntBuffer buf, int x, int y, int scanlineStride,
int argb)
{
buf.put(y * scanlineStride + x, NonPretoPre(argb));
}
}
static class ByteBgra extends WritablePixelFormat<ByteBuffer> {
static final ByteBgra INSTANCE = new ByteBgra();
private ByteBgra() {
super(Type.BYTE_BGRA);
}
@Override
public boolean isPremultiplied() {
return false;
}
@Override
public int getArgb(ByteBuffer buf, int x, int y, int scanlineStride) {
int index = y * scanlineStride + x * 4;
int b = buf.get(index ) & 0xff;
int g = buf.get(index + 1) & 0xff;
int r = buf.get(index + 2) & 0xff;
int a = buf.get(index + 3) & 0xff;
return (a << 24) | (r << 16) | (g << 8) | b;
}
@Override
public void setArgb(ByteBuffer buf, int x, int y, int scanlineStride,
int argb)
{
int index = y * scanlineStride + x * 4;
buf.put(index, (byte) (argb ));
buf.put(index + 1, (byte) (argb >> 8));
buf.put(index + 2, (byte) (argb >> 16));
buf.put(index + 3, (byte) (argb >> 24));
}
}
static class ByteBgraPre extends WritablePixelFormat<ByteBuffer> {
static final ByteBgraPre INSTANCE = new ByteBgraPre();
private ByteBgraPre() {
super(Type.BYTE_BGRA_PRE);
}
@Override
public boolean isPremultiplied() {
return true;
}
@Override
public int getArgb(ByteBuffer buf, int x, int y, int scanlineStride) {
int index = y * scanlineStride + x * 4;
int b = buf.get(index ) & 0xff;
int g = buf.get(index + 1) & 0xff;
int r = buf.get(index + 2) & 0xff;
int a = buf.get(index + 3) & 0xff;
if (a > 0x00 && a < 0xff) {
int halfa = a >> 1;
r = (r >= a) ? 0xff : (r * 0xff + halfa) / a;
g = (g >= a) ? 0xff : (g * 0xff + halfa) / a;
b = (b >= a) ? 0xff : (b * 0xff + halfa) / a;
}
return (a << 24) | (r << 16) | (g << 8) | b;
}
@Override
public void setArgb(ByteBuffer buf, int x, int y, int scanlineStride,
int argb)
{
int index = y * scanlineStride + x * 4;
int a = (argb >>> 24);
int r, g, b;
if (a > 0x00) {
r = (argb >> 16) & 0xff;
g = (argb >> 8) & 0xff;
b = (argb ) & 0xff;
if (a < 0xff) {
r = (r * a + 127) / 0xff;
g = (g * a + 127) / 0xff;
b = (b * a + 127) / 0xff;
}
} else {
a = r = g = b = 0;
}
buf.put(index, (byte) b);
buf.put(index + 1, (byte) g);
buf.put(index + 2, (byte) r);
buf.put(index + 3, (byte) a);
}
}
}
