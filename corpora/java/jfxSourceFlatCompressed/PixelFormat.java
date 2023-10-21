package javafx.scene.image;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
public abstract class PixelFormat<T extends Buffer> {
public enum Type {
INT_ARGB_PRE,
INT_ARGB,
BYTE_BGRA_PRE,
BYTE_BGRA,
BYTE_RGB,
BYTE_INDEXED,
}
private Type type;
PixelFormat(Type type) {
this.type = type;
}
public static WritablePixelFormat<IntBuffer> getIntArgbInstance() {
return WritablePixelFormat.IntArgb.INSTANCE;
}
public static WritablePixelFormat<IntBuffer> getIntArgbPreInstance() {
return WritablePixelFormat.IntArgbPre.INSTANCE;
}
public static WritablePixelFormat<ByteBuffer> getByteBgraInstance() {
return WritablePixelFormat.ByteBgra.INSTANCE;
}
public static WritablePixelFormat<ByteBuffer> getByteBgraPreInstance() {
return WritablePixelFormat.ByteBgraPre.INSTANCE;
}
public static PixelFormat<ByteBuffer> getByteRgbInstance() {
return ByteRgb.instance;
}
public static PixelFormat<ByteBuffer>
createByteIndexedPremultipliedInstance(int colors[])
{
return IndexedPixelFormat.createByte(colors, true);
}
public static PixelFormat<ByteBuffer>
createByteIndexedInstance(int colors[])
{
return IndexedPixelFormat.createByte(colors, false);
}
public Type getType() {
return type;
}
public abstract boolean isWritable();
public abstract boolean isPremultiplied();
static int NonPretoPre(int nonpre) {
int a = nonpre >>> 24;
if (a == 0xff) return nonpre;
if (a == 0x00) return 0;
int r = (nonpre >> 16) & 0xff;
int g = (nonpre >> 8) & 0xff;
int b = (nonpre ) & 0xff;
r = (r * a + 127) / 0xff;
g = (g * a + 127) / 0xff;
b = (b * a + 127) / 0xff;
return (a << 24) | (r << 16) | (g << 8) | b;
}
static int PretoNonPre(int pre) {
int a = pre >>> 24;
if (a == 0xff || a == 0x00) return pre;
int r = (pre >> 16) & 0xff;
int g = (pre >> 8) & 0xff;
int b = (pre ) & 0xff;
int halfa = a >> 1;
r = (r >= a) ? 0xff : (r * 0xff + halfa) / a;
g = (g >= a) ? 0xff : (g * 0xff + halfa) / a;
b = (b >= a) ? 0xff : (b * 0xff + halfa) / a;
return (a << 24) | (r << 16) | (g << 8) | b;
}
public abstract int getArgb(T buf, int x, int y, int scanlineStride);
static class ByteRgb extends PixelFormat<ByteBuffer> {
static final ByteRgb instance = new ByteRgb();
private ByteRgb() {
super(Type.BYTE_RGB);
}
@Override
public boolean isWritable() {
return true;
}
@Override
public boolean isPremultiplied() {
return false;
}
@Override
public int getArgb(ByteBuffer buf, int x, int y, int scanlineStride) {
int index = y * scanlineStride + x * 3;
int r = buf.get(index ) & 0xff;
int g = buf.get(index + 1) & 0xff;
int b = buf.get(index + 2) & 0xff;
return (0xff << 24) | (r << 16) | (g << 8) | b;
}
}
static class IndexedPixelFormat extends PixelFormat<ByteBuffer> {
int precolors[];
int nonprecolors[];
boolean premult;
static PixelFormat createByte(int colors[], boolean premult) {
return new IndexedPixelFormat(Type.BYTE_INDEXED, premult,
Arrays.copyOf(colors, 256));
}
private IndexedPixelFormat(Type type, boolean premult, int colors[]) {
super(type);
if (premult) {
this.precolors = colors;
} else {
this.nonprecolors = colors;
}
this.premult = premult;
}
@Override
public boolean isWritable() {
return false;
}
@Override
public boolean isPremultiplied() {
return premult;
}
int[] getPreColors() {
if (precolors == null) {
int colors[] = new int[nonprecolors.length];
for (int i = 0; i < colors.length; i++) {
colors[i] = NonPretoPre(nonprecolors[i]);
}
this.precolors = colors;
}
return precolors;
}
int[] getNonPreColors() {
if (nonprecolors == null) {
int colors[] = new int[precolors.length];
for (int i = 0; i < colors.length; i++) {
colors[i] = PretoNonPre(precolors[i]);
}
this.nonprecolors = colors;
}
return nonprecolors;
}
@Override
public int getArgb(ByteBuffer buf, int x, int y, int scanlineStride) {
return getNonPreColors()[buf.get(y * scanlineStride + x) & 0xff];
}
}
}
