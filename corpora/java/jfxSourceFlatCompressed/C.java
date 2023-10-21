package com.sun.glass.ui.monocle;
import java.nio.ByteBuffer;
import java.security.Permission;
class C {
private static Permission permission = new RuntimePermission("loadLibrary.*");
private static C instance = new C();
static C getC() {
checkPermissions();
return instance;
}
private static void checkPermissions() {
@SuppressWarnings("removal")
SecurityManager security = System.getSecurityManager();
if (security != null) {
security.checkPermission(permission);
}
}
private C() {
}
static abstract class Structure {
final ByteBuffer b;
final long p;
protected Structure() {
b = ByteBuffer.allocateDirect(sizeof());
p = getC().GetDirectBufferAddress(b);
}
protected Structure(long ptr) {
b = getC().NewDirectByteBuffer(ptr, sizeof());
p = ptr;
}
abstract int sizeof();
}
native ByteBuffer NewDirectByteBuffer(long ptr, int size);
native long GetDirectBufferAddress(ByteBuffer b);
}
