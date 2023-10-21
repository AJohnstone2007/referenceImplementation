package com.sun.javafx.sg.prism;
import java.lang.ref.WeakReference;
import java.nio.BufferOverflowException;
import java.util.Arrays;
public class GrowableDataBuffer {
static final int VAL_GROW_QUANTUM = 1024;
static final int MAX_VAL_GROW = 1024 * 1024;
static final int MIN_OBJ_GROW = 32;
static class WeakLink {
WeakReference<GrowableDataBuffer> bufref;
WeakLink next;
}
static WeakLink buflist = new WeakLink();
public static GrowableDataBuffer getBuffer(int minsize) {
return getBuffer(minsize, MIN_OBJ_GROW);
}
public synchronized static GrowableDataBuffer getBuffer(int minvals, int minobjs) {
WeakLink prev = buflist;
WeakLink cur = buflist.next;
while (cur != null) {
GrowableDataBuffer curgdb = cur.bufref.get();
WeakLink next = cur.next;
if (curgdb == null) {
prev.next = cur = next;
continue;
}
if (curgdb.valueCapacity() >= minvals && curgdb.objectCapacity() >= minobjs) {
prev.next = next;
return curgdb;
}
prev = cur;
cur = next;
}
return new GrowableDataBuffer(minvals, minobjs);
}
public synchronized static void returnBuffer(GrowableDataBuffer retgdb) {
int retvlen = retgdb.valueCapacity();
int retolen = retgdb.objectCapacity();
retgdb.reset();
WeakLink prev = buflist;
WeakLink cur = buflist.next;
while (cur != null) {
GrowableDataBuffer curgdb = cur.bufref.get();
WeakLink next = cur.next;
if (curgdb == null) {
prev.next = cur = next;
continue;
}
int curvlen = curgdb.valueCapacity();
int curolen = curgdb.objectCapacity();
if (curvlen > retvlen ||
(curvlen == retvlen && curolen >= retolen))
{
break;
}
prev = cur;
cur = next;
}
WeakLink retlink = new WeakLink();
retlink.bufref = new WeakReference<>(retgdb);
prev.next = retlink;
retlink.next = cur;
}
byte vals[];
int writevalpos;
int readvalpos;
int savevalpos;
Object objs[];
int writeobjpos;
int readobjpos;
int saveobjpos;
private GrowableDataBuffer(int initvalsize, int initobjsize) {
vals = new byte[initvalsize];
objs = new Object[initobjsize];
}
public int readValuePosition() {
return readvalpos;
}
public int writeValuePosition() {
return writevalpos;
}
public int readObjectPosition() {
return readobjpos;
}
public int writeObjectPosition() {
return writeobjpos;
}
public int valueCapacity() {
return vals.length;
}
public int objectCapacity() {
return objs.length;
}
public void save() {
savevalpos = readvalpos;
saveobjpos = readobjpos;
}
public void restore() {
readvalpos = savevalpos;
readobjpos = saveobjpos;
}
public boolean hasValues() {
return (readvalpos < writevalpos);
}
public boolean hasObjects() {
return (readobjpos < writeobjpos);
}
public boolean isEmpty() {
return (writevalpos == 0);
}
public void reset() {
readvalpos = savevalpos = writevalpos = 0;
readobjpos = saveobjpos = 0;
if (writeobjpos > 0) {
Arrays.fill(objs, 0, writeobjpos, null);
writeobjpos = 0;
}
}
public void append(GrowableDataBuffer gdb) {
ensureWriteCapacity(gdb.writevalpos);
System.arraycopy(gdb.vals, 0, vals, writevalpos, gdb.writevalpos);
writevalpos += gdb.writevalpos;
if (writeobjpos + gdb.writeobjpos > objs.length) {
objs = Arrays.copyOf(objs, writeobjpos + gdb.writeobjpos);
}
System.arraycopy(gdb.objs, 0, objs, writeobjpos, gdb.writeobjpos);
writeobjpos += gdb.writeobjpos;
}
private void ensureWriteCapacity(int newbytes) {
if (newbytes > vals.length - writevalpos) {
newbytes = writevalpos + newbytes - vals.length;
int growbytes = Math.min(vals.length, MAX_VAL_GROW);
if (growbytes < newbytes) growbytes = newbytes;
int newsize = vals.length + growbytes;
newsize = (newsize + (VAL_GROW_QUANTUM - 1)) & ~(VAL_GROW_QUANTUM - 1);
vals = Arrays.copyOf(vals, newsize);
}
}
private void ensureReadCapacity(int bytesneeded) {
if (readvalpos + bytesneeded > writevalpos) {
throw new BufferOverflowException();
}
}
public void putBoolean(boolean b) {
putByte(b ? (byte) 1 : (byte) 0);
}
public void putByte(byte b) {
ensureWriteCapacity(1);
vals[writevalpos++] = b;
}
public void putChar(char c) {
ensureWriteCapacity(2);
vals[writevalpos++] = (byte) (c >> 8);
vals[writevalpos++] = (byte) (c );
}
public void putShort(short s) {
ensureWriteCapacity(2);
vals[writevalpos++] = (byte) (s >> 8);
vals[writevalpos++] = (byte) (s );
}
public void putInt(int i) {
ensureWriteCapacity(4);
vals[writevalpos++] = (byte) (i >> 24);
vals[writevalpos++] = (byte) (i >> 16);
vals[writevalpos++] = (byte) (i >> 8);
vals[writevalpos++] = (byte) (i );
}
public void putLong(long l) {
ensureWriteCapacity(8);
vals[writevalpos++] = (byte) (l >> 56);
vals[writevalpos++] = (byte) (l >> 48);
vals[writevalpos++] = (byte) (l >> 40);
vals[writevalpos++] = (byte) (l >> 32);
vals[writevalpos++] = (byte) (l >> 24);
vals[writevalpos++] = (byte) (l >> 16);
vals[writevalpos++] = (byte) (l >> 8);
vals[writevalpos++] = (byte) (l );
}
public void putFloat(float f) {
putInt(Float.floatToIntBits(f));
}
public void putDouble(double d) {
putLong(Double.doubleToLongBits(d));
}
public void putObject(Object o) {
if (writeobjpos >= objs.length) {
objs = Arrays.copyOf(objs, writeobjpos+MIN_OBJ_GROW);
}
objs[writeobjpos++] = o;
}
public byte peekByte(int i) {
if (i >= writevalpos) {
throw new BufferOverflowException();
}
return vals[i];
}
public Object peekObject(int i) {
if (i >= writeobjpos) {
throw new BufferOverflowException();
}
return objs[i];
}
public boolean getBoolean() {
ensureReadCapacity(1);
return vals[readvalpos++] != 0;
}
public byte getByte() {
ensureReadCapacity(1);
return vals[readvalpos++];
}
public int getUByte() {
ensureReadCapacity(1);
return vals[readvalpos++] & 0xff;
}
public char getChar() {
ensureReadCapacity(2);
int c = vals[readvalpos++];
c = (c << 8) | (vals[readvalpos++] & 0xff);
return (char) c;
}
public short getShort() {
ensureReadCapacity(2);
int s = vals[readvalpos++];
s = (s << 8) | (vals[readvalpos++] & 0xff);
return (short) s;
}
public int getInt() {
ensureReadCapacity(4);
int i = vals[readvalpos++];
i = (i << 8) | (vals[readvalpos++] & 0xff);
i = (i << 8) | (vals[readvalpos++] & 0xff);
i = (i << 8) | (vals[readvalpos++] & 0xff);
return i;
}
public long getLong() {
ensureReadCapacity(8);
long l = vals[readvalpos++];
l = (l << 8) | (vals[readvalpos++] & 0xff);
l = (l << 8) | (vals[readvalpos++] & 0xff);
l = (l << 8) | (vals[readvalpos++] & 0xff);
l = (l << 8) | (vals[readvalpos++] & 0xff);
l = (l << 8) | (vals[readvalpos++] & 0xff);
l = (l << 8) | (vals[readvalpos++] & 0xff);
l = (l << 8) | (vals[readvalpos++] & 0xff);
return l;
}
public float getFloat() {
return Float.intBitsToFloat(getInt());
}
public double getDouble() {
return Double.longBitsToDouble(getLong());
}
public Object getObject() {
if (readobjpos >= objs.length) {
throw new BufferOverflowException();
}
return objs[readobjpos++];
}
}
