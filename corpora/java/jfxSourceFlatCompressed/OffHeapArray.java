package com.sun.marlin;
import static com.sun.marlin.MarlinConst.LOG_UNSAFE_MALLOC;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;
final class OffHeapArray {
static final Unsafe UNSAFE;
static final int SIZE_INT;
static {
@SuppressWarnings("removal")
Unsafe tmp = AccessController.doPrivileged(new PrivilegedAction<Unsafe>() {
@Override
public Unsafe run() {
Unsafe ref = null;
try {
final Field field = Unsafe.class.getDeclaredField("theUnsafe");
field.setAccessible(true);
ref = (Unsafe) field.get(null);
} catch (Exception e) {
throw new InternalError("Unable to get sun.misc.Unsafe instance", e);
}
return ref;
}
});
UNSAFE = tmp;
SIZE_INT = Unsafe.ARRAY_INT_INDEX_SCALE;
}
long address;
long length;
int used;
OffHeapArray(final Object parent, final long len) {
this.address = UNSAFE.allocateMemory(len);
this.length = len;
this.used = 0;
if (LOG_UNSAFE_MALLOC) {
MarlinUtils.logInfo(System.currentTimeMillis()
+ ": OffHeapArray.allocateMemory =   "
+ len + " to addr = " + this.address);
}
MarlinUtils.getCleaner().register(parent, () -> this.free());
}
void resize(final long len) {
this.address = UNSAFE.reallocateMemory(address, len);
this.length = len;
if (LOG_UNSAFE_MALLOC) {
MarlinUtils.logInfo(System.currentTimeMillis()
+ ": OffHeapArray.reallocateMemory = "
+ len + " to addr = " + this.address);
}
}
void free() {
UNSAFE.freeMemory(this.address);
if (LOG_UNSAFE_MALLOC) {
MarlinUtils.logInfo(System.currentTimeMillis()
+ ": OffHeapArray.freeMemory =       "
+ this.length
+ " at addr = " + this.address);
}
this.address = 0L;
}
void fill(final byte val) {
UNSAFE.setMemory(this.address, this.length, val);
}
}
