package com.sun.scenario.effect.impl;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
public class BufferUtil {
public static final int SIZEOF_BYTE = 1;
public static final int SIZEOF_SHORT = 2;
public static final int SIZEOF_INT = 4;
public static final int SIZEOF_FLOAT = 4;
public static final int SIZEOF_DOUBLE = 8;
private static boolean isCDCFP;
private static Class byteOrderClass;
private static Object nativeOrderObject;
private static Method orderMethod;
private BufferUtil() {
}
@SuppressWarnings("removal")
public static void nativeOrder(ByteBuffer buf) {
if (!isCDCFP) {
try {
if (byteOrderClass == null) {
byteOrderClass = (Class) AccessController.doPrivileged(
(PrivilegedExceptionAction) () -> Class.forName("java.nio.ByteOrder", true, null));
orderMethod = ByteBuffer.class.getMethod("order", new Class[]{byteOrderClass});
Method nativeOrderMethod = byteOrderClass.getMethod("nativeOrder", (Class[])null);
nativeOrderObject = nativeOrderMethod.invoke(null, (Object[])null);
}
} catch (Throwable t) {
isCDCFP = true;
}
if (!isCDCFP) {
try {
orderMethod.invoke(buf, new Object[]{nativeOrderObject});
} catch (Throwable t) {
}
}
}
}
public static ByteBuffer newByteBuffer(int numElements) {
ByteBuffer bb = ByteBuffer.allocateDirect(numElements);
nativeOrder(bb);
return bb;
}
public static DoubleBuffer newDoubleBuffer(int numElements) {
ByteBuffer bb = newByteBuffer(numElements * SIZEOF_DOUBLE);
return bb.asDoubleBuffer();
}
public static FloatBuffer newFloatBuffer(int numElements) {
ByteBuffer bb = newByteBuffer(numElements * SIZEOF_FLOAT);
return bb.asFloatBuffer();
}
public static IntBuffer newIntBuffer(int numElements) {
ByteBuffer bb = newByteBuffer(numElements * SIZEOF_INT);
return bb.asIntBuffer();
}
public static ShortBuffer newShortBuffer(int numElements) {
ByteBuffer bb = newByteBuffer(numElements * SIZEOF_SHORT);
return bb.asShortBuffer();
}
}
